package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.acme.entity.Vendor;
import org.acme.repository.VendorRepository;
import org.jboss.logging.Logger;

@ApplicationScoped
public class VendorService {

    private static final Logger LOG = Logger.getLogger(VendorService.class);

    @Inject
    VendorRepository vendorRepository;

    public List<Vendor> list() {
        return vendorRepository.listAll();
    }

    public Vendor findById(UUID id) {
        return vendorRepository.findById(id);
    }

    @Transactional
    public Vendor create(Vendor vendor) {
        if (vendor == null || vendor.name == null || vendor.name.isBlank()) {
            throw new IllegalArgumentException("Vendor name is required.");
        }
        vendor.id = null;
        vendorRepository.persist(vendor);
        LOG.infof("VENDOR_CREATED id=%s name=%s", vendor.id, vendor.name);
        return vendor;
    }

    @Transactional
    public Vendor update(UUID id, Vendor updateData, long version) {
        if (updateData == null) {
            throw new IllegalArgumentException("Vendor payload is required.");
        }
        Vendor existing = vendorRepository.findById(id);
        if (existing == null) {
            return null;
        }
        if (existing.version != version) {
             throw new jakarta.persistence.OptimisticLockException("Version mismatch. Expected " + version + " but found " + existing.version);
        }
        existing.name = updateData.name;
        existing.type = updateData.type;
        existing.supplierName = updateData.supplierName;
        existing.supplierId = updateData.supplierId;
        existing.supplierNumber = updateData.supplierNumber;
        existing.vendorGroup = updateData.vendorGroup;
        existing.agreementStatus = updateData.agreementStatus;
        existing.status = updateData.status;
        LOG.infof("VENDOR_UPDATED id=%s", existing.id);
        return existing;
    }

    @Transactional
    public boolean delete(UUID id) {
        boolean deleted = vendorRepository.deleteById(id);
        if (deleted) {
            LOG.infof("VENDOR_DELETED id=%s", id);
        }
        return deleted;
    }
}
