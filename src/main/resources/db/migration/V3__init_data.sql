-- =================================================================================================
-- 3. DATA SEEDING (Approx 50 rows per table)
-- =================================================================================================

-- 3.1 Lines (50 rows)
DO $$
DECLARE
    new_id UUID;
    ts TIMESTAMP;
BEGIN
    ts := LOCALTIMESTAMP;
    FOR i IN 1..50 LOOP
        new_id := uuidv7();
        INSERT INTO line (id, line_code, season_code, year, brand_id, market_id, channel_id, created_by, created_at, updated_at)
        VALUES (new_id, 'LINE-' || i, CASE WHEN i % 2 = 0 THEN 'SS' ELSE 'FW' END, 2026, uuidv7(), uuidv7(), uuidv7(), 'system', ts, ts);
    END LOOP;
END $$;

-- 3.2 Colors (50 rows)
DO $$
DECLARE
    new_id UUID;
    ts TIMESTAMP;
BEGIN
    ts := LOCALTIMESTAMP;
    FOR i IN 1..50 LOOP
        new_id := uuidv7();
        INSERT INTO color (id, name, rgb, created_by, created_at, updated_at)
        VALUES (new_id, 'Color-' || i, (i*5)%255 || ',' || (i*4)%255 || ',' || (i*3)%255, 'system', ts, ts);
    END LOOP;
END $$;

-- 3.3 Sizes (50 rows)
DO $$
DECLARE
    new_id UUID;
    ts TIMESTAMP;
BEGIN
    ts := LOCALTIMESTAMP;
    FOR i IN 1..50 LOOP
        new_id := uuidv7();
        INSERT INTO size (id, name, sizes, created_by, created_at, updated_at)
        VALUES (new_id, 'Size-Grp-' || i, 'S' || i || ', M' || i || ', L' || i, 'system', ts, ts);
    END LOOP;
END $$;

-- 3.4 Vendors (50 rows)
DO $$
DECLARE
    new_id UUID;
    ts TIMESTAMP;
BEGIN
    ts := LOCALTIMESTAMP;
    FOR i IN 1..50 LOOP
        new_id := uuidv7();
        INSERT INTO vendor (id, name, type, status, created_by, created_at, updated_at)
        VALUES (new_id, 'Vendor-' || i, 'Supplier', TRUE, 'system', ts, ts);
    END LOOP;
END $$;

-- 3.5 Products (50 rows)
DO $$
DECLARE
    new_id UUID;
    l_id UUID;
    ts TIMESTAMP;
BEGIN
    ts := LOCALTIMESTAMP;
    FOR i IN 1..50 LOOP
        new_id := uuidv7();
        SELECT id INTO l_id FROM line ORDER BY RANDOM() LIMIT 1;
        INSERT INTO product (id, name, line_id, price, quantity, lifecycle, created_by, created_at, updated_at)
        VALUES (new_id, 'Product-' || i, l_id, (10.0 + i), 100 + i, 'Active', 'system', ts, ts);
    END LOOP;
END $$;

-- 3.6 Product Vendor Sourcing (Assign 1 primary vendor per product)
DO $$
DECLARE
    new_id UUID;
    v_id UUID;
    p_rec RECORD;
    ts TIMESTAMP;
BEGIN
    ts := LOCALTIMESTAMP;
    -- Iterate through all products to ensure each gets exactly one primary vendor
    FOR p_rec IN SELECT id FROM product LOOP
        new_id := uuidv7();
        SELECT id INTO v_id FROM vendor ORDER BY RANDOM() LIMIT 1;
        
        INSERT INTO product_vendor_sourcing (id, product_id, vendor_id, primary_vendor, created_by, created_at, updated_at)
        VALUES (new_id, p_rec.id, v_id, TRUE, 'system', ts, ts);
    END LOOP;
END $$;

-- 3.7 Vendor Quote (50 rows)
DO $$
DECLARE
    new_id UUID;
    pvs_id UUID;
    ts TIMESTAMP;
BEGIN
    ts := LOCALTIMESTAMP;
    FOR i IN 1..50 LOOP
        new_id := uuidv7();
        SELECT id INTO pvs_id FROM product_vendor_sourcing ORDER BY RANDOM() LIMIT 1;
        
        IF pvs_id IS NOT NULL THEN
             INSERT INTO vendor_quote (id, product_vendor_sourcing_id, quote_number, version_number, currency_code, unit_cost, moq, lead_time_days, status, created_by, created_at, updated_at)
             VALUES (new_id, pvs_id, 'Q-' || i, 1, 'USD', 15.00, 500, 30, 'DRAFT', 'system', ts, ts);
        END IF;
    END LOOP;
END $$;
