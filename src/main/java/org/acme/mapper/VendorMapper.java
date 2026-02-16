package org.acme.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.dto.VendorDTO;
import org.acme.entity.Vendor;

@ApplicationScoped
public class VendorMapper {

    public Vendor toEntity(VendorDTO.Create request) {
        if (request == null) return null;
        Vendor vendor = new Vendor();
        vendor.name = request.name();
        vendor.type = request.type();
        vendor.supplierName = request.supplierName();
        vendor.supplierId = request.supplierId();
        vendor.supplierNumber = request.supplierNumber();
        vendor.vendorGroup = request.vendorGroup();
        vendor.agreementStatus = request.agreementStatus();
        vendor.status = request.status();
        return vendor;
    }

    public void updateEntity(Vendor entity, VendorDTO.Update request) {
        if (entity == null || request == null) return;
        entity.name = request.name();
        entity.type = request.type();
        entity.supplierName = request.supplierName();
        entity.supplierId = request.supplierId();
        entity.supplierNumber = request.supplierNumber();
        entity.vendorGroup = request.vendorGroup();
        entity.agreementStatus = request.agreementStatus();
        entity.status = request.status();
    }

    public VendorDTO.Response toResponse(Vendor entity) {
        if (entity == null) return null;
        return new VendorDTO.Response(
            entity.id,
            entity.version,
            entity.name,
            entity.type,
            entity.supplierName,
            entity.supplierId,
            entity.supplierNumber,
            entity.vendorGroup,
            entity.agreementStatus,
            entity.status,
            entity.createdBy,
            entity.createdAt,
            entity.updatedBy,
            entity.updatedAt
        );
    }
}
