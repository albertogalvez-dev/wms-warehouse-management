import { apiGet, apiPost } from "../api.js";
import { qs, qsa, badgeForOrderStatus, badgeForToteStatus, badgeForWaveStatus, formatDateTime } from "../utils.js";

export async function renderWavesList({ root, ui }) {
  root.innerHTML = `
    <div class="grid">
      <div class="card">
        <div class="row">
          <div>
            <div class="card__title">Pick Waves</div>
            <p class="card__subtitle">Batch picking by wave + totes.</p>
          </div>
          <div class="row row--start">
            <button class="btn btn--secondary" id="btnRefreshWaves">Refresh</button>
          </div>
        </div>
        <div class="divider"></div>
        <div id="wavesTable" class="table-wrap"></div>
      </div>

      <div class="card">
        <div class="row">
          <div>
            <div class="card__title">Create Wave</div>
            <p class="card__subtitle">Select eligible orders (DRAFT/RELEASED/PICKING).</p>
          </div>
          <div class="row row--start">
            <button class="btn btn--secondary" id="btnLoadEligible">Load eligible</button>
          </div>
        </div>
        <div class="divider"></div>
        <div id="eligibleOrdersHost" class="table-wrap"></div>
        <div class="row" style="margin-top:12px">
          <button class="btn btn--primary" id="btnCreateWave" disabled>Create Wave</button>
          <div class="muted" id="eligibleMeta"></div>
        </div>
      </div>
    </div>
  `;

  async function loadWaves() {
    const host = qs("#wavesTable", root);
    try {
      const data = await apiGet("/api/pick-waves?page=0&size=50");
      const rows = (data.content || []).map(
        (w) => `
          <tr>
            <td class="mono">${w.id}</td>
            <td class="mono">${w.code}</td>
            <td>${badgeForWaveStatus(w.status)}</td>
            <td class="mono">${(w.orders || []).length}</td>
            <td>${formatDateTime(w.createdAt)}</td>
            <td><a class="btn btn--secondary btn--sm" href="#/waves/${w.id}">View</a></td>
          </tr>`
      );
      host.innerHTML = `
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Code</th>
              <th>Status</th>
              <th>#Orders</th>
              <th>Created</th>
              <th></th>
            </tr>
          </thead>
          <tbody>${rows.join("") || `<tr><td colspan="6" class="muted">No waves</td></tr>`}</tbody>
        </table>
      `;
    } catch (e) {
      host.innerHTML = `<div class="muted">Error: ${e.message}</div>`;
    }
  }

  let eligibleOrders = [];
  const selected = new Set();

  function renderEligible() {
    const host = qs("#eligibleOrdersHost", root);
    const meta = qs("#eligibleMeta", root);
    const btn = qs("#btnCreateWave", root);

    const rows = eligibleOrders.map(
      (o) => `
        <tr>
          <td><input type="checkbox" data-order-select="${o.id}" ${selected.has(o.id) ? "checked" : ""} /></td>
          <td class="mono">${o.id}</td>
          <td class="mono">${o.externalRef || ""}</td>
          <td>${badgeForOrderStatus(o.status)}</td>
          <td>${o.carrier || ""}</td>
          <td>${o.shipping?.city || ""}</td>
        </tr>`
    );

    host.innerHTML = `
      <table style="min-width:820px">
        <thead>
          <tr>
            <th></th>
            <th>ID</th>
            <th>ExternalRef</th>
            <th>Status</th>
            <th>Carrier</th>
            <th>City</th>
          </tr>
        </thead>
        <tbody>${rows.join("") || `<tr><td colspan="6" class="muted">No eligible orders</td></tr>`}</tbody>
      </table>
    `;

    meta.textContent = `${selected.size} selected`;
    btn.disabled = selected.size === 0;

    qsa("[data-order-select]", root).forEach((cb) => {
      cb.addEventListener("change", () => {
        const id = Number(cb.getAttribute("data-order-select"));
        if (cb.checked) selected.add(id);
        else selected.delete(id);
        meta.textContent = `${selected.size} selected`;
        btn.disabled = selected.size === 0;
      });
    });
  }

  qs("#btnRefreshWaves", root).addEventListener("click", loadWaves);

  qs("#btnLoadEligible", root).addEventListener("click", async () => {
    selected.clear();
    try {
      const statuses = ["DRAFT", "RELEASED", "PICKING"];
      const results = await Promise.all(
        statuses.map((s) => apiGet(`/api/orders?status=${s}&page=0&size=50`).catch(() => ({ content: [] })))
      );
      const merged = new Map();
      results.flatMap((r) => r.content || []).forEach((o) => merged.set(o.id, o));
      eligibleOrders = Array.from(merged.values()).sort((a, b) => b.id - a.id);
      renderEligible();
      ui.toastOk("Eligible loaded", `${eligibleOrders.length} orders`);
    } catch (e) {
      ui.toastError("Failed", e.message);
    }
  });

  qs("#btnCreateWave", root).addEventListener("click", async () => {
    try {
      const orderIds = Array.from(selected.values());
      const created = await apiPost("/api/pick-waves", { orderIds });
      ui.toastOk("Wave created", `Wave ${created.id}`);
      window.location.hash = `#/waves/${created.id}`;
    } catch (e) {
      ui.toastError("Create wave failed", e.message);
    }
  });

  await loadWaves();
  renderEligible();
}

