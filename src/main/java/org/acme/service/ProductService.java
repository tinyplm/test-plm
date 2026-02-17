package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.acme.entity.Line;
import org.acme.entity.Product;
import org.acme.repository.LineRepository;
import org.acme.repository.ProductRepository;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ProductService {

    private static final Logger LOG = Logger.getLogger(ProductService.class);

    @Inject
    ProductRepository productRepository;

    @Inject
    LineRepository lineRepository;

    @Inject
    ProductImageStorageService productImageStorageService;

    public List<Product> list() {
        List<Product> products = productRepository.listAll();
        products.forEach(this::enrichWithImageUrl);
        return products;
    }

    public Product findById(UUID id) {
        Product product = productRepository.findById(id);
        if (product != null) {
            enrichWithImageUrl(product);
        }
        return product;
    }

    public List<Product> listByLineId(UUID lineId) {
        List<Product> products = productRepository.list("line.id", lineId);
        products.forEach(this::enrichWithImageUrl);
        return products;
    }

    @Transactional
    public Product create(Product product) {
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
        enrichWithImageUrl(product);
        LOG.infof("PRODUCT_CREATED id=%s name=%s", product.id, product.name);
        return product;
    }

    @Transactional
    public Product update(UUID id, Product product, long version) {
        if (product == null) {
            throw new IllegalArgumentException("Product payload is required.");
        }
        Product existing = productRepository.findById(id);
        if (existing == null) {
            return null;
        }
        if (existing.version != version) {
            throw new jakarta.persistence.OptimisticLockException("Version mismatch. Expected " + version + " but found " + existing.version);
        }
        
        if (product.line != null) {
             existing.line = product.line;
        }

        existing.name = product.name;
        existing.description = product.description;
        existing.lifecycle = product.lifecycle;
        existing.assortment = product.assortment;
        existing.buyPlan = product.buyPlan;
        existing.storeCost = product.storeCost;
        existing.retailCost = product.retailCost;
        existing.margin = product.margin;
        existing.buyer = product.buyer;
        existing.setWeek = product.setWeek;
        existing.inspiration = product.inspiration;
        existing.price = product.price;
        existing.quantity = product.quantity;
        enrichWithImageUrl(existing);
        LOG.infof("PRODUCT_UPDATED id=%s", existing.id);
        return existing;
    }

    @Transactional
    public Product addImage(UUID id, byte[] imageBytes) {
        Product product = getProductOrThrow(id);
        if (product.imageReference != null && !product.imageReference.isBlank()) {
            throw new IllegalArgumentException("Product image already exists. Use update image.");
        }
        saveImageReference(product, imageBytes);
        LOG.infof("PRODUCT_IMAGE_ADDED id=%s", id);
        return product;
    }

    @Transactional
    public Product updateImage(UUID id, byte[] imageBytes) {
        Product product = getProductOrThrow(id);
        saveImageReference(product, imageBytes);
        LOG.infof("PRODUCT_IMAGE_UPDATED id=%s", id);
        return product;
    }

    @Transactional
    public boolean removeImage(UUID id) {
        Product product = getProductOrThrow(id);
        if (product.imageReference == null || product.imageReference.isBlank()) {
            return false;
        }
        productImageStorageService.deleteByReference(product.imageReference);
        product.imageReference = null;
        product.imageUrl = null;
        LOG.infof("PRODUCT_IMAGE_REMOVED id=%s", id);
        return true;
    }

    private void saveImageReference(Product product, byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new IllegalArgumentException("Image payload is required.");
        }
        if (product.imageReference != null && !product.imageReference.isBlank()) {
            productImageStorageService.deleteByReference(product.imageReference);
        }
        product.imageReference = productImageStorageService.putProductImage(product.id, imageBytes);
        enrichWithImageUrl(product);
    }

    private Product getProductOrThrow(UUID id) {
        Product product = productRepository.findById(id);
        if (product == null) {
            throw new NoSuchElementException("Product not found.");
        }
        return product;
    }

    private void enrichWithImageUrl(Product product) {
        product.imageUrl = productImageStorageService.imageUrl(product.imageReference);
    }

    @Transactional
    public boolean delete(UUID id) {
        Product existing = productRepository.findById(id);
        if (existing == null) {
            return false;
        }
        if (existing.imageReference != null && !existing.imageReference.isBlank()) {
            productImageStorageService.deleteByReference(existing.imageReference);
        }
        boolean deleted = productRepository.deleteById(id);
        if (deleted) {
             LOG.infof("PRODUCT_DELETED id=%s", id);
        }
        return deleted;
    }
}
