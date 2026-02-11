-- Create Line Table
CREATE TABLE IF NOT EXISTS line (
    line_id UUID PRIMARY KEY,
    line_code VARCHAR(255) NOT NULL,
    season_code VARCHAR(255) NOT NULL,
    year INTEGER,
    brand_id UUID NOT NULL,
    market_id UUID NOT NULL,
    channel_id UUID NOT NULL,
    start_date timestamptz,
    end_date timestamptz,
    planned_style_count INTEGER,
    planned_units INTEGER,
    planned_revenue NUMERIC(19, 2),
    created_by VARCHAR(255),
    created_at timestamptz DEFAULT now() NOT NULL,
    updated_at timestamptz DEFAULT now() NOT NULL
);

CREATE UNIQUE INDEX line_id_idx ON line(line_id);

-- Seed Line Data
INSERT INTO line (
    line_id,
    line_code,
    season_code,
    year,
    brand_id,
    market_id,
    channel_id,
    start_date,
    end_date,
    planned_style_count,
    planned_units,
    planned_revenue,
    created_by,
    created_at,
    updated_at
)
VALUES (
    '018f2e76-3b10-7c9a-8a2f-4f1b5e2a1e01',
    'CORE',
    'ALL',
    2026,
    '018f2e76-3b10-7c9a-8a2f-4f1b5e2a1e02',
    '018f2e76-3b10-7c9a-8a2f-4f1b5e2a1e03',
    '018f2e76-3b10-7c9a-8a2f-4f1b5e2a1e04',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    0,
    0.00,
    'system',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Create Product Table
CREATE TABLE IF NOT EXISTS product (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    price NUMERIC(19, 2),
    quantity INTEGER,
    line_id UUID NOT NULL
);

CREATE UNIQUE INDEX product_id_idx ON product(id);
CREATE INDEX product_line_id_idx ON product(line_id);

-- Seed Product Data
INSERT INTO product (id, name, description, price, quantity, line_id)
VALUES
    ('018f2e76-3b10-7c9a-8a2f-4f1b5e2a1c01', 'Widget A', 'Compact widget for daily tasks.', 9.99, 100, '018f2e76-3b10-7c9a-8a2f-4f1b5e2a1e01'),
    ('018f2e76-3b10-7c9a-8a2f-4f1b5e2a1c02', 'Widget B', 'Mid-range widget with extra durability.', 14.50, 60, '018f2e76-3b10-7c9a-8a2f-4f1b5e2a1e01'),
    ('018f2e76-3b10-7c9a-8a2f-4f1b5e2a1c03', 'Widget C', 'Economy widget for bulk usage.', 7.25, 200, '018f2e76-3b10-7c9a-8a2f-4f1b5e2a1e01'),
    ('018f2e76-3b10-7c9a-8a2f-4f1b5e2a1c04', 'Gadget A', 'Feature-rich gadget for home setups.', 19.99, 45, '018f2e76-3b10-7c9a-8a2f-4f1b5e2a1e01'),
    ('018f2e76-3b10-7c9a-8a2f-4f1b5e2a1c05', 'Gadget B', 'Premium gadget with extended warranty.', 29.00, 30, '018f2e76-3b10-7c9a-8a2f-4f1b5e2a1e01'),
    ('018f2e76-3b10-7c9a-8a2f-4f1b5e2a1c06', 'Gadget C', 'Balanced gadget for everyday use.', 24.75, 80, '018f2e76-3b10-7c9a-8a2f-4f1b5e2a1e01'),
    ('018f2e76-3b10-7c9a-8a2f-4f1b5e2a1c07', 'Tool A', 'Lightweight tool for quick fixes.', 4.99, 500, '018f2e76-3b10-7c9a-8a2f-4f1b5e2a1e01'),
    ('018f2e76-3b10-7c9a-8a2f-4f1b5e2a1c08', 'Tool B', 'Reinforced tool for heavy duty work.', 5.49, 350, '018f2e76-3b10-7c9a-8a2f-4f1b5e2a1e01'),
    ('018f2e76-3b10-7c9a-8a2f-4f1b5e2a1c09', 'Accessory A', 'Small accessory compatible with widgets.', 2.99, 1000, '018f2e76-3b10-7c9a-8a2f-4f1b5e2a1e01'),
    ('018f2e76-3b10-7c9a-8a2f-4f1b5e2a1c0a', 'Accessory B', 'Accessory pack for gadgets and tools.', 3.49, 750, '018f2e76-3b10-7c9a-8a2f-4f1b5e2a1e01');
