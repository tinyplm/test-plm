package org.acme.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

@Entity
public class Line extends PanacheEntityBase {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "line_id")
    public UUID id;

    @NotBlank
    @Column(name = "line_code", nullable = false)
    public String lineCode;

    @NotBlank
    @Column(name = "season_code", nullable = false)
    public String seasonCode;

    @Column(name = "year")
    public Integer year;

    @NotNull
    @Column(name = "brand_id", nullable = false)
    public UUID brandId;

    @NotNull
    @Column(name = "market_id", nullable = false)
    public UUID marketId;

    @NotNull
    @Column(name = "channel_id", nullable = false)
    public UUID channelId;

    @Column(name = "start_date")
    public LocalDateTime startDate;

    @Column(name = "end_date")
    public LocalDateTime endDate;

    @Column(name = "planned_style_count")
    public Integer plannedStyleCount;

    @Column(name = "planned_units")
    public Integer plannedUnits;

    @Column(name = "planned_revenue")
    public BigDecimal plannedRevenue;

    @Column(name = "created_by")
    public String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;
}
