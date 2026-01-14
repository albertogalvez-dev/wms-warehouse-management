-- Demo data refresh for 2025 portfolio flow

-- Normalize existing timestamps to 2025 for portfolio consistency
UPDATE products
SET created_at = TIMESTAMP '2025-01-08 09:00:00',
    updated_at = TIMESTAMP '2025-03-05 12:00:00'
WHERE created_at < TIMESTAMP '2025-01-01 00:00:00'
   OR created_at >= TIMESTAMP '2026-01-01 00:00:00'
   OR updated_at < TIMESTAMP '2025-01-01 00:00:00'
   OR updated_at >= TIMESTAMP '2026-01-01 00:00:00';

UPDATE orders
SET created_at = TIMESTAMP '2025-03-01 09:00:00',
    updated_at = TIMESTAMP '2025-03-01 09:05:00'
WHERE created_at < TIMESTAMP '2025-01-01 00:00:00'
   OR created_at >= TIMESTAMP '2026-01-01 00:00:00'
   OR updated_at < TIMESTAMP '2025-01-01 00:00:00'
   OR updated_at >= TIMESTAMP '2026-01-01 00:00:00';

UPDATE orders
SET external_ref = REPLACE(external_ref, 'ORD-2024', 'ORD-2025')
WHERE external_ref LIKE 'ORD-2024%';

-- Clean packing station names (ASCII) and mark active
UPDATE packing_stations
SET name = 'Packing Station 1',
    active = true,
    updated_at = TIMESTAMP '2025-01-05 09:00:00'
WHERE code = 'PACK-1';

UPDATE packing_stations
SET name = 'Packing Station 2',
    active = true,
    updated_at = TIMESTAMP '2025-01-05 09:00:00'
WHERE code = 'PACK-2';

UPDATE packing_stations
SET name = 'Packing Station 3',
    active = true,
    updated_at = TIMESTAMP '2025-01-05 09:00:00'
WHERE code = 'PACK-3';

-- Normalize demo users (admin + manager + 2 operators)
UPDATE users
SET username = 'operator1',
    role = 'PICKER',
    active = true,
    created_at = TIMESTAMP '2025-01-10 09:00:00',
    updated_at = TIMESTAMP '2025-02-01 10:00:00'
WHERE username = 'picker';

UPDATE users
SET username = 'operator2',
    role = 'PACKER',
    active = true,
    created_at = TIMESTAMP '2025-01-10 09:05:00',
    updated_at = TIMESTAMP '2025-02-01 10:05:00'
WHERE username = 'packer';

UPDATE users
SET created_at = TIMESTAMP '2025-01-10 08:30:00',
    updated_at = TIMESTAMP '2025-02-01 09:00:00'
WHERE username = 'admin';

INSERT INTO users (username, password_hash, role, active, created_at, updated_at)
VALUES ('manager', '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQkLgpXOXTaLQqGBl.aIcdrITblOfC', 'MANAGER', true,
        TIMESTAMP '2025-01-10 08:45:00', TIMESTAMP '2025-02-01 09:10:00')
ON CONFLICT (username) DO NOTHING;

-- Demo orders (2025)
INSERT INTO orders (
    external_ref, status, shipping_name, shipping_phone, shipping_email,
    shipping_address1, shipping_address2, shipping_postal_code, shipping_city,
    shipping_province, shipping_country, carrier, created_at, updated_at
) VALUES
('ORD-2025-1001', 'DRAFT', 'Laura Santos', '+34620000001', 'laura.santos@demo.com',
 'Calle Mayor 15', NULL, '28013', 'Madrid', 'Madrid', 'ES', 'DHL',
 TIMESTAMP '2025-04-10 09:10:00', TIMESTAMP '2025-04-10 09:10:00'),
('ORD-2025-1002', 'RELEASED', 'Diego Martin', '+34620000002', 'diego.martin@demo.com',
 'Av Diagonal 450', 'Piso 2', '08006', 'Barcelona', 'Barcelona', 'ES', 'GLS',
 TIMESTAMP '2025-04-10 09:25:00', TIMESTAMP '2025-04-10 09:30:00'),
('ORD-2025-1003', 'RELEASED', 'Elena Ruiz', '+34620000003', 'elena.ruiz@demo.com',
 'Calle Colon 25', NULL, '46004', 'Valencia', 'Valencia', 'ES', 'TDN',
 TIMESTAMP '2025-04-10 09:40:00', TIMESTAMP '2025-04-10 09:45:00'),
('ORD-2025-1004', 'PICKING', 'Marta Lopez', '+34620000004', 'marta.lopez@demo.com',
 'Plaza Nueva 7', NULL, '41001', 'Sevilla', 'Sevilla', 'ES', 'CORREOS',
 TIMESTAMP '2025-04-10 10:05:00', TIMESTAMP '2025-04-10 10:20:00'),
('ORD-2025-1005', 'PICKING', 'Javier Gomez', '+34620000005', 'javier.gomez@demo.com',
 'Gran Via 82', NULL, '48011', 'Bilbao', 'Vizcaya', 'ES', 'DHL',
 TIMESTAMP '2025-04-10 10:20:00', TIMESTAMP '2025-04-10 10:35:00'),
