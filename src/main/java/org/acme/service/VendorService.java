package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.entity.Vendor;
import org.acme.repository.VendorRepository;
import org.jboss.logging.Logger;

@ApplicationScoped
public class VendorService extends AbstractCrudService<Vendor> {

    private static final Logger LOG = Logger.getLogger(VendorService.class);

    @Inject
    public VendorService(VendorRepository vendorRepository) {
        super(vendorRepository, LOG);
    }

    @Override
    protected void validateForCreate(Vendor vendor) {
        if (vendor.name == null || vendor.name.isBlank()) {
            throw new IllegalArgumentException("Vendor name is required.");
        }
    }

    @Override
    protected void applyUpdates(Vendor existing, Vendor updateData) {
        existing.name = updateData.name;
        existing.type = updateData.type;
        existing.supplierName = updateData.supplierName;
        existing.supplierId = updateData.supplierId;
        existing.supplierNumber = updateData.supplierNumber;
        existing.vendorGroup = updateData.vendorGroup;
        existing.agreementStatus = updateData.agreementStatus;
        existing.status = updateData.status;
    }

    @Override
    protected String entityLabel() {
        return "VENDOR";
    }
}
