-- =================================================================================================
-- 2. TABLE DEFINITIONS
-- =================================================================================================

-- 2.1 LINE
CREATE TABLE line (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    
    line_code VARCHAR(255) NOT NULL,
    season_code VARCHAR(255) NOT NULL,
    year INTEGER,
    brand_id UUID NOT NULL,
    market_id UUID NOT NULL,
    channel_id UUID NOT NULL,
    start_date TIMESTAMPTZ,
    end_date TIMESTAMPTZ,
    planned_style_count INTEGER,
    planned_units INTEGER,
    planned_revenue NUMERIC(19, 2)
);
CREATE UNIQUE INDEX line_code_season_idx ON line(line_code, season_code, year);

CREATE TABLE line_audit (
    id BIGSERIAL PRIMARY KEY,
    operation TEXT NOT NULL CHECK (operation IN ('INSERT', 'UPDATE', 'DELETE')),
    old_data JSONB,
    new_data JSONB,
    changed_at TIMESTAMPTZ DEFAULT NOW()
);
CREATE TRIGGER trg_line_audit AFTER INSERT OR UPDATE OR DELETE ON line FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();


-- 2.2 SIZE
CREATE TABLE size (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    
    name VARCHAR(255) NOT NULL UNIQUE,
    sizes VARCHAR(255) NOT NULL
);

CREATE TABLE size_audit (
    id BIGSERIAL PRIMARY KEY,
    operation TEXT NOT NULL CHECK (operation IN ('INSERT', 'UPDATE', 'DELETE')),
    old_data JSONB,
    new_data JSONB,
    changed_at TIMESTAMPTZ DEFAULT NOW()
);
CREATE TRIGGER trg_size_audit AFTER INSERT OR UPDATE OR DELETE ON size FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();


-- 2.3 COLOR
CREATE TABLE color (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    rgb VARCHAR(50) NOT NULL
);

CREATE TABLE color_audit (
    id BIGSERIAL PRIMARY KEY,
    operation TEXT NOT NULL CHECK (operation IN ('INSERT', 'UPDATE', 'DELETE')),
    old_data JSONB,
    new_data JSONB,
    changed_at TIMESTAMPTZ DEFAULT NOW()
);
CREATE TRIGGER trg_color_audit AFTER INSERT OR UPDATE OR DELETE ON color FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();


