-- ============================================================
-- V13: Demo Seed Data for WMS Portfolio Demo
-- Creates products with images and demo orders
-- ============================================================

-- ============================================================
-- PRODUCTS: Electronics Category
-- ============================================================
INSERT INTO products (sku, name, barcode, weight_grams, width_cm, depth_cm, height_cm, image_url, created_at) VALUES
('ELEC-001', 'Smartphone Pro X', '5901234123457', 185, 7.5, 1.0, 15.5, 'https://picsum.photos/seed/phone1/400/400', NOW()),
('ELEC-002', 'Tablet Ultra 10"', '5901234123464', 450, 25.0, 0.8, 17.0, 'https://picsum.photos/seed/tablet1/400/400', NOW()),
('ELEC-003', 'Wireless Headphones Pro', '5901234123471', 280, 18.0, 8.0, 20.0, 'https://picsum.photos/seed/headphones1/400/400', NOW()),
('ELEC-004', 'Wireless Earbuds Elite', '5901234123488', 55, 6.0, 4.5, 3.0, 'https://picsum.photos/seed/earbuds1/400/400', NOW()),
('ELEC-005', 'Smartwatch Series 5', '5901234123495', 45, 4.5, 1.2, 4.5, 'https://picsum.photos/seed/watch1/400/400', NOW()),
('ELEC-006', 'Laptop Sleeve 15"', '5901234123501', 200, 40.0, 2.0, 28.0, 'https://picsum.photos/seed/sleeve1/400/400', NOW()),
('ELEC-007', 'USB-C Charger 65W', '5901234123518', 150, 6.5, 3.0, 6.5, 'https://picsum.photos/seed/charger1/400/400', NOW()),
('ELEC-008', 'Wireless Mouse Pro', '5901234123525', 95, 12.0, 6.0, 4.0, 'https://picsum.photos/seed/mouse1/400/400', NOW());

-- ============================================================
-- PRODUCTS: Clothing Category
-- ============================================================
INSERT INTO products (sku, name, barcode, weight_grams, width_cm, depth_cm, height_cm, image_url, created_at) VALUES
('CLTH-001', 'Cotton T-Shirt Navy M', '5901234123532', 180, 35.0, 2.0, 45.0, 'https://picsum.photos/seed/tshirt1/400/400', NOW()),
('CLTH-002', 'Cotton T-Shirt Black L', '5901234123549', 190, 38.0, 2.0, 48.0, 'https://picsum.photos/seed/tshirt2/400/400', NOW()),
('CLTH-003', 'Denim Jeans Classic 32', '5901234123556', 550, 40.0, 3.0, 55.0, 'https://picsum.photos/seed/jeans1/400/400', NOW()),
('CLTH-004', 'Denim Jeans Slim 34', '5901234123563', 530, 42.0, 3.0, 55.0, 'https://picsum.photos/seed/jeans2/400/400', NOW()),
('CLTH-005', 'Running Shoes White 42', '5901234123570', 320, 32.0, 12.0, 12.0, 'https://picsum.photos/seed/shoes1/400/400', NOW()),
('CLTH-006', 'Running Shoes Black 43', '5901234123587', 330, 33.0, 12.0, 12.0, 'https://picsum.photos/seed/shoes2/400/400', NOW()),
('CLTH-007', 'Winter Jacket Blue L', '5901234123594', 850, 55.0, 5.0, 70.0, 'https://picsum.photos/seed/jacket1/400/400', NOW()),
('CLTH-008', 'Hoodie Grey XL', '5901234123600', 450, 50.0, 4.0, 65.0, 'https://picsum.photos/seed/hoodie1/400/400', NOW());

-- ============================================================
-- PRODUCTS: Home & Kitchen Category
-- ============================================================
INSERT INTO products (sku, name, barcode, weight_grams, width_cm, depth_cm, height_cm, image_url, created_at) VALUES
('HOME-001', 'Non-Stick Frying Pan 28cm', '5901234123617', 980, 50.0, 30.0, 8.0, 'https://picsum.photos/seed/pan1/400/400', NOW()),
('HOME-002', 'Stainless Steel Pot 5L', '5901234123624', 1200, 30.0, 30.0, 20.0, 'https://picsum.photos/seed/pot1/400/400', NOW()),
('HOME-003', 'Electric Kettle 1.7L', '5901234123631', 850, 22.0, 15.0, 25.0, 'https://picsum.photos/seed/kettle1/400/400', NOW()),
('HOME-004', 'Coffee Maker Drip', '5901234123648', 1500, 20.0, 25.0, 35.0, 'https://picsum.photos/seed/coffee1/400/400', NOW()),
('HOME-005', 'Knife Set 6-Piece', '5901234123655', 800, 35.0, 15.0, 5.0, 'https://picsum.photos/seed/knife1/400/400', NOW()),
('HOME-006', 'Cutting Board Bamboo', '5901234123662', 650, 40.0, 25.0, 2.0, 'https://picsum.photos/seed/board1/400/400', NOW()),
('HOME-007', 'Blender Pro 1000W', '5901234123679', 2200, 20.0, 20.0, 45.0, 'https://picsum.photos/seed/blender1/400/400', NOW()),
('HOME-008', 'Toaster 2-Slice', '5901234123686', 1100, 30.0, 18.0, 20.0, 'https://picsum.photos/seed/toaster1/400/400', NOW());

