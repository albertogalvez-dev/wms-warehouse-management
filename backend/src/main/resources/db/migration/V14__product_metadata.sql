-- Add product metadata for richer catalog display
ALTER TABLE products
    ADD COLUMN IF NOT EXISTS description VARCHAR(2000),
    ADD COLUMN IF NOT EXISTS image_url VARCHAR(512);
