-- Enrich demo products with descriptions and images (2025)
UPDATE products SET
    description = '6.7 inch OLED display, 256GB storage, 5G, dual SIM.',
    image_url = '/assets/products/product_smartphone_1766152492081.png',
    created_at = TIMESTAMP '2025-01-12 09:15:00',
    updated_at = TIMESTAMP '2025-03-20 14:20:00',
    active = true
WHERE sku = 'ELEC-001';

UPDATE products SET
    description = '10.9 inch tablet, 128GB storage, Wi-Fi 6.',
    image_url = '/assets/products/product_tablet_1766152519744.png',
    created_at = TIMESTAMP '2025-01-18 11:10:00',
    updated_at = TIMESTAMP '2025-03-22 16:05:00',
    active = true
WHERE sku = 'ELEC-002';

UPDATE products SET
    description = 'Wireless over-ear headphones with ANC and 30h battery.',
    image_url = '/assets/products/product_headphones_1766152534013.png',
    created_at = TIMESTAMP '2025-01-22 10:00:00',
    updated_at = TIMESTAMP '2025-03-18 13:45:00',
    active = true
WHERE sku = 'ELEC-003';

UPDATE products SET
    description = 'Compact earbuds with charging case and voice assistant.',
    image_url = '/assets/products/product_earbuds_1766152563279.png',
    created_at = TIMESTAMP '2025-01-28 15:30:00',
    updated_at = TIMESTAMP '2025-03-25 09:20:00',
    active = true
WHERE sku = 'ELEC-004';

UPDATE products SET
    description = 'GPS smartwatch with health tracking and 5-day battery.',
    image_url = '/assets/products/product_smartwatch_1766152578578.png',
    created_at = TIMESTAMP '2025-02-02 08:40:00',
    updated_at = TIMESTAMP '2025-03-26 12:10:00',
    active = true
WHERE sku = 'ELEC-005';

UPDATE products SET
    description = 'Padded laptop sleeve for 15 inch devices.',
    image_url = '/assets/products/product_tablet_1766152519744.png',
    created_at = TIMESTAMP '2025-02-05 09:05:00',
    updated_at = TIMESTAMP '2025-03-21 10:35:00',
    active = true
WHERE sku = 'ELEC-006';

UPDATE products SET
    description = 'USB-C fast charger, 65W with foldable plug.',
    image_url = '/assets/products/product_smartphone_1766152492081.png',
    created_at = TIMESTAMP '2025-02-08 12:20:00',
    updated_at = TIMESTAMP '2025-03-29 11:00:00',
    active = true
WHERE sku = 'ELEC-007';

UPDATE products SET
    description = 'Wireless mouse with precision sensor and 2.4G dongle.',
    image_url = '/assets/products/product_headphones_1766152534013.png',
    created_at = TIMESTAMP '2025-02-10 14:55:00',
    updated_at = TIMESTAMP '2025-03-30 09:40:00',
    active = true
WHERE sku = 'ELEC-008';

UPDATE products SET
    description = 'Cotton t-shirt, navy, size M.',
    image_url = '/assets/products/placeholder_apparel.svg',
    created_at = TIMESTAMP '2025-02-12 11:30:00',
    updated_at = TIMESTAMP '2025-04-01 15:15:00',
    active = true
WHERE sku = 'CLTH-001';

UPDATE products SET
    description = 'Cotton t-shirt, black, size L.',
    image_url = '/assets/products/placeholder_apparel.svg',
    created_at = TIMESTAMP '2025-02-13 11:40:00',
    updated_at = TIMESTAMP '2025-04-01 15:20:00',
    active = true
WHERE sku = 'CLTH-002';

UPDATE products SET
    description = 'Classic denim jeans, size 32.',
    image_url = '/assets/products/placeholder_apparel.svg',
    created_at = TIMESTAMP '2025-02-14 12:05:00',
    updated_at = TIMESTAMP '2025-04-02 10:00:00',
    active = true
WHERE sku = 'CLTH-003';

UPDATE products SET
    description = 'Slim denim jeans, size 34.',
    image_url = '/assets/products/placeholder_apparel.svg',
    created_at = TIMESTAMP '2025-02-14 12:10:00',
    updated_at = TIMESTAMP '2025-04-02 10:05:00',
    active = true
WHERE sku = 'CLTH-004';

UPDATE products SET
    description = 'Non-stick frying pan, 28cm.',
    image_url = '/assets/products/placeholder_home.svg',
    created_at = TIMESTAMP '2025-02-16 09:00:00',
    updated_at = TIMESTAMP '2025-04-03 11:45:00',
    active = true
WHERE sku = 'HOME-001';

UPDATE products SET
    description = 'Stainless steel pot, 5L.',
    image_url = '/assets/products/placeholder_home.svg',
    created_at = TIMESTAMP '2025-02-16 09:05:00',
    updated_at = TIMESTAMP '2025-04-03 11:50:00',
    active = true
WHERE sku = 'HOME-002';

UPDATE products SET
    description = 'Electric kettle, 1.7L.',
    image_url = '/assets/products/placeholder_home.svg',
    created_at = TIMESTAMP '2025-02-17 09:20:00',
    updated_at = TIMESTAMP '2025-04-03 12:05:00',
    active = true
WHERE sku = 'HOME-003';

UPDATE products SET
    description = 'Drip coffee maker with timer.',
    image_url = '/assets/products/placeholder_home.svg',
    created_at = TIMESTAMP '2025-02-17 09:30:00',
    updated_at = TIMESTAMP '2025-04-03 12:15:00',
    active = true
WHERE sku = 'HOME-004';

UPDATE products SET
    description = 'Premium yoga mat, anti-slip surface.',
    image_url = '/assets/products/placeholder_sports.svg',
    created_at = TIMESTAMP '2025-02-20 08:30:00',
    updated_at = TIMESTAMP '2025-04-04 09:00:00',
    active = true
WHERE sku = 'SPRT-001';

UPDATE products SET
    description = '10kg dumbbell set with grip texture.',
    image_url = '/assets/products/placeholder_sports.svg',
    created_at = TIMESTAMP '2025-02-20 08:40:00',
    updated_at = TIMESTAMP '2025-04-04 09:05:00',
    active = true
WHERE sku = 'SPRT-002';

UPDATE products SET
    description = 'Resistance bands set, 5 levels.',
    image_url = '/assets/products/placeholder_sports.svg',
    created_at = TIMESTAMP '2025-02-20 08:50:00',
    updated_at = TIMESTAMP '2025-04-04 09:10:00',
    active = true
WHERE sku = 'SPRT-003';

UPDATE products SET
    description = 'Power bank, 20000mAh, dual USB output.',
    image_url = '/assets/products/placeholder_misc.svg',
    created_at = TIMESTAMP '2025-02-22 10:10:00',
    updated_at = TIMESTAMP '2025-04-05 10:30:00',
    active = true
WHERE sku = 'MISC-001';

UPDATE products SET
    description = 'HDMI cable, 2m, 4K compatible.',
    image_url = '/assets/products/placeholder_misc.svg',
    created_at = TIMESTAMP '2025-02-22 10:15:00',
    updated_at = TIMESTAMP '2025-04-05 10:35:00',
    active = true
WHERE sku = 'MISC-002';
