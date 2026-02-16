package org.acme.repository;

import io.quarkus.hibernate.panache.PanacheRepository;
import java.util.UUID;
import org.acme.entity.Line;
import org.hibernate.annotations.processing.Find;
import org.hibernate.annotations.processing.HQL;
import org.hibernate.query.SelectionQuery;

public interface LineRepository extends PanacheRepository.Managed<Line, UUID> {
    
    @Find
    Line findByLineCode(String lineCode);

    @HQL("from Line order by createdAt desc")
    SelectionQuery<Line> findAllQuery();

}
