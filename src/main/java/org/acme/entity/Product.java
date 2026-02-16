package org.acme.entity;

import io.quarkus.hibernate.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import java.math.BigDecimal;

@Entity
public class Product extends CoreEntity implements PanacheEntity.Managed {

    @Column(nullable = false)
    public String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "line_id", nullable = false)
    public Line line;

    public String description;

    public String lifecycle;

    public String assortment;

    @Column(name = "buy_plan")
    public String buyPlan;

    @Column(name = "store_cost")
    public BigDecimal storeCost;

    @Column(name = "retail_cost")
    public BigDecimal retailCost;

    public BigDecimal margin;

    public String buyer;

    @Column(name = "set_week")
    public Integer setWeek;

    public String inspiration;

    @Column(name = "image_reference")
    public String imageReference;

    @Transient
    public String imageUrl;

    public BigDecimal price;

    public int quantity;
}
