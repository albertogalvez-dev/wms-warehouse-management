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
import { fileURLToPath } from 'url';

// ============================================
// Configuration
// ============================================

const BASE_URL = process.env.WMS_BASE_URL || 'http://127.0.0.1:8081';
const USERNAME = process.env.WMS_E2E_USER || 'admin';
const PASSWORD = process.env.WMS_E2E_PASS || 'admin123';

const DESKTOP_VIEWPORT = { width: 1920, height: 1080 };
const MOBILE_VIEWPORT = { width: 390, height: 844 }; // iPhone 13

// ES modules don't have __dirname, so we compute it
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

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
// Authentication
// ============================================

async function loginBackoffice(page: Page): Promise<boolean> {
    const loginUrl = `${BASE_URL}/backoffice/login.html`;
    console.log(`🔐 Logging in to Backoffice at ${loginUrl}...`);

    try {
        await page.goto(loginUrl, { waitUntil: 'networkidle', timeout: 30000 });

        // CRITICAL: Set the API base URL in localStorage to use the nginx proxy
        // The frontend config defaults to localhost:8080 for port 8081, but
        // in Docker setup, the backend is only accessible via nginx proxy
        await page.evaluate((baseUrl) => {
            localStorage.setItem('wms_backoffice_api_base_url', baseUrl);
        }, BASE_URL);
        console.log(`  📍 Set API base URL: ${BASE_URL}`);

        // Reload to pick up the new API URL
        await page.reload({ waitUntil: 'networkidle' });

        // Wait for the login form to be ready
        await page.waitForSelector('#loginForm', { timeout: 10000 });
        await page.waitForSelector('#username', { timeout: 5000 });
        await page.waitForSelector('#password', { timeout: 5000 });

        // Clear and fill username
        await page.fill('#username', '');
        await page.fill('#username', USERNAME);

        // Clear and fill password
        await page.fill('#password', '');
        await page.fill('#password', PASSWORD);

        console.log(`  📝 Filled credentials: ${USERNAME} / ****`);

        // Click submit button and wait for navigation
        await Promise.all([
            page.waitForURL('**/index.html**', { timeout: 15000 }).catch(() => { }),
            page.click('#loginBtn'),
        ]);

        // Wait for the app to load after login
        await page.waitForTimeout(2000);

        // Check if we're on the main app (not login page anymore)
        const currentUrl = page.url();
        const isLoggedIn = currentUrl.includes('index.html') || !currentUrl.includes('login');

        if (isLoggedIn) {
            console.log('  ✅ Backoffice login successful!');
            // Wait for dashboard to render
            await page.waitForSelector('.sidebar, #appMain, .nav-link', { timeout: 10000 }).catch(() => { });
            return true;
        }

        // Check for error message
        const errorVisible = await page.$('.error-message.show');
        if (errorVisible) {
            const errorText = await errorVisible.textContent();
            console.error(`  ❌ Login failed: ${errorText}`);
        } else {
            console.error('  ❌ Login failed: still on login page');
        }

        return false;
    } catch (e) {
        console.error(`  ❌ Login error: ${e}`);
        return false;
    }
}

async function loginHandheld(page: Page): Promise<boolean> {
    const loginUrl = `${BASE_URL}/handheld/login.html`;
    console.log(`🔐 Logging in to Handheld at ${loginUrl}...`);

    try {
        await page.goto(loginUrl, { waitUntil: 'networkidle', timeout: 30000 });

        // CRITICAL: Set the API base URL in localStorage to use the nginx proxy
        await page.evaluate((baseUrl) => {
            localStorage.setItem('wms_handheld_api_base_url', baseUrl);
        }, BASE_URL);
        console.log(`  📍 Set API base URL: ${BASE_URL}`);

        // Reload to pick up the new API URL
        await page.reload({ waitUntil: 'networkidle' });

        // Wait for the login form
        await page.waitForSelector('#loginForm', { timeout: 10000 });
        await page.waitForSelector('#username', { timeout: 5000 });
        await page.waitForSelector('#password', { timeout: 5000 });

        // Fill credentials
        await page.fill('#username', '');
        await page.fill('#username', USERNAME);
        await page.fill('#password', '');
        await page.fill('#password', PASSWORD);

        console.log(`  📝 Filled credentials: ${USERNAME} / ****`);

        // Click submit and wait
        await Promise.all([
            page.waitForURL('**/index.html**', { timeout: 15000 }).catch(() => { }),
            page.click('#loginBtn'),
        ]);

        await page.waitForTimeout(2000);

        const currentUrl = page.url();
        const isLoggedIn = currentUrl.includes('index.html') || !currentUrl.includes('login');

        if (isLoggedIn) {
            console.log('  ✅ Handheld login successful!');
            return true;
        }

        console.error('  ❌ Handheld login failed');
        return false;
    } catch (e) {
        console.error(`  ❌ Handheld login error: ${e}`);
        return false;
    }
}

