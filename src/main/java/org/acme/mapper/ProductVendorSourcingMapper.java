package org.acme.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.dto.ProductVendorSourcingDTO;
import org.acme.entity.ProductVendorSourcing;
import org.acme.repository.VendorRepository;

@ApplicationScoped
public class ProductVendorSourcingMapper {

    @Inject
    VendorRepository vendorRepository;

    public ProductVendorSourcing toEntity(ProductVendorSourcingDTO.Create request) {
        if (request == null) return null;
        ProductVendorSourcing entity = new ProductVendorSourcing();
        if (request.vendorId() != null) {
            entity.vendor = vendorRepository.findById(request.vendorId());
        }
        entity.primaryVendor = request.primaryVendor();
        entity.vsn = request.vsn();
        entity.factoryName = request.factoryName();
        entity.factoryCode = request.factoryCode();
        entity.factoryCountry = request.factoryCountry();
        entity.sustainable = request.sustainable();
        entity.contactName = request.contactName();
        entity.contactEmail = request.contactEmail();
        entity.contactPhone = request.contactPhone();
        return entity;
    }

    public void updateEntity(ProductVendorSourcing entity, ProductVendorSourcingDTO.Update request) {
        if (entity == null || request == null) return;
        entity.primaryVendor = request.primaryVendor();
        entity.vsn = request.vsn();
        entity.factoryName = request.factoryName();
        entity.factoryCode = request.factoryCode();
        entity.factoryCountry = request.factoryCountry();
        entity.sustainable = request.sustainable();
        entity.contactName = request.contactName();
        entity.contactEmail = request.contactEmail();
        entity.contactPhone = request.contactPhone();
    }

    public ProductVendorSourcingDTO.Response toResponse(ProductVendorSourcing entity) {
        if (entity == null) return null;
        return new ProductVendorSourcingDTO.Response(
            entity.id,
            entity.version,
            entity.product != null ? entity.product.id : null,
            entity.vendor != null ? entity.vendor.id : null,
            entity.vendor != null ? entity.vendor.name : null,
            entity.primaryVendor,
            entity.vsn,
            entity.factoryName,
            entity.factoryCode,
            entity.factoryCountry,
            entity.sustainable,
            entity.contactName,
            entity.contactEmail,
            entity.contactPhone,
            entity.createdBy,
            entity.createdAt,
            entity.updatedBy,
            entity.updatedAt
        );
    }
}
