-- =================================================================================================
-- DEMO DATA SEEDING
-- PostgreSQL 18+
-- Uses native uuidv7()
-- Set-based inserts (Flyway safe)
-- =================================================================================================


-- ================================================================================================
-- 3.1 LINE (50 rows)
-- ================================================================================================
INSERT INTO line (
    id,
    line_code,
    season_code,
    year,
    brand_id,
    market_id,
    channel_id,
    created_by
)
SELECT
    uuidv7(),
    'LINE-' || i,
    CASE WHEN i % 2 = 0 THEN 'SS' ELSE 'FW' END,
    2026,
    uuidv7(),
    uuidv7(),
    uuidv7(),
    'system'
FROM generate_series(1,50) AS i;



-- ================================================================================================
-- 3.2 COLOR (50 rows)
-- ================================================================================================
INSERT INTO color (id, name, rgb, created_by)
SELECT
    uuidv7(),
    'Color-' || i,
    (i*5)%255 || ',' || (i*4)%255 || ',' || (i*3)%255,
    'system'
FROM generate_series(1,50) AS i;



-- ================================================================================================
-- 3.3 SIZE (50 rows)
-- ================================================================================================
INSERT INTO size (id, name, sizes, created_by)
SELECT
    uuidv7(),
    'Size-Grp-' || i,
    'S' || i || ', M' || i || ', L' || i,
    'system'
FROM generate_series(1,50) AS i;



-- ================================================================================================
-- 3.4 VENDOR (50 rows)
-- ================================================================================================
INSERT INTO vendor (id, name, type, status, created_by)
SELECT
    uuidv7(),
    'Vendor-' || i,
    'Supplier',
    TRUE,
    'system'
FROM generate_series(1,50) AS i;



-- ================================================================================================
-- 3.5 PRODUCT (50 rows)
-- Randomly attaches to existing lines without RANDOM sort
-- ================================================================================================
INSERT INTO product (
    id,
    name,
    line_id,
    price,
    quantity,
    lifecycle,
    created_by
)
SELECT
    uuidv7(),
    'Product-' || i,
    l.id,
    10.0 + i,
    100 + i,
    'Active',
    'system'
FROM generate_series(1,50) i
         JOIN LATERAL (
    SELECT id
    FROM line
    OFFSET floor(random()*50)
        LIMIT 1
    ) l ON TRUE;



-- ================================================================================================
-- 3.6 PRODUCT_VENDOR_SOURCING (≈50 rows)
-- Uses constraint-safe insert
-- ================================================================================================
INSERT INTO product_vendor_sourcing (
    id,
    product_id,
    vendor_id,
    primary_vendor,
    created_by
)
SELECT DISTINCT
    uuidv7(),
    p.id,
    v.id,
    TRUE,
    'system'
FROM product p
         JOIN vendor v
              ON random() < 0.1
ON CONFLICT DO NOTHING;



-- ================================================================================================
-- 3.7 VENDOR_QUOTE (50 rows)
-- ================================================================================================
INSERT INTO vendor_quote (
    id,
    product_vendor_sourcing_id,
    quote_number,
    version_number,
    currency_code,
    unit_cost,
    moq,
    lead_time_days,
    status,
    created_by
)
SELECT
    uuidv7(),
    pvs.id,
    'Q-' || row_number() OVER (),
    1,
    'USD',
    15.00,
    500,
    30,
    'DRAFT',
    'system'
FROM product_vendor_sourcing pvs
LIMIT 50;



-- =================================================================================================
-- END OF SEED DATA
-- =================================================================================================
