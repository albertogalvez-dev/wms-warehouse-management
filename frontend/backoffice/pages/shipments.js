import { apiGet, apiGetText } from "../api.js";
import { qs, qsa, escapeHtml, formatDateTime, downloadText } from "../utils.js";

const CARRIER_META = {
  DHL: { label: "DHL", logo: "../assets/carriers/dhl.svg" },
  GLS: { label: "GLS", logo: "../assets/carriers/gls.svg" },
  TDN: { label: "TDN", logo: "../assets/carriers/tdn.svg" },
  CORREOS: { label: "CORREOS", logo: "../assets/carriers/correos.svg" },
};

function carrierBadge(carrier) {
  const meta = CARRIER_META[carrier] || { label: carrier || "Unknown", logo: "" };
  const logo = meta.logo ? `<img src="${meta.logo}" alt="${meta.label} logo" />` : "";
  return `<span class="carrier-badge">${logo}${escapeHtml(meta.label)}</span>`;
}

function statusBadge(status) {
  const s = String(status || "");
  const map = { CREATED: "badge--muted", LABELLED: "badge--warn", PRINTED: "badge--ok" };
  return `<span class="badge ${map[s] || "badge--muted"}">${escapeHtml(s || "-")}</span>`;
}

export async function renderShipments({ root, ui }) {
  root.innerHTML = `
    <div class="grid grid-2">
      <div class="card">
        <div class="card-header">
          <div>
            <h3 class="card-title">Shipments</h3>
            <p class="card-subtitle">Demo shipments with carrier badges and labels.</p>
          </div>
          <div class="shipment-actions">
            <button class="btn btn-outline btn-sm" id="btnShipmentsRefresh">Refresh</button>
          </div>
        </div>
        <div class="card-body">
          <div class="filters-bar">
            <div class="filter-group">
              <label class="label">Search</label>
              <input id="shipmentQuery" class="input" placeholder="ID, order ref, carrier, city..." />
            </div>
            <div class="filter-group">
              <label class="label">Status</label>
              <select id="shipmentStatus" class="select">
                <option value="all">All</option>
                <option value="CREATED">CREATED</option>
                <option value="LABELLED">LABELLED</option>
                <option value="PRINTED">PRINTED</option>
              </select>
            </div>
          </div>
          <div id="shipmentsTable" class="table-wrap"></div>
        </div>
      </div>

      <div id="shipmentDetail" class="card">
        <div class="card-header">
          <h3 class="card-title">Shipment Detail</h3>
        </div>
        <div class="card-body shipment-detail-empty">Select a shipment to see packages and labels.</div>
      </div>
    </div>
  `;

  const state = {
    data: [],
  };

  async function loadShipments() {
    const tableHost = qs("#shipmentsTable", root);
    tableHost.innerHTML = `<div class="text-muted">Loading shipments...</div>`;
    try {
      const data = await apiGet("/api/shipments?page=0&size=50&sort=createdAt,desc");
      state.data = data.content || [];
      renderList();
    } catch (e) {
      tableHost.innerHTML = `<div class="text-muted">Error: ${e.message}</div>`;
    }
  }

  function renderList() {
    const query = qs("#shipmentQuery", root).value.trim().toLowerCase();
    const statusFilter = qs("#shipmentStatus", root).value;
    const rows = (state.data || []).filter((s) => {
      const matchesStatus = statusFilter === "all" || s.status === statusFilter;
      if (!query) return matchesStatus;
      const haystack = [
        s.id,
        s.orderId,
        s.externalRef,
        s.carrier,
        s.destinationCity,
        s.destinationCountry,
      ]
        .filter(Boolean)
        .join(" ")
        .toLowerCase();
      return matchesStatus && haystack.includes(query);
    });

    const tableHost = qs("#shipmentsTable", root);
    tableHost.innerHTML = `
      <table class="table shipment-table">
        <thead>
          <tr>
            <th>Shipment</th>
            <th>Order</th>
            <th>Carrier</th>
            <th>Status</th>
            <th>Created</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          ${rows
            .map((s) => {
              const orderRef = s.externalRef || `#${s.orderId}`;
              const destination = s.destinationCity ? ` - ${s.destinationCity}` : "";
              return `
                <tr>
                  <td class="font-mono">#${escapeHtml(s.id)}</td>
                  <td>
                    <div class="font-mono">${escapeHtml(orderRef)}</div>
                    <div class="text-muted" style="font-size:0.8125rem">${escapeHtml(destination)}</div>
                  </td>
                  <td>${carrierBadge(s.carrier)}</td>
                  <td>${statusBadge(s.status)}</td>
                  <td>${formatDateTime(s.createdAt)}</td>
                  <td><button class="btn btn-outline btn-sm" data-view-shipment="${s.id}">View</button></td>
                </tr>
              `;
            })
            .join("") || `<tr><td colspan="6" class="text-muted">No shipments found</td></tr>`}
        </tbody>
      </table>
    `;

    qsa("[data-view-shipment]", root).forEach((btn) => {
      btn.addEventListener("click", async () => {
        const id = btn.getAttribute("data-view-shipment");
        await loadShipmentDetail(id);
      });
    });
  }

  async function loadShipmentDetail(id) {
    const detailHost = qs("#shipmentDetail", root);
    detailHost.innerHTML = `
      <div class="card-header">
        <h3 class="card-title">Shipment Detail</h3>
      </div>
      <div class="card-body">
        <div class="text-muted">Loading shipment...</div>
      </div>
    `;
    try {
      const shipment = await apiGet(`/api/shipments/${encodeURIComponent(id)}`);
      renderShipmentDetail(shipment);
    } catch (e) {
      detailHost.innerHTML = `
        <div class="card-header">
          <h3 class="card-title">Shipment Detail</h3>
        </div>
        <div class="card-body text-err">Error: ${e.message}</div>
      `;
    }
  }

  function renderShipmentDetail(shipment) {
    const rows = (shipment.packages || []).map(
      (p) => `
        <tr>
          <td class="font-mono">${escapeHtml(p.id)}</td>
          <td class="font-mono">${escapeHtml(p.packageNo)}/${escapeHtml(p.packageCount)}</td>
          <td class="font-mono">${escapeHtml(p.trackingCode)}</td>
          <td class="font-mono">${escapeHtml(p.labelFormat)}</td>
          <td class="font-mono">${escapeHtml(p.printedAt || "")}</td>
          <td>
            <div class="shipment-actions">
              <button class="btn btn-outline btn-sm" data-view-zpl="${p.id}">View ZPL</button>
              <button class="btn btn-ghost btn-sm" data-download-zpl="${p.id}">Download</button>
            </div>
          </td>
        </tr>
      `
    );

    const detailHost = qs("#shipmentDetail", root);
    const orderRef = shipment.externalRef || `#${shipment.orderId}`;
    detailHost.innerHTML = `
      <div class="card-header">
        <div>
          <h3 class="card-title">Shipment <span class="font-mono">#${escapeHtml(shipment.id)}</span></h3>
          <p class="card-subtitle">Order ${escapeHtml(orderRef)}</p>
        </div>
        <div class="shipment-meta">
          ${carrierBadge(shipment.carrier)}
          ${statusBadge(shipment.status)}
        </div>
      </div>
      <div class="card-body">
        ${shipment.printError
          ? `<div class="badge badge--danger">Printer: ${escapeHtml(shipment.printError)}</div><div class="divider"></div>`
          : ""
        }
        <div class="row" style="gap: var(--space-md); margin-bottom: var(--space-md)">
          <div class="text-muted">Created</div>
          <div class="font-mono">${formatDateTime(shipment.createdAt)}</div>
        </div>
        <div class="table-wrap">
          <table class="table">
            <thead>
              <tr>
                <th>Package ID</th>
                <th>No</th>
                <th>Tracking</th>
                <th>Format</th>
                <th>Printed</th>
                <th></th>
              </tr>
            </thead>
            <tbody>${rows.join("") || `<tr><td colspan="6" class="text-muted">No packages</td></tr>`}</tbody>
          </table>
        </div>
      </div>
    `;

    qsa("[data-view-zpl]", root).forEach((btn) => {
      btn.addEventListener("click", async () => {
        const pkgId = btn.getAttribute("data-view-zpl");
        try {
          const zpl = await apiGetText(`/api/shipments/${shipment.id}/packages/${pkgId}/label.zpl`);
          ui.openModal({
            title: `ZPL - ${shipment.id}/${pkgId}`,
            bodyHtml: `<div class="row"><button class="btn btn-outline btn-sm" id="btnCopyZpl">Copy</button></div>
              <div class="divider"></div>
              <textarea readonly class="input" style="width:100%; min-height:360px">${escapeHtml(zpl)}</textarea>`,
          });
          qs("#btnCopyZpl")?.addEventListener("click", async () => {
            await navigator.clipboard.writeText(zpl);
            ui.toastOk("Copied", "ZPL copied to clipboard");
          });
        } catch (e) {
          ui.toastError("ZPL", e.message);
        }
      });
    });

    qsa("[data-download-zpl]", root).forEach((btn) => {
      btn.addEventListener("click", async () => {
        const pkgId = btn.getAttribute("data-download-zpl");
        try {
          const zpl = await apiGetText(`/api/shipments/${shipment.id}/packages/${pkgId}/label.zpl`);
          downloadText(`label-${shipment.id}-${pkgId}.zpl`, zpl);
          ui.toastOk("Downloaded", `label-${shipment.id}-${pkgId}.zpl`);
        } catch (e) {
          ui.toastError("Download failed", e.message);
        }
      });
    });
  }

  qs("#shipmentQuery", root).addEventListener("input", renderList);
  qs("#shipmentStatus", root).addEventListener("change", renderList);
  qs("#btnShipmentsRefresh", root).addEventListener("click", loadShipments);

  await loadShipments();
}
