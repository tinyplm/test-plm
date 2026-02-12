package org.acme.repository;

import io.quarkus.hibernate.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;
import org.acme.entity.VendorQuote;

@ApplicationScoped
public class VendorQuoteRepository implements PanacheRepository.Managed<VendorQuote, UUID> {

    public List<VendorQuote> listByLinkId(UUID linkId, boolean includeDeleted) {
        if (includeDeleted) {
            return list("productVendorSourcing.id = ?1 order by createdAt desc", linkId);
        }
        return list("productVendorSourcing.id = ?1 and deleted = false order by createdAt desc", linkId);
    }

    public List<VendorQuote> listByProductId(UUID productId, boolean includeDeleted) {
        if (includeDeleted) {
            return list("productVendorSourcing.product.id = ?1 order by createdAt desc", productId);
        }
        return list("productVendorSourcing.product.id = ?1 and deleted = false order by createdAt desc", productId);
    }

    public VendorQuote findByProductAndLinkAndId(UUID productId, UUID linkId, UUID quoteId, boolean includeDeleted) {
        if (includeDeleted) {
            return find(
                    "productVendorSourcing.product.id = ?1 and productVendorSourcing.id = ?2 and id = ?3",
                    productId,
                    linkId,
                    quoteId
            ).firstResult();
        }
        return find(
                "productVendorSourcing.product.id = ?1 and productVendorSourcing.id = ?2 and id = ?3 and deleted = false",
                productId,
                linkId,
                quoteId
        ).firstResult();
    }

    public VendorQuote findByLinkAndQuoteAndVersion(UUID linkId, String quoteNumber, int versionNumber) {
        return find(
                "productVendorSourcing.id = ?1 and quoteNumber = ?2 and versionNumber = ?3",
                linkId,
                quoteNumber,
                versionNumber
        ).firstResult();
    }
}
