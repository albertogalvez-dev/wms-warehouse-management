export function qs(sel, root = document) {
  return root.querySelector(sel);
}

export function qsa(sel, root = document) {
  return Array.from(root.querySelectorAll(sel));
}

export function formatDateTime(value) {
  if (!value) return "";
  const d = new Date(value);
  if (Number.isNaN(d.getTime())) return String(value);
  return d.toLocaleString();
}

export function badgeForOrderStatus(status) {
  const s = String(status || "");
  const map = {
    DRAFT: "badge--muted",
    RELEASED: "badge--warn",
    PICKING: "badge--warn",
    PICKED: "badge--ok",
    PACKING: "badge--warn",
    PACKED: "badge--ok",
    SHIPPED: "badge--ok",
    CANCELLED: "badge--danger",
  };
  return `<span class="badge ${map[s] || "badge--muted"}">${s || "-"}</span>`;
}

export function badgeForWaveStatus(status) {
  const s = String(status || "");
  const map = { PLANNED: "badge--muted", IN_PROGRESS: "badge--warn", DONE: "badge--ok", CANCELLED: "badge--danger" };
  return `<span class="badge ${map[s] || "badge--muted"}">${s || "-"}</span>`;
}

export function badgeForToteStatus(status) {
  const s = String(status || "");
  const map = { OPEN: "badge--muted", AT_PACKING: "badge--warn", CLOSED: "badge--ok" };
  return `<span class="badge ${map[s] || "badge--muted"}">${s || "-"}</span>`;
}

export function escapeHtml(str) {
  return String(str).replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
}

export function downloadText(filename, content) {
  const blob = new Blob([content], { type: "text/plain" });
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  a.remove();
  URL.revokeObjectURL(url);
}

