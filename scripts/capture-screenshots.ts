/**
 * WMS Screenshot Capture Script
 * 
 * Captures full-page screenshots of all WMS pages for documentation.
 * Uses Playwright to automate browser navigation and authentication.
 * 
 * Usage:
 *   npm run screenshots
 * 
 * Environment Variables:
 *   WMS_E2E_USER     - Login username (default: admin)
 *   WMS_E2E_PASS     - Login password (default: admin123)
 *   WMS_BASE_URL     - Base URL (default: http://127.0.0.1:8081)
 */

import { chromium, Browser, Page, BrowserContext } from '@playwright/test';
import * as fs from 'fs';
import * as path from 'path';

// ============================================
// Configuration
// ============================================

const BASE_URL = process.env.WMS_BASE_URL || 'http://127.0.0.1:8081';
const USERNAME = process.env.WMS_E2E_USER || 'admin';
const PASSWORD = process.env.WMS_E2E_PASS || 'admin123';

const DESKTOP_VIEWPORT = { width: 1920, height: 1080 };
const MOBILE_VIEWPORT = { width: 390, height: 844 }; // iPhone 13

const OUTPUT_DIR = path.resolve(__dirname, '..', '@fotos');
const DESKTOP_DIR = path.join(OUTPUT_DIR, 'desktop');
const MOBILE_DIR = path.join(OUTPUT_DIR, 'mobile');

// Routes to capture
const ROUTES = {
    // Landing page (public)
    landing: [
        { path: '/landing/', name: 'landing', public: true },
    ],

    // Backoffice routes (require auth)
    backoffice: [
        { path: '/backoffice/#/dashboard', name: 'dashboard' },
        { path: '/backoffice/#/orders', name: 'orders' },
        { path: '/backoffice/#/orders/new', name: 'order-create' },
        { path: '/backoffice/#/products', name: 'products' },
        { path: '/backoffice/#/waves', name: 'waves' },
        { path: '/backoffice/#/totes', name: 'totes' },
        { path: '/backoffice/#/shipments', name: 'shipments' },
        { path: '/backoffice/#/settings', name: 'settings' },
        { path: '/backoffice/#/guide', name: 'guide' },
        { path: '/backoffice/#/admin/users', name: 'admin-users' },
    ],

    // Handheld routes (require auth)
    handheld: [
        { path: '/handheld/', name: 'handheld-start', waitForSelector: '#viewStart:not(.hidden), #viewLogin:not(.hidden)' },
    ],
};

// Dangerous patterns to avoid
const DANGEROUS_PATTERNS = [
    /logout/i,
    /delete/i,
    /remove/i,
    /reset/i,
    /destroy/i,
];

interface RouteConfig {
    path: string;
    name: string;
    public?: boolean;
    waitForSelector?: string;
}

interface ManifestEntry {
    route: string;
    filename: string;
    timestamp: string;
    viewport: string;
    category: string;
}

// ============================================
// Utility Functions
// ============================================

function ensureDir(dir: string): void {
    if (!fs.existsSync(dir)) {
        fs.mkdirSync(dir, { recursive: true });
    }
}

