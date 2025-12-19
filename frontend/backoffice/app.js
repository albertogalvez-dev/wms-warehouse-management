import { apiGet } from "./api.js";
import { getApiBaseUrl } from "./config.js";
import { createRouter } from "./router.js";
import { createUi } from "./ui.js";

import { renderDashboard } from "./pages/dashboard.js";
import { renderOrdersList, renderOrderCreate, renderOrderDetail } from "./pages/orders.js";
import { renderWavesList, renderWaveDetail } from "./pages/waves.js";
import { renderTotes } from "./pages/totes.js";
import { renderShipments } from "./pages/shipments.js";
import { renderSettings } from "./pages/settings.js";

const appMain = document.getElementById("appMain");

const ui = createUi({
  apiGet,
  getApiBaseUrl,
  apiBaseUrlLabel: document.getElementById("apiBaseUrlLabel"),
  connectionBadge: document.getElementById("connectionBadge"),
  toastHost: document.getElementById("toastHost"),
  modalHost: document.getElementById("modalHost"),
  modalTitle: document.getElementById("modalTitle"),
  modalBody: document.getElementById("modalBody"),
});

function setPageTitle(title) {
  document.getElementById("pageTitle").textContent = title;
  document.title = `WMS Backoffice — ${title}`;
}

function setNavActive(path) {
  document.querySelectorAll(".nav__link").forEach((a) => {
    const route = a.getAttribute("data-route");
    a.classList.toggle("is-active", route === path);
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
];

const router = createRouter({
  routes,
  onRoute: async ({ route, params, query, path }) => {
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
ui.testConnection({ silent: true });
router.start();
