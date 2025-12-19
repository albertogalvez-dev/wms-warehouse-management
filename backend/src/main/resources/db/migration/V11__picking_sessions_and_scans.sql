-- M4.0: Picking handheld sessions + scan events (batch picking by wave)

CREATE TABLE picking_sessions (
    id BIGSERIAL PRIMARY KEY,
    wave_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    current_location_id BIGINT,
    current_product_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_picking_sessions_wave FOREIGN KEY (wave_id) REFERENCES pick_waves(id),
    CONSTRAINT fk_picking_sessions_location FOREIGN KEY (current_location_id) REFERENCES locations(id),
    CONSTRAINT fk_picking_sessions_product FOREIGN KEY (current_product_id) REFERENCES products(id),
    CONSTRAINT chk_picking_sessions_status CHECK (status IN ('OPEN', 'DONE', 'CANCELLED'))
);

CREATE TABLE picking_scan_events (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL,
    scanned_code VARCHAR(100) NOT NULL,
    event_type VARCHAR(20) NOT NULL,
    message VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_picking_scan_events_session FOREIGN KEY (session_id) REFERENCES picking_sessions(id) ON DELETE CASCADE,
    CONSTRAINT chk_picking_scan_event_type CHECK (event_type IN ('LOCATION', 'PRODUCT', 'TOTE', 'ERROR'))
);

-- Indexes
CREATE INDEX idx_picking_sessions_wave ON picking_sessions(wave_id);
CREATE INDEX idx_picking_sessions_status ON picking_sessions(status);
CREATE INDEX idx_picking_scan_events_session ON picking_scan_events(session_id);
CREATE INDEX idx_picking_scan_events_created ON picking_scan_events(created_at);

