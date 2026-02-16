package org.acme.entity;

import io.quarkus.hibernate.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "product_vendor_sourcing",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "vendor_id"})
)
public class ProductVendorSourcing extends CoreEntity implements PanacheEntity.Managed {

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    public Product product;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vendor_id", nullable = false)
    public Vendor vendor;

    @Column(name = "primary_vendor", nullable = false)
    public boolean primaryVendor;

    public String vsn;

    @Column(name = "factory_name")
    public String factoryName;

    @Column(name = "factory_code")
    public String factoryCode;

    @Column(name = "factory_country")
    public String factoryCountry;

    @Column(name = "sustainable", nullable = false)
    public boolean sustainable;

    @Column(name = "contact_name")
    public String contactName;

    @Column(name = "contact_email")
    public String contactEmail;

    @Column(name = "contact_phone")
    public String contactPhone;
}
