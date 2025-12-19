-- M3.1: Shipping address and carrier support

-- Add shipping columns to orders (NULLABLE initially for backfill)
ALTER TABLE orders
    ADD COLUMN shipping_name VARCHAR(255),
    ADD COLUMN shipping_phone VARCHAR(50),
    ADD COLUMN shipping_email VARCHAR(255),
    ADD COLUMN shipping_address1 VARCHAR(255),
    ADD COLUMN shipping_address2 VARCHAR(255),
    ADD COLUMN shipping_postal_code VARCHAR(20),
    ADD COLUMN shipping_city VARCHAR(100),
    ADD COLUMN shipping_province VARCHAR(100),
    ADD COLUMN shipping_country VARCHAR(10) DEFAULT 'ES',
    ADD COLUMN carrier VARCHAR(20);

-- Update existing orders with dummy shipping data
UPDATE orders SET
    shipping_name = 'Cliente Seed ' || id,
    shipping_phone = '+34600000000',
    shipping_email = 'seed' || id || '@example.com',
    shipping_address1 = 'Calle Ejemplo ' || id,
    shipping_postal_code = '28001',
    shipping_city = 'Madrid',
    shipping_province = 'Madrid',
    shipping_country = 'ES',
    carrier = 'DHL'
WHERE shipping_name IS NULL;

-- Now make required columns NOT NULL
ALTER TABLE orders
    ALTER COLUMN shipping_name SET NOT NULL,
    ALTER COLUMN shipping_address1 SET NOT NULL,
    ALTER COLUMN shipping_postal_code SET NOT NULL,
    ALTER COLUMN shipping_city SET NOT NULL,
    ALTER COLUMN shipping_country SET NOT NULL,
    ALTER COLUMN carrier SET NOT NULL;

-- Add check constraint for carrier
ALTER TABLE orders
    ADD CONSTRAINT chk_orders_carrier CHECK (carrier IN ('DHL', 'GLS', 'TDN', 'CORREOS'));
