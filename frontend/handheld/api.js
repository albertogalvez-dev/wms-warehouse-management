import { getApiBaseUrl } from "./config.js";
import { getToken } from "../shared/auth.js";

export class ApiError extends Error {
    constructor(message, status) {
        super(message);
        this.status = status;
    }
}

async function request(method, path, body = null) {
    const baseUrl = getApiBaseUrl();
    const url = `${baseUrl}${path}`;
    const token = getToken();

    const options = {
        method,
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json",
            ...(token ? { "Authorization": `Bearer ${token}` } : {})
        }
    };

    if (body) {
        options.body = JSON.stringify(body);
    }

    try {
        const response = await fetch(url, options);

        if (!response.ok) {
            // Try to parse error message from JSON
            let errorMessage = `Error ${response.status}`;
            try {
                const errorData = await response.json();
                if (errorData.message) errorMessage = errorData.message;
                else if (errorData.error) errorMessage = errorData.error;
            } catch (e) {
                // Fallback to text if not JSON
                const text = await response.text();
                if (text) errorMessage = text;
            }
            throw new ApiError(errorMessage, response.status);
        }

        // Handle empty responses
        const text = await response.text();
        return text ? JSON.parse(text) : {};

    } catch (error) {
        if (error instanceof ApiError) throw error;
        throw new Error(`Network error: ${error.message}`);
    }
}

export const api = {
    get: (path) => request("GET", path),
    post: (path, body) => request("POST", path, body),
    put: (path, body) => request("PUT", path, body),
};
