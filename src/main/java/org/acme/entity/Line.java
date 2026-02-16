package org.acme.entity;

import io.quarkus.hibernate.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Line extends CoreEntity implements PanacheEntity.Managed {

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
}
