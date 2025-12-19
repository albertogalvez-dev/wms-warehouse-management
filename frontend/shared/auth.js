/**
 * Authentication module for WMS frontends
 * Handles JWT token storage and login state
 */

const TOKEN_KEY = 'wms_token';
const USER_KEY = 'wms_user';

/**
 * Store token and user info after successful login
 */
export function setAuth(token, user) {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(USER_KEY, JSON.stringify(user));
}

/**
 * Get stored token
 */
export function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

/**
 * Get stored user info
 */
export function getUser() {
    try {
        const data = localStorage.getItem(USER_KEY);
        return data ? JSON.parse(data) : null;
    } catch {
        return null;
    }
}

/**
 * Check if user is authenticated
 */
export function isAuthenticated() {
    const token = getToken();
    if (!token) return false;

    // Check if token is expired (simple check)
    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const exp = payload.exp * 1000; // Convert to ms
        return Date.now() < exp;
    } catch {
        return false;
    }
}

/**
 * Clear auth data (logout)
 */
export function logout() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
}

/**
 * Login with username and password
 */
export async function login(apiBaseUrl, username, password) {
    const response = await fetch(`${apiBaseUrl}/auth/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ username, password })
    });

    if (!response.ok) {
        const error = await response.json().catch(() => ({ error: 'Login failed' }));
        throw new Error(error.error || error.message || 'Invalid credentials');
    }

    const data = await response.json();
    setAuth(data.token, { username: data.username, role: data.role });
    return data;
}

/**
 * Get authorization header for API calls
 */
export function getAuthHeader() {
    const token = getToken();
    return token ? { 'Authorization': `Bearer ${token}` } : {};
}
