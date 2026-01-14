import { apiGet } from "./api.js";

export function createUi({
  apiGet: apiGetFn,
  getApiBaseUrl,
  apiBaseUrlLabel,
  connectionBadge,
  toastHost,
  modalHost,
  modalTitle,
  modalBody,
  modalFooter
}) {
  const apiGetCall = apiGetFn || apiGet;
  const defaultFooter = modalFooter ? modalFooter.innerHTML : "";

  function showToast(type, title, message) {
    const toast = document.createElement("div");
    const variant =
      type === "ok" ? "toast-ok" :
        type === "error" ? "toast-err" :
          type === "warn" ? "toast-warn" : "";
    toast.className = `toast ${variant}`.trim();
    toast.innerHTML = `<div class="toast__title"></div><div class="toast__body"></div>`;
    toast.querySelector(".toast__title").textContent = title;
    toast.querySelector(".toast__body").textContent = message || "";
    toastHost.appendChild(toast);
    setTimeout(() => toast.remove(), 4200);
  }

  function setConnectionBadge(state, label, title) {
    if (!connectionBadge) return;
    const css =
      state === "ok" ? "badge-ok" :
        state === "error" ? "badge-err" :
          state === "checking" ? "badge-info" : "badge-neutral";
    connectionBadge.className = `badge ${css}`.trim();
    connectionBadge.textContent = label;
    if (title) connectionBadge.title = title;
  }

  async function testConnection({ silent = false } = {}) {
    setConnectionBadge("checking", "API Check", "Health check: GET /api/ping");
    try {
      await apiGetCall("/api/ping");
      setConnectionBadge("ok", "API OK", "Health check: GET /api/ping");
      if (!silent) showToast("ok", "Connection", `Connected to ${getApiBaseUrl()}`);
      return true;
    } catch (e) {
      setConnectionBadge("error", "API Down", "Health check: GET /api/ping");
      if (!silent) showToast("error", "Connection", e.message || "Failed to reach API");
      return false;
    }
  }

  function updateApiLabel() {
    if (!apiBaseUrlLabel) return;
    apiBaseUrlLabel.textContent = getApiBaseUrl();
  }

  function openModal({ title, bodyHtml, footerHtml }) {
    modalTitle.textContent = title || "Modal";
    modalBody.innerHTML = bodyHtml || "";
    if (modalFooter) {
      modalFooter.innerHTML = footerHtml || defaultFooter;
    }
    modalHost.classList.remove("hidden");
    modalHost.hidden = false;
  }

  function closeModal() {
    modalHost.hidden = true;
    modalHost.classList.add("hidden");
    modalBody.innerHTML = "";
    if (modalFooter) {
      modalFooter.innerHTML = defaultFooter;
    }
  }

  modalHost.addEventListener("click", (e) => {
    if (e.target && e.target.hasAttribute("data-close-modal")) closeModal();
  });

  let connectionTimer = null;
  function startConnectionMonitor({ intervalMs = 30000 } = {}) {
    if (connectionTimer) clearInterval(connectionTimer);
    connectionTimer = setInterval(() => testConnection({ silent: true }), intervalMs);
  }

  return {
    showToast,
    toastOk: (title, message) => showToast("ok", title, message),
    toastError: (title, message) => showToast("error", title, message),
    setConnectionBadge,
    testConnection,
    startConnectionMonitor,
    updateApiLabel,
    openModal,
    closeModal,
  };
}
