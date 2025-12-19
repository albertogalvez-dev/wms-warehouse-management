import { apiGet, apiPost } from "../api.js";
import { qs, qsa, badgeForOrderStatus, formatDateTime } from "../utils.js";

export async function renderOrdersList({ root, query }) {
  const page = Number.parseInt(query.page || "0", 10) || 0;
  const size = Number.parseInt(query.size || "20", 10) || 20;
  const status = query.status || "";

  root.innerHTML = `
    <div class="card">
      <div class="row">
        <div>
          <div class="card__title">Orders</div>
          <p class="card__subtitle">Create, release, and track order status.</p>
        </div>
        <div class="row row--start">
          <a class="btn btn--primary" href="#/orders/new">Create Order</a>
        </div>
      </div>
      <div class="divider"></div>

      <div class="row">
        <div class="row row--start" style="gap:10px; width:100%">
          <div style="min-width:240px">
            <label>Status</label>
            <select id="orderStatusFilter">
              <option value="">(all)</option>
              <option>DRAFT</option>
              <option>RELEASED</option>
              <option>PICKING</option>
              <option>PICKED</option>
              <option>PACKING</option>
              <option>PACKED</option>
              <option>SHIPPED</option>
              <option>CANCELLED</option>
            </select>
          </div>
          <div style="min-width:140px">
            <label>Page size</label>
            <select id="orderSize">
              <option value="10">10</option>
              <option value="20">20</option>
              <option value="50">50</option>
            </select>
          </div>
          <div class="row row--start" style="gap:8px; margin-top:18px">
            <button class="btn btn--secondary" id="btnOrderApply">Apply</button>
            <button class="btn btn--ghost" id="btnOrderReset">Reset</button>
          </div>
        </div>
      </div>

      <div class="divider"></div>
      <div id="ordersTable" class="table-wrap"></div>
      <div class="row" style="margin-top:12px">
        <div class="muted" id="ordersMeta"></div>
        <div class="row row--start" style="gap:8px">
          <button class="btn btn--secondary" id="ordersPrev">Prev</button>
          <button class="btn btn--secondary" id="ordersNext">Next</button>
        </div>
      </div>
    </div>
  `;

  qs("#orderStatusFilter", root).value = status;
  qs("#orderSize", root).value = String(size);

  const tableHost = qs("#ordersTable", root);
  const metaHost = qs("#ordersMeta", root);

  try {
    const params = new URLSearchParams({ page: String(page), size: String(size) });
    if (status) params.set("status", status);
    const data = await apiGet(`/api/orders?${params.toString()}`);

    const rows = (data.content || []).map(
      (o) => `
        <tr>
          <td class="mono">${o.id}</td>
          <td class="mono">${o.externalRef || ""}</td>
          <td>${badgeForOrderStatus(o.status)}</td>
          <td>${o.carrier || ""}</td>
          <td>${o.shipping?.city || ""}</td>
          <td>${formatDateTime(o.createdAt)}</td>
          <td><a class="btn btn--secondary btn--sm" href="#/orders/${o.id}">View</a></td>
        </tr>`
    );

    tableHost.innerHTML = `
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>External Ref</th>
            <th>Status</th>
            <th>Carrier</th>
            <th>City</th>
            <th>Created</th>
            <th></th>
          </tr>
        </thead>
        <tbody>${rows.join("") || `<tr><td colspan="7" class="muted">No results</td></tr>`}</tbody>
      </table>
    `;

    metaHost.textContent = `Page ${data.number + 1} / ${data.totalPages} — ${data.totalElements} orders`;

    qs("#ordersPrev", root).disabled = data.first;
    qs("#ordersNext", root).disabled = data.last;
    qs("#ordersPrev", root).onclick = () => {
      window.location.hash = `#/orders?page=${Math.max(0, page - 1)}&size=${size}${status ? `&status=${encodeURIComponent(status)}` : ""}`;
    };
    qs("#ordersNext", root).onclick = () => {
      window.location.hash = `#/orders?page=${page + 1}&size=${size}${status ? `&status=${encodeURIComponent(status)}` : ""}`;
    };
  } catch (e) {
    tableHost.innerHTML = `<div class="muted">Error: ${e.message}</div>`;
  }

  qs("#btnOrderApply", root).onclick = () => {
    const newStatus = qs("#orderStatusFilter", root).value;
    const newSize = qs("#orderSize", root).value;
    window.location.hash = `#/orders?page=0&size=${newSize}${newStatus ? `&status=${encodeURIComponent(newStatus)}` : ""}`;
  };
  qs("#btnOrderReset", root).onclick = () => (window.location.hash = "#/orders");
}

