package org.acme.repository;

import io.quarkus.hibernate.panache.PanacheRepository;

import java.util.UUID;
import org.acme.entity.Line;
import org.hibernate.annotations.processing.Find;

public interface LineRepository extends PanacheRepository.Managed<Line, UUID> {
    
    @Find
    Line findByLineCode(String lineCode);

}
