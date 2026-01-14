import { apiGet } from "../api.js";
import { qs, badgeForOrderStatus, escapeHtml } from "../utils.js";

export async function renderDashboard({ root, ui }) {
  root.innerHTML = `
    <div class="stats-grid">
      <div class="stat-box">
        <div class="stat-box-value" id="kpiOrdersToday">--</div>
        <div class="stat-box-label">Orders Today</div>
      </div>
      <div class="stat-box">
        <div class="stat-box-value" id="kpiOrdersLast7">--</div>
        <div class="stat-box-label">Orders Last 7 Days</div>
      </div>
      <div class="stat-box">
        <div class="stat-box-value" id="kpiLinesPending">--</div>
        <div class="stat-box-label">Lines Pending</div>
      </div>
      <div class="stat-box">
        <div class="stat-box-value" id="kpiLinesPicked">--</div>
        <div class="stat-box-label">Lines Picked</div>
      </div>
      <div class="stat-box">
        <div class="stat-box-value" id="kpiShipmentsPending">--</div>
        <div class="stat-box-label">Shipments Pending</div>
      </div>
    </div>

    <div class="grid grid-3">
      <div class="card">
        <div class="card-header">
          <div>
            <h3 class="card-title">Orders by Status</h3>
            <p class="card-subtitle" id="ordersRangeLabel">Range: last 7 days</p>
          </div>
        </div>
        <div class="card-body">
          <div id="ordersByStatus" class="stack-sm"></div>
        </div>
      </div>

      <div class="card">
        <div class="card-header">
          <div>
            <h3 class="card-title">Shipments Pending</h3>
            <p class="card-subtitle">Open labels by carrier</p>
          </div>
        </div>
        <div class="card-body">
          <div id="shipmentsByCarrier" class="stack-sm"></div>
        </div>
      </div>

      <div class="card">
        <div class="card-header">
          <div>
            <h3 class="card-title">Top Workers</h3>
            <p class="card-subtitle">Picking and packing performance</p>
          </div>
        </div>
        <div class="card-body">
          <div id="workerStats" class="stack-sm"></div>
        </div>
      </div>
    </div>
    
    <div class="card mt-lg">
      <div class="card-header">
        <h3 class="card-title">Quick Actions</h3>
      </div>
      <div class="card-body">
        <p class="card-subtitle mb-md">Start your warehouse operations.</p>
        <div class="button-grid">
          <a class="action-btn action-btn-primary" href="#/orders/new">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"></circle>
              <line x1="12" y1="8" x2="12" y2="16"></line>
              <line x1="8" y1="12" x2="16" y2="12"></line>
            </svg>
            <span>Create Order</span>
          </a>
          <a class="action-btn action-btn-secondary" href="#/waves">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M22 12h-4l-3 9L9 3l-3 9H2"></path>
            </svg>
            <span>Pick Waves</span>
          </a>
          <a class="action-btn action-btn-secondary" href="#/totes">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 16V8a2 2 0 00-1-1.73l-7-4a2 2 0 00-2 0l-7 4A2 2 0 003 8v8a2 2 0 001 1.73l7 4a2 2 0 002 0l7-4A2 2 0 0021 16z"></path>
            </svg>
            <span>Totes</span>
          </a>
          <a class="action-btn action-btn-secondary" href="#/shipments">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="1" y="3" width="15" height="13"></rect>
              <polygon points="16 8 20 8 23 11 23 16 16 16 16 8"></polygon>
              <circle cx="5.5" cy="18.5" r="2.5"></circle>
              <circle cx="18.5" cy="18.5" r="2.5"></circle>
            </svg>
            <span>Shipments</span>
          </a>
          <a class="action-btn action-btn-secondary" href="#/products">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="4" width="18" height="16" rx="2"></rect>
              <path d="M7 8h10M7 12h10M7 16h6"></path>
            </svg>
            <span>Products</span>
          </a>
        </div>
      </div>
    </div>
  `;

  function setText(id, value) {
    const el = qs(id, root);
    if (el) el.textContent = value;
  }

  function formatNumber(value) {
    return value == null ? "0" : String(value);
  }

  function formatPerHour(value) {
    if (value == null || Number.isNaN(value)) return "0.0";
    return Number(value).toFixed(1);
  }

  try {
    const [orderStats, workerStats] = await Promise.all([
      apiGet("/api/stats/orders"),
      apiGet("/api/stats/workers"),
    ]);

    setText("#kpiOrdersToday", formatNumber(orderStats.ordersToday));
    setText("#kpiOrdersLast7", formatNumber(orderStats.ordersLast7Days));
    setText("#kpiLinesPending", formatNumber(orderStats.linesPending));
    setText("#kpiLinesPicked", formatNumber(orderStats.linesPicked));
    setText("#kpiShipmentsPending", formatNumber(orderStats.shipmentsPendingTotal));

    if (orderStats.rangeStart && orderStats.rangeEnd) {
      setText("#ordersRangeLabel", `Range: ${orderStats.rangeStart} to ${orderStats.rangeEnd}`);
    }

    const statusHost = qs("#ordersByStatus", root);
    const statusOrder = ["DRAFT", "RELEASED", "PICKING", "PICKED", "PACKING", "PACKED", "SHIPPED", "CANCELLED"];
    const statusRows = statusOrder.map((status) => {
      const count = orderStats.ordersByStatus?.[status] ?? 0;
      return `
        <div class="split">
          ${badgeForOrderStatus(status)}
          <span class="font-mono">${count}</span>
        </div>
      `;
    });
    statusHost.innerHTML = statusRows.join("");

    const shipmentHost = qs("#shipmentsByCarrier", root);
    const carriers = ["DHL", "GLS", "TDN", "CORREOS"];
    const shipmentRows = carriers.map((carrier) => {
      const count = orderStats.shipmentsPendingByCarrier?.[carrier] ?? 0;
      return `
        <div class="split">
          <span class="font-medium">${carrier}</span>
          <span class="font-mono">${count}</span>
        </div>
      `;
    });
    shipmentHost.innerHTML = shipmentRows.join("");

    const workerHost = qs("#workerStats", root);
    const pickers = workerStats.picking || [];
    const packers = workerStats.packing || [];
    const pickerRows = pickers.length
      ? pickers.slice(0, 4).map((p) => `
          <div class="split">
            <span class="font-medium">${escapeHtml(p.operator || "-")}</span>
            <span class="font-mono">${formatNumber(p.linesPicked)} lines | ${formatPerHour(p.picksPerHour)}/h</span>
          </div>
        `).join("")
      : `<div class="text-muted small">No picking activity in range.</div>`;

    const packerRows = packers.length
      ? packers.slice(0, 4).map((p) => `
          <div class="split">
            <span class="font-medium">${escapeHtml(p.operator || "-")}</span>
            <span class="font-mono">${formatNumber(p.linesPacked)} lines | ${formatPerHour(p.packsPerHour)}/h</span>
          </div>
        `).join("")
      : `<div class="text-muted small">No packing activity in range.</div>`;

    workerHost.innerHTML = `
      <div class="text-muted small">Picking</div>
      ${pickerRows}
      <div class="divider"></div>
      <div class="text-muted small">Packing</div>
      ${packerRows}
    `;
  } catch (e) {
    setText("#kpiOrdersToday", "--");
    setText("#kpiOrdersLast7", "--");
    setText("#kpiLinesPending", "--");
    setText("#kpiLinesPicked", "--");
    setText("#kpiShipmentsPending", "--");
    qs("#ordersByStatus", root).innerHTML = `<div class="text-muted">Error: ${e.message}</div>`;
    qs("#shipmentsByCarrier", root).innerHTML = `<div class="text-muted">Error: ${e.message}</div>`;
    qs("#workerStats", root).innerHTML = `<div class="text-muted">Error: ${e.message}</div>`;
  }
}
