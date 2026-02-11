CREATE TABLE IF NOT EXISTS product_vendor_sourcing (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    vendor_id UUID NOT NULL,
    primary_vendor BOOLEAN NOT NULL DEFAULT FALSE,
    vsn VARCHAR(255),
    factory_name VARCHAR(255),
    factory_code VARCHAR(255),
    factory_country VARCHAR(255),
    sustainable BOOLEAN NOT NULL DEFAULT FALSE,
    contact_name VARCHAR(255),
    contact_email VARCHAR(255),
    contact_phone VARCHAR(255),
    created_by VARCHAR(255),
    created_at timestamptz DEFAULT now() NOT NULL,
    updated_at timestamptz DEFAULT now() NOT NULL,
    CONSTRAINT uq_product_vendor UNIQUE (product_id, vendor_id)
);

CREATE INDEX product_vendor_product_id_idx ON product_vendor_sourcing(product_id);
CREATE INDEX product_vendor_vendor_id_idx ON product_vendor_sourcing(vendor_id);
CREATE UNIQUE INDEX product_vendor_primary_idx ON product_vendor_sourcing(product_id) WHERE primary_vendor = TRUE;
