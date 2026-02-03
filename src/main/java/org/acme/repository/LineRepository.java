package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;
import org.acme.entity.Line;

@ApplicationScoped
public class LineRepository implements PanacheRepositoryBase<Line, UUID> {
    public Line findByLineCode(String lineCode) {
        return find("lineCode", lineCode).firstResult();
    }
}