('ORD-2025-1006', 'PACKING', 'Sonia Torres', '+34620000006', 'sonia.torres@demo.com',
 'Calle Alcala 120', NULL, '28009', 'Madrid', 'Madrid', 'ES', 'DHL',
 TIMESTAMP '2025-04-10 10:40:00', TIMESTAMP '2025-04-10 10:55:00'),
('ORD-2025-1007', 'PACKED', 'Carlos Ortega', '+34620000007', 'carlos.ortega@demo.com',
 'Ronda Sur 14', 'Bloque B', '30010', 'Murcia', 'Murcia', 'ES', 'GLS',
 TIMESTAMP '2025-04-10 11:05:00', TIMESTAMP '2025-04-10 11:20:00'),
('ORD-2025-1008', 'PACKED', 'Lucia Perez', '+34620000008', 'lucia.perez@demo.com',
 'Calle San Vicente 22', NULL, '50001', 'Zaragoza', 'Zaragoza', 'ES', 'TDN',
 TIMESTAMP '2025-04-10 11:20:00', TIMESTAMP '2025-04-10 11:35:00'),
('ORD-2025-1009', 'PACKED', 'Hugo Navarro', '+34620000009', 'hugo.navarro@demo.com',
 'Av Libertad 9', NULL, '33004', 'Oviedo', 'Asturias', 'ES', 'CORREOS',
 TIMESTAMP '2025-04-10 11:35:00', TIMESTAMP '2025-04-10 11:50:00'),
('ORD-2025-1010', 'SHIPPED', 'Nuria Castillo', '+34620000010', 'nuria.castillo@demo.com',
 'Calle Aragon 102', NULL, '08013', 'Barcelona', 'Barcelona', 'ES', 'DHL',
 TIMESTAMP '2025-04-10 12:05:00', TIMESTAMP '2025-04-10 12:35:00'),
('ORD-2025-1011', 'SHIPPED', 'Pablo Vega', '+34620000011', 'pablo.vega@demo.com',
 'Paseo Zorrilla 15', NULL, '47007', 'Valladolid', 'Valladolid', 'ES', 'GLS',
 TIMESTAMP '2025-04-10 12:25:00', TIMESTAMP '2025-04-10 12:50:00');

-- Order lines (2 per order)
INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 1, 0, 0
FROM orders o, products p
WHERE o.external_ref = 'ORD-2025-1001' AND p.sku = 'ELEC-001';
INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 2, 0, 0
FROM orders o, products p
WHERE o.external_ref = 'ORD-2025-1001' AND p.sku = 'CLTH-001';

INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 1, 1, 0
FROM orders o, products p
WHERE o.external_ref = 'ORD-2025-1002' AND p.sku = 'ELEC-003';
INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 1, 1, 0
FROM orders o, products p
WHERE o.external_ref = 'ORD-2025-1002' AND p.sku = 'HOME-001';

INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 2, 2, 0
FROM orders o, products p
WHERE o.external_ref = 'ORD-2025-1003' AND p.sku = 'ELEC-004';
INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 1, 1, 0
FROM orders o, products p
WHERE o.external_ref = 'ORD-2025-1003' AND p.sku = 'SPRT-001';

INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 1, 1, 1
FROM orders o, products p
WHERE o.external_ref = 'ORD-2025-1004' AND p.sku = 'HOME-003';
INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 1, 1, 0
FROM orders o, products p
WHERE o.external_ref = 'ORD-2025-1004' AND p.sku = 'MISC-001';

INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 1, 1, 0
FROM orders o, products p
WHERE o.external_ref = 'ORD-2025-1005' AND p.sku = 'CLTH-003';
INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 1, 1, 0
FROM orders o, products p
WHERE o.external_ref = 'ORD-2025-1005' AND p.sku = 'ELEC-002';

INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 1, 1, 1
FROM orders o, products p
WHERE o.external_ref = 'ORD-2025-1006' AND p.sku = 'ELEC-005';
INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 2, 2, 2
FROM orders o, products p
WHERE o.external_ref = 'ORD-2025-1006' AND p.sku = 'HOME-002';

INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 1, 1, 1
FROM orders o, products p
WHERE o.external_ref = 'ORD-2025-1007' AND p.sku = 'CLTH-004';
INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 1, 1, 1
FROM orders o, products p
WHERE o.external_ref = 'ORD-2025-1007' AND p.sku = 'SPRT-002';

INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 1, 1, 1
FROM orders o, products p
WHERE o.external_ref = 'ORD-2025-1008' AND p.sku = 'ELEC-006';
INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 1, 1, 1
FROM orders o, products p
WHERE o.external_ref = 'ORD-2025-1008' AND p.sku = 'HOME-004';

INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 2, 2, 2
FROM orders o, products p
WHERE o.external_ref = 'ORD-2025-1009' AND p.sku = 'SPRT-003';
INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 1, 1, 1
FROM orders o, products p
WHERE o.external_ref = 'ORD-2025-1009' AND p.sku = 'MISC-002';

INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 1, 1, 1
FROM orders o, products p
WHERE o.external_ref = 'ORD-2025-1010' AND p.sku = 'ELEC-007';
INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 2, 2, 2
FROM orders o, products p
WHERE o.external_ref = 'ORD-2025-1010' AND p.sku = 'CLTH-002';

INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 1, 1, 1
FROM orders o, products p
WHERE o.external_ref = 'ORD-2025-1011' AND p.sku = 'ELEC-008';
INSERT INTO order_lines (order_id, product_id, requested_qty, allocated_qty, picked_qty)
SELECT o.id, p.id, 1, 1, 1
FROM orders o, products p
WHERE o.external_ref = 'ORD-2025-1011' AND p.sku = 'HOME-001';

-- Pick tasks
INSERT INTO pick_tasks (order_id, status, created_at, updated_at)
SELECT id, 'OPEN', TIMESTAMP '2025-04-10 09:45:00', TIMESTAMP '2025-04-10 09:45:00'
FROM orders
WHERE external_ref IN ('ORD-2025-1002', 'ORD-2025-1003');

INSERT INTO pick_tasks (order_id, status, created_at, updated_at)
SELECT id, 'IN_PROGRESS', TIMESTAMP '2025-04-10 10:15:00', TIMESTAMP '2025-04-10 10:25:00'
FROM orders
WHERE external_ref IN ('ORD-2025-1004', 'ORD-2025-1005');

INSERT INTO pick_tasks (order_id, status, created_at, updated_at)
SELECT id, 'DONE', TIMESTAMP '2025-04-10 10:45:00', TIMESTAMP '2025-04-10 11:45:00'
FROM orders
WHERE external_ref IN ('ORD-2025-1006', 'ORD-2025-1007', 'ORD-2025-1008', 'ORD-2025-1009', 'ORD-2025-1010', 'ORD-2025-1011');

-- Pick lines (locations by product category)
INSERT INTO pick_lines (pick_task_id, order_line_id, product_id, location_id, assigned_qty, picked_qty, status)
SELECT pt.id, ol.id, ol.product_id, l.id, ol.allocated_qty, ol.picked_qty,
       CASE WHEN ol.picked_qty >= ol.allocated_qty THEN 'DONE' ELSE 'OPEN' END
FROM pick_tasks pt
JOIN orders o ON pt.order_id = o.id
JOIN order_lines ol ON ol.order_id = o.id
JOIN products p ON p.id = ol.product_id
JOIN locations l ON l.code = 'A-01-01'
WHERE o.external_ref = 'ORD-2025-1002' AND p.sku = 'ELEC-003';

INSERT INTO pick_lines (pick_task_id, order_line_id, product_id, location_id, assigned_qty, picked_qty, status)
SELECT pt.id, ol.id, ol.product_id, l.id, ol.allocated_qty, ol.picked_qty,
       CASE WHEN ol.picked_qty >= ol.allocated_qty THEN 'DONE' ELSE 'OPEN' END
FROM pick_tasks pt
JOIN orders o ON pt.order_id = o.id
JOIN order_lines ol ON ol.order_id = o.id
JOIN products p ON p.id = ol.product_id
JOIN locations l ON l.code = 'A-02-01'
WHERE o.external_ref = 'ORD-2025-1002' AND p.sku = 'HOME-001';

INSERT INTO pick_lines (pick_task_id, order_line_id, product_id, location_id, assigned_qty, picked_qty, status)
SELECT pt.id, ol.id, ol.product_id, l.id, ol.allocated_qty, ol.picked_qty,
       CASE WHEN ol.picked_qty >= ol.allocated_qty THEN 'DONE' ELSE 'OPEN' END
FROM pick_tasks pt
JOIN orders o ON pt.order_id = o.id
JOIN order_lines ol ON ol.order_id = o.id
JOIN products p ON p.id = ol.product_id
JOIN locations l ON l.code = 'A-01-01'
WHERE o.external_ref = 'ORD-2025-1003' AND p.sku = 'ELEC-004';

INSERT INTO pick_lines (pick_task_id, order_line_id, product_id, location_id, assigned_qty, picked_qty, status)
SELECT pt.id, ol.id, ol.product_id, l.id, ol.allocated_qty, ol.picked_qty,
       CASE WHEN ol.picked_qty >= ol.allocated_qty THEN 'DONE' ELSE 'OPEN' END
FROM pick_tasks pt
JOIN orders o ON pt.order_id = o.id
JOIN order_lines ol ON ol.order_id = o.id
JOIN products p ON p.id = ol.product_id
JOIN locations l ON l.code = 'B-01-02'
WHERE o.external_ref = 'ORD-2025-1003' AND p.sku = 'SPRT-001';

INSERT INTO pick_lines (pick_task_id, order_line_id, product_id, location_id, assigned_qty, picked_qty, status)
SELECT pt.id, ol.id, ol.product_id, l.id, ol.allocated_qty, ol.picked_qty,
       CASE WHEN ol.picked_qty >= ol.allocated_qty THEN 'DONE' ELSE 'OPEN' END
FROM pick_tasks pt
JOIN orders o ON pt.order_id = o.id
JOIN order_lines ol ON ol.order_id = o.id
JOIN products p ON p.id = ol.product_id
JOIN locations l ON l.code = 'A-02-01'
WHERE o.external_ref = 'ORD-2025-1004' AND p.sku = 'HOME-003';

