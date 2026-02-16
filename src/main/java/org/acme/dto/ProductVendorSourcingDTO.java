package org.acme.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProductVendorSourcingDTO {
    public record Create(
        @NotNull(message = "Vendor ID is required") UUID vendorId,
        boolean primaryVendor,
        String vsn,
        String factoryName,
        String factoryCode,
        String factoryCountry,
        boolean sustainable,
        String contactName,
        String contactEmail,
        String contactPhone
    ) {}

    public record Update(
        boolean primaryVendor,
        String vsn,
        String factoryName,
        String factoryCode,
        String factoryCountry,
        boolean sustainable,
        String contactName,
        String contactEmail,
        String contactPhone,
        long version
    ) {}

    public record Response(
        UUID id,
        long version,
        UUID productId,
        UUID vendorId,
        String vendorName,
        boolean primaryVendor,
        String vsn,
        String factoryName,
        String factoryCode,
        String factoryCountry,
        boolean sustainable,
        String contactName,
        String contactEmail,
        String contactPhone,
        String createdBy,
        LocalDateTime createdAt,
        String updatedBy,
        LocalDateTime updatedAt
    ) {}
}
