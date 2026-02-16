package org.acme.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class LineDTO {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Create(
        @NotBlank String lineCode,
        @NotBlank String seasonCode,
        Integer year,
        @NotNull UUID brandId,
        @NotNull UUID marketId,
        @NotNull UUID channelId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer plannedStyleCount,
        Integer plannedUnits,
        BigDecimal plannedRevenue
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Update(
        @NotBlank String lineCode,
        @NotBlank String seasonCode,
        Integer year,
        @NotNull UUID brandId,
        @NotNull UUID marketId,
        @NotNull UUID channelId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer plannedStyleCount,
        Integer plannedUnits,
        BigDecimal plannedRevenue,
        long version
    ) {}

    public record Response(
        UUID id,
        long version,
        String lineCode,
        String seasonCode,
        Integer year,
        UUID brandId,
        UUID marketId,
        UUID channelId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer plannedStyleCount,
        Integer plannedUnits,
        BigDecimal plannedRevenue,
        String createdBy,
        LocalDateTime createdAt,
        String updatedBy,
        LocalDateTime updatedAt
    ) {}
}
