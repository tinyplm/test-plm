package org.acme.entity;

import io.quarkus.hibernate.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
public class Product implements PanacheEntity.Managed {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    public UUID id;

    @NotBlank
    @Column(nullable = false)
    public String name;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "line_id", nullable = false)
    public Line line;

    public String description;

    public BigDecimal price;

    public int quantity;
}
