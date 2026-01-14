-- ============================================================
-- V13: Demo Seed Data for WMS Portfolio Demo
-- Creates sample products and demo orders
-- ============================================================

-- ============================================================
-- PRODUCTS: Electronics Category
-- ============================================================
INSERT INTO products (sku, name, barcode, active, created_at) VALUES
('ELEC-001', 'Smartphone Pro X', '5901234123457', true, NOW()),
('ELEC-002', 'Tablet Ultra 10', '5901234123464', true, NOW()),
('ELEC-003', 'Wireless Headphones Pro', '5901234123471', true, NOW()),
('ELEC-004', 'Wireless Earbuds Elite', '5901234123488', true, NOW()),
('ELEC-005', 'Smartwatch Series 5', '5901234123495', true, NOW()),
('ELEC-006', 'Laptop Sleeve 15', '5901234123501', true, NOW()),
('ELEC-007', 'USB-C Charger 65W', '5901234123518', true, NOW()),
('ELEC-008', 'Wireless Mouse Pro', '5901234123525', true, NOW());

-- ============================================================
-- PRODUCTS: Clothing Category
-- ============================================================
INSERT INTO products (sku, name, barcode, active, created_at) VALUES
('CLTH-001', 'Cotton T-Shirt Navy M', '5901234123532', true, NOW()),
('CLTH-002', 'Cotton T-Shirt Black L', '5901234123549', true, NOW()),
('CLTH-003', 'Denim Jeans Classic 32', '5901234123556', true, NOW()),
('CLTH-004', 'Denim Jeans Slim 34', '5901234123563', true, NOW()),
('CLTH-005', 'Running Shoes White 42', '5901234123570', true, NOW()),
('CLTH-006', 'Running Shoes Black 43', '5901234123587', true, NOW()),
('CLTH-007', 'Winter Jacket Blue L', '5901234123594', true, NOW()),
('CLTH-008', 'Hoodie Grey XL', '5901234123600', true, NOW());

-- ============================================================
-- PRODUCTS: Home & Kitchen Category
-- ============================================================
INSERT INTO products (sku, name, barcode, active, created_at) VALUES
('HOME-001', 'Non-Stick Frying Pan 28cm', '5901234123617', true, NOW()),
('HOME-002', 'Stainless Steel Pot 5L', '5901234123624', true, NOW()),
('HOME-003', 'Electric Kettle 1.7L', '5901234123631', true, NOW()),
('HOME-004', 'Coffee Maker Drip', '5901234123648', true, NOW()),
('HOME-005', 'Knife Set 6-Piece', '5901234123655', true, NOW()),
('HOME-006', 'Cutting Board Bamboo', '5901234123662', true, NOW()),
('HOME-007', 'Blender Pro 1000W', '5901234123679', true, NOW()),
('HOME-008', 'Toaster 2-Slice', '5901234123686', true, NOW());

-- ============================================================
-- PRODUCTS: Sports & Fitness Category
-- ============================================================
INSERT INTO products (sku, name, barcode, active, created_at) VALUES
('SPRT-001', 'Yoga Mat Premium', '5901234123693', true, NOW()),
('SPRT-002', 'Dumbbell Set 10kg', '5901234123709', true, NOW()),
('SPRT-003', 'Resistance Bands Set', '5901234123716', true, NOW()),
('SPRT-004', 'Jump Rope Pro', '5901234123723', true, NOW()),
('SPRT-005', 'Sports Water Bottle 1L', '5901234123730', true, NOW()),
('SPRT-006', 'Fitness Tracker Band', '5901234123747', true, NOW()),
('SPRT-007', 'Foam Roller 45cm', '5901234123754', true, NOW()),
('SPRT-008', 'Gym Bag Large', '5901234123761', true, NOW());

-- ============================================================
-- PRODUCTS: Additional Items
-- ============================================================
INSERT INTO products (sku, name, barcode, active, created_at) VALUES
('MISC-001', 'Power Bank 20000mAh', '5901234123778', true, NOW()),
('MISC-002', 'HDMI Cable 2m', '5901234123785', true, NOW()),
('MISC-003', 'Desk Organizer', '5901234123792', true, NOW()),
('MISC-004', 'LED Desk Lamp', '5901234123808', true, NOW()),
('MISC-005', 'Wireless Charger Pad', '5901234123815', true, NOW()),
('MISC-006', 'Backpack Laptop 17', '5901234123822', true, NOW());

