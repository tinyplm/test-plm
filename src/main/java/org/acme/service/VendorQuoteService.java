package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import org.acme.entity.Product;
import org.acme.entity.ProductVendorSourcing;
import org.acme.entity.VendorQuote;
import org.acme.entity.VendorQuoteStatus;
import org.acme.repository.ProductRepository;
import org.acme.repository.ProductVendorSourcingRepository;
import org.acme.repository.VendorQuoteRepository;

@ApplicationScoped
public class VendorQuoteService {

    private static final Map<VendorQuoteStatus, Set<VendorQuoteStatus>> ALLOWED_TRANSITIONS = Map.of(
            VendorQuoteStatus.DRAFT,
            EnumSet.of(VendorQuoteStatus.SUBMITTED),
            VendorQuoteStatus.SUBMITTED,
            EnumSet.of(VendorQuoteStatus.UNDER_REVIEW, VendorQuoteStatus.APPROVED, VendorQuoteStatus.REJECTED),
            VendorQuoteStatus.UNDER_REVIEW,
            EnumSet.of(VendorQuoteStatus.APPROVED, VendorQuoteStatus.REJECTED, VendorQuoteStatus.SUBMITTED),
            VendorQuoteStatus.APPROVED,
            EnumSet.of(VendorQuoteStatus.UNDER_REVIEW),
            VendorQuoteStatus.REJECTED,
            EnumSet.of(VendorQuoteStatus.SUBMITTED, VendorQuoteStatus.UNDER_REVIEW)
    );

    @Inject
    ProductRepository productRepository;

    @Inject
    ProductVendorSourcingRepository productVendorSourcingRepository;

    @Inject
    VendorQuoteRepository vendorQuoteRepository;

    public List<VendorQuote> listByLink(UUID productId, UUID linkId, boolean includeDeleted) {
        getProductOrThrow(productId);
        getLinkOrThrow(productId, linkId);
        return vendorQuoteRepository.listByLinkId(linkId, includeDeleted);
    }

    public List<VendorQuote> listByProduct(UUID productId, boolean includeDeleted) {
        getProductOrThrow(productId);
        return vendorQuoteRepository.listByProductId(productId, includeDeleted);
    }

    public VendorQuote findById(UUID productId, UUID linkId, UUID quoteId, boolean includeDeleted) {
        getProductOrThrow(productId);
        getLinkOrThrow(productId, linkId);
        VendorQuote quote = vendorQuoteRepository.findByProductAndLinkAndId(productId, linkId, quoteId, includeDeleted);
        if (quote == null) {
            throw new NoSuchElementException("Vendor quote not found.");
        }
        return quote;
    }

    @Transactional
    public VendorQuote create(UUID productId, UUID linkId, VendorQuote quote) {
        ProductVendorSourcing link = getLinkOrThrow(productId, linkId);
        ensureUniqueQuoteVersion(linkId, quote.quoteNumber, quote.versionNumber, null);

        quote.productVendorSourcing = link;
        quote.status = VendorQuoteStatus.SUBMITTED;
        quote.submittedAt = LocalDateTime.now();
        // quote.submittedBy set by caller or from context? Assuming passed in entity for now or handle later
        quote.deleted = false;
        vendorQuoteRepository.persist(quote);
        return quote;
    }

