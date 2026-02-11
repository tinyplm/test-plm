package org.acme.service;

import java.util.UUID;

public record ProductVendorSourcingCommand(
        UUID vendorId,
        boolean primaryVendor,
        String vsn,
        String factoryName,
        String factoryCode,
        String factoryCountry,
        boolean sustainable,
        String contactName,
        String contactEmail,
        String contactPhone,
        String createdBy
) {}
