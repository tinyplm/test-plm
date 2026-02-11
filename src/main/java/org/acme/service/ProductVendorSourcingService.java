package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import org.acme.entity.Product;
import org.acme.entity.ProductVendorSourcing;
import org.acme.entity.Vendor;
import org.acme.repository.ProductRepository;
import org.acme.repository.ProductVendorSourcingRepository;
import org.acme.repository.VendorRepository;

@ApplicationScoped
public class ProductVendorSourcingService {

    @Inject
    ProductRepository productRepository;

    @Inject
    VendorRepository vendorRepository;

    @Inject
    ProductVendorSourcingRepository productVendorSourcingRepository;

    public List<ProductVendorSourcing> listByProduct(UUID productId) {
        getProductOrThrow(productId);
        return productVendorSourcingRepository.listByProductId(productId);
    }

    @Transactional
    public ProductVendorSourcing create(UUID productId, ProductVendorSourcingCommand command) {
        Product product = getProductOrThrow(productId);
        validateCommand(command);

        if (productVendorSourcingRepository.findByProductIdAndVendorId(productId, command.vendorId()) != null) {
            throw new IllegalArgumentException("Vendor is already linked to this product.");
        }
        if (command.primaryVendor()) {
            ensureNoOtherPrimary(productId, null);
        }

        Vendor vendor = getVendorOrThrow(command.vendorId());
        ProductVendorSourcing link = new ProductVendorSourcing();
        applyCommand(link, product, vendor, command);
        productVendorSourcingRepository.persist(link);
        return link;
    }

    @Transactional
    public List<ProductVendorSourcing> replaceAll(UUID productId, List<ProductVendorSourcingCommand> commands) {
        Product product = getProductOrThrow(productId);
        if (commands == null) {
            throw new IllegalArgumentException("Vendor links payload is required.");
        }
        validateBatch(commands);

        for (ProductVendorSourcing existing : productVendorSourcingRepository.listByProductId(productId)) {
            productVendorSourcingRepository.delete(existing);
        }

        for (ProductVendorSourcingCommand command : commands) {
            Vendor vendor = getVendorOrThrow(command.vendorId());
            ProductVendorSourcing link = new ProductVendorSourcing();
            applyCommand(link, product, vendor, command);
            productVendorSourcingRepository.persist(link);
        }

        return productVendorSourcingRepository.listByProductId(productId);
    }

    @Transactional
    public ProductVendorSourcing update(UUID productId, UUID linkId, ProductVendorSourcingCommand command) {
        validateCommand(command);

        ProductVendorSourcing existing = productVendorSourcingRepository.findByProductIdAndId(productId, linkId);
        if (existing == null) {
            throw new NoSuchElementException("Product vendor link not found.");
        }

        ProductVendorSourcing duplicate = productVendorSourcingRepository.findByProductIdAndVendorId(productId, command.vendorId());
        if (duplicate != null && !duplicate.id.equals(linkId)) {
            throw new IllegalArgumentException("Vendor is already linked to this product.");
        }

        if (command.primaryVendor()) {
            ensureNoOtherPrimary(productId, linkId);
        }

        Vendor vendor = getVendorOrThrow(command.vendorId());
        applyCommand(existing, existing.product, vendor, command);
        return existing;
    }

    @Transactional
    public boolean delete(UUID productId, UUID linkId) {
        ProductVendorSourcing existing = productVendorSourcingRepository.findByProductIdAndId(productId, linkId);
        if (existing == null) {
            return false;
        }
        productVendorSourcingRepository.delete(existing);
        return true;
    }

    private void validateBatch(List<ProductVendorSourcingCommand> commands) {
        Set<UUID> vendorIds = new HashSet<>();
        int primaryCount = 0;

        for (ProductVendorSourcingCommand command : commands) {
            validateCommand(command);
            if (!vendorIds.add(command.vendorId())) {
                throw new IllegalArgumentException("Duplicate vendor in payload.");
            }
            if (command.primaryVendor()) {
                primaryCount++;
            }
        }

        if (primaryCount > 1) {
            throw new IllegalArgumentException("Only one primary vendor is allowed per product.");
        }
    }

    private void validateCommand(ProductVendorSourcingCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Vendor link payload is required.");
        }
        if (command.vendorId() == null) {
            throw new IllegalArgumentException("Vendor id is required.");
        }
    }

    private void ensureNoOtherPrimary(UUID productId, UUID currentLinkId) {
        ProductVendorSourcing primary = productVendorSourcingRepository.findPrimaryByProductId(productId);
        if (primary != null && (currentLinkId == null || !primary.id.equals(currentLinkId))) {
            throw new IllegalArgumentException("Only one primary vendor is allowed per product.");
        }
    }

    private Product getProductOrThrow(UUID productId) {
        Product product = productRepository.findById(productId);
        if (product == null) {
            throw new NoSuchElementException("Product not found.");
        }
        return product;
    }

    private Vendor getVendorOrThrow(UUID vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId);
        if (vendor == null) {
            throw new NoSuchElementException("Vendor not found.");
        }
        return vendor;
    }

    private void applyCommand(
            ProductVendorSourcing target,
            Product product,
            Vendor vendor,
            ProductVendorSourcingCommand command
    ) {
        target.product = product;
        target.vendor = vendor;
        target.primaryVendor = command.primaryVendor();
        target.vsn = command.vsn();
        target.factoryName = command.factoryName();
        target.factoryCode = command.factoryCode();
        target.factoryCountry = command.factoryCountry();
        target.sustainable = command.sustainable();
        target.contactName = command.contactName();
        target.contactEmail = command.contactEmail();
        target.contactPhone = command.contactPhone();
        target.createdBy = command.createdBy();
    }
}
