import { apiGet, apiPost } from "../api.js";
import { qs, qsa, badgeForOrderStatus, badgeForToteStatus, badgeForWaveStatus, formatDateTime, escapeHtml } from "../utils.js";

export async function renderWavesList({ root, ui }) {
  root.innerHTML = `
    <div class="grid grid-2">
      <div class="card">
        <div class="card-header">
          <div>
            <h3 class="card-title">Pick Waves</h3>
            <p class="card-subtitle">Batch picking by wave and totes.</p>
          </div>
          <div class="row row--start">
            <button class="btn btn-outline btn-sm" id="btnRefreshWaves">Refresh</button>
          </div>
        </div>
        <div class="card-body">
          <div id="wavesTable" class="table-wrap"></div>
        </div>
      </div>

      <div class="card">
        <div class="card-header">
          <div>
            <h3 class="card-title">Create Wave</h3>
            <p class="card-subtitle">Select eligible orders (DRAFT/RELEASED/PICKING).</p>
          </div>
          <div class="row row--start">
            <button class="btn btn-outline btn-sm" id="btnLoadEligible">Load eligible</button>
          </div>
        </div>
        <div class="card-body">
          <div id="eligibleOrdersHost" class="table-wrap"></div>
          <div class="row mt-md" style="gap: var(--space-sm)">
            <button class="btn btn-primary" id="btnCreateWave" disabled>Create Wave</button>
            <div class="text-muted" id="eligibleMeta"></div>
          </div>
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
            <td class="font-mono">${w.id}</td>
            <td class="font-mono">${w.code}</td>
            <td>${badgeForWaveStatus(w.status)}</td>
            <td class="font-mono">${(w.orders || []).length}</td>
            <td>${formatDateTime(w.createdAt)}</td>
            <td><a class="btn btn-outline btn-sm" href="#/waves/${w.id}">View</a></td>
          </tr>`
      );
      host.innerHTML = `
        <table class="table">
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
          <tbody>${rows.join("") || `<tr><td colspan="6" class="text-muted">No waves</td></tr>`}</tbody>
        </table>
      `;
    } catch (e) {
      host.innerHTML = `<div class="text-muted">Error: ${e.message}</div>`;
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
          <td class="font-mono">${o.id}</td>
          <td class="font-mono">${o.externalRef || ""}</td>
          <td>${badgeForOrderStatus(o.status)}</td>
          <td>${o.carrier || ""}</td>
          <td>${o.shipping?.city || ""}</td>
        </tr>`
    );

    host.innerHTML = `
      <table class="table table-wide">
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
        <tbody>${rows.join("") || `<tr><td colspan="6" class="text-muted">No eligible orders</td></tr>`}</tbody>
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
  root.innerHTML = `<div class="card"><div class="card-body"><div class="text-muted">Loading...</div></div></div>`;

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
          <td class="font-mono">${t.barcode}</td>
          <td class="font-mono">${order?.externalRef || ""}</td>
          <td>${badgeForOrderStatus(order?.status || "")}</td>
          <td>${badgeForToteStatus(t.status)}</td>
          <td class="font-mono">${t.packingStationCode || ""}</td>
          <td style="min-width:220px">
            <div class="row row--start" style="gap:8px">
              <select class="select" data-station-select="${t.barcode}" style="width:120px">${stationOptions}</select>
              <button class="btn btn-outline btn-sm" data-assign="${t.barcode}">Assign</button>
            </div>
          </td>
        </tr>
      `;
    });

    root.innerHTML = `
      <div class="grid">
        <div class="card">
          <div class="card-header">
            <div>
              <h3 class="card-title">Wave <span class="font-mono">#${wave.id}</span> - <span class="font-mono">${wave.code}</span></h3>
              <p class="card-subtitle">Status: ${badgeForWaveStatus(wave.status)}</p>
            </div>
            <div class="row row--start">
              <a class="btn btn-ghost btn-sm" href="#/waves">Back</a>
              <button class="btn btn-outline btn-sm" id="btnWaveStart" ${wave.status === "PLANNED" ? "" : "disabled"}>Start Wave</button>
              <button class="btn btn-primary btn-sm" id="btnWaveComplete" ${wave.status === "IN_PROGRESS" ? "" : "disabled"}>Complete Wave</button>
            </div>
          </div>
        </div>

        <div class="card">
          <div class="card-header">
            <div>
              <h3 class="card-title">Totes</h3>
              <p class="card-subtitle">Assign to packing stations (PACK-1/2/3).</p>
            </div>
          </div>
          <div class="card-body">
            <div class="table-wrap">
              <table class="table table-wide-lg">
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
                <tbody>${toteRows.join("") || `<tr><td colspan="6" class="text-muted">No totes</td></tr>`}</tbody>
              </table>
            </div>
          </div>
        </div>

        <div class="card">
          <div class="card-header">
            <div>
              <h3 class="card-title">Pick List (Grouped)</h3>
              <p class="card-subtitle">By location and SKU, with tote breakdown.</p>
            </div>
          </div>
          <div class="card-body">
            <div id="pickListHost" class="grid"></div>
          </div>
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
      pickHost.innerHTML = `<div class="text-muted">Pick list unavailable</div>`;
    } else {
      const grouped = new Map();
      (pickList.itemsGroupedByLocation || []).forEach((item) => {
        const key = item.locationCode;
        if (!grouped.has(key)) grouped.set(key, []);
        grouped.get(key).push(item);
      });

      const groupEntries = Array.from(grouped.entries());
      pickHost.innerHTML = groupEntries.length ? groupEntries
        .map(([locationCode, items]) => {
          const totalUnits = items.reduce((sum, item) => sum + Number(item.totalQtyAssigned || 0), 0);
          const rows = items
            .map((i) => {
              const imageUrl = i.imageUrl ? encodeURI(i.imageUrl).replace(/'/g, "%27") : "";
              const breakdown = (i.breakdown || [])
                .map((b) => `<span class="tote-chip font-mono">${escapeHtml(b.toteBarcode || "")}: ${b.qtyForThatOrderAtThatLocationSku}</span>`)
                .join("");
              return `
                <tr>
                  <td>
                    <div class="row row--start">
                      <div class="product-thumb product-thumb-sm" style="${imageUrl ? `background-image:url('${imageUrl}')` : ""}">
                        ${imageUrl ? "" : `<span>${escapeHtml(i.productName?.slice(0, 1) || "P")}</span>`}
                      </div>
                      <div>
                        <div class="product-name">${escapeHtml(i.productName || "")}</div>
                        <div class="product-meta">
                          <span class="font-mono">${escapeHtml(i.sku || "-")}</span>
                        </div>
                      </div>
                    </div>
                  </td>
                  <td class="font-mono">${i.totalQtyAssigned}</td>
                  <td>
                    <div class="tote-breakdown">
                      ${breakdown || `<span class="text-muted small">No tote splits</span>`}
                    </div>
                  </td>
                </tr>
              `;
            })
            .join("");
          return `
            <div class="card">
              <div class="card-header">
                <div>
                  <div class="card-title font-mono">${escapeHtml(locationCode)}</div>
                  <div class="card-subtitle">${items.length} item(s) | ${totalUnits} units</div>
                </div>
              </div>
              <div class="card-body">
                <div class="table-wrap">
                  <table class="table table-compact">
                    <thead>
                      <tr>
                        <th>Product</th>
                        <th style="width:120px">Qty</th>
                        <th>By Tote</th>
                      </tr>
                    </thead>
                    <tbody>${rows || `<tr><td colspan="3" class="text-muted">No items</td></tr>`}</tbody>
                  </table>
                </div>
              </div>
            </div>
          `;
        })
        .join("") : `<div class="text-muted">No pick list items</div>`;
    }
  } catch (e) {
    root.innerHTML = `<div class="card"><div class="card-body"><h3 class="card-title">Error</h3><p class="card-subtitle">${e.message}</p></div></div>`;
  }
}
