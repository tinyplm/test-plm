package org.acme.repository;

import io.quarkus.hibernate.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;
import org.acme.entity.ProductVendorSourcing;

@ApplicationScoped
public class ProductVendorSourcingRepository implements PanacheRepository.Managed<ProductVendorSourcing, UUID> {

    public List<ProductVendorSourcing> listByProductId(UUID productId) {
        return list("product.id", productId);
    }

    public ProductVendorSourcing findByProductIdAndId(UUID productId, UUID id) {
        return find("product.id = ?1 and id = ?2", productId, id).firstResult();
    }

    public ProductVendorSourcing findByProductIdAndVendorId(UUID productId, UUID vendorId) {
        return find("product.id = ?1 and vendor.id = ?2", productId, vendorId).firstResult();
    }

    public ProductVendorSourcing findPrimaryByProductId(UUID productId) {
        return find("product.id = ?1 and primaryVendor = true", productId).firstResult();
    }
}