-- 2.4 VENDOR
CREATE TABLE vendor (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    
    name VARCHAR(255) NOT NULL UNIQUE,
    type VARCHAR(255),
    supplier_name VARCHAR(255),
    supplier_id VARCHAR(255),
    supplier_number VARCHAR(255),
    vendor_group VARCHAR(255),
    agreement_status VARCHAR(255),
    status BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE vendor_audit (
    id BIGSERIAL PRIMARY KEY,
    operation TEXT NOT NULL CHECK (operation IN ('INSERT', 'UPDATE', 'DELETE')),
    old_data JSONB,
    new_data JSONB,
    changed_at TIMESTAMPTZ DEFAULT NOW()
);
CREATE TRIGGER trg_vendor_audit AFTER INSERT OR UPDATE OR DELETE ON vendor FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();


-- 2.5 PRODUCT
CREATE TABLE product (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    
    line_id UUID NOT NULL REFERENCES line(id),
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    price NUMERIC(19, 2),
    quantity INTEGER,
    lifecycle VARCHAR(255),
    assortment VARCHAR(255),
    buy_plan VARCHAR(255),
    store_cost NUMERIC(19, 2),
    retail_cost NUMERIC(19, 2),
    margin NUMERIC(19, 2),
    buyer VARCHAR(255),
    set_week INTEGER,
    inspiration VARCHAR(1000),
    image_reference VARCHAR(1024)
);
CREATE INDEX product_line_idx ON product(line_id);

CREATE TABLE product_audit (
    id BIGSERIAL PRIMARY KEY,
    operation TEXT NOT NULL CHECK (operation IN ('INSERT', 'UPDATE', 'DELETE')),
    old_data JSONB,
    new_data JSONB,
    changed_at TIMESTAMPTZ DEFAULT NOW()
);
CREATE TRIGGER trg_product_audit AFTER INSERT OR UPDATE OR DELETE ON product FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();


-- 2.6 PRODUCT_VENDOR_SOURCING
CREATE TABLE product_vendor_sourcing (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    
    product_id UUID NOT NULL REFERENCES product(id),
    vendor_id UUID NOT NULL REFERENCES vendor(id),
    primary_vendor BOOLEAN NOT NULL DEFAULT FALSE,
    vsn VARCHAR(255),
    factory_name VARCHAR(255),
    factory_code VARCHAR(255),
    factory_country VARCHAR(255),
    sustainable BOOLEAN NOT NULL DEFAULT FALSE,
    contact_name VARCHAR(255),
    contact_email VARCHAR(255),
    contact_phone VARCHAR(255),
    
    CONSTRAINT uq_product_vendor UNIQUE (product_id, vendor_id)
);
CREATE UNIQUE INDEX product_vendor_primary_idx ON product_vendor_sourcing(product_id) WHERE primary_vendor = TRUE;

CREATE TABLE product_vendor_sourcing_audit (
    id BIGSERIAL PRIMARY KEY,
    operation TEXT NOT NULL CHECK (operation IN ('INSERT', 'UPDATE', 'DELETE')),
    old_data JSONB,
    new_data JSONB,
    changed_at TIMESTAMPTZ DEFAULT NOW()
);
CREATE TRIGGER trg_product_vendor_sourcing_audit AFTER INSERT OR UPDATE OR DELETE ON product_vendor_sourcing FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();


-- 2.7 VENDOR_QUOTE
CREATE TABLE vendor_quote (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    
    product_vendor_sourcing_id UUID NOT NULL REFERENCES product_vendor_sourcing(id),
    quote_number VARCHAR(100) NOT NULL,
    version_number INTEGER NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    incoterm VARCHAR(30),
    unit_cost NUMERIC(19, 4) NOT NULL,
    moq INTEGER NOT NULL,
    lead_time_days INTEGER NOT NULL,
    sample_lead_time_days INTEGER,
    material_cost NUMERIC(19, 4),
    labor_cost NUMERIC(19, 4),
    overhead_cost NUMERIC(19, 4),
    logistics_cost NUMERIC(19, 4),
    duty_cost NUMERIC(19, 4),
    packaging_cost NUMERIC(19, 4),
    margin_percent NUMERIC(5, 2),
    total_cost NUMERIC(19, 4),
    capacity_per_month INTEGER,
    payment_terms VARCHAR(255),
    valid_from DATE,
    valid_to DATE,
    compliance_notes VARCHAR(1000),
    sustainability_notes VARCHAR(1000),
    status VARCHAR(30) NOT NULL,
    submitted_by VARCHAR(255),
    submitted_at TIMESTAMPTZ,
    reviewed_by VARCHAR(255),
    reviewed_at TIMESTAMPTZ,
    approval_comment VARCHAR(1000),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ,
    deleted_by VARCHAR(255),
    
    CONSTRAINT uq_vendor_quote_version UNIQUE (product_vendor_sourcing_id, quote_number, version_number)
);
CREATE INDEX vendor_quote_status_idx ON vendor_quote(status);

CREATE TABLE vendor_quote_audit (
    id BIGSERIAL PRIMARY KEY,
    operation TEXT NOT NULL CHECK (operation IN ('INSERT', 'UPDATE', 'DELETE')),
    old_data JSONB,
    new_data JSONB,
    changed_at TIMESTAMPTZ DEFAULT NOW()
);
CREATE TRIGGER trg_vendor_quote_audit AFTER INSERT OR UPDATE OR DELETE ON vendor_quote FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();