-- ============================================================
-- PRODUCTS: Sports & Fitness Category
-- ============================================================
INSERT INTO products (sku, name, barcode, weight_grams, width_cm, depth_cm, height_cm, image_url, created_at) VALUES
('SPRT-001', 'Yoga Mat Premium', '5901234123693', 1200, 180.0, 1.0, 60.0, 'https://picsum.photos/seed/yoga1/400/400', NOW()),
('SPRT-002', 'Dumbbell Set 10kg', '5901234123709', 10000, 40.0, 20.0, 15.0, 'https://picsum.photos/seed/dumbbell1/400/400', NOW()),
('SPRT-003', 'Resistance Bands Set', '5901234123716', 250, 25.0, 15.0, 5.0, 'https://picsum.photos/seed/bands1/400/400', NOW()),
('SPRT-004', 'Jump Rope Pro', '5901234123723', 180, 20.0, 10.0, 5.0, 'https://picsum.photos/seed/rope1/400/400', NOW()),
('SPRT-005', 'Sports Water Bottle 1L', '5901234123730', 150, 8.0, 8.0, 25.0, 'https://picsum.photos/seed/bottle1/400/400', NOW()),
('SPRT-006', 'Fitness Tracker Band', '5901234123747', 25, 3.0, 1.5, 25.0, 'https://picsum.photos/seed/tracker1/400/400', NOW()),
('SPRT-007', 'Foam Roller 45cm', '5901234123754', 400, 45.0, 15.0, 15.0, 'https://picsum.photos/seed/roller1/400/400', NOW()),
('SPRT-008', 'Gym Bag Large', '5901234123761', 600, 55.0, 25.0, 30.0, 'https://picsum.photos/seed/gymbag1/400/400', NOW());

-- ============================================================
-- PRODUCTS: Additional Items
-- ============================================================
INSERT INTO products (sku, name, barcode, weight_grams, width_cm, depth_cm, height_cm, image_url, created_at) VALUES
('MISC-001', 'Power Bank 20000mAh', '5901234123778', 350, 15.0, 7.0, 2.5, 'https://picsum.photos/seed/powerbank1/400/400', NOW()),
('MISC-002', 'HDMI Cable 2m', '5901234123785', 100, 25.0, 10.0, 3.0, 'https://picsum.photos/seed/cable1/400/400', NOW()),
('MISC-003', 'Desk Organizer', '5901234123792', 400, 30.0, 15.0, 10.0, 'https://picsum.photos/seed/organizer1/400/400', NOW()),
('MISC-004', 'LED Desk Lamp', '5901234123808', 650, 15.0, 15.0, 45.0, 'https://picsum.photos/seed/lamp1/400/400', NOW()),
('MISC-005', 'Wireless Charger Pad', '5901234123815', 120, 10.0, 10.0, 1.0, 'https://picsum.photos/seed/charger2/400/400', NOW()),
('MISC-006', 'Backpack Laptop 17"', '5901234123822', 800, 35.0, 20.0, 50.0, 'https://picsum.photos/seed/backpack1/400/400', NOW());

-- ============================================================
-- DEMO ORDERS: Various statuses for demonstration
-- ============================================================

-- Order 1: DRAFT status
INSERT INTO orders (external_ref, status, carrier_code, created_at, updated_at) VALUES
('ORD-2024-001', 'DRAFT', 'SEUR', NOW() - INTERVAL '2 hours', NOW());

-- Order 2: RELEASED status (ready for picking)
INSERT INTO orders (external_ref, status, carrier_code, created_at, updated_at) VALUES
('ORD-2024-002', 'RELEASED', 'MRW', NOW() - INTERVAL '1 hour', NOW());

