package org.acme.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.dto.VendorQuoteDTO;
import org.acme.entity.VendorQuote;

@ApplicationScoped
public class VendorQuoteMapper {

    public VendorQuote toEntity(VendorQuoteDTO.Create request) {
        if (request == null) return null;
        VendorQuote entity = new VendorQuote();
        entity.quoteNumber = request.quoteNumber();
        entity.versionNumber = request.versionNumber();
        entity.currencyCode = request.currencyCode();
        entity.incoterm = request.incoterm();
        entity.unitCost = request.unitCost();
        entity.moq = request.moq();
        entity.leadTimeDays = request.leadTimeDays();
        entity.sampleLeadTimeDays = request.sampleLeadTimeDays();
        entity.materialCost = request.materialCost();
        entity.laborCost = request.laborCost();
        entity.overheadCost = request.overheadCost();
        entity.logisticsCost = request.logisticsCost();
        entity.dutyCost = request.dutyCost();
        entity.packagingCost = request.packagingCost();
        entity.marginPercent = request.marginPercent();
        entity.totalCost = request.totalCost();
        entity.capacityPerMonth = request.capacityPerMonth();
        entity.paymentTerms = request.paymentTerms();
        entity.validFrom = request.validFrom();
        entity.validTo = request.validTo();
        entity.complianceNotes = request.complianceNotes();
        entity.sustainabilityNotes = request.sustainabilityNotes();
        return entity;
    }

    public void updateEntity(VendorQuote entity, VendorQuoteDTO.Update request) {
        if (entity == null || request == null) return;
        entity.quoteNumber = request.quoteNumber();
        entity.versionNumber = request.versionNumber();
        entity.currencyCode = request.currencyCode();
        entity.incoterm = request.incoterm();
        entity.unitCost = request.unitCost();
        entity.moq = request.moq();
        entity.leadTimeDays = request.leadTimeDays();
        entity.sampleLeadTimeDays = request.sampleLeadTimeDays();
        entity.materialCost = request.materialCost();
        entity.laborCost = request.laborCost();
        entity.overheadCost = request.overheadCost();
        entity.logisticsCost = request.logisticsCost();
        entity.dutyCost = request.dutyCost();
        entity.packagingCost = request.packagingCost();
        entity.marginPercent = request.marginPercent();
        entity.totalCost = request.totalCost();
        entity.capacityPerMonth = request.capacityPerMonth();
        entity.paymentTerms = request.paymentTerms();
        entity.validFrom = request.validFrom();
        entity.validTo = request.validTo();
        entity.complianceNotes = request.complianceNotes();
        entity.sustainabilityNotes = request.sustainabilityNotes();
    }

    public VendorQuoteDTO.Response toResponse(VendorQuote entity) {
        if (entity == null) return null;
        return new VendorQuoteDTO.Response(
            entity.id,
            entity.version,
            entity.productVendorSourcing != null ? entity.productVendorSourcing.id : null,
            entity.quoteNumber,
            entity.versionNumber,
            entity.currencyCode,
            entity.incoterm,
            entity.unitCost,
            entity.moq,
            entity.leadTimeDays,
            entity.sampleLeadTimeDays,
            entity.materialCost,
            entity.laborCost,
            entity.overheadCost,
            entity.logisticsCost,
            entity.dutyCost,
            entity.packagingCost,
            entity.marginPercent,
            entity.totalCost,
            entity.capacityPerMonth,
            entity.paymentTerms,
            entity.validFrom,
            entity.validTo,
            entity.complianceNotes,
            entity.sustainabilityNotes,
            entity.status,
            entity.submittedBy,
            entity.submittedAt,
            entity.reviewedBy,
            entity.reviewedAt,
            entity.approvalComment,
            entity.deleted,
            entity.deletedBy,
            entity.deletedAt,
            entity.createdBy,
            entity.createdAt,
            entity.updatedBy,
            entity.updatedAt
        );
    }
}
