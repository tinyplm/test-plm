package org.acme.entity;

import io.quarkus.hibernate.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

@Entity
public class Vendor implements PanacheEntity.Managed {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    public UUID id;

    @NotBlank
    @Column(nullable = false)
    public String name;

    public String type;

    @Column(name = "supplier_name")
    public String supplierName;

    @Column(name = "supplier_id")
    public String supplierId;

    @Column(name = "supplier_number")
    public String supplierNumber;

    @Column(name = "vendor_group")
    public String vendorGroup;

    @Column(name = "agreement_status")
    public String agreementStatus;

    @Column(nullable = false)
    public boolean status;

    @Column(name = "created_by")
    public String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;
}