export async function renderOrderCreate({ root, ui }) {
  root.innerHTML = `<div class="card"><div class="card__title">Loading</div><p class="card__subtitle">Loading carriers...</p></div>`;

  let carriers = [];
  try {
    const data = await apiGet("/api/meta/carriers");
    carriers = data.carriers || [];
  } catch (e) {
    ui.toastError("Carriers", e.message);
  }

  const linesState = [];

  function renderLines() {
    const host = qs("#linesHost", root);
    host.innerHTML = `
      <div class="table-wrap">
        <table style="min-width: 640px">
          <thead>
            <tr>
              <th>SKU</th>
              <th>Name</th>
              <th>Qty</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            ${
              linesState.length
                ? linesState
                    .map(
                      (l) => `
                        <tr>
                          <td class="mono">${l.sku}</td>
                          <td>${l.name}</td>
                          <td style="max-width:140px">
                            <input class="input" type="number" min="1" value="${l.requestedQty}" data-line-qty="${l.productId}" />
                          </td>
                          <td><button class="btn btn--danger btn--sm" data-line-remove="${l.productId}">Remove</button></td>
                        </tr>`
                    )
                    .join("")
                : `<tr><td colspan="4" class="muted">Add at least one product line</td></tr>`
            }
          </tbody>
        </table>
      </div>
    `;

    qsa("[data-line-qty]", root).forEach((input) => {
      input.addEventListener("change", () => {
        const id = Number(input.getAttribute("data-line-qty"));
        const line = linesState.find((x) => x.productId === id);
        if (!line) return;
        const value = Math.max(1, Number.parseInt(input.value || "1", 10) || 1);
        line.requestedQty = value;
        input.value = String(value);
      });
    });

    qsa("[data-line-remove]", root).forEach((btn) => {
      btn.addEventListener("click", () => {
        const id = Number(btn.getAttribute("data-line-remove"));
        const idx = linesState.findIndex((x) => x.productId === id);
        if (idx >= 0) linesState.splice(idx, 1);
        renderLines();
      });
    });
  }

  function addLine(product) {
    const existing = linesState.find((l) => l.productId === product.id);
    if (existing) {
      existing.requestedQty += 1;
      ui.toastOk("Line updated", `${existing.sku} qty = ${existing.requestedQty}`);
      renderLines();
      return;
    }
    linesState.push({ productId: product.id, sku: product.sku, name: product.name, requestedQty: 1 });
    renderLines();
  }

  root.innerHTML = `
    <div class="grid grid--2">
      <div class="card">
        <div class="row">
          <div>
            <div class="card__title">Create Order</div>
            <p class="card__subtitle">Shipping + carrier + product lines.</p>
          </div>
          <div class="row row--start">
            <a class="btn btn--ghost" href="#/orders">Back to list</a>
          </div>
        </div>
        <div class="divider"></div>

        <div class="grid">
          <div class="field">
            <label>External Ref (optional)</label>
            <input id="externalRef" class="input mono" placeholder="ERP-0001" />
          </div>

          <div class="field">
            <label>Carrier</label>
            <select id="carrier">
              ${carriers.map((c) => `<option value="${c}">${c}</option>`).join("")}
            </select>
          </div>

          <div class="divider"></div>

          <div class="grid grid--2">
            <div class="field">
              <label>Name</label>
              <input id="shipName" class="input" />
            </div>
            <div class="field">
              <label>Phone</label>
              <input id="shipPhone" class="input" />
            </div>
            <div class="field">
              <label>Email</label>
              <input id="shipEmail" class="input" />
            </div>
            <div class="field">
              <label>Country</label>
              <input id="shipCountry" class="input" value="ES" />
            </div>
          </div>

          <div class="field">
            <label>Address 1</label>
            <input id="shipAddr1" class="input" />
          </div>
          <div class="field">
            <label>Address 2</label>
            <input id="shipAddr2" class="input" />
          </div>

          <div class="grid grid--3">
            <div class="field">
              <label>Postal Code</label>
              <input id="shipPostal" class="input" />
            </div>
            <div class="field">
              <label>City</label>
              <input id="shipCity" class="input" />
            </div>
            <div class="field">
              <label>Province</label>
              <input id="shipProvince" class="input" />
            </div>
          </div>

          <div class="divider"></div>

          <div class="row">
            <button class="btn btn--primary" id="btnCreateOrder">Create</button>
          </div>
          <div class="help">Validation runs both client-side and server-side.</div>
        </div>
      </div>

      <div class="card">
        <div class="card__title">Lines</div>
        <p class="card__subtitle">Search products and add quantities.</p>
        <div class="divider"></div>

        <div class="field">
          <label>Search products</label>
          <input id="productQuery" class="input" placeholder="sku / name / barcode" />
        </div>
        <div id="productResults" class="grid" style="margin-top:10px"></div>

        <div class="divider"></div>
        <div id="linesHost"></div>
      </div>
    </div>
  `;

  renderLines();

  let searchTimer = null;
  qs("#productQuery", root).addEventListener("input", () => {
    const q = qs("#productQuery", root).value.trim();
    if (searchTimer) clearTimeout(searchTimer);
    searchTimer = setTimeout(async () => {
      if (!q) {
        qs("#productResults", root).innerHTML = "";
        return;
      }
      try {
        const data = await apiGet(`/api/products?query=${encodeURIComponent(q)}&page=0&size=10`);
        const products = data.content || [];
        qs("#productResults", root).innerHTML = products
          .map(
            (p) => `
              <div class="card" style="box-shadow:none">
                <div class="row">
                  <div>
                    <div style="font-weight:800">${p.sku}</div>
                    <div class="muted small">${p.name}</div>
                    <div class="muted small mono">${p.barcode || ""}</div>
                  </div>
                  <button class="btn btn--secondary btn--sm" data-add-product="${p.id}">Add</button>
                </div>
              </div>`
          )
          .join("");

        qsa("[data-add-product]", root).forEach((btn) => {
          btn.addEventListener("click", () => {
            const id = Number(btn.getAttribute("data-add-product"));
            const product = products.find((x) => x.id === id);
            if (product) addLine(product);
          });
        });
      } catch (e) {
        qs("#productResults", root).innerHTML = `<div class="muted">Error: ${e.message}</div>`;
      }
    }, 250);
  });

  qs("#btnCreateOrder", root).addEventListener("click", async () => {
    const shipping = {
      name: qs("#shipName", root).value.trim(),
      phone: qs("#shipPhone", root).value.trim() || null,
      email: qs("#shipEmail", root).value.trim() || null,
      address1: qs("#shipAddr1", root).value.trim(),
      address2: qs("#shipAddr2", root).value.trim() || null,
      postalCode: qs("#shipPostal", root).value.trim(),
      city: qs("#shipCity", root).value.trim(),
      province: qs("#shipProvince", root).value.trim() || null,
      country: qs("#shipCountry", root).value.trim() || "ES",
    };

    const externalRef = qs("#externalRef", root).value.trim();
    const carrier = qs("#carrier", root).value;

    const missing = [];
    if (!shipping.name) missing.push("name");
    if (!shipping.address1) missing.push("address1");
    if (!shipping.postalCode) missing.push("postalCode");
    if (!shipping.city) missing.push("city");
    if (!shipping.country) missing.push("country");
    if (!linesState.length) missing.push("at least one line");
    if (missing.length) {
      ui.toastError("Validation", `Missing: ${missing.join(", ")}`);
      return;
    }

    try {
      const payload = {
        externalRef: externalRef || null,
        carrier,
        shipping,
        lines: linesState.map((l) => ({ productId: l.productId, requestedQty: l.requestedQty })),
      };
      const created = await apiPost("/api/orders", payload);
      ui.toastOk("Order created", `Order ${created.id}`);
      window.location.hash = `#/orders/${created.id}`;
    } catch (e) {
      ui.toastError("Create failed", e.message);
    }
  });
}

