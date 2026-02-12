package org.acme.entity;

import io.quarkus.hibernate.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "vendor_quote")
public class VendorQuote implements PanacheEntity.Managed {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    public UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_vendor_sourcing_id", nullable = false)
    public ProductVendorSourcing productVendorSourcing;

    @Column(name = "quote_number", nullable = false)
    public String quoteNumber;

    @Column(name = "version_number", nullable = false)
    public int versionNumber;

    @Column(name = "currency_code", nullable = false)
    public String currencyCode;

    public String incoterm;

    @Column(name = "unit_cost", nullable = false)
    public BigDecimal unitCost;

    @Column(nullable = false)
    public int moq;

    @Column(name = "lead_time_days", nullable = false)
    public int leadTimeDays;

    @Column(name = "sample_lead_time_days")
    public Integer sampleLeadTimeDays;

    @Column(name = "material_cost")
    public BigDecimal materialCost;

    @Column(name = "labor_cost")
    public BigDecimal laborCost;

    @Column(name = "overhead_cost")
    public BigDecimal overheadCost;

    @Column(name = "logistics_cost")
    public BigDecimal logisticsCost;

    @Column(name = "duty_cost")
    public BigDecimal dutyCost;

    @Column(name = "packaging_cost")
    public BigDecimal packagingCost;

    @Column(name = "margin_percent")
    public BigDecimal marginPercent;

    @Column(name = "total_cost")
    public BigDecimal totalCost;

    @Column(name = "capacity_per_month")
    public Integer capacityPerMonth;

    @Column(name = "payment_terms")
    public String paymentTerms;

    @Column(name = "valid_from")
    public LocalDate validFrom;

    @Column(name = "valid_to")
    public LocalDate validTo;

    @Column(name = "compliance_notes")
    public String complianceNotes;

    @Column(name = "sustainability_notes")
    public String sustainabilityNotes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public VendorQuoteStatus status;

    @Column(name = "created_by")
    public String createdBy;

    @Column(name = "submitted_by")
    public String submittedBy;

    @Column(name = "submitted_at")
    public LocalDateTime submittedAt;

    @Column(name = "reviewed_by")
    public String reviewedBy;

    @Column(name = "reviewed_at")
    public LocalDateTime reviewedAt;

    @Column(name = "approval_comment")
    public String approvalComment;

    @Column(nullable = false)
    public boolean deleted;

    @Column(name = "deleted_at")
    public LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    public String deletedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;
}
