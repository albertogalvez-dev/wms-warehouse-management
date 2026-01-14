import { apiGet } from "../api.js";
import { getApiBaseUrl } from "../config.js";
import { qs, badgeForToteStatus, escapeHtml } from "../utils.js";

export async function renderTotes({ root }) {
  root.innerHTML = `
    <div class="grid">
      <div class="card">
        <div class="card-header">
          <div>
            <h3 class="card-title">Tote Lookup</h3>
            <p class="card-subtitle">Scan or type a tote barcode.</p>
          </div>
        </div>
        <div class="card-body">
          <div class="form-row">
            <div class="form-group" style="min-width:240px; flex:1">
              <label class="label" for="toteBarcode">Tote barcode</label>
              <input id="toteBarcode" class="input font-mono" placeholder="TOTE-..." />
            </div>
            <div class="form-group" style="align-self:flex-end">
              <button class="btn btn-primary" id="btnToteSearch">Search</button>
            </div>
          </div>
        </div>
      </div>

      <div id="toteResult"></div>
    </div>
  `;

  async function doSearch() {
    const code = qs("#toteBarcode", root).value.trim();
    if (!code) return;
    const host = qs("#toteResult", root);
    host.innerHTML = `<div class="card"><div class="card-body"><div class="text-muted">Loading...</div></div></div>`;
    try {
      const tote = await apiGet(`/api/totes/${encodeURIComponent(code)}`);
      const api = getApiBaseUrl();
      const lines = (tote.lines || []).map((line) => {
        const imageUrl = line.imageUrl ? encodeURI(line.imageUrl).replace(/'/g, "%27") : "";
        return `
          <tr>
            <td>
              <div class="row row--start">
                <div class="product-thumb product-thumb-sm" style="${imageUrl ? `background-image:url('${imageUrl}')` : ""}">
                  ${imageUrl ? "" : `<span>${escapeHtml(line.productName?.slice(0, 1) || "P")}</span>`}
                </div>
                <div>
                  <div class="product-name">${escapeHtml(line.productName || "")}</div>
                  <div class="product-meta">
                    <span class="font-mono">${escapeHtml(line.sku || "-")}</span>
                  </div>
                </div>
              </div>
            </td>
            <td class="font-mono">${escapeHtml(line.locationCode || "-")}</td>
            <td class="font-mono">${line.assignedQty ?? 0}</td>
            <td class="font-mono">${line.pickedQty ?? 0}</td>
          </tr>
        `;
      }).join("");
      host.innerHTML = `
        <div class="card">
          <div class="card-header">
            <div>
              <h3 class="card-title font-mono">${tote.barcode}</h3>
              <p class="card-subtitle">Wave: <span class="font-mono">${tote.waveCode}</span> - Order: <span class="font-mono">#${tote.orderId}</span></p>
            </div>
            <div class="row row--start">
              ${badgeForToteStatus(tote.status)}
              <a class="btn btn-outline btn-sm" href="#/waves/${tote.waveId}">Open Wave</a>
              <a class="btn btn-outline btn-sm" href="#/orders/${tote.orderId}">Open Order</a>
            </div>
          </div>
          <div class="card-body">
            <div class="grid grid-3">
              <div class="card card-flat bg-soft p-md">
                <div class="text-muted small">Packing Station</div>
                <div class="font-mono">${tote.packingStationCode || ""}</div>
              </div>
              <div class="card card-flat bg-soft p-md">
                <div class="text-muted small">Carrier</div>
                <div class="font-mono">${tote.carrier || ""}</div>
              </div>
              <div class="card card-flat bg-soft p-md">
                <div class="text-muted small">Picking</div>
                <div class="font-mono">${tote.pickingSummary?.totalPicked || 0} / ${tote.pickingSummary?.totalAssigned || 0}</div>
              </div>
            </div>

            <div class="divider"></div>

            <h4 class="font-medium mb-md">Tote Lines</h4>
            <div class="table-wrap">
              <table class="table">
                <thead>
                  <tr>
                    <th>Product</th>
                    <th>Location</th>
                    <th>Assigned</th>
                    <th>Picked</th>
                  </tr>
                </thead>
                <tbody>
                  ${lines || `<tr><td colspan="4" class="text-muted">No pick lines found</td></tr>`}
                </tbody>
              </table>
            </div>

            <div class="divider"></div>

            <div class="form-section">
              <div class="form-section-title">Packing handheld</div>
              <p class="text-muted small">Quick API hint for the handheld flow.</p>
              <div class="code-block">
                <div class="text-muted small">POST ${api}/api/packing/sessions/start</div>
                <div class="font-mono small">{ "toteBarcode": "${tote.barcode}", "stationId": ${tote.packingStationId || 1} }</div>
              </div>
            </div>
          </div>
        </div>
      `;
    } catch (e) {
      host.innerHTML = `<div class="card"><div class="card-body"><h3 class="card-title">Not found</h3><p class="card-subtitle">${e.message}</p></div></div>`;
    }
  }

  qs("#btnToteSearch", root).addEventListener("click", doSearch);
  qs("#toteBarcode", root).addEventListener("keydown", (e) => {
    if (e.key === "Enter") doSearch();
  });
}
