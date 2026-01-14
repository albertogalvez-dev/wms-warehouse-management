-- Expand order status constraint to include packing/shipping stages (2025 demo)
ALTER TABLE orders DROP CONSTRAINT IF EXISTS chk_orders_status;

ALTER TABLE orders
    ADD CONSTRAINT chk_orders_status CHECK (
        status IN ('DRAFT', 'RELEASED', 'PICKING', 'PICKED', 'PACKING', 'PACKED', 'SHIPPED', 'CANCELLED')
    );
