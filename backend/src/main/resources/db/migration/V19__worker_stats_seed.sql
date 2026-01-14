-- Add operator tracking for picking tasks + seed worker stats

ALTER TABLE pick_tasks
    ADD COLUMN IF NOT EXISTS operator VARCHAR(100);

CREATE INDEX IF NOT EXISTS idx_pick_tasks_operator ON pick_tasks(operator);

-- Assign demo operators to pick tasks (2025 flow)
UPDATE pick_tasks pt
SET operator = 'operator1',
    created_at = TIMESTAMP '2025-04-10 09:50:00',
    updated_at = TIMESTAMP '2025-04-10 10:25:00'
FROM orders o
WHERE pt.order_id = o.id
  AND o.external_ref IN ('ORD-2025-1002', 'ORD-2025-1003');

UPDATE pick_tasks pt
SET operator = 'operator2',
    created_at = TIMESTAMP '2025-04-10 10:15:00',
    updated_at = TIMESTAMP '2025-04-10 10:50:00'
FROM orders o
WHERE pt.order_id = o.id
  AND o.external_ref IN ('ORD-2025-1004', 'ORD-2025-1005');

UPDATE pick_tasks pt
SET operator = 'operator1',
    created_at = TIMESTAMP '2025-04-10 10:45:00',
    updated_at = TIMESTAMP '2025-04-10 11:35:00'
FROM orders o
WHERE pt.order_id = o.id
  AND o.external_ref IN ('ORD-2025-1006', 'ORD-2025-1007', 'ORD-2025-1008', 'ORD-2025-1009');

UPDATE pick_tasks pt
SET operator = 'operator2',
    created_at = TIMESTAMP '2025-04-10 11:00:00',
    updated_at = TIMESTAMP '2025-04-10 12:05:00'
FROM orders o
WHERE pt.order_id = o.id
  AND o.external_ref IN ('ORD-2025-1010', 'ORD-2025-1011');

-- Packing sessions for demo productivity stats
INSERT INTO packing_sessions (tote_id, station_id, status, operator, started_at, finished_at, created_at, updated_at)
SELECT t.id, t.packing_station_id, 'DONE', 'operator2',
       TIMESTAMP '2025-04-10 15:30:00', TIMESTAMP '2025-04-10 15:55:00',
       TIMESTAMP '2025-04-10 15:30:00', TIMESTAMP '2025-04-10 15:55:00'
FROM totes t
WHERE t.barcode = 'TOTE-20250117-0003-01'
  AND t.packing_station_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM packing_sessions ps WHERE ps.tote_id = t.id);

INSERT INTO packing_sessions (tote_id, station_id, status, operator, started_at, finished_at, created_at, updated_at)
SELECT t.id, t.packing_station_id, 'DONE', 'operator1',
       TIMESTAMP '2025-04-10 15:40:00', TIMESTAMP '2025-04-10 16:05:00',
       TIMESTAMP '2025-04-10 15:40:00', TIMESTAMP '2025-04-10 16:05:00'
FROM totes t
WHERE t.barcode = 'TOTE-20250117-0003-02'
  AND t.packing_station_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM packing_sessions ps WHERE ps.tote_id = t.id);

INSERT INTO packing_sessions (tote_id, station_id, status, operator, started_at, finished_at, created_at, updated_at)
SELECT t.id, t.packing_station_id, 'DONE', 'operator2',
       TIMESTAMP '2025-04-10 15:50:00', TIMESTAMP '2025-04-10 16:20:00',
       TIMESTAMP '2025-04-10 15:50:00', TIMESTAMP '2025-04-10 16:20:00'
FROM totes t
WHERE t.barcode = 'TOTE-20250117-0003-03'
  AND t.packing_station_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM packing_sessions ps WHERE ps.tote_id = t.id);

-- Packing lines (mirror order lines as fully packed)
INSERT INTO packing_lines (session_id, product_id, required_qty, packed_qty)
SELECT ps.id, ol.product_id, ol.requested_qty, ol.requested_qty
FROM packing_sessions ps
JOIN totes t ON ps.tote_id = t.id
JOIN order_lines ol ON ol.order_id = t.order_id
WHERE t.barcode = 'TOTE-20250117-0003-01'
ON CONFLICT (session_id, product_id)
DO UPDATE SET required_qty = EXCLUDED.required_qty, packed_qty = EXCLUDED.packed_qty;

INSERT INTO packing_lines (session_id, product_id, required_qty, packed_qty)
SELECT ps.id, ol.product_id, ol.requested_qty, ol.requested_qty
FROM packing_sessions ps
JOIN totes t ON ps.tote_id = t.id
JOIN order_lines ol ON ol.order_id = t.order_id
WHERE t.barcode = 'TOTE-20250117-0003-02'
ON CONFLICT (session_id, product_id)
DO UPDATE SET required_qty = EXCLUDED.required_qty, packed_qty = EXCLUDED.packed_qty;

INSERT INTO packing_lines (session_id, product_id, required_qty, packed_qty)
SELECT ps.id, ol.product_id, ol.requested_qty, ol.requested_qty
FROM packing_sessions ps
JOIN totes t ON ps.tote_id = t.id
JOIN order_lines ol ON ol.order_id = t.order_id
WHERE t.barcode = 'TOTE-20250117-0003-03'
ON CONFLICT (session_id, product_id)
DO UPDATE SET required_qty = EXCLUDED.required_qty, packed_qty = EXCLUDED.packed_qty;
