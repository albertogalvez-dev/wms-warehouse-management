export const DEFAULT_API_BASE_URL = "http://localhost:8080";

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

