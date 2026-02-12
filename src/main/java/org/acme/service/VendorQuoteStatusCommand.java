package org.acme.service;

import org.acme.entity.VendorQuoteStatus;

public record VendorQuoteStatusCommand(
        VendorQuoteStatus status,
        String actor,
        String comment
) {}
