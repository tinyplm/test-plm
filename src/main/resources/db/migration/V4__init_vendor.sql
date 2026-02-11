-- Create Vendor Table
CREATE TABLE IF NOT EXISTS vendor (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255),
    supplier_name VARCHAR(255),
    supplier_id VARCHAR(255),
    supplier_number VARCHAR(255),
    vendor_group VARCHAR(255),
    agreement_status VARCHAR(255),
    status BOOLEAN NOT NULL DEFAULT TRUE,
    created_by VARCHAR(255),
    created_at timestamptz DEFAULT now() NOT NULL,
    updated_at timestamptz DEFAULT now() NOT NULL
);

CREATE UNIQUE INDEX vendor_id_idx ON vendor(id);

-- Create Product Vendor Sourcing Table
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

INSERT INTO vendor (id, name, type, status) VALUES ('018f2e76-3b10-7c9a-8a2f-4f1b5e2a1b01', 'Global Supplies Inc.', 'Manufacturer', TRUE);