INSERT INTO pick_lines (pick_task_id, order_line_id, product_id, location_id, assigned_qty, picked_qty, status)
SELECT pt.id, ol.id, ol.product_id, l.id, ol.allocated_qty, ol.picked_qty,
       CASE WHEN ol.picked_qty >= ol.allocated_qty THEN 'DONE' ELSE 'OPEN' END
FROM pick_tasks pt
JOIN orders o ON pt.order_id = o.id
JOIN order_lines ol ON ol.order_id = o.id
JOIN products p ON p.id = ol.product_id
JOIN locations l ON l.code = 'C-01-01'
WHERE o.external_ref = 'ORD-2025-1004' AND p.sku = 'MISC-001';

INSERT INTO pick_lines (pick_task_id, order_line_id, product_id, location_id, assigned_qty, picked_qty, status)
SELECT pt.id, ol.id, ol.product_id, l.id, ol.allocated_qty, ol.picked_qty,
       CASE WHEN ol.picked_qty >= ol.allocated_qty THEN 'DONE' ELSE 'OPEN' END
FROM pick_tasks pt
JOIN orders o ON pt.order_id = o.id
JOIN order_lines ol ON ol.order_id = o.id
JOIN products p ON p.id = ol.product_id
JOIN locations l ON l.code = 'B-01-01'
WHERE o.external_ref = 'ORD-2025-1005' AND p.sku = 'CLTH-003';

INSERT INTO pick_lines (pick_task_id, order_line_id, product_id, location_id, assigned_qty, picked_qty, status)
SELECT pt.id, ol.id, ol.product_id, l.id, ol.allocated_qty, ol.picked_qty,
       CASE WHEN ol.picked_qty >= ol.allocated_qty THEN 'DONE' ELSE 'OPEN' END
FROM pick_tasks pt
JOIN orders o ON pt.order_id = o.id
JOIN order_lines ol ON ol.order_id = o.id
JOIN products p ON p.id = ol.product_id
JOIN locations l ON l.code = 'A-01-01'
WHERE o.external_ref = 'ORD-2025-1005' AND p.sku = 'ELEC-002';

INSERT INTO pick_lines (pick_task_id, order_line_id, product_id, location_id, assigned_qty, picked_qty, status)
SELECT pt.id, ol.id, ol.product_id, l.id, ol.allocated_qty, ol.picked_qty,
       CASE WHEN ol.picked_qty >= ol.allocated_qty THEN 'DONE' ELSE 'OPEN' END
FROM pick_tasks pt
JOIN orders o ON pt.order_id = o.id
JOIN order_lines ol ON ol.order_id = o.id
JOIN products p ON p.id = ol.product_id
JOIN locations l ON l.code = 'A-01-01'
WHERE o.external_ref = 'ORD-2025-1006' AND p.sku = 'ELEC-005';

INSERT INTO pick_lines (pick_task_id, order_line_id, product_id, location_id, assigned_qty, picked_qty, status)
SELECT pt.id, ol.id, ol.product_id, l.id, ol.allocated_qty, ol.picked_qty,
       CASE WHEN ol.picked_qty >= ol.allocated_qty THEN 'DONE' ELSE 'OPEN' END
FROM pick_tasks pt
JOIN orders o ON pt.order_id = o.id
JOIN order_lines ol ON ol.order_id = o.id
JOIN products p ON p.id = ol.product_id
JOIN locations l ON l.code = 'A-02-01'
WHERE o.external_ref = 'ORD-2025-1006' AND p.sku = 'HOME-002';

INSERT INTO pick_lines (pick_task_id, order_line_id, product_id, location_id, assigned_qty, picked_qty, status)
SELECT pt.id, ol.id, ol.product_id, l.id, ol.allocated_qty, ol.picked_qty,
       CASE WHEN ol.picked_qty >= ol.allocated_qty THEN 'DONE' ELSE 'OPEN' END
FROM pick_tasks pt
JOIN orders o ON pt.order_id = o.id
JOIN order_lines ol ON ol.order_id = o.id
JOIN products p ON p.id = ol.product_id
JOIN locations l ON l.code = 'B-01-01'
WHERE o.external_ref = 'ORD-2025-1007' AND p.sku = 'CLTH-004';

INSERT INTO pick_lines (pick_task_id, order_line_id, product_id, location_id, assigned_qty, picked_qty, status)
SELECT pt.id, ol.id, ol.product_id, l.id, ol.allocated_qty, ol.picked_qty,
       CASE WHEN ol.picked_qty >= ol.allocated_qty THEN 'DONE' ELSE 'OPEN' END
FROM pick_tasks pt
JOIN orders o ON pt.order_id = o.id
JOIN order_lines ol ON ol.order_id = o.id
JOIN products p ON p.id = ol.product_id
JOIN locations l ON l.code = 'B-01-02'
WHERE o.external_ref = 'ORD-2025-1007' AND p.sku = 'SPRT-002';

INSERT INTO pick_lines (pick_task_id, order_line_id, product_id, location_id, assigned_qty, picked_qty, status)
SELECT pt.id, ol.id, ol.product_id, l.id, ol.allocated_qty, ol.picked_qty,
       CASE WHEN ol.picked_qty >= ol.allocated_qty THEN 'DONE' ELSE 'OPEN' END
