package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.acme.entity.Line;
import org.acme.entity.Product;
import org.acme.repository.LineRepository;
import org.acme.repository.ProductRepository;

@ApplicationScoped
public class ProductService {

    @Inject
    ProductRepository productRepository;

    @Inject
    LineRepository lineRepository;
    public List<Product> list() {
        return productRepository.listAll();
    }

    public Product findById(UUID id) {
        return productRepository.findById(id);
    }

    public List<Product> listByLineId(UUID lineId) {
        return productRepository.list("line.id", lineId);
    }

    @Transactional
    public Product create(Product product) {
        if (product == null || product.name == null || product.name.isBlank()) {
            throw new IllegalArgumentException("Product name is required.");
        }
        if (product.line == null || product.line.id == null) {
            throw new IllegalArgumentException("Product line is required.");
        }
        Line line = lineRepository.findById(product.line.id);
        if (line == null) {
            throw new IllegalArgumentException("Product line is invalid.");
        }
        product.id = null;
        product.line = line;
        productRepository.persist(product);
        return product;
    }

    @Transactional
    public Product update(UUID id, Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product payload is required.");
        }
        if (product.line == null || product.line.id == null) {
            throw new IllegalArgumentException("Product line is required.");
        }
        Product existing = productRepository.findById(id);
        if (existing == null) {
            return null;
        }
        Line line = lineRepository.findById(product.line.id);
        if (line == null) {
            throw new IllegalArgumentException("Product line is invalid.");
        }
        existing.name = product.name;
        existing.line = line;
        existing.price = product.price;
        existing.quantity = product.quantity;
        return existing;
    }

    @Transactional
    public boolean delete(UUID id) {
        return productRepository.deleteById(id);
    }
}
