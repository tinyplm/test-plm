package org.acme.entity;

import io.quarkus.hibernate.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;


@MappedSuperclass
public abstract class CoreEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(nullable = false)
    public UUID id;

    @Version
    @Column(nullable = false)
    public long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;

    @CreationTimestamp
    @Column(name = "created_by", nullable = false)
    public String createdBy;

    @UpdateTimestamp
    @Column(name = "updated_by", nullable = false)
    public String updatedBy;

}
