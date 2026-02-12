package org.acme.service;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VendorQuoteCommand(
        String quoteNumber,
        Integer versionNumber,
        String currencyCode,
        String incoterm,
        BigDecimal unitCost,
        Integer moq,
        Integer leadTimeDays,
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
        String createdBy
) {}
