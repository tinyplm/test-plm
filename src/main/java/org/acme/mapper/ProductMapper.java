package org.acme.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.dto.ProductDTO;
import org.acme.entity.Line;
import org.acme.entity.Product;
import org.acme.repository.LineRepository;

@ApplicationScoped
public class ProductMapper {

    @Inject
    LineRepository lineRepository;

    public Product toEntity(ProductDTO.Create request) {
        if (request == null) return null;
        Product product = new Product();
        product.name = request.name();
        if (request.lineId() != null) {
            product.line = lineRepository.findById(request.lineId());
        }
        product.description = request.description();
        product.lifecycle = request.lifecycle();
        product.assortment = request.assortment();
        product.buyPlan = request.buyPlan();
        product.storeCost = request.storeCost();
        product.retailCost = request.retailCost();
        product.margin = request.margin();
        product.buyer = request.buyer();
        product.setWeek = request.setWeek();
        product.inspiration = request.inspiration();
        product.imageUrl = request.imageUrl();
        product.price = request.price();
        product.quantity = request.quantity();
        return product;
    }

    public void updateEntity(Product entity, ProductDTO.Update request) {
        if (entity == null || request == null) return;
        entity.name = request.name();
        if (request.lineId() != null) {
            entity.line = lineRepository.findById(request.lineId());
        }
        entity.description = request.description();
        entity.lifecycle = request.lifecycle();
        entity.assortment = request.assortment();
        entity.buyPlan = request.buyPlan();
        entity.storeCost = request.storeCost();
        entity.retailCost = request.retailCost();
        entity.margin = request.margin();
        entity.buyer = request.buyer();
        entity.setWeek = request.setWeek();
        entity.inspiration = request.inspiration();
        entity.imageUrl = request.imageUrl();
        entity.price = request.price();
        entity.quantity = request.quantity();
    }

    public ProductDTO.Response toResponse(Product entity) {
        if (entity == null) return null;
        return new ProductDTO.Response(
            entity.id,
            entity.version,
            entity.name,
            entity.line != null ? entity.line.id : null,
            entity.description,
            entity.lifecycle,
            entity.assortment,
            entity.buyPlan,
            entity.storeCost,
            entity.retailCost,
            entity.margin,
            entity.buyer,
            entity.setWeek,
            entity.inspiration,
            entity.imageReference,
            entity.imageUrl,
            entity.price,
            entity.quantity,
            entity.createdBy,
            entity.createdAt,
            entity.updatedBy,
            entity.updatedAt
        );
    }
}
