import { apiGet } from "../api.js";
import { getApiBaseUrl } from "../config.js";
import { qs, badgeForToteStatus } from "../utils.js";

export async function renderTotes({ root }) {
  root.innerHTML = `
    <div class="grid">
      <div class="card">
        <div class="card__title">Tote Lookup</div>
        <p class="card__subtitle">Scan or type a tote barcode.</p>
        <div class="divider"></div>
        <div class="row row--start">
          <div style="min-width:360px; flex:1">
            <label>Tote barcode</label>
            <input id="toteBarcode" class="input mono" placeholder="TOTE-..." />
          </div>
          <div style="margin-top:18px">
            <button class="btn btn--primary" id="btnToteSearch">Search</button>
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
    host.innerHTML = `<div class="card"><div class="card__title">Loading</div></div>`;
    try {
      const tote = await apiGet(`/api/totes/${encodeURIComponent(code)}`);
      const api = getApiBaseUrl();
      host.innerHTML = `
        <div class="card">
          <div class="row">
            <div>
              <div class="card__title mono">${tote.barcode}</div>
              <p class="card__subtitle">Wave: <span class="mono">${tote.waveCode}</span> — Order: <span class="mono">#${tote.orderId}</span></p>
            </div>
            <div class="row row--start">
              ${badgeForToteStatus(tote.status)}
              <a class="btn btn--secondary" href="#/waves/${tote.waveId}">Open Wave</a>
              <a class="btn btn--secondary" href="#/orders/${tote.orderId}">Open Order</a>
            </div>
          </div>
          <div class="divider"></div>

          <div class="grid grid--3">
            <div class="card" style="box-shadow:none">
              <div class="muted small">Packing Station</div>
              <div class="mono">${tote.packingStationCode || ""}</div>
            </div>
            <div class="card" style="box-shadow:none">
              <div class="muted small">Carrier</div>
              <div class="mono">${tote.carrier || ""}</div>
            </div>
            <div class="card" style="box-shadow:none">
              <div class="muted small">Picking</div>
              <div class="mono">${tote.pickingSummary?.totalPicked || 0} / ${tote.pickingSummary?.totalAssigned || 0}</div>
            </div>
          </div>

          <div class="divider"></div>
          <div class="card__title">Packing handheld (API hint)</div>
          <p class="card__subtitle">Use the handheld endpoints to start packing.</p>
          <div class="card" style="box-shadow:none">
            <div class="muted small">POST ${api}/api/packing/sessions/start</div>
            <div class="mono small">{ "toteBarcode": "${tote.barcode}", "stationId": ${tote.packingStationId || 1} }</div>
          </div>
        </div>
      `;
    } catch (e) {
      host.innerHTML = `<div class="card"><div class="card__title">Not found</div><p class="card__subtitle">${e.message}</p></div>`;
    }
  }

  qs("#btnToteSearch", root).addEventListener("click", doSearch);
  qs("#toteBarcode", root).addEventListener("keydown", (e) => {
    if (e.key === "Enter") doSearch();
  });
}

