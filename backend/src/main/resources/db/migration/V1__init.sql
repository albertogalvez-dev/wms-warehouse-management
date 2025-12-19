-- WMS Initial Migration
-- Creates core tables: products, locations, stock

-- Products table
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    barcode VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_products_sku UNIQUE (sku),
    CONSTRAINT uk_products_barcode UNIQUE (barcode)
);

-- Locations table
CREATE TABLE locations (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    zone VARCHAR(20),
    description VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_locations_code UNIQUE (code)
);

-- Stock table
CREATE TABLE stock (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stock_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_stock_location FOREIGN KEY (location_id) REFERENCES locations(id),
    CONSTRAINT uk_stock_product_location UNIQUE (product_id, location_id),
    CONSTRAINT chk_stock_quantity CHECK (quantity >= 0)
);

-- Indexes for common queries
CREATE INDEX idx_products_active ON products(active);
CREATE INDEX idx_products_sku ON products(sku);
CREATE INDEX idx_locations_active ON locations(active);
CREATE INDEX idx_locations_code ON locations(code);
CREATE INDEX idx_stock_product ON stock(product_id);
CREATE INDEX idx_stock_location ON stock(location_id);