FROM pick_tasks pt
JOIN orders o ON pt.order_id = o.id
JOIN order_lines ol ON ol.order_id = o.id
JOIN products p ON p.id = ol.product_id
JOIN locations l ON l.code = 'A-01-01'
WHERE o.external_ref = 'ORD-2025-1008' AND p.sku = 'ELEC-006';

INSERT INTO pick_lines (pick_task_id, order_line_id, product_id, location_id, assigned_qty, picked_qty, status)
SELECT pt.id, ol.id, ol.product_id, l.id, ol.allocated_qty, ol.picked_qty,
       CASE WHEN ol.picked_qty >= ol.allocated_qty THEN 'DONE' ELSE 'OPEN' END
FROM pick_tasks pt
JOIN orders o ON pt.order_id = o.id
JOIN order_lines ol ON ol.order_id = o.id
JOIN products p ON p.id = ol.product_id
JOIN locations l ON l.code = 'A-02-01'
WHERE o.external_ref = 'ORD-2025-1008' AND p.sku = 'HOME-004';

INSERT INTO pick_lines (pick_task_id, order_line_id, product_id, location_id, assigned_qty, picked_qty, status)
SELECT pt.id, ol.id, ol.product_id, l.id, ol.allocated_qty, ol.picked_qty,
       CASE WHEN ol.picked_qty >= ol.allocated_qty THEN 'DONE' ELSE 'OPEN' END
FROM pick_tasks pt
JOIN orders o ON pt.order_id = o.id
JOIN order_lines ol ON ol.order_id = o.id
JOIN products p ON p.id = ol.product_id
JOIN locations l ON l.code = 'B-01-02'
WHERE o.external_ref = 'ORD-2025-1009' AND p.sku = 'SPRT-003';

INSERT INTO pick_lines (pick_task_id, order_line_id, product_id, location_id, assigned_qty, picked_qty, status)
SELECT pt.id, ol.id, ol.product_id, l.id, ol.allocated_qty, ol.picked_qty,
       CASE WHEN ol.picked_qty >= ol.allocated_qty THEN 'DONE' ELSE 'OPEN' END
FROM pick_tasks pt
JOIN orders o ON pt.order_id = o.id
JOIN order_lines ol ON ol.order_id = o.id
JOIN products p ON p.id = ol.product_id
JOIN locations l ON l.code = 'C-01-01'
WHERE o.external_ref = 'ORD-2025-1009' AND p.sku = 'MISC-002';

INSERT INTO pick_lines (pick_task_id, order_line_id, product_id, location_id, assigned_qty, picked_qty, status)
SELECT pt.id, ol.id, ol.product_id, l.id, ol.allocated_qty, ol.picked_qty,
       CASE WHEN ol.picked_qty >= ol.allocated_qty THEN 'DONE' ELSE 'OPEN' END
FROM pick_tasks pt
JOIN orders o ON pt.order_id = o.id
JOIN order_lines ol ON ol.order_id = o.id
JOIN products p ON p.id = ol.product_id
JOIN locations l ON l.code = 'A-01-01'
WHERE o.external_ref = 'ORD-2025-1010' AND p.sku = 'ELEC-007';

INSERT INTO pick_lines (pick_task_id, order_line_id, product_id, location_id, assigned_qty, picked_qty, status)
SELECT pt.id, ol.id, ol.product_id, l.id, ol.allocated_qty, ol.picked_qty,
       CASE WHEN ol.picked_qty >= ol.allocated_qty THEN 'DONE' ELSE 'OPEN' END
FROM pick_tasks pt
JOIN orders o ON pt.order_id = o.id
JOIN order_lines ol ON ol.order_id = o.id
JOIN products p ON p.id = ol.product_id
JOIN locations l ON l.code = 'B-01-01'
WHERE o.external_ref = 'ORD-2025-1010' AND p.sku = 'CLTH-002';

INSERT INTO pick_lines (pick_task_id, order_line_id, product_id, location_id, assigned_qty, picked_qty, status)
SELECT pt.id, ol.id, ol.product_id, l.id, ol.allocated_qty, ol.picked_qty,
       CASE WHEN ol.picked_qty >= ol.allocated_qty THEN 'DONE' ELSE 'OPEN' END
FROM pick_tasks pt
JOIN orders o ON pt.order_id = o.id
JOIN order_lines ol ON ol.order_id = o.id
JOIN products p ON p.id = ol.product_id
JOIN locations l ON l.code = 'A-01-01'
WHERE o.external_ref = 'ORD-2025-1011' AND p.sku = 'ELEC-008';

INSERT INTO pick_lines (pick_task_id, order_line_id, product_id, location_id, assigned_qty, picked_qty, status)
SELECT pt.id, ol.id, ol.product_id, l.id, ol.allocated_qty, ol.picked_qty,
       CASE WHEN ol.picked_qty >= ol.allocated_qty THEN 'DONE' ELSE 'OPEN' END
FROM pick_tasks pt
JOIN orders o ON pt.order_id = o.id
JOIN order_lines ol ON ol.order_id = o.id
JOIN products p ON p.id = ol.product_id
JOIN locations l ON l.code = 'A-02-01'
WHERE o.external_ref = 'ORD-2025-1011' AND p.sku = 'HOME-001';

