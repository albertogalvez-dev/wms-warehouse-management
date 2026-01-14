-- V12: Users table for authentication
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'PICKER',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create index for faster lookups
CREATE INDEX idx_users_username ON users(username);

-- Insert default users
-- Password for all users: admin123
-- BCrypt hash generated with cost 10 for "admin123"
-- Hash: $2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQkLgpXOXTaLQqGBl.aIcdrITblOfC
INSERT INTO users (username, password_hash, role, active, created_at) VALUES
    ('admin', '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQkLgpXOXTaLQqGBl.aIcdrITblOfC', 'ADMIN', true, CURRENT_TIMESTAMP),
    ('picker', '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQkLgpXOXTaLQqGBl.aIcdrITblOfC', 'PICKER', true, CURRENT_TIMESTAMP),
    ('packer', '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQkLgpXOXTaLQqGBl.aIcdrITblOfC', 'PACKER', true, CURRENT_TIMESTAMP);
