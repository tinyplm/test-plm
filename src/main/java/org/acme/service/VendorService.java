package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.acme.entity.Vendor;
import org.acme.repository.VendorRepository;

@ApplicationScoped
public class VendorService {

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
        return vendor;
    }

    @Transactional
    public Vendor update(UUID id, Vendor vendor) {
        if (vendor == null) {
            throw new IllegalArgumentException("Vendor payload is required.");
        }
        Vendor existing = vendorRepository.findById(id);
        if (existing == null) {
            return null;
        }
        existing.name = vendor.name;
        existing.type = vendor.type;
        existing.supplierName = vendor.supplierName;
        existing.supplierId = vendor.supplierId;
        existing.supplierNumber = vendor.supplierNumber;
        existing.vendorGroup = vendor.vendorGroup;
        existing.agreementStatus = vendor.agreementStatus;
        existing.status = vendor.status;
        existing.createdBy = vendor.createdBy;
        return existing;
    }

    @Transactional
    public boolean delete(UUID id) {
        return vendorRepository.deleteById(id);
    }
}
