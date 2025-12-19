-- Seed orders for testing

-- Order 1: Can be fully allocated (products have enough stock)
INSERT INTO orders (external_ref, status) VALUES ('ERP-001', 'DRAFT');

INSERT INTO order_lines (order_id, product_id, requested_qty) VALUES
    (1, 1, 20),  -- Widget Standard: have 150 in stock (100 + 50)
    (1, 2, 10);  -- Widget Pro: have 75 in stock

-- Order 2: Partial allocation (not enough stock for all)
INSERT INTO orders (external_ref, status) VALUES ('ERP-002', 'DRAFT');

INSERT INTO order_lines (order_id, product_id, requested_qty) VALUES
    (2, 3, 50),   -- Gadget Basic: have 200 in stock
    (2, 4, 100),  -- Gadget Advanced: only have 30 in stock (will be partial)
    (2, 5, 1000); -- Component X: have 500 in stock (will be partial)
