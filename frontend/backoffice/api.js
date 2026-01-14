import { getApiBaseUrl } from "./config.js";
import { getToken } from "../shared/auth.js";

export class ApiError extends Error {
  constructor(message, { status = 0, details = null } = {}) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.details = details;
  }
}

function buildUrl(path) {
  const base = getApiBaseUrl();
  const trimmedPath = path.startsWith("/") ? path : `/${path}`;
  return `${base}${trimmedPath}`;
}

async function parseErrorResponse(response) {
  const contentType = response.headers.get("content-type") || "";
  if (contentType.includes("application/json")) {
    const data = await response.json().catch(() => null);
    if (data && typeof data === "object") return data;
  }
  const text = await response.text().catch(() => "");
  return text ? { message: text } : null;
}

async function request(method, path, { body = null, headers = {}, accept = "application/json" } = {}) {
  const url = buildUrl(path);
  const token = getToken();
  try {
    const res = await fetch(url, {
      method,
      headers: {
        Accept: accept,
        ...(body ? { "Content-Type": "application/json" } : {}),
        ...(token ? { "Authorization": `Bearer ${token}` } : {}),
        ...headers,
      },
      body: body ? JSON.stringify(body) : null,
    });

    if (!res.ok) {
      const errBody = await parseErrorResponse(res);
      const message = errBody?.message || `${res.status} ${res.statusText}`;
      throw new ApiError(message, { status: res.status, details: errBody });
    }

    if (accept === "text/plain") {
      return await res.text();
    }

    const contentType = res.headers.get("content-type") || "";
    if (contentType.includes("application/json")) {
      return await res.json();
    }
    return await res.text();
  } catch (e) {
    if (e instanceof ApiError) throw e;
    throw new ApiError("Server unreachable", { status: 0, details: e?.message || String(e) });
  }
}

export function apiGet(path) {
  return request("GET", path);
}

export function apiGetText(path) {
  return request("GET", path, { accept: "text/plain" });
}

export function apiPost(path, body) {
  return request("POST", path, { body });
}

export function apiPut(path, body) {
  return request("PUT", path, { body });
}

export function apiDelete(path) {
  return request("DELETE", path);
}

