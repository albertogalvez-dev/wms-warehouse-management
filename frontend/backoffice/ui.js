import { apiGet } from "./api.js";

export function createUi({ apiGet: apiGetFn, getApiBaseUrl, apiBaseUrlLabel, connectionBadge, toastHost, modalHost, modalTitle, modalBody }) {
  const apiGetCall = apiGetFn || apiGet;

  function showToast(type, title, message) {
    const toast = document.createElement("div");
    toast.className = `toast ${type === "ok" ? "toast--ok" : type === "error" ? "toast--error" : ""}`;
    toast.innerHTML = `<div class="toast__title"></div><div class="toast__body"></div>`;
    toast.querySelector(".toast__title").textContent = title;
    toast.querySelector(".toast__body").textContent = message || "";
    toastHost.appendChild(toast);
    setTimeout(() => toast.remove(), 4200);
  }

  function setConnectionBadge(state, label) {
    connectionBadge.className = `badge ${state === "ok" ? "badge--ok" : state === "error" ? "badge--danger" : "badge--muted"}`;
    connectionBadge.textContent = label;
  }

  async function testConnection({ silent = false } = {}) {
    try {
      await apiGetCall("/api/ping");
      setConnectionBadge("ok", "API OK");
      if (!silent) showToast("ok", "Connection", `Connected to ${getApiBaseUrl()}`);
      return true;
    } catch (e) {
      setConnectionBadge("error", "API ERROR");
      if (!silent) showToast("error", "Connection", e.message || "Failed to reach API");
      return false;
    }
  }

  function updateApiLabel() {
    apiBaseUrlLabel.textContent = getApiBaseUrl();
  }

  function openModal({ title, bodyHtml }) {
    modalTitle.textContent = title || "Modal";
    modalBody.innerHTML = bodyHtml || "";
    modalHost.hidden = false;
  }

  function closeModal() {
    modalHost.hidden = true;
    modalBody.innerHTML = "";
  }

  modalHost.addEventListener("click", (e) => {
    if (e.target && e.target.hasAttribute("data-close-modal")) closeModal();
  });

  return {
    showToast,
    toastOk: (title, message) => showToast("ok", title, message),
    toastError: (title, message) => showToast("error", title, message),
    setConnectionBadge,
    testConnection,
    updateApiLabel,
    openModal,
    closeModal,
  };
}

