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
    public ProductVendorSourcing create(UUID productId, ProductVendorSourcing link) {
        Product product = getProductOrThrow(productId);
        if (link.vendor == null || link.vendor.id == null) {
            throw new IllegalArgumentException("Vendor is required.");
        }
        Vendor vendor = getVendorOrThrow(link.vendor.id);
        
        if (productVendorSourcingRepository.findByProductIdAndVendorId(productId, vendor.id) != null) {
            throw new IllegalArgumentException("Vendor is already linked to this product.");
        }
        if (link.primaryVendor) {
            ensureNoOtherPrimary(productId, null);
        }

        link.product = product;
        link.vendor = vendor;
        productVendorSourcingRepository.persist(link);
        return link;
    }

    @Transactional
    public ProductVendorSourcing update(UUID productId, UUID linkId, ProductVendorSourcing updateData, long version) {
        ProductVendorSourcing existing = productVendorSourcingRepository.findByProductIdAndId(productId, linkId);
        if (existing == null) {
            throw new NoSuchElementException("Product vendor link not found.");
        }
        
        if (existing.version != version) {
            throw new jakarta.persistence.OptimisticLockException("Version mismatch. Expected " + version + " but found " + existing.version);
        }

        // Check for duplicate vendor if vendor changed (though typically vendor ID shouldn't change in update)
        // Assuming vendor ID is not updatable in this context, or if it is:
        /*
        if (updateData.vendor != null && !updateData.vendor.id.equals(existing.vendor.id)) {
             // check duplicate
        }
        */

        if (updateData.primaryVendor) {
            ensureNoOtherPrimary(productId, linkId);
        }

        existing.primaryVendor = updateData.primaryVendor;
        existing.vsn = updateData.vsn;
        existing.factoryName = updateData.factoryName;
        existing.factoryCode = updateData.factoryCode;
        existing.factoryCountry = updateData.factoryCountry;
        existing.sustainable = updateData.sustainable;
        existing.contactName = updateData.contactName;
        existing.contactEmail = updateData.contactEmail;
        existing.contactPhone = updateData.contactPhone;
        
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
}
