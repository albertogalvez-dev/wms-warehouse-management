-- V10: Users table for authentication
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

-- Insert default admin user (password: admin123)
-- BCrypt hash for 'admin123'
INSERT INTO users (username, password_hash, role, active, created_at) VALUES
    ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGYDGY7lVVL1.f0pTnZxLVFPVJwG', 'ADMIN', true, CURRENT_TIMESTAMP);
