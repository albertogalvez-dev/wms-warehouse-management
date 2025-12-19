import { apiGet, apiPost } from "../api.js";

export async function renderAdminUsers({ ui, root }) {
    await loadUsers(root, ui);
    setupCreateForm(root, ui);
}

async function loadUsers(root, ui) {
    const listEl = root.querySelector("#usersList");
    if (!listEl) return;

    listEl.innerHTML = '<div class="text-muted">Loading users...</div>';

    try {
        const users = await apiGet("/api/users");

        if (!users || users.length === 0) {
            listEl.innerHTML = '<div class="text-muted">No users found</div>';
            return;
        }

        listEl.innerHTML = users.map(user => `
            <div class="user-item">
                <div class="user-info">
                    <div class="user-avatar">${user.username.charAt(0).toUpperCase()}</div>
                    <div class="user-details">
                        <strong>${escapeHtml(user.username)}</strong>
                        <small>Created: ${formatDate(user.createdAt)}</small>
                    </div>
                </div>
                <span class="role-badge role-${user.role}">${user.role}</span>
            </div>
        `).join('');
    } catch (err) {
        listEl.innerHTML = `<div class="text-muted">Error loading users: ${err.message}</div>`;
    }
}

function setupCreateForm(root, ui) {
    const form = root.querySelector("#createUserForm");
    if (!form) return;

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const username = form.querySelector("#newUsername").value.trim();
        const password = form.querySelector("#newPassword").value;
        const role = form.querySelector("#newRole").value;

        if (!username || !password) {
            ui.toast("Please fill all fields", "error");
            return;
        }

        try {
            await apiPost("/api/users", { username, password, role });
            ui.toast(`User "${username}" created successfully`, "success");
            form.reset();
            await loadUsers(root, ui);
        } catch (err) {
            ui.toast(`Error: ${err.message}`, "error");
        }
    });
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function formatDate(dateStr) {
    if (!dateStr) return 'N/A';
    try {
        return new Date(dateStr).toLocaleDateString();
    } catch {
        return dateStr;
    }
}
