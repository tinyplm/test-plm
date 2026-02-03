package org.acme.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
public class Size extends PanacheEntityBase {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    public UUID id;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public String sizes;
}