export async function renderWaveDetail({ root, ui, params }) {
  const id = params.id;
  root.innerHTML = `<div class="card"><div class="card__title">Loading</div></div>`;

  let stations = [];
  try {
    stations = await apiGet("/api/packing-stations");
  } catch {
    stations = [];
  }

  try {
    const wave = await apiGet(`/api/pick-waves/${id}`);
    const pickList = await apiGet(`/api/pick-waves/${id}/pick-list`).catch(() => null);

    const stationOptions = stations.map((s) => `<option value="${s.id}">${s.code}</option>`).join("");

    const toteRows = (wave.totes || []).map((t) => {
      const order = (wave.orders || []).find((o) => o.orderId === t.orderId);
      return `
        <tr>
          <td class="mono">${t.barcode}</td>
          <td class="mono">${order?.externalRef || ""}</td>
          <td>${badgeForOrderStatus(order?.status || "")}</td>
          <td>${badgeForToteStatus(t.status)}</td>
          <td class="mono">${t.packingStationCode || ""}</td>
          <td style="min-width:220px">
            <div class="row row--start" style="gap:8px">
              <select data-station-select="${t.barcode}" style="width:120px">${stationOptions}</select>
              <button class="btn btn--secondary btn--sm" data-assign="${t.barcode}">Assign</button>
            </div>
          </td>
        </tr>
      `;
    });

    root.innerHTML = `
      <div class="grid">
        <div class="card">
          <div class="row">
            <div>
              <div class="card__title">Wave <span class="mono">#${wave.id}</span> — <span class="mono">${wave.code}</span></div>
              <p class="card__subtitle">Status: ${badgeForWaveStatus(wave.status)}</p>
            </div>
            <div class="row row--start">
              <a class="btn btn--ghost" href="#/waves">Back</a>
              <button class="btn btn--secondary" id="btnWaveStart" ${wave.status === "PLANNED" ? "" : "disabled"}>Start Wave</button>
              <button class="btn btn--primary" id="btnWaveComplete" ${wave.status === "IN_PROGRESS" ? "" : "disabled"}>Complete Wave</button>
            </div>
          </div>
        </div>

        <div class="card">
          <div class="row">
            <div>
              <div class="card__title">Totes</div>
              <p class="card__subtitle">Assign to packing stations (PACK-1/2/3).</p>
            </div>
          </div>
          <div class="divider"></div>
          <div class="table-wrap">
            <table style="min-width: 980px">
              <thead>
                <tr>
                  <th>Tote</th>
                  <th>Order Ref</th>
                  <th>Order Status</th>
                  <th>Tote Status</th>
                  <th>Station</th>
                  <th>Assign</th>
                </tr>
              </thead>
              <tbody>${toteRows.join("") || `<tr><td colspan="6" class="muted">No totes</td></tr>`}</tbody>
            </table>
          </div>
        </div>

        <div class="card">
          <div class="row">
            <div>
              <div class="card__title">Pick List (Grouped)</div>
              <p class="card__subtitle">By location and SKU, with tote breakdown.</p>
            </div>
          </div>
          <div class="divider"></div>
          <div id="pickListHost" class="grid"></div>
        </div>
      </div>
    `;

    qs("#btnWaveStart", root)?.addEventListener("click", async () => {
      try {
        await apiPost(`/api/pick-waves/${wave.id}/start`, {});
        ui.toastOk("Wave started", wave.code);
        window.location.hash = `#/waves/${wave.id}`;
      } catch (e) {
        ui.toastError("Start failed", e.message);
      }
    });

    qs("#btnWaveComplete", root)?.addEventListener("click", async () => {
      try {
        await apiPost(`/api/pick-waves/${wave.id}/complete`, {});
        ui.toastOk("Wave completed", wave.code);
        window.location.hash = `#/waves/${wave.id}`;
      } catch (e) {
        ui.toastError("Complete failed", e.message);
      }
    });

    qsa("[data-assign]", root).forEach((btn) => {
      btn.addEventListener("click", async () => {
        const barcode = btn.getAttribute("data-assign");
        const select = qs(`[data-station-select="${barcode}"]`, root);
        const stationId = Number(select.value);
        try {
          await apiPost(`/api/totes/${encodeURIComponent(barcode)}/assign-station`, { stationId });
          ui.toastOk("Assigned", `${barcode} -> station ${stationId}`);
          window.location.hash = `#/waves/${wave.id}`;
        } catch (e) {
          ui.toastError("Assign failed", e.message);
        }
      });
    });

    const pickHost = qs("#pickListHost", root);
    if (!pickList) {
      pickHost.innerHTML = `<div class="muted">Pick list unavailable</div>`;
    } else {
      const grouped = new Map();
      (pickList.itemsGroupedByLocation || []).forEach((item) => {
        const key = item.locationCode;
        if (!grouped.has(key)) grouped.set(key, []);
        grouped.get(key).push(item);
      });

      pickHost.innerHTML = Array.from(grouped.entries())
        .map(([locationCode, items]) => {
          const lines = items
            .map((i) => {
              const breakdown = (i.breakdown || [])
                .map((b) => `<div class="muted small mono">${b.toteBarcode}: ${b.qtyForThatOrderAtThatLocationSku}</div>`)
                .join("");
              return `
                <div class="card" style="box-shadow:none">
                  <div class="row">
                    <div>
                      <div style="font-weight:900" class="mono">${i.sku}</div>
                      <div class="muted small">${i.productName}</div>
                    </div>
                    <div class="pill mono">${i.totalQtyAssigned}</div>
                  </div>
                  <div style="margin-top:10px">${breakdown}</div>
                </div>
              `;
            })
            .join("");
          return `
            <div class="card">
              <div class="row">
                <div class="card__title mono" style="margin:0">${locationCode}</div>
                <div class="muted small">${items.length} item(s)</div>
              </div>
              <div class="divider"></div>
              <div class="grid">${lines}</div>
            </div>
          `;
        })
        .join("");
    }
  } catch (e) {
    root.innerHTML = `<div class="card"><div class="card__title">Error</div><p class="card__subtitle">${e.message}</p></div>`;
  }
}

