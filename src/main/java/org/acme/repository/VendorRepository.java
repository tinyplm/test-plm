package org.acme.repository;

import io.quarkus.hibernate.panache.PanacheRepository;
import java.util.UUID;
import org.acme.entity.Vendor;
import org.hibernate.annotations.processing.Find;

public interface VendorRepository extends PanacheRepository.Managed<Vendor, UUID> {
    @Find
    Vendor findByName(String name);
}