-- Pick waves
INSERT INTO pick_waves (code, status, created_at, updated_at) VALUES
('WAVE-20250115-0001', 'PLANNED', TIMESTAMP '2025-04-10 13:10:00', TIMESTAMP '2025-04-10 13:10:00'),
('WAVE-20250116-0002', 'IN_PROGRESS', TIMESTAMP '2025-04-10 14:10:00', TIMESTAMP '2025-04-10 14:20:00'),
('WAVE-20250117-0003', 'DONE', TIMESTAMP '2025-04-10 15:10:00', TIMESTAMP '2025-04-10 16:00:00');

-- Wave orders
INSERT INTO pick_wave_orders (wave_id, order_id, created_at)
SELECT w.id, o.id, TIMESTAMP '2025-04-10 13:12:00'
FROM pick_waves w, orders o
WHERE w.code = 'WAVE-20250115-0001' AND o.external_ref = 'ORD-2025-1002';
INSERT INTO pick_wave_orders (wave_id, order_id, created_at)
SELECT w.id, o.id, TIMESTAMP '2025-04-10 13:12:00'
FROM pick_waves w, orders o
WHERE w.code = 'WAVE-20250115-0001' AND o.external_ref = 'ORD-2025-1003';

INSERT INTO pick_wave_orders (wave_id, order_id, created_at)
SELECT w.id, o.id, TIMESTAMP '2025-04-10 14:12:00'
FROM pick_waves w, orders o
WHERE w.code = 'WAVE-20250116-0002' AND o.external_ref = 'ORD-2025-1004';
INSERT INTO pick_wave_orders (wave_id, order_id, created_at)
SELECT w.id, o.id, TIMESTAMP '2025-04-10 14:12:00'
FROM pick_waves w, orders o
WHERE w.code = 'WAVE-20250116-0002' AND o.external_ref = 'ORD-2025-1005';

INSERT INTO pick_wave_orders (wave_id, order_id, created_at)
SELECT w.id, o.id, TIMESTAMP '2025-04-10 15:12:00'
FROM pick_waves w, orders o
WHERE w.code = 'WAVE-20250117-0003' AND o.external_ref = 'ORD-2025-1006';
INSERT INTO pick_wave_orders (wave_id, order_id, created_at)
SELECT w.id, o.id, TIMESTAMP '2025-04-10 15:12:00'
FROM pick_waves w, orders o
WHERE w.code = 'WAVE-20250117-0003' AND o.external_ref = 'ORD-2025-1007';
INSERT INTO pick_wave_orders (wave_id, order_id, created_at)
SELECT w.id, o.id, TIMESTAMP '2025-04-10 15:12:00'
FROM pick_waves w, orders o
WHERE w.code = 'WAVE-20250117-0003' AND o.external_ref = 'ORD-2025-1008';
INSERT INTO pick_wave_orders (wave_id, order_id, created_at)
SELECT w.id, o.id, TIMESTAMP '2025-04-10 15:12:00'
FROM pick_waves w, orders o
WHERE w.code = 'WAVE-20250117-0003' AND o.external_ref = 'ORD-2025-1009';
INSERT INTO pick_wave_orders (wave_id, order_id, created_at)
SELECT w.id, o.id, TIMESTAMP '2025-04-10 15:12:00'
FROM pick_waves w, orders o
WHERE w.code = 'WAVE-20250117-0003' AND o.external_ref = 'ORD-2025-1010';
INSERT INTO pick_wave_orders (wave_id, order_id, created_at)
SELECT w.id, o.id, TIMESTAMP '2025-04-10 15:12:00'
FROM pick_waves w, orders o
WHERE w.code = 'WAVE-20250117-0003' AND o.external_ref = 'ORD-2025-1011';

-- Totes
INSERT INTO totes (wave_id, order_id, barcode, status, packing_station_id, created_at, updated_at)
SELECT w.id, o.id, 'TOTE-20250115-0001-01', 'OPEN', NULL,
       TIMESTAMP '2025-04-10 13:15:00', TIMESTAMP '2025-04-10 13:15:00'
FROM pick_waves w, orders o
WHERE w.code = 'WAVE-20250115-0001' AND o.external_ref = 'ORD-2025-1002';
INSERT INTO totes (wave_id, order_id, barcode, status, packing_station_id, created_at, updated_at)
SELECT w.id, o.id, 'TOTE-20250115-0001-02', 'OPEN', NULL,
       TIMESTAMP '2025-04-10 13:15:00', TIMESTAMP '2025-04-10 13:15:00'
FROM pick_waves w, orders o
WHERE w.code = 'WAVE-20250115-0001' AND o.external_ref = 'ORD-2025-1003';

INSERT INTO totes (wave_id, order_id, barcode, status, packing_station_id, created_at, updated_at)
SELECT w.id, o.id, 'TOTE-20250116-0002-01', 'OPEN', NULL,
       TIMESTAMP '2025-04-10 14:15:00', TIMESTAMP '2025-04-10 14:15:00'
FROM pick_waves w, orders o
WHERE w.code = 'WAVE-20250116-0002' AND o.external_ref = 'ORD-2025-1004';
INSERT INTO totes (wave_id, order_id, barcode, status, packing_station_id, created_at, updated_at)
SELECT w.id, o.id, 'TOTE-20250116-0002-02', 'OPEN', NULL,
       TIMESTAMP '2025-04-10 14:15:00', TIMESTAMP '2025-04-10 14:15:00'
