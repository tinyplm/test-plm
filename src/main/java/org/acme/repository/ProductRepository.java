package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;
import org.acme.entity.Product;

@ApplicationScoped
public class ProductRepository implements PanacheRepositoryBase<Product, UUID> {
    public Product findByName(String name) {
        return find("name", name).firstResult();
    }
}