    @Transactional
    public VendorQuote update(UUID productId, UUID linkId, UUID quoteId, VendorQuote updateData, long version) {
        VendorQuote quote = findById(productId, linkId, quoteId, false);
        
        if (quote.version != version) {
            throw new jakarta.persistence.OptimisticLockException("Version mismatch. Expected " + version + " but found " + quote.version);
        }

        if (quote.status == VendorQuoteStatus.APPROVED) {
            throw new IllegalArgumentException("Approved quote cannot be updated.");
        }

        ensureUniqueQuoteVersion(linkId, updateData.quoteNumber, updateData.versionNumber, quote.id);
        
        quote.quoteNumber = updateData.quoteNumber;
        quote.versionNumber = updateData.versionNumber;
        quote.currencyCode = updateData.currencyCode != null ? updateData.currencyCode.toUpperCase() : null;
        quote.incoterm = updateData.incoterm;
        quote.unitCost = updateData.unitCost;
        quote.moq = updateData.moq;
        quote.leadTimeDays = updateData.leadTimeDays;
        quote.sampleLeadTimeDays = updateData.sampleLeadTimeDays;
        quote.materialCost = updateData.materialCost;
        quote.laborCost = updateData.laborCost;
        quote.overheadCost = updateData.overheadCost;
        quote.logisticsCost = updateData.logisticsCost;
        quote.dutyCost = updateData.dutyCost;
        quote.packagingCost = updateData.packagingCost;
        quote.marginPercent = updateData.marginPercent;
        quote.totalCost = updateData.totalCost;
        quote.capacityPerMonth = updateData.capacityPerMonth;
        quote.paymentTerms = updateData.paymentTerms;
        quote.validFrom = updateData.validFrom;
        quote.validTo = updateData.validTo;
        quote.complianceNotes = updateData.complianceNotes;
        quote.sustainabilityNotes = updateData.sustainabilityNotes;
        
        return quote;
    }

    @Transactional
    public VendorQuote updateStatus(UUID productId, UUID linkId, UUID quoteId, VendorQuoteStatusCommand command) {
        if (command == null || command.status() == null) {
            throw new IllegalArgumentException("Status payload is required.");
        }

        VendorQuote quote = findById(productId, linkId, quoteId, false);
        VendorQuoteStatus current = quote.status;
        VendorQuoteStatus target = command.status();

        if (current != target) {
            Set<VendorQuoteStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, Set.of());
            if (!allowed.contains(target)) {
                throw new IllegalArgumentException("Invalid quote status transition.");
            }
        }

        if (target == VendorQuoteStatus.APPROVED && quote.validTo != null && quote.validTo.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Expired quote cannot be approved.");
        }

        quote.status = target;
        if (target == VendorQuoteStatus.SUBMITTED) {
            quote.submittedAt = LocalDateTime.now();
            quote.submittedBy = command.actor();
        }
        if (target == VendorQuoteStatus.UNDER_REVIEW || target == VendorQuoteStatus.APPROVED || target == VendorQuoteStatus.REJECTED) {
            quote.reviewedAt = LocalDateTime.now();
            quote.reviewedBy = command.actor();
        }
        quote.approvalComment = command.comment();

        return quote;
    }

    @Transactional
    public boolean softDelete(UUID productId, UUID linkId, UUID quoteId, String deletedBy) {
        VendorQuote quote = vendorQuoteRepository.findByProductAndLinkAndId(productId, linkId, quoteId, false);
        if (quote == null) {
            return false;
        }

        quote.deleted = true;
        quote.deletedAt = LocalDateTime.now();
        quote.deletedBy = deletedBy;
        return true;
    }

    private void ensureUniqueQuoteVersion(UUID linkId, String quoteNumber, Integer versionNumber, UUID currentQuoteId) {
        VendorQuote duplicate = vendorQuoteRepository.findByLinkAndQuoteAndVersion(linkId, quoteNumber, versionNumber);
        if (duplicate == null) {
            return;
        }
        if (currentQuoteId != null && duplicate.id.equals(currentQuoteId)) {
            return;
        }
        throw new IllegalArgumentException("Quote number and version must be unique per vendor link.");
    }

    private Product getProductOrThrow(UUID productId) {
        Product product = productRepository.findById(productId);
        if (product == null) {
            throw new NoSuchElementException("Product not found.");
        }
        return product;
    }

    private ProductVendorSourcing getLinkOrThrow(UUID productId, UUID linkId) {
        ProductVendorSourcing link = productVendorSourcingRepository.findByProductIdAndId(productId, linkId);
        if (link == null) {
            throw new NoSuchElementException("Product vendor link not found.");
        }
        return link;
    }
}
