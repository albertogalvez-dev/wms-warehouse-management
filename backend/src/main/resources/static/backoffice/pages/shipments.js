import { apiGet, apiGetText } from "../api.js";
import { qs, qsa, escapeHtml, downloadText } from "../utils.js";

export async function renderShipments({ root, ui }) {
  root.innerHTML = `
    <div class="grid">
      <div class="card">
        <div class="card__title">Shipment Lookup</div>
        <p class="card__subtitle">Search by shipmentId. (OrderId lookup requires backend support.)</p>
        <div class="divider"></div>
        <div class="row row--start">
          <div style="min-width:220px">
            <label>Shipment ID</label>
            <input id="shipmentId" class="input mono" placeholder="123" />
          </div>
          <div style="min-width:220px">
            <label>Order ID (optional)</label>
            <input id="orderIdForShipment" class="input mono" placeholder="1" />
          </div>
          <div style="margin-top:18px">
            <button class="btn btn--primary" id="btnShipmentSearch">Search</button>
          </div>
        </div>
      </div>

      <div id="shipmentResult"></div>
    </div>
  `;

  async function renderShipment(shipment) {
    const rows = (shipment.packages || []).map(
      (p) => `
        <tr>
          <td class="mono">${p.id}</td>
          <td class="mono">${p.packageNo}/${p.packageCount}</td>
          <td class="mono">${p.trackingCode}</td>
          <td class="mono">${p.labelFormat}</td>
          <td class="mono">${p.printedAt || ""}</td>
          <td>
            <div class="row row--start" style="gap:8px">
              <button class="btn btn--secondary btn--sm" data-view-zpl="${p.id}">View ZPL</button>
              <button class="btn btn--secondary btn--sm" data-download-zpl="${p.id}">Download</button>
            </div>
          </td>
        </tr>
      `
    );

    qs("#shipmentResult", root).innerHTML = `
      <div class="card">
        <div class="row">
          <div>
            <div class="card__title">Shipment <span class="mono">#${shipment.id}</span></div>
            <p class="card__subtitle">Order: <span class="mono">#${shipment.orderId}</span> — Carrier: <span class="mono">${shipment.carrier}</span></p>
          </div>
          <div class="row row--start">
            <span class="badge">${shipment.status}</span>
          </div>
        </div>
        ${shipment.printError ? `<div class="divider"></div><div class="badge badge--danger">Printer: ${escapeHtml(shipment.printError)}</div>` : ""}
        <div class="divider"></div>
        <div class="table-wrap">
          <table style="min-width: 980px">
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
            <tbody>${rows.join("") || `<tr><td colspan="6" class="muted">No packages</td></tr>`}</tbody>
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
            title: `ZPL — ${shipment.id}/${pkgId}`,
            bodyHtml: `<div class="row"><button class="btn btn--secondary btn--sm" id="btnCopyZpl">Copy</button></div>
              <div class="divider"></div>
              <textarea readonly class="input" style="width:100%; min-height:420px">${escapeHtml(zpl)}</textarea>`,
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

  async function doSearch() {
    const shipmentId = qs("#shipmentId", root).value.trim();
    const orderId = qs("#orderIdForShipment", root).value.trim();
    const host = qs("#shipmentResult", root);
    host.innerHTML = `<div class="card"><div class="card__title">Loading</div></div>`;

    try {
      if (shipmentId) {
        const shipment = await apiGet(`/api/shipments/${encodeURIComponent(shipmentId)}`);
        await renderShipment(shipment);
        return;
      }

      if (orderId) {
        const shipment = await apiGet(`/api/shipments/by-order/${encodeURIComponent(orderId)}`);
        await renderShipment(shipment);
        return;
      }

      host.innerHTML = `<div class="card"><div class="card__title">Enter an ID</div><p class="card__subtitle">Provide shipmentId or orderId.</p></div>`;
    } catch (e) {
      host.innerHTML = `<div class="card"><div class="card__title">Not found</div><p class="card__subtitle">${e.message}</p></div>`;
    }
  }

  qs("#btnShipmentSearch", root).addEventListener("click", doSearch);
  qs("#shipmentId", root).addEventListener("keydown", (e) => {
    if (e.key === "Enter") doSearch();
  });
  qs("#orderIdForShipment", root).addEventListener("keydown", (e) => {
    if (e.key === "Enter") doSearch();
  });
}