-- ============================================================
-- DEMO ORDERS: Various statuses for demonstration
-- Full schema: id, external_ref, status, created_at, updated_at,
--   shipping_name, shipping_phone, shipping_email, shipping_address1,
--   shipping_address2, shipping_postal_code, shipping_city,
--   shipping_province, shipping_country, carrier
-- Status CHECK: DRAFT, RELEASED, PICKING, PICKED, CANCELLED
-- Carrier CHECK: DHL, GLS, TDN, CORREOS
-- ============================================================

INSERT INTO orders (external_ref, status, shipping_name, shipping_phone, shipping_email, 
    shipping_address1, shipping_postal_code, shipping_city, shipping_province, shipping_country, carrier, created_at, updated_at) VALUES
('ORD-2024-001', 'DRAFT', 'Maria Garcia Lopez', '+34612345001', 'maria.garcia@demo.com', 
    'Calle Mayor 15, 3A', '28013', 'Madrid', 'Madrid', 'ES', 'DHL', NOW() - INTERVAL '2 hours', NOW()),
('ORD-2024-002', 'RELEASED', 'Carlos Rodriguez Fernandez', '+34612345002', 'carlos.rf@demo.com', 
    'Av. Diagonal 450', '08006', 'Barcelona', 'Barcelona', 'ES', 'GLS', NOW() - INTERVAL '1 hour', NOW()),
('ORD-2024-003', 'PICKING', 'Ana Martinez Ruiz', '+34612345003', 'ana.martinez@demo.com', 
    'Plaza Nueva 7', '41001', 'Sevilla', 'Sevilla', 'ES', 'CORREOS', NOW() - INTERVAL '45 minutes', NOW()),
('ORD-2024-004', 'PICKED', 'Pedro Sanchez Gomez', '+34612345004', 'pedro.sg@demo.com', 
    'Gran Via 82', '48011', 'Bilbao', 'Vizcaya', 'ES', 'TDN', NOW() - INTERVAL '30 minutes', NOW()),
('ORD-2024-005', 'RELEASED', 'Laura Fernandez Torres', '+34612345005', 'laura.ft@demo.com', 
    'Calle Colon 25', '46004', 'Valencia', 'Valencia', 'ES', 'DHL', NOW() - INTERVAL '15 minutes', NOW());

-- ============================================================
-- ORDER LINES: Link orders to products
-- order_lines uses: order_id, product_id, requested_qty, allocated_qty, picked_qty
-- ============================================================

-- Order 1 lines (DRAFT)
INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 2, 0, 0 FROM orders o, products p WHERE o.external_ref = 'ORD-2024-001' AND p.sku = 'ELEC-001';
INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 1, 0, 0 FROM orders o, products p WHERE o.external_ref = 'ORD-2024-001' AND p.sku = 'ELEC-004';

-- Order 2 lines (RELEASED)
INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 1, 1, 0 FROM orders o, products p WHERE o.external_ref = 'ORD-2024-002' AND p.sku = 'CLTH-001';
INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 1, 1, 0 FROM orders o, products p WHERE o.external_ref = 'ORD-2024-002' AND p.sku = 'CLTH-003';
INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 1, 1, 0 FROM orders o, products p WHERE o.external_ref = 'ORD-2024-002' AND p.sku = 'SPRT-005';

-- Order 3 lines (PICKING)
INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 1, 1, 1 FROM orders o, products p WHERE o.external_ref = 'ORD-2024-003' AND p.sku = 'HOME-001';
INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 2, 2, 1 FROM orders o, products p WHERE o.external_ref = 'ORD-2024-003' AND p.sku = 'HOME-006';

-- Order 4 lines (PICKED)
INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 1, 1, 1 FROM orders o, products p WHERE o.external_ref = 'ORD-2024-004' AND p.sku = 'SPRT-001';
INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 1, 1, 1 FROM orders o, products p WHERE o.external_ref = 'ORD-2024-004' AND p.sku = 'SPRT-003';
INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 1, 1, 1 FROM orders o, products p WHERE o.external_ref = 'ORD-2024-004' AND p.sku = 'SPRT-005';

-- Order 5 lines (RELEASED)
INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 1, 1, 0 FROM orders o, products p WHERE o.external_ref = 'ORD-2024-005' AND p.sku = 'ELEC-002';
INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 1, 1, 0 FROM orders o, products p WHERE o.external_ref = 'ORD-2024-005' AND p.sku = 'ELEC-003';
INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 2, 2, 0 FROM orders o, products p WHERE o.external_ref = 'ORD-2024-005' AND p.sku = 'MISC-001';
