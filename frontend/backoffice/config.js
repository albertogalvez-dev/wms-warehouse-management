// In production (behind nginx), use empty string for same-origin API calls
// In development (localhost:8080), use full URL
function detectDefaultApiUrl() {
  const host = window.location.hostname;
  const port = window.location.port;
  // If running on port 80/443 or no port, assume production (nginx proxy)
  if (!port || port === "80" || port === "443") {
    return ""; // Relative path: /api/...
  }
  // Development: direct backend access
  return "http://localhost:8080";
}

export const DEFAULT_API_BASE_URL = detectDefaultApiUrl();

const STORAGE_KEY = "wms_backoffice_api_base_url";

export function normalizeBaseUrl(value) {
  if (!value) return "";
  return value.trim().replace(/\/+$/, "");
}

export function getApiBaseUrl() {
  const stored = normalizeBaseUrl(localStorage.getItem(STORAGE_KEY) || "");
  return stored || DEFAULT_API_BASE_URL;
}

export function setApiBaseUrl(value) {
  const normalized = normalizeBaseUrl(value);
  if (!normalized) {
    localStorage.removeItem(STORAGE_KEY);
    return DEFAULT_API_BASE_URL;
  }
  localStorage.setItem(STORAGE_KEY, normalized);
  return normalized;
}

export function getStoredApiBaseUrlRaw() {
  return normalizeBaseUrl(localStorage.getItem(STORAGE_KEY) || "");
}

