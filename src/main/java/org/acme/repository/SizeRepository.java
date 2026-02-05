package org.acme.repository;

import io.quarkus.hibernate.panache.PanacheRepository;
import java.util.UUID;
import org.acme.entity.Size;
import org.hibernate.annotations.processing.Find;

public interface SizeRepository extends PanacheRepository.Managed<Size, UUID> {
    @Find
    Size findByName(String name);
}
