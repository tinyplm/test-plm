package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.acme.entity.Product;
import org.acme.repository.ProductRepository;

@ApplicationScoped
public class ProductService {

    @Inject
    ProductRepository productRepository;

    public List<Product> list() {
        return productRepository.listAll();
    }

    public Product findById(UUID id) {
        return productRepository.findById(id);
    }

    @Transactional
    public Product create(Product product) {
        if (product == null || product.name == null || product.name.isBlank()) {
            throw new IllegalArgumentException("Product name is required.");
        }
        product.id = null;
        productRepository.persist(product);
        return product;
    }

    @Transactional
    public Product update(UUID id, Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product payload is required.");
        }
        Product existing = productRepository.findById(id);
        if (existing == null) {
            return null;
        }
        existing.name = product.name;
        existing.price = product.price;
        existing.quantity = product.quantity;
        return existing;
    }

    @Transactional
    public boolean delete(UUID id) {
        return productRepository.deleteById(id);
    }
}
