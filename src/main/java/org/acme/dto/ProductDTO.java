package org.acme.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProductDTO {
    public record Create(
        @NotBlank(message = "Name is required") String name,
        @NotNull(message = "Line ID is required") UUID lineId,
        String description,
        String lifecycle,
        String assortment,
        String buyPlan,
        BigDecimal storeCost,
        BigDecimal retailCost,
        BigDecimal margin,
        String buyer,
        Integer setWeek,
        String inspiration,
        String imageUrl,
        BigDecimal price,
        int quantity
    ) {}

    public record Update(
        @NotBlank(message = "Name is required") String name,
        @NotNull(message = "Line ID is required") UUID lineId,
        String description,
        String lifecycle,
        String assortment,
        String buyPlan,
        BigDecimal storeCost,
        BigDecimal retailCost,
        BigDecimal margin,
        String buyer,
        Integer setWeek,
        String inspiration,
        String imageUrl,
        BigDecimal price,
        int quantity,
        long version
    ) {}

    public record Response(
        UUID id,
        long version,
        String name,
        UUID lineId,
        String description,
        String lifecycle,
        String assortment,
        String buyPlan,
        BigDecimal storeCost,
        BigDecimal retailCost,
        BigDecimal margin,
        String buyer,
        Integer setWeek,
        String inspiration,
        String imageReference,
        String imageUrl,
        BigDecimal price,
        int quantity,
        String createdBy,
        LocalDateTime createdAt,
        String updatedBy,
        LocalDateTime updatedAt
    ) {}
}
