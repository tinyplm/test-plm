package org.acme.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.acme.entity.VendorQuoteStatus;

public class VendorQuoteDTO {
    public record Create(
        @NotBlank(message = "Quote number is required") String quoteNumber,
        @Min(value = 1, message = "Version number must be at least 1") int versionNumber,
        @NotBlank(message = "Currency code is required") String currencyCode,
        String incoterm,
        @NotNull(message = "Unit cost is required") @Min(0) BigDecimal unitCost,
        @Min(0) int moq,
        @Min(0) int leadTimeDays,
        Integer sampleLeadTimeDays,
        BigDecimal materialCost,
        BigDecimal laborCost,
        BigDecimal overheadCost,
        BigDecimal logisticsCost,
        BigDecimal dutyCost,
        BigDecimal packagingCost,
        BigDecimal marginPercent,
        BigDecimal totalCost,
        Integer capacityPerMonth,
        String paymentTerms,
        LocalDate validFrom,
        LocalDate validTo,
        String complianceNotes,
        String sustainabilityNotes
    ) {}

    public record Update(
        @NotBlank(message = "Quote number is required") String quoteNumber,
        @Min(value = 1, message = "Version number must be at least 1") int versionNumber,
        @NotBlank(message = "Currency code is required") String currencyCode,
        String incoterm,
        @NotNull(message = "Unit cost is required") @Min(0) BigDecimal unitCost,
        @Min(0) int moq,
        @Min(0) int leadTimeDays,
        Integer sampleLeadTimeDays,
        BigDecimal materialCost,
        BigDecimal laborCost,
        BigDecimal overheadCost,
        BigDecimal logisticsCost,
        BigDecimal dutyCost,
        BigDecimal packagingCost,
        BigDecimal marginPercent,
        BigDecimal totalCost,
        Integer capacityPerMonth,
        String paymentTerms,
        LocalDate validFrom,
        LocalDate validTo,
        String complianceNotes,
        String sustainabilityNotes,
        long version
    ) {}
    
    public record UpdateStatus(
        @NotNull(message = "Status is required") VendorQuoteStatus status,
        String comment,
        long version
    ) {}

    public record Response(
        UUID id,
        long version,
        UUID productVendorSourcingId,
        String quoteNumber,
        int versionNumber,
        String currencyCode,
        String incoterm,
        BigDecimal unitCost,
        int moq,
        int leadTimeDays,
        Integer sampleLeadTimeDays,
        BigDecimal materialCost,
        BigDecimal laborCost,
        BigDecimal overheadCost,
        BigDecimal logisticsCost,
        BigDecimal dutyCost,
        BigDecimal packagingCost,
        BigDecimal marginPercent,
        BigDecimal totalCost,
        Integer capacityPerMonth,
        String paymentTerms,
        LocalDate validFrom,
        LocalDate validTo,
        String complianceNotes,
        String sustainabilityNotes,
        VendorQuoteStatus status,
        String submittedBy,
        LocalDateTime submittedAt,
        String reviewedBy,
        LocalDateTime reviewedAt,
        String approvalComment,
        boolean deleted,
        String deletedBy,
        LocalDateTime deletedAt,
        String createdBy,
        LocalDateTime createdAt,
        String updatedBy,
        LocalDateTime updatedAt
    ) {}
}
