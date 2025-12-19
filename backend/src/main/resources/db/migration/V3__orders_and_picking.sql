-- M3: Orders and Picking tables

-- Orders table
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    external_ref VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_orders_external_ref UNIQUE (external_ref),
    CONSTRAINT chk_orders_status CHECK (status IN ('DRAFT', 'RELEASED', 'PICKING', 'PICKED', 'CANCELLED'))
);

-- Order Lines table
CREATE TABLE order_lines (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    requested_qty INTEGER NOT NULL,
    allocated_qty INTEGER NOT NULL DEFAULT 0,
    picked_qty INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_order_lines_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_lines_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT uk_order_lines_order_product UNIQUE (order_id, product_id),
    CONSTRAINT chk_order_lines_requested_qty CHECK (requested_qty > 0),
    CONSTRAINT chk_order_lines_allocated_qty CHECK (allocated_qty >= 0),
    CONSTRAINT chk_order_lines_picked_qty CHECK (picked_qty >= 0)
);

-- Pick Tasks table
CREATE TABLE pick_tasks (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pick_tasks_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT uk_pick_tasks_order UNIQUE (order_id),
    CONSTRAINT chk_pick_tasks_status CHECK (status IN ('OPEN', 'IN_PROGRESS', 'DONE', 'CANCELLED'))
);

-- Pick Lines table
CREATE TABLE pick_lines (
    id BIGSERIAL PRIMARY KEY,
    pick_task_id BIGINT NOT NULL,
    order_line_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    assigned_qty INTEGER NOT NULL,
    picked_qty INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    CONSTRAINT fk_pick_lines_pick_task FOREIGN KEY (pick_task_id) REFERENCES pick_tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_pick_lines_order_line FOREIGN KEY (order_line_id) REFERENCES order_lines(id),
    CONSTRAINT fk_pick_lines_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_pick_lines_location FOREIGN KEY (location_id) REFERENCES locations(id),
    CONSTRAINT chk_pick_lines_assigned_qty CHECK (assigned_qty > 0),
    CONSTRAINT chk_pick_lines_picked_qty CHECK (picked_qty >= 0),
    CONSTRAINT chk_pick_lines_status CHECK (status IN ('OPEN', 'DONE'))
);

-- Indexes
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_external_ref ON orders(external_ref);
CREATE INDEX idx_order_lines_order ON order_lines(order_id);
CREATE INDEX idx_order_lines_product ON order_lines(product_id);
CREATE INDEX idx_pick_tasks_order ON pick_tasks(order_id);
CREATE INDEX idx_pick_tasks_status ON pick_tasks(status);
CREATE INDEX idx_pick_lines_pick_task ON pick_lines(pick_task_id);
CREATE INDEX idx_pick_lines_order_line ON pick_lines(order_line_id);
CREATE INDEX idx_pick_lines_location ON pick_lines(location_id);
