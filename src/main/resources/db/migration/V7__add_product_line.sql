ALTER TABLE product
ADD COLUMN line_id UUID;

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

UPDATE product
SET line_id = '018f2e76-3b10-7c9a-8a2f-4f1b5e2a1e01'
WHERE line_id IS NULL;

ALTER TABLE product
ALTER COLUMN line_id SET NOT NULL;

CREATE INDEX product_line_id_idx ON product(line_id);
