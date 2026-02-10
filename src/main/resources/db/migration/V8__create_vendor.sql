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