-- Order 3: PICKING status (in progress)
INSERT INTO orders (external_ref, status, carrier_code, created_at, updated_at) VALUES
('ORD-2024-003', 'PICKING', 'CORREOS', NOW() - INTERVAL '45 minutes', NOW());

-- Order 4: PACKED status (completed packing)
INSERT INTO orders (external_ref, status, carrier_code, created_at, updated_at) VALUES
('ORD-2024-004', 'PACKED', 'DHL', NOW() - INTERVAL '30 minutes', NOW());

-- Order 5: SHIPPED status (delivered to carrier)
INSERT INTO orders (external_ref, status, carrier_code, created_at, updated_at) VALUES
('ORD-2024-005', 'SHIPPED', 'UPS', NOW() - INTERVAL '15 minutes', NOW());

-- ============================================================
-- ORDER LINES: Link orders to products
-- ============================================================

-- Order 1 lines (DRAFT - Electronics order)
INSERT INTO order_lines (order_id, product_id, qty_ordered, qty_picked, qty_packed)
SELECT o.id, p.id, 2, 0, 0 FROM orders o, products p WHERE o.external_ref = 'ORD-2024-001' AND p.sku = 'ELEC-001';
INSERT INTO order_lines (order_id, product_id, qty_ordered, qty_picked, qty_packed)
SELECT o.id, p.id, 1, 0, 0 FROM orders o, products p WHERE o.external_ref = 'ORD-2024-001' AND p.sku = 'ELEC-004';

-- Order 2 lines (RELEASED - Mixed order)
INSERT INTO order_lines (order_id, product_id, qty_ordered, qty_picked, qty_packed)
SELECT o.id, p.id, 1, 0, 0 FROM orders o, products p WHERE o.external_ref = 'ORD-2024-002' AND p.sku = 'CLTH-001';
INSERT INTO order_lines (order_id, product_id, qty_ordered, qty_picked, qty_packed)
SELECT o.id, p.id, 1, 0, 0 FROM orders o, products p WHERE o.external_ref = 'ORD-2024-002' AND p.sku = 'CLTH-003';
INSERT INTO order_lines (order_id, product_id, qty_ordered, qty_picked, qty_packed)
SELECT o.id, p.id, 1, 0, 0 FROM orders o, products p WHERE o.external_ref = 'ORD-2024-002' AND p.sku = 'SPRT-005';

-- Order 3 lines (PICKING - Home order)
INSERT INTO order_lines (order_id, product_id, qty_ordered, qty_picked, qty_packed)
SELECT o.id, p.id, 1, 1, 0 FROM orders o, products p WHERE o.external_ref = 'ORD-2024-003' AND p.sku = 'HOME-001';
INSERT INTO order_lines (order_id, product_id, qty_ordered, qty_picked, qty_packed)
SELECT o.id, p.id, 2, 1, 0 FROM orders o, products p WHERE o.external_ref = 'ORD-2024-003' AND p.sku = 'HOME-006';

-- Order 4 lines (PACKED - Sports order)
INSERT INTO order_lines (order_id, product_id, qty_ordered, qty_picked, qty_packed)
SELECT o.id, p.id, 1, 1, 1 FROM orders o, products p WHERE o.external_ref = 'ORD-2024-004' AND p.sku = 'SPRT-001';
INSERT INTO order_lines (order_id, product_id, qty_ordered, qty_picked, qty_packed)
SELECT o.id, p.id, 1, 1, 1 FROM orders o, products p WHERE o.external_ref = 'ORD-2024-004' AND p.sku = 'SPRT-003';
INSERT INTO order_lines (order_id, product_id, qty_ordered, qty_picked, qty_packed)
SELECT o.id, p.id, 1, 1, 1 FROM orders o, products p WHERE o.external_ref = 'ORD-2024-004' AND p.sku = 'SPRT-005';

-- Order 5 lines (SHIPPED - Electronics order)
INSERT INTO order_lines (order_id, product_id, qty_ordered, qty_picked, qty_packed)
SELECT o.id, p.id, 1, 1, 1 FROM orders o, products p WHERE o.external_ref = 'ORD-2024-005' AND p.sku = 'ELEC-002';
INSERT INTO order_lines (order_id, product_id, qty_ordered, qty_picked, qty_packed)
SELECT o.id, p.id, 1, 1, 1 FROM orders o, products p WHERE o.external_ref = 'ORD-2024-005' AND p.sku = 'ELEC-003';
INSERT INTO order_lines (order_id, product_id, qty_ordered, qty_picked, qty_packed)
SELECT o.id, p.id, 2, 2, 2 FROM orders o, products p WHERE o.external_ref = 'ORD-2024-005' AND p.sku = 'MISC-001';
