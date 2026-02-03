package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;
import org.acme.entity.Color;

@ApplicationScoped
public class ColorRepository implements PanacheRepositoryBase<Color, UUID> {
    public Color findByName(String name) {
        return find("name", name).firstResult();
    }
}
