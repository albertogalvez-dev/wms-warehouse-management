-- M3.2: PickWaves, Totes, and Packing Stations

-- Packing Stations table
CREATE TABLE packing_stations (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_packing_stations_code UNIQUE (code)
);

-- Pick Waves table
CREATE TABLE pick_waves (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PLANNED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_pick_waves_code UNIQUE (code),
    CONSTRAINT chk_pick_waves_status CHECK (status IN ('PLANNED', 'IN_PROGRESS', 'DONE', 'CANCELLED'))
);

-- Pick Wave Orders (link table)
CREATE TABLE pick_wave_orders (
    id BIGSERIAL PRIMARY KEY,
    wave_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pick_wave_orders_wave FOREIGN KEY (wave_id) REFERENCES pick_waves(id) ON DELETE CASCADE,
    CONSTRAINT fk_pick_wave_orders_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT uk_pick_wave_orders_order UNIQUE (order_id)
);

-- Totes table
CREATE TABLE totes (
    id BIGSERIAL PRIMARY KEY,
    wave_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    barcode VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    packing_station_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_totes_wave FOREIGN KEY (wave_id) REFERENCES pick_waves(id),
    CONSTRAINT fk_totes_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_totes_packing_station FOREIGN KEY (packing_station_id) REFERENCES packing_stations(id),
    CONSTRAINT uk_totes_barcode UNIQUE (barcode),
    CONSTRAINT uk_totes_wave_order UNIQUE (wave_id, order_id),
    CONSTRAINT chk_totes_status CHECK (status IN ('OPEN', 'AT_PACKING', 'CLOSED'))
);

-- Indexes
CREATE INDEX idx_pick_wave_orders_wave ON pick_wave_orders(wave_id);
CREATE INDEX idx_pick_waves_status ON pick_waves(status);
CREATE INDEX idx_totes_barcode ON totes(barcode);
CREATE INDEX idx_totes_wave ON totes(wave_id);
CREATE INDEX idx_totes_order ON totes(order_id);
CREATE INDEX idx_totes_station ON totes(packing_station_id);
