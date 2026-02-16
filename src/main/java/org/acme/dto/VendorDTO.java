package org.acme.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

public class VendorDTO {
    public record Create(
        @NotBlank(message = "Name is required") String name,
        String type,
        String supplierName,
        String supplierId,
        String supplierNumber,
        String vendorGroup,
        String agreementStatus,
        boolean status
    ) {}

    public record Update(
        @NotBlank(message = "Name is required") String name,
        String type,
        String supplierName,
        String supplierId,
        String supplierNumber,
        String vendorGroup,
        String agreementStatus,
        boolean status,
        long version
    ) {}

    public record Response(
        UUID id,
        long version,
        String name,
        String type,
        String supplierName,
        String supplierId,
        String supplierNumber,
        String vendorGroup,
        String agreementStatus,
        boolean status,
        String createdBy,
        LocalDateTime createdAt,
        String updatedBy,
        LocalDateTime updatedAt
    ) {}
}
