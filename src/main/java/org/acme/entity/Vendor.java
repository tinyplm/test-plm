package org.acme.entity;

import io.quarkus.hibernate.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Vendor extends CoreEntity implements PanacheEntity.Managed {

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
}
