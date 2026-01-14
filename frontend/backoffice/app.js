import { apiGet } from "./api.js";
import { getApiBaseUrl } from "./config.js";
import { createRouter } from "./router.js";
import { createUi } from "./ui.js";
import { isAuthenticated, getUser, logout } from "../shared/auth.js";

import { renderDashboard } from "./pages/dashboard.js";
import { renderOrdersList, renderOrderCreate, renderOrderDetail } from "./pages/orders.js";
import { renderProducts } from "./pages/products.js";
import { renderWavesList, renderWaveDetail } from "./pages/waves.js";
import { renderTotes } from "./pages/totes.js";
import { renderShipments } from "./pages/shipments.js";
import { renderSettings } from "./pages/settings.js";
import { renderAdminUsers } from "./pages/admin_users.js";

// Auth check - redirect to login if not authenticated
if (!isAuthenticated()) {
  window.location.href = "./login.html";
  throw new Error("Not authenticated");
}

const currentUser = getUser();
const isAdmin = currentUser?.role === "ADMIN";

// Setup after DOM is ready
document.addEventListener("DOMContentLoaded", () => {
  // Display user info
  const user = currentUser;
  if (user) {
    const userDisplay = document.getElementById("userDisplay");
    if (userDisplay) {
      userDisplay.textContent = `${user.username} (${user.role})`;
    }
  }

  const adminSection = document.getElementById("adminSection");
  if (adminSection && !isAdmin) {
    adminSection.classList.add("hidden");
  }

  document.querySelectorAll(".nav-link").forEach((link) => {
    link.addEventListener("click", () => setSidebarOpen(false));
  });
});

// Logout handler - must be on window for onclick attribute
window.handleLogout = function () {
  logout();
  window.location.href = "./login.html";
};

const sidebar = document.querySelector(".sidebar");
const sidebarToggle = document.getElementById("sidebarToggle");
const sidebarOverlay = document.getElementById("sidebarOverlay");

function setSidebarOpen(open) {
  if (!sidebar) return;
  sidebar.classList.toggle("open", open);
  if (sidebarOverlay) {
    sidebarOverlay.classList.toggle("active", open);
    sidebarOverlay.setAttribute("aria-hidden", open ? "false" : "true");
  }
  if (sidebarToggle) {
    sidebarToggle.setAttribute("aria-expanded", open ? "true" : "false");
  }
}

function toggleSidebar() {
  if (!sidebar) return;
  setSidebarOpen(!sidebar.classList.contains("open"));
}

sidebarToggle?.addEventListener("click", toggleSidebar);
sidebarOverlay?.addEventListener("click", () => setSidebarOpen(false));

const appMain = document.getElementById("appMain");

const ui = createUi({
  apiGet,
  getApiBaseUrl,
  apiBaseUrlLabel: document.getElementById("apiBaseUrlLabel"),
  toastHost: document.getElementById("toastHost"),
  modalHost: document.getElementById("modalHost"),
  modalTitle: document.getElementById("modalTitle"),
  modalBody: document.getElementById("modalBody"),
  modalFooter: document.getElementById("modalFooter"),
});

function setPageTitle(title) {
  document.getElementById("pageTitle").textContent = title;
  document.title = `WMS Backoffice - ${title}`;
}

function setNavActive(path) {
  document.querySelectorAll(".nav-link").forEach((a) => {
    const route = a.getAttribute("data-route");
    a.classList.toggle("active", route === path);
  });
}

async function loadPageHtml(path) {
  const res = await fetch(path, { cache: "no-store" });
  if (!res.ok) throw new Error(`Failed to load page: ${path}`);
  return await res.text();
}

const routes = [
  { path: "/dashboard", page: "./pages/dashboard.html", title: "Dashboard", handler: renderDashboard },
  { path: "/settings", page: "./pages/settings.html", title: "Settings", handler: renderSettings },

  { path: "/orders", page: "./pages/orders.html", title: "Orders", handler: renderOrdersList },
  { path: "/products", page: "./pages/products.html", title: "Products", handler: renderProducts },
  { path: "/orders/new", page: "./pages/order_create.html", title: "Create Order", handler: renderOrderCreate },
  {
    pattern: /^\/orders\/(\d+)$/,
    page: "./pages/order_detail.html",
    title: "Order Detail",
    getParams: (m) => ({ id: m[1] }),
    nav: "/orders",
    handler: renderOrderDetail,
  },

  { path: "/waves", page: "./pages/waves.html", title: "Pick Waves", handler: renderWavesList },
  {
    pattern: /^\/waves\/(\d+)$/,
    page: "./pages/wave_detail.html",
    title: "Wave Detail",
    getParams: (m) => ({ id: m[1] }),
    nav: "/waves",
    handler: renderWaveDetail,
  },

  { path: "/totes", page: "./pages/totes.html", title: "Totes", handler: renderTotes },
  { path: "/shipments", page: "./pages/shipments.html", title: "Shipments", handler: renderShipments },

  // Guide
  { path: "/guide", page: "./pages/guide.html", title: "Quick Start Guide", handler: async () => {} },

  // Admin routes
  { path: "/admin/users", page: "./pages/admin_users.html", title: "User Management", handler: renderAdminUsers, requiresRole: "ADMIN" },
];

const router = createRouter({
  routes,
  onRoute: async ({ route, params, query, path }) => {
    if (route.requiresRole && currentUser?.role !== route.requiresRole) {
      ui.toastError("Access denied", "Admin only");
      window.location.hash = "#/dashboard";
      return;
    }

    setSidebarOpen(false);
    ui.updateApiLabel();
    setPageTitle(route.title);
    setNavActive(route.nav || route.path || path);

    try {
      appMain.innerHTML = await loadPageHtml(route.page);
    } catch {
      appMain.innerHTML = "";
    }

    await route.handler({ params, query, path, ui, root: appMain });
  },
});

ui.updateApiLabel();
router.start();