FROM pick_waves w, orders o
WHERE w.code = 'WAVE-20250116-0002' AND o.external_ref = 'ORD-2025-1005';

INSERT INTO totes (wave_id, order_id, barcode, status, packing_station_id, created_at, updated_at)
SELECT w.id, o.id, 'TOTE-20250117-0003-01', 'AT_PACKING',
       (SELECT id FROM packing_stations WHERE code = 'PACK-1'),
       TIMESTAMP '2025-04-10 15:15:00', TIMESTAMP '2025-04-10 15:45:00'
FROM pick_waves w, orders o
WHERE w.code = 'WAVE-20250117-0003' AND o.external_ref = 'ORD-2025-1006';
INSERT INTO totes (wave_id, order_id, barcode, status, packing_station_id, created_at, updated_at)
SELECT w.id, o.id, 'TOTE-20250117-0003-02', 'CLOSED',
       (SELECT id FROM packing_stations WHERE code = 'PACK-2'),
       TIMESTAMP '2025-04-10 15:20:00', TIMESTAMP '2025-04-10 16:10:00'
FROM pick_waves w, orders o
WHERE w.code = 'WAVE-20250117-0003' AND o.external_ref = 'ORD-2025-1007';
INSERT INTO totes (wave_id, order_id, barcode, status, packing_station_id, created_at, updated_at)
SELECT w.id, o.id, 'TOTE-20250117-0003-03', 'CLOSED',
       (SELECT id FROM packing_stations WHERE code = 'PACK-2'),
       TIMESTAMP '2025-04-10 15:22:00', TIMESTAMP '2025-04-10 16:15:00'
FROM pick_waves w, orders o
WHERE w.code = 'WAVE-20250117-0003' AND o.external_ref = 'ORD-2025-1008';
INSERT INTO totes (wave_id, order_id, barcode, status, packing_station_id, created_at, updated_at)
SELECT w.id, o.id, 'TOTE-20250117-0003-04', 'CLOSED',
       (SELECT id FROM packing_stations WHERE code = 'PACK-2'),
       TIMESTAMP '2025-04-10 15:24:00', TIMESTAMP '2025-04-10 16:20:00'
FROM pick_waves w, orders o
WHERE w.code = 'WAVE-20250117-0003' AND o.external_ref = 'ORD-2025-1009';
INSERT INTO totes (wave_id, order_id, barcode, status, packing_station_id, created_at, updated_at)
SELECT w.id, o.id, 'TOTE-20250117-0003-05', 'CLOSED',
       (SELECT id FROM packing_stations WHERE code = 'PACK-3'),
       TIMESTAMP '2025-04-10 15:26:00', TIMESTAMP '2025-04-10 16:30:00'
FROM pick_waves w, orders o
WHERE w.code = 'WAVE-20250117-0003' AND o.external_ref = 'ORD-2025-1010';
INSERT INTO totes (wave_id, order_id, barcode, status, packing_station_id, created_at, updated_at)
SELECT w.id, o.id, 'TOTE-20250117-0003-06', 'CLOSED',
       (SELECT id FROM packing_stations WHERE code = 'PACK-3'),
       TIMESTAMP '2025-04-10 15:28:00', TIMESTAMP '2025-04-10 16:35:00'
FROM pick_waves w, orders o
WHERE w.code = 'WAVE-20250117-0003' AND o.external_ref = 'ORD-2025-1011';

-- Shipments
INSERT INTO shipments (order_id, carrier, status, print_error, created_at, updated_at)
SELECT o.id, o.carrier, 'LABELLED', NULL, TIMESTAMP '2025-04-10 16:00:00', TIMESTAMP '2025-04-10 16:10:00'
FROM orders o WHERE o.external_ref = 'ORD-2025-1006';
INSERT INTO shipments (order_id, carrier, status, print_error, created_at, updated_at)
SELECT o.id, o.carrier, 'LABELLED', NULL, TIMESTAMP '2025-04-10 16:05:00', TIMESTAMP '2025-04-10 16:15:00'
FROM orders o WHERE o.external_ref = 'ORD-2025-1007';
INSERT INTO shipments (order_id, carrier, status, print_error, created_at, updated_at)
SELECT o.id, o.carrier, 'PRINTED', NULL, TIMESTAMP '2025-04-10 16:10:00', TIMESTAMP '2025-04-10 16:25:00'
FROM orders o WHERE o.external_ref = 'ORD-2025-1008';
INSERT INTO shipments (order_id, carrier, status, print_error, created_at, updated_at)
SELECT o.id, o.carrier, 'LABELLED', NULL, TIMESTAMP '2025-04-10 16:12:00', TIMESTAMP '2025-04-10 16:22:00'
FROM orders o WHERE o.external_ref = 'ORD-2025-1009';
INSERT INTO shipments (order_id, carrier, status, print_error, created_at, updated_at)
SELECT o.id, o.carrier, 'PRINTED', NULL, TIMESTAMP '2025-04-10 16:18:00', TIMESTAMP '2025-04-10 16:40:00'
FROM orders o WHERE o.external_ref = 'ORD-2025-1010';
INSERT INTO shipments (order_id, carrier, status, print_error, created_at, updated_at)
SELECT o.id, o.carrier, 'PRINTED', NULL, TIMESTAMP '2025-04-10 16:22:00', TIMESTAMP '2025-04-10 16:45:00'
FROM orders o WHERE o.external_ref = 'ORD-2025-1011';

