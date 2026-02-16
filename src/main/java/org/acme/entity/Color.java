package org.acme.entity;

import io.quarkus.hibernate.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Color extends CoreEntity implements PanacheEntity.Managed {

    @NotBlank
    @Column(nullable = false)
    public String name;

    public String description;

    @NotBlank
    @Column(nullable = false)
    public String rgb;
}
