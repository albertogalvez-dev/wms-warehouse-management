import { apiGet, apiPost, apiPut } from "../api.js";
import { getUser } from "../../shared/auth.js";

const ROLE_META = {
    ADMIN: { label: "ADMIN", desc: "Full system access" },
    MANAGER: { label: "MANAGER", desc: "Operations and inventory" },
    PICKER: { label: "OPERATOR", desc: "Picking workflow" },
    PACKER: { label: "OPERATOR", desc: "Packing workflow" }
};

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
        const currentUser = getUser();

        if (!users || users.length === 0) {
            listEl.innerHTML = '<div class="text-muted">No users found</div>';
            return;
        }

        listEl.innerHTML = users.map(user => {
            const meta = ROLE_META[user.role] || { label: user.role, desc: "User role" };
            const statusBadge = user.active
                ? '<span class="badge badge-ok">Active</span>'
                : '<span class="badge badge-neutral">Inactive</span>';
            const selfLabel = currentUser?.username === user.username ? " (you)" : "";
            return `
            <div class="user-item ${user.active ? "" : "user-item-inactive"}">
                <div class="user-info">
                    <div class="user-avatar">${user.username.charAt(0).toUpperCase()}</div>
                    <div class="user-details">
                        <strong>${escapeHtml(user.username)}${selfLabel}</strong>
                        <small>Role: ${meta.label} - ${meta.desc}</small>
                        <small>Created: ${formatDate(user.createdAt)}</small>
                    </div>
                </div>
                <div class="user-actions">
                    <div class="user-badges">
                        <span class="role-badge role-${user.role}">${meta.label}</span>
                        ${statusBadge}
                    </div>
                    <button class="btn btn-outline btn-sm" data-edit-user="${user.id}">Edit</button>
                </div>
            </div>
        `;
        }).join('');

        listEl.querySelectorAll("[data-edit-user]").forEach((btn) => {
            btn.addEventListener("click", () => {
                const id = Number(btn.getAttribute("data-edit-user"));
                const user = users.find((item) => item.id === id);
                if (!user) return;
                openEditModal(user, root, ui);
            });
        });
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
            ui.toastError("Missing fields", "Please fill all fields");
            return;
        }

        try {
            await apiPost("/api/users", { username, password, role });
            ui.toastOk("User created", `${username} added`);
            form.reset();
            await loadUsers(root, ui);
        } catch (err) {
            ui.toastError("Create failed", err.message);
        }
    });
}

function openEditModal(user, root, ui) {
    const roleOptions = Object.keys(ROLE_META).map((role) => {
        const label = ROLE_META[role]?.label || role;
        return `<option value="${role}" ${user.role === role ? "selected" : ""}>${label}</option>`;
    }).join("");

    const bodyHtml = `
      <form id="editUserForm">
        <div class="form-group">
          <label class="label">Username</label>
          <input class="input" value="${escapeHtml(user.username)}" disabled />
        </div>
        <div class="form-group">
          <label class="label" for="editRole">Role</label>
          <select id="editRole" class="select">${roleOptions}</select>
        </div>
        <div class="form-group">
          <label class="label" for="editPassword">New Password (optional)</label>
          <input id="editPassword" class="input" type="password" minlength="6" placeholder="Leave blank to keep" />
        </div>
        <div class="row row--start">
          <input type="checkbox" id="editActive" ${user.active ? "checked" : ""} />
          <label class="label" for="editActive" style="margin:0">Active user</label>
        </div>
      </form>
    `;

    const footerHtml = `
      <button type="button" class="btn btn-ghost" data-close-modal>Cancel</button>
      <button type="submit" class="btn btn-primary" form="editUserForm">Save</button>
    `;

    ui.openModal({ title: `Edit User - ${user.username}`, bodyHtml, footerHtml });

    const form = document.getElementById("editUserForm");
    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const payload = {
            role: document.getElementById("editRole").value,
            active: document.getElementById("editActive").checked
        };

        const password = document.getElementById("editPassword").value.trim();
        if (password) payload.password = password;

        try {
            await apiPut(`/api/users/${user.id}`, payload);
            ui.toastOk("User updated", user.username);
            ui.closeModal();
            await loadUsers(root, ui);
        } catch (err) {
            ui.toastError("Update failed", err.message);
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
