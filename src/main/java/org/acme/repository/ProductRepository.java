package org.acme.repository;

import io.quarkus.hibernate.panache.PanacheRepository;
import java.util.UUID;
import org.acme.entity.Product;
import org.hibernate.annotations.processing.Find;

public interface ProductRepository extends PanacheRepository.Managed<Product, UUID> {
    @Find
    Product findByName(String name);
}
