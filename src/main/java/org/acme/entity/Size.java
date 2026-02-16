package org.acme.entity;

import io.quarkus.hibernate.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Size extends CoreEntity implements PanacheEntity.Managed {

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public String sizes;
}
