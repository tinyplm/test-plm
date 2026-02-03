package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;
import org.acme.entity.Size;

@ApplicationScoped
public class SizeRepository implements PanacheRepositoryBase<Size, UUID> {
    public Size findByName(String name) {
        return find("name", name).firstResult();
    }
}
