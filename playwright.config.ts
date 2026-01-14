import { defineConfig } from '@playwright/test';

export default defineConfig({
    timeout: 60000,
    use: {
        headless: true,
        viewport: { width: 1920, height: 1080 },
        ignoreHTTPSErrors: true,
        screenshot: 'only-on-failure',
    },
});