-- Packages (simple ZPL payloads)
INSERT INTO packages (shipment_id, package_no, package_count, tracking_code, label_format, label_zpl, printed_at, created_at)
SELECT s.id, 1, 2, 'DHL-2025-1006-01', 'ZPL', '^XA^FO40,40^ADN,36,20^FDWMS DEMO 2025^FS^XZ', NULL,
       TIMESTAMP '2025-04-10 16:02:00'
FROM shipments s WHERE s.order_id = (SELECT id FROM orders WHERE external_ref = 'ORD-2025-1006');
INSERT INTO packages (shipment_id, package_no, package_count, tracking_code, label_format, label_zpl, printed_at, created_at)
SELECT s.id, 2, 2, 'DHL-2025-1006-02', 'ZPL', '^XA^FO40,40^ADN,36,20^FDWMS DEMO 2025^FS^XZ', NULL,
       TIMESTAMP '2025-04-10 16:02:00'
FROM shipments s WHERE s.order_id = (SELECT id FROM orders WHERE external_ref = 'ORD-2025-1006');

INSERT INTO packages (shipment_id, package_no, package_count, tracking_code, label_format, label_zpl, printed_at, created_at)
SELECT s.id, 1, 1, 'GLS-2025-1007-01', 'ZPL', '^XA^FO40,40^ADN,36,20^FDWMS DEMO 2025^FS^XZ', NULL,
       TIMESTAMP '2025-04-10 16:06:00'
FROM shipments s WHERE s.order_id = (SELECT id FROM orders WHERE external_ref = 'ORD-2025-1007');

INSERT INTO packages (shipment_id, package_no, package_count, tracking_code, label_format, label_zpl, printed_at, created_at)
SELECT s.id, 1, 2, 'TDN-2025-1008-01', 'ZPL', '^XA^FO40,40^ADN,36,20^FDWMS DEMO 2025^FS^XZ',
       TIMESTAMP '2025-04-10 16:26:00', TIMESTAMP '2025-04-10 16:12:00'
FROM shipments s WHERE s.order_id = (SELECT id FROM orders WHERE external_ref = 'ORD-2025-1008');
INSERT INTO packages (shipment_id, package_no, package_count, tracking_code, label_format, label_zpl, printed_at, created_at)
SELECT s.id, 2, 2, 'TDN-2025-1008-02', 'ZPL', '^XA^FO40,40^ADN,36,20^FDWMS DEMO 2025^FS^XZ',
       TIMESTAMP '2025-04-10 16:26:00', TIMESTAMP '2025-04-10 16:12:00'
FROM shipments s WHERE s.order_id = (SELECT id FROM orders WHERE external_ref = 'ORD-2025-1008');

INSERT INTO packages (shipment_id, package_no, package_count, tracking_code, label_format, label_zpl, printed_at, created_at)
SELECT s.id, 1, 1, 'CORREOS-2025-1009-01', 'ZPL', '^XA^FO40,40^ADN,36,20^FDWMS DEMO 2025^FS^XZ', NULL,
       TIMESTAMP '2025-04-10 16:14:00'
FROM shipments s WHERE s.order_id = (SELECT id FROM orders WHERE external_ref = 'ORD-2025-1009');

INSERT INTO packages (shipment_id, package_no, package_count, tracking_code, label_format, label_zpl, printed_at, created_at)
SELECT s.id, 1, 2, 'DHL-2025-1010-01', 'ZPL', '^XA^FO40,40^ADN,36,20^FDWMS DEMO 2025^FS^XZ',
       TIMESTAMP '2025-04-10 16:42:00', TIMESTAMP '2025-04-10 16:20:00'
FROM shipments s WHERE s.order_id = (SELECT id FROM orders WHERE external_ref = 'ORD-2025-1010');
INSERT INTO packages (shipment_id, package_no, package_count, tracking_code, label_format, label_zpl, printed_at, created_at)
SELECT s.id, 2, 2, 'DHL-2025-1010-02', 'ZPL', '^XA^FO40,40^ADN,36,20^FDWMS DEMO 2025^FS^XZ',
       TIMESTAMP '2025-04-10 16:42:00', TIMESTAMP '2025-04-10 16:20:00'
FROM shipments s WHERE s.order_id = (SELECT id FROM orders WHERE external_ref = 'ORD-2025-1010');

INSERT INTO packages (shipment_id, package_no, package_count, tracking_code, label_format, label_zpl, printed_at, created_at)
SELECT s.id, 1, 1, 'GLS-2025-1011-01', 'ZPL', '^XA^FO40,40^ADN,36,20^FDWMS DEMO 2025^FS^XZ',
       TIMESTAMP '2025-04-10 16:46:00', TIMESTAMP '2025-04-10 16:24:00'
FROM shipments s WHERE s.order_id = (SELECT id FROM orders WHERE external_ref = 'ORD-2025-1011');
