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
    public VendorQuote create(UUID productId, UUID linkId, VendorQuoteCommand command) {
        ProductVendorSourcing link = getLinkOrThrow(productId, linkId);
        validateQuoteCommand(command);
        ensureUniqueQuoteVersion(linkId, command.quoteNumber(), command.versionNumber(), null);

        VendorQuote quote = new VendorQuote();
        applyCommand(quote, link, command);
        quote.status = VendorQuoteStatus.SUBMITTED;
        quote.submittedAt = LocalDateTime.now();
        quote.submittedBy = command.createdBy();
        quote.deleted = false;
        vendorQuoteRepository.persist(quote);
        return quote;
    }

    @Transactional
    public VendorQuote update(UUID productId, UUID linkId, UUID quoteId, VendorQuoteCommand command) {
        validateQuoteCommand(command);

        VendorQuote quote = findById(productId, linkId, quoteId, false);
        if (quote.status == VendorQuoteStatus.APPROVED) {
            throw new IllegalArgumentException("Approved quote cannot be updated.");
        }

        ensureUniqueQuoteVersion(linkId, command.quoteNumber(), command.versionNumber(), quote.id);
        applyCommand(quote, quote.productVendorSourcing, command);
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

    private void validateQuoteCommand(VendorQuoteCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Vendor quote payload is required.");
        }
        if (command.quoteNumber() == null || command.quoteNumber().isBlank()) {
            throw new IllegalArgumentException("Quote number is required.");
        }
        if (command.versionNumber() == null || command.versionNumber() < 1) {
            throw new IllegalArgumentException("Version number must be at least 1.");
        }
        if (command.currencyCode() == null || command.currencyCode().isBlank()) {
            throw new IllegalArgumentException("Currency code is required.");
        }
        if (command.unitCost() == null || command.unitCost().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit cost must be zero or positive.");
        }
        if (command.moq() == null || command.moq() < 0) {
            throw new IllegalArgumentException("MOQ must be zero or positive.");
        }
        if (command.leadTimeDays() == null || command.leadTimeDays() < 0) {
            throw new IllegalArgumentException("Lead time must be zero or positive.");
        }
        if (command.sampleLeadTimeDays() != null && command.sampleLeadTimeDays() < 0) {
            throw new IllegalArgumentException("Sample lead time must be zero or positive.");
        }
        if (command.validFrom() != null && command.validTo() != null && command.validTo().isBefore(command.validFrom())) {
            throw new IllegalArgumentException("Quote validity dates are invalid.");
        }
        validateNonNegative(command.materialCost(), "Material cost must be zero or positive.");
        validateNonNegative(command.laborCost(), "Labor cost must be zero or positive.");
        validateNonNegative(command.overheadCost(), "Overhead cost must be zero or positive.");
        validateNonNegative(command.logisticsCost(), "Logistics cost must be zero or positive.");
        validateNonNegative(command.dutyCost(), "Duty cost must be zero or positive.");
        validateNonNegative(command.packagingCost(), "Packaging cost must be zero or positive.");
        validateNonNegative(command.marginPercent(), "Margin percent must be zero or positive.");
        validateNonNegative(command.totalCost(), "Total cost must be zero or positive.");

        if (command.capacityPerMonth() != null && command.capacityPerMonth() < 0) {
            throw new IllegalArgumentException("Capacity per month must be zero or positive.");
        }
    }

    private void validateNonNegative(BigDecimal value, String message) {
        if (value != null && value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(message);
        }
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

    private void applyCommand(VendorQuote quote, ProductVendorSourcing link, VendorQuoteCommand command) {
        quote.productVendorSourcing = link;
        quote.quoteNumber = command.quoteNumber();
        quote.versionNumber = command.versionNumber();
        quote.currencyCode = command.currencyCode().toUpperCase();
        quote.incoterm = command.incoterm();
        quote.unitCost = command.unitCost();
        quote.moq = command.moq();
        quote.leadTimeDays = command.leadTimeDays();
        quote.sampleLeadTimeDays = command.sampleLeadTimeDays();
        quote.materialCost = command.materialCost();
        quote.laborCost = command.laborCost();
        quote.overheadCost = command.overheadCost();
        quote.logisticsCost = command.logisticsCost();
        quote.dutyCost = command.dutyCost();
        quote.packagingCost = command.packagingCost();
        quote.marginPercent = command.marginPercent();
        quote.totalCost = command.totalCost();
        quote.capacityPerMonth = command.capacityPerMonth();
        quote.paymentTerms = command.paymentTerms();
        quote.validFrom = command.validFrom();
        quote.validTo = command.validTo();
        quote.complianceNotes = command.complianceNotes();
        quote.sustainabilityNotes = command.sustainabilityNotes();
        quote.createdBy = command.createdBy();
    }
}
