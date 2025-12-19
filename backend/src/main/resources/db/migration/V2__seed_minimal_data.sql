-- Seed minimal data for testing

-- Products (5 items)
INSERT INTO products (sku, name, barcode, active) VALUES
    ('PROD-001', 'Widget Standard', '7501234567890', TRUE),
    ('PROD-002', 'Widget Pro', '7501234567891', TRUE),
    ('PROD-003', 'Gadget Basic', '7501234567892', TRUE),
    ('PROD-004', 'Gadget Advanced', '7501234567893', TRUE),
    ('PROD-005', 'Component X', NULL, TRUE);

-- Locations (6 items)
INSERT INTO locations (code, zone, description, active) VALUES
    ('A-01-01', 'A', 'Rack A, Row 1, Shelf 1', TRUE),
    ('A-01-02', 'A', 'Rack A, Row 1, Shelf 2', TRUE),
    ('A-02-01', 'A', 'Rack A, Row 2, Shelf 1', TRUE),
    ('B-01-01', 'B', 'Rack B, Row 1, Shelf 1', TRUE),
    ('B-01-02', 'B', 'Rack B, Row 1, Shelf 2', TRUE),
    ('C-01-01', 'C', 'Receiving Area', TRUE);

-- Initial stock
INSERT INTO stock (product_id, location_id, quantity) VALUES
    (1, 1, 100),  -- Widget Standard in A-01-01
    (1, 2, 50),   -- Widget Standard in A-01-02
    (2, 1, 75),   -- Widget Pro in A-01-01
    (3, 3, 200),  -- Gadget Basic in A-02-01
    (4, 4, 30),   -- Gadget Advanced in B-01-01
    (5, 6, 500);  -- Component X in Receiving Area
