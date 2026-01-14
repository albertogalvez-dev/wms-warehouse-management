-- Seed stock quantities for demo catalog (location + on-hand)
-- Uses existing locations from V2__seed_minimal_data.sql

-- Electronics (A-01-01)
INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 120
FROM products p, locations l
WHERE p.sku = 'ELEC-001' AND l.code = 'A-01-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 90
FROM products p, locations l
WHERE p.sku = 'ELEC-002' AND l.code = 'A-01-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 80
FROM products p, locations l
WHERE p.sku = 'ELEC-003' AND l.code = 'A-01-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 110
FROM products p, locations l
WHERE p.sku = 'ELEC-004' AND l.code = 'A-01-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 70
FROM products p, locations l
WHERE p.sku = 'ELEC-005' AND l.code = 'A-01-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 65
FROM products p, locations l
WHERE p.sku = 'ELEC-006' AND l.code = 'A-01-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 150
FROM products p, locations l
WHERE p.sku = 'ELEC-007' AND l.code = 'A-01-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 95
FROM products p, locations l
WHERE p.sku = 'ELEC-008' AND l.code = 'A-01-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

-- Clothing (B-01-01)
INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 220
FROM products p, locations l
WHERE p.sku = 'CLTH-001' AND l.code = 'B-01-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 180
FROM products p, locations l
WHERE p.sku = 'CLTH-002' AND l.code = 'B-01-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 160
FROM products p, locations l
WHERE p.sku = 'CLTH-003' AND l.code = 'B-01-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 140
FROM products p, locations l
WHERE p.sku = 'CLTH-004' AND l.code = 'B-01-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 120
FROM products p, locations l
WHERE p.sku = 'CLTH-005' AND l.code = 'B-01-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 110
FROM products p, locations l
WHERE p.sku = 'CLTH-006' AND l.code = 'B-01-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 90
FROM products p, locations l
WHERE p.sku = 'CLTH-007' AND l.code = 'B-01-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 100
FROM products p, locations l
WHERE p.sku = 'CLTH-008' AND l.code = 'B-01-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

-- Home (A-02-01)
INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 60
FROM products p, locations l
WHERE p.sku = 'HOME-001' AND l.code = 'A-02-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 55
FROM products p, locations l
WHERE p.sku = 'HOME-002' AND l.code = 'A-02-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 70
FROM products p, locations l
WHERE p.sku = 'HOME-003' AND l.code = 'A-02-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 80
FROM products p, locations l
WHERE p.sku = 'HOME-004' AND l.code = 'A-02-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 75
FROM products p, locations l
WHERE p.sku = 'HOME-005' AND l.code = 'A-02-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 65
FROM products p, locations l
WHERE p.sku = 'HOME-006' AND l.code = 'A-02-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 50
FROM products p, locations l
WHERE p.sku = 'HOME-007' AND l.code = 'A-02-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 45
FROM products p, locations l
WHERE p.sku = 'HOME-008' AND l.code = 'A-02-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

-- Sports (B-01-02)
INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 140
FROM products p, locations l
WHERE p.sku = 'SPRT-001' AND l.code = 'B-01-02'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 120
FROM products p, locations l
WHERE p.sku = 'SPRT-002' AND l.code = 'B-01-02'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 100
FROM products p, locations l
WHERE p.sku = 'SPRT-003' AND l.code = 'B-01-02'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 95
FROM products p, locations l
WHERE p.sku = 'SPRT-004' AND l.code = 'B-01-02'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 110
FROM products p, locations l
WHERE p.sku = 'SPRT-005' AND l.code = 'B-01-02'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 85
FROM products p, locations l
WHERE p.sku = 'SPRT-006' AND l.code = 'B-01-02'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 60
FROM products p, locations l
WHERE p.sku = 'SPRT-007' AND l.code = 'B-01-02'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 75
FROM products p, locations l
WHERE p.sku = 'SPRT-008' AND l.code = 'B-01-02'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

-- Misc (C-01-01)
INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 200
FROM products p, locations l
WHERE p.sku = 'MISC-001' AND l.code = 'C-01-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 240
FROM products p, locations l
WHERE p.sku = 'MISC-002' AND l.code = 'C-01-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 130
FROM products p, locations l
WHERE p.sku = 'MISC-003' AND l.code = 'C-01-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 100
FROM products p, locations l
WHERE p.sku = 'MISC-004' AND l.code = 'C-01-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 150
FROM products p, locations l
WHERE p.sku = 'MISC-005' AND l.code = 'C-01-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO stock (product_id, location_id, quantity)
SELECT p.id, l.id, 105
FROM products p, locations l
WHERE p.sku = 'MISC-006' AND l.code = 'C-01-01'
ON CONFLICT (product_id, location_id) DO UPDATE SET quantity = EXCLUDED.quantity;