function sanitizeFilename(name: string): string {
    return name
        .replace(/^\/+|\/+$/g, '')
        .replace(/[\/\\:*?"<>|#]/g, '-')
        .replace(/-+/g, '-')
        .replace(/^-|-$/g, '') || 'home';
}

function isDangerous(url: string): boolean {
    return DANGEROUS_PATTERNS.some(pattern => pattern.test(url));
}

async function waitForHealth(maxRetries = 30, delayMs = 2000): Promise<boolean> {
    console.log(`⏳ Waiting for WMS to be healthy at ${BASE_URL}...`);

    for (let i = 0; i < maxRetries; i++) {
        try {
            const response = await fetch(`${BASE_URL}/actuator/health`);
            if (response.ok) {
                const data = await response.json();
                if (data.status === 'UP') {
                    console.log('✅ WMS is healthy!');
                    return true;
                }
            }
        } catch (e) {
            // Server not ready yet
        }

        if (i < maxRetries - 1) {
            process.stdout.write(`  Attempt ${i + 1}/${maxRetries}...\r`);
            await new Promise(r => setTimeout(r, delayMs));
        }
    }

    console.warn('⚠️ Could not verify health, proceeding anyway...');
    return false;
}

// ============================================
// Screenshot Capture
// ============================================

async function login(page: Page, loginUrl: string): Promise<boolean> {
    console.log(`🔐 Logging in at ${loginUrl}...`);

    try {
        await page.goto(loginUrl, { waitUntil: 'networkidle' });

        // Wait for login form
        await page.waitForSelector('input[name="username"], #username, input[type="text"]', { timeout: 10000 });

        // Fill credentials
        const usernameInput = await page.$('input[name="username"]') || await page.$('#username') || await page.$('input[type="text"]');
        const passwordInput = await page.$('input[name="password"]') || await page.$('#password') || await page.$('input[type="password"]');

        if (usernameInput && passwordInput) {
            await usernameInput.fill(USERNAME);
            await passwordInput.fill(PASSWORD);

            // Submit form
            const submitBtn = await page.$('button[type="submit"]') || await page.$('#loginBtn') || await page.$('button');
            if (submitBtn) {
                await submitBtn.click();
                await page.waitForTimeout(2000);

                // Check if login was successful (no longer on login page)
                const currentUrl = page.url();
                if (!currentUrl.includes('login')) {
                    console.log('✅ Login successful!');
                    return true;
                }
            }
        }

        console.warn('⚠️ Login form not found or login failed');
        return false;
    } catch (e) {
        console.error('❌ Login error:', e);
        return false;
    }
}

async function captureScreenshot(
    page: Page,
    route: RouteConfig,
    viewport: { width: number; height: number },
    outputDir: string,
    category: string
): Promise<ManifestEntry | null> {
    const fullUrl = `${BASE_URL}${route.path}`;

    if (isDangerous(fullUrl)) {
        console.log(`  ⚠️ Skipping dangerous route: ${route.path}`);
        return null;
    }

    const filename = `${sanitizeFilename(route.name)}.png`;
    const filepath = path.join(outputDir, filename);

    try {
        console.log(`  📸 Capturing ${route.name}...`);

        await page.setViewportSize(viewport);
        await page.goto(fullUrl, { waitUntil: 'networkidle', timeout: 30000 });

        // Wait for specific selector if provided
        if (route.waitForSelector) {
            await page.waitForSelector(route.waitForSelector, { timeout: 10000 }).catch(() => { });
        }

        // Extra wait for dynamic content
        await page.waitForTimeout(1500);

        // Take full page screenshot
        await page.screenshot({
            path: filepath,
            fullPage: true,
        });

        return {
            route: route.path,
            filename,
            timestamp: new Date().toISOString(),
            viewport: `${viewport.width}x${viewport.height}`,
            category,
        };
    } catch (e) {
        console.error(`  ❌ Failed to capture ${route.name}: ${e}`);
        return null;
    }
}

// ============================================
// Main
// ============================================

async function main(): Promise<void> {
    const args = process.argv.slice(2);
    const desktopOnly = args.includes('--desktop-only');
    const mobileOnly = args.includes('--mobile-only');

    console.log('🚀 WMS Screenshot Capture');
    console.log('========================');
    console.log(`Base URL: ${BASE_URL}`);
    console.log(`Username: ${USERNAME}`);
    console.log(`Output: ${OUTPUT_DIR}`);
    console.log('');

    // Ensure output directories exist
    ensureDir(OUTPUT_DIR);
    ensureDir(DESKTOP_DIR);
    ensureDir(MOBILE_DIR);

    // Wait for health check
    await waitForHealth();

    // Launch browser
    const browser: Browser = await chromium.launch({ headless: true });
    const manifest: ManifestEntry[] = [];

    try {
        // =====================
        // Desktop Screenshots
        // =====================
        if (!mobileOnly) {
            console.log('\n📱 Desktop Screenshots (1920x1080)');
            console.log('──────────────────────────────────');

            const desktopContext: BrowserContext = await browser.newContext({
                viewport: DESKTOP_VIEWPORT,
                ignoreHTTPSErrors: true,
            });
            const desktopPage: Page = await desktopContext.newPage();

            // Capture landing (public)
            for (const route of ROUTES.landing) {
                const entry = await captureScreenshot(desktopPage, route, DESKTOP_VIEWPORT, DESKTOP_DIR, 'landing');
                if (entry) manifest.push(entry);
            }

            // Login for backoffice
            await login(desktopPage, `${BASE_URL}/backoffice/login.html`);

            // Capture backoffice routes
            for (const route of ROUTES.backoffice) {
                const entry = await captureScreenshot(desktopPage, route, DESKTOP_VIEWPORT, DESKTOP_DIR, 'backoffice');
                if (entry) manifest.push(entry);
            }

            // Login for handheld (separate session may be needed)
            await login(desktopPage, `${BASE_URL}/handheld/login.html`);

            // Capture handheld routes
            for (const route of ROUTES.handheld) {
                const entry = await captureScreenshot(desktopPage, route, DESKTOP_VIEWPORT, DESKTOP_DIR, 'handheld');
                if (entry) manifest.push(entry);
            }

            await desktopContext.close();
        }

        // =====================
        // Mobile Screenshots
        // =====================
        if (!desktopOnly) {
            console.log('\n📱 Mobile Screenshots (390x844)');
            console.log('───────────────────────────────');

            const mobileContext: BrowserContext = await browser.newContext({
                viewport: MOBILE_VIEWPORT,
                ignoreHTTPSErrors: true,
                isMobile: true,
            });
            const mobilePage: Page = await mobileContext.newPage();

            // Capture landing (public)
            for (const route of ROUTES.landing) {
                const entry = await captureScreenshot(mobilePage, route, MOBILE_VIEWPORT, MOBILE_DIR, 'landing');
                if (entry) manifest.push(entry);
            }

            // Login for handheld (main mobile target)
            await login(mobilePage, `${BASE_URL}/handheld/login.html`);

            // Capture handheld routes  
            for (const route of ROUTES.handheld) {
                const entry = await captureScreenshot(mobilePage, route, MOBILE_VIEWPORT, MOBILE_DIR, 'handheld');
                if (entry) manifest.push(entry);
            }

            await mobileContext.close();
        }

        // Write manifest
        const manifestPath = path.join(OUTPUT_DIR, 'manifest.json');
        fs.writeFileSync(manifestPath, JSON.stringify(manifest, null, 2));
        console.log(`\n📄 Manifest written to ${manifestPath}`);

        // Summary
        console.log('\n✅ Screenshot Capture Complete!');
        console.log('================================');
        console.log(`Total screenshots: ${manifest.length}`);
        console.log(`Output directory: ${OUTPUT_DIR}`);

    } finally {
        await browser.close();
    }
}

main().catch(console.error);
