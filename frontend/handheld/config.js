// In production (behind nginx), use empty string for same-origin API calls
// In development (localhost:8080), use full URL
function detectDefaultApiUrl() {
    const port = window.location.port;
    const path = window.location.pathname || "";
    const basePath = path === "/wms" || path.startsWith("/wms/") ? "/wms" : "";
    // If running on port 80/443 or no port, assume production (nginx proxy)
    if (!port || port === "80" || port === "443") {
        return basePath; // Relative path: /api/... or /wms/api/...
    }
    // Development: direct backend access
    return "http://localhost:8080";
}

export const DEFAULT_API_BASE_URL = detectDefaultApiUrl();

const STORAGE_KEY = "wms_handheld_api_base_url";

export function getApiBaseUrl() {
    const stored = localStorage.getItem(STORAGE_KEY);
    return stored || DEFAULT_API_BASE_URL;
}

export function saveApiBaseUrl(url) {
    if (!url) {
        localStorage.removeItem(STORAGE_KEY);
        return;
    }
    localStorage.setItem(STORAGE_KEY, url);
}