// ============================================
// Screenshot Capture
// ============================================

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

        // Extra wait for dynamic content to load
        await page.waitForTimeout(2000);

        // Take full page screenshot
        await page.screenshot({
            path: filepath,
            fullPage: true,
        });

        console.log(`    ✓ Saved: ${filename}`);

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

    console.log('');
    console.log('🚀 WMS Screenshot Capture');
    console.log('========================');
    console.log(`Base URL: ${BASE_URL}`);
    console.log(`Username: ${USERNAME}`);
    console.log(`Password: ${'*'.repeat(PASSWORD.length)}`);
    console.log(`Output: ${OUTPUT_DIR}`);
    console.log('');

    // Ensure output directories exist
    ensureDir(OUTPUT_DIR);
    ensureDir(DESKTOP_DIR);
    ensureDir(MOBILE_DIR);

    // Wait for health check
    await waitForHealth();

    // Launch browser
    const browser: Browser = await chromium.launch({
        headless: true,
        args: ['--no-sandbox', '--disable-setuid-sandbox']
    });
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

            // Capture landing (public, no login needed)
            for (const route of ROUTES.landing) {
                const entry = await captureScreenshot(desktopPage, route, DESKTOP_VIEWPORT, DESKTOP_DIR, 'landing');
                if (entry) manifest.push(entry);
            }

            // Login for backoffice
            const backofficeLoggedIn = await loginBackoffice(desktopPage);

            if (backofficeLoggedIn) {
                // Capture backoffice routes
                for (const route of ROUTES.backoffice) {
                    const entry = await captureScreenshot(desktopPage, route, DESKTOP_VIEWPORT, DESKTOP_DIR, 'backoffice');
                    if (entry) manifest.push(entry);
                }
            } else {
                console.error('\n⚠️ Skipping backoffice routes due to login failure');
            }

            // New context for handheld (fresh session)
            await desktopContext.close();

            const handheldDesktopContext: BrowserContext = await browser.newContext({
                viewport: DESKTOP_VIEWPORT,
                ignoreHTTPSErrors: true,
            });
            const handheldDesktopPage: Page = await handheldDesktopContext.newPage();

            // Login for handheld
            const handheldLoggedIn = await loginHandheld(handheldDesktopPage);

            if (handheldLoggedIn) {
                // Capture handheld routes
                for (const route of ROUTES.handheld) {
                    const entry = await captureScreenshot(handheldDesktopPage, route, DESKTOP_VIEWPORT, DESKTOP_DIR, 'handheld');
                    if (entry) manifest.push(entry);
                }
            } else {
                console.error('\n⚠️ Skipping handheld routes due to login failure');
            }

            await handheldDesktopContext.close();
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
                hasTouch: true,
            });
            const mobilePage: Page = await mobileContext.newPage();

            // Capture landing (public)
            for (const route of ROUTES.landing) {
                const entry = await captureScreenshot(mobilePage, route, MOBILE_VIEWPORT, MOBILE_DIR, 'landing');
                if (entry) manifest.push(entry);
            }

            // Login for handheld (main mobile target)
            const mobileLoggedIn = await loginHandheld(mobilePage);

            if (mobileLoggedIn) {
                // Capture handheld routes  
                for (const route of ROUTES.handheld) {
                    const entry = await captureScreenshot(mobilePage, route, MOBILE_VIEWPORT, MOBILE_DIR, 'handheld');
                    if (entry) manifest.push(entry);
                }
            } else {
                console.error('\n⚠️ Skipping mobile handheld due to login failure');
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

        if (manifest.length === 0) {
            console.error('\n⚠️ WARNING: No screenshots were captured!');
            console.error('   Check that the WMS backend is running and credentials are correct.');
            process.exit(1);
        }

    } finally {
        await browser.close();
    }
}

main().catch((err) => {
    console.error('Fatal error:', err);
    process.exit(1);
});
