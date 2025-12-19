export const DEFAULT_API_BASE_URL = "http://localhost:8080";

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