export async function renderOrderDetail({ root, ui, params }) {
  const id = params.id;
  root.innerHTML = `<div class="card"><div class="card__title">Loading</div></div>`;

  try {
    const order = await apiGet(`/api/orders/${id}`);

    root.innerHTML = `
      <div class="card">
        <div class="row">
          <div>
            <div class="card__title">Order <span class="mono">#${order.id}</span></div>
            <p class="card__subtitle">ExternalRef: <span class="mono">${order.externalRef || ""}</span></p>
          </div>
          <div class="row row--start">
            <a class="btn btn--ghost" href="#/orders">Back</a>
            <button class="btn btn--secondary" id="btnCopyId">Copy ID</button>
            <button class="btn btn--secondary" id="btnCopyRef" ${order.externalRef ? "" : "disabled"}>Copy ExternalRef</button>
            <button class="btn btn--primary" id="btnRelease" ${order.status === "DRAFT" ? "" : "disabled"}>Release</button>
          </div>
        </div>
        <div class="divider"></div>

        <div class="row">
          <div>${badgeForOrderStatus(order.status)}</div>
          <div class="pill">Carrier: <span class="mono">${order.carrier || ""}</span></div>
          <div class="pill">Updated: <span class="mono">${formatDateTime(order.updatedAt)}</span></div>
        </div>

        <div class="divider"></div>

        <div class="grid grid--2">
          <div class="card" style="box-shadow:none">
            <div class="card__title">Shipping</div>
            <div class="muted small">Name</div>
            <div>${order.shipping?.name || ""}</div>
            <div class="muted small" style="margin-top:8px">Address</div>
            <div>${order.shipping?.address1 || ""}</div>
            <div class="muted small">${order.shipping?.address2 || ""}</div>
            <div>${order.shipping?.postalCode || ""} ${order.shipping?.city || ""} ${order.shipping?.province || ""}</div>
            <div>${order.shipping?.country || ""}</div>
          </div>

          <div class="card" style="box-shadow:none">
            <div class="card__title">Details</div>
            <div class="muted small">Created</div>
            <div class="mono">${formatDateTime(order.createdAt)}</div>
            <div class="muted small" style="margin-top:8px">Order ID</div>
            <div class="mono">${order.id}</div>
          </div>
        </div>

        <div class="divider"></div>

        <div class="card__title">Lines</div>
        <div class="table-wrap" style="margin-top:10px">
          <table style="min-width: 820px">
            <thead>
              <tr>
                <th>SKU</th>
                <th>Name</th>
                <th>Requested</th>
                <th>Allocated</th>
                <th>Picked</th>
              </tr>
            </thead>
            <tbody>
              ${
                (order.lines || [])
                  .map(
                    (l) => `
                      <tr>
                        <td class="mono">${l.productSku}</td>
                        <td>${l.productName}</td>
                        <td class="mono">${l.requestedQty}</td>
                        <td class="mono">${l.allocatedQty}</td>
                        <td class="mono">${l.pickedQty}</td>
                      </tr>
                    `
                  )
                  .join("") || `<tr><td colspan="5" class="muted">No lines</td></tr>`
              }
            </tbody>
          </table>
        </div>
      </div>
    `;

    qs("#btnCopyId", root).addEventListener("click", async () => {
      await navigator.clipboard.writeText(String(order.id));
      ui.toastOk("Copied", "Order ID copied");
    });
    qs("#btnCopyRef", root).addEventListener("click", async () => {
      await navigator.clipboard.writeText(String(order.externalRef || ""));
      ui.toastOk("Copied", "ExternalRef copied");
    });

    qs("#btnRelease", root).addEventListener("click", async () => {
      try {
        await apiPost(`/api/orders/${order.id}/release`, {});
        ui.toastOk("Released", `Order ${order.id} released`);
        window.location.hash = `#/orders/${order.id}`;
      } catch (e) {
        ui.toastError("Release failed", e.message);
      }
    });
  } catch (e) {
    root.innerHTML = `<div class="card"><div class="card__title">Error</div><p class="card__subtitle">${e.message}</p></div>`;
  }
}

