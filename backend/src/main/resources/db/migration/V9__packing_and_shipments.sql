-- M4: Packing workflow with multi-package shipments and ZPL labels

-- Packing Sessions (one active per tote)
CREATE TABLE packing_sessions (
    id BIGSERIAL PRIMARY KEY,
    tote_id BIGINT NOT NULL,
    station_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    operator VARCHAR(100),
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    finished_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_packing_sessions_tote FOREIGN KEY (tote_id) REFERENCES totes(id),
    CONSTRAINT fk_packing_sessions_station FOREIGN KEY (station_id) REFERENCES packing_stations(id),
    CONSTRAINT uk_packing_sessions_tote UNIQUE (tote_id),
    CONSTRAINT chk_packing_sessions_status CHECK (status IN ('OPEN', 'DONE', 'CANCELLED'))
);

-- Packing Lines (expected vs scanned per product)
CREATE TABLE packing_lines (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    required_qty INT NOT NULL CHECK (required_qty > 0),
    packed_qty INT NOT NULL DEFAULT 0 CHECK (packed_qty >= 0),
    CONSTRAINT fk_packing_lines_session FOREIGN KEY (session_id) REFERENCES packing_sessions(id) ON DELETE CASCADE,
    CONSTRAINT fk_packing_lines_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT uk_packing_lines_session_product UNIQUE (session_id, product_id)
);

-- Shipments (one per order)
CREATE TABLE shipments (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    carrier VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    print_error TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_shipments_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT uk_shipments_order UNIQUE (order_id),
    CONSTRAINT chk_shipments_status CHECK (status IN ('CREATED', 'LABELLED', 'PRINTED'))
);

-- Packages (multi-package support with ZPL labels)
CREATE TABLE packages (
    id BIGSERIAL PRIMARY KEY,
    shipment_id BIGINT NOT NULL,
    package_no INT NOT NULL CHECK (package_no >= 1),
    package_count INT NOT NULL CHECK (package_count >= 1),
    tracking_code VARCHAR(100) NOT NULL,
    label_format VARCHAR(10) NOT NULL DEFAULT 'ZPL',
    label_zpl TEXT NOT NULL,
    printed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_packages_shipment FOREIGN KEY (shipment_id) REFERENCES shipments(id) ON DELETE CASCADE,
    CONSTRAINT uk_packages_tracking UNIQUE (tracking_code),
    CONSTRAINT uk_packages_shipment_no UNIQUE (shipment_id, package_no)
);

-- Indexes
CREATE INDEX idx_packing_sessions_tote ON packing_sessions(tote_id);
CREATE INDEX idx_packing_sessions_status ON packing_sessions(status);
CREATE INDEX idx_packing_lines_session ON packing_lines(session_id);
CREATE INDEX idx_shipments_order ON shipments(order_id);
CREATE INDEX idx_packages_tracking ON packages(tracking_code);
CREATE INDEX idx_packages_shipment ON packages(shipment_id);
