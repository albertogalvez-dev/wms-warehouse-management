import { apiGet, apiPost } from "../api.js";
import { qs, qsa, badgeForOrderStatus, formatDateTime, escapeHtml } from "../utils.js";

export async function renderOrdersList({ root, query }) {
  const page = Number.parseInt(query.page || "0", 10) || 0;
  const size = Number.parseInt(query.size || "20", 10) || 20;
  const status = query.status || "";

  root.innerHTML = `
    <div class="card">
      <div class="card-header">
        <div>
          <h3 class="card-title">Orders</h3>
          <p class="card-subtitle">Create, release, and track order status.</p>
        </div>
        <a class="btn btn-primary" href="#/orders/new">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="16"/><line x1="8" y1="12" x2="16" y2="12"/></svg>
          Create Order
        </a>
      </div>
      <div class="card-body">
        <div class="filters-bar">
          <div class="filter-group">
            <label class="label">Status</label>
            <select id="orderStatusFilter" class="select">
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
          <div class="filter-group">
            <label class="label">Page size</label>
            <select id="orderSize" class="select">
              <option value="10">10</option>
              <option value="20">20</option>
              <option value="50">50</option>
            </select>
          </div>
          <div class="row" style="gap: var(--space-sm); margin-top: 18px">
            <button class="btn btn-primary btn-sm" id="btnOrderApply">Apply</button>
            <button class="btn btn-ghost btn-sm" id="btnOrderReset">Reset</button>
          </div>
        </div>

        <div id="ordersTable" class="table-wrap"></div>
        
        <div class="split mt-md">
          <div class="text-muted" id="ordersMeta"></div>
          <div class="row" style="gap: var(--space-sm)">
            <button class="btn btn-outline btn-sm" id="ordersPrev">Prev</button>
            <button class="btn btn-outline btn-sm" id="ordersNext">Next</button>
          </div>
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
          <td class="font-mono">${o.id}</td>
          <td class="font-mono">${o.externalRef || ""}</td>
          <td>${badgeForOrderStatus(o.status)}</td>
          <td>${o.carrier || ""}</td>
          <td>${o.shipping?.city || ""}</td>
          <td>${formatDateTime(o.createdAt)}</td>
          <td><a class="btn btn-outline btn-sm" href="#/orders/${o.id}">View</a></td>
        </tr>`
    );

    tableHost.innerHTML = `
      <table class="table">
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
        <tbody>${rows.join("") || `<tr><td colspan="7" class="text-muted">No results</td></tr>`}</tbody>
      </table>
    `;

    metaHost.textContent = `Page ${data.number + 1} / ${data.totalPages} - ${data.totalElements} orders`;

    qs("#ordersPrev", root).disabled = data.first;
    qs("#ordersNext", root).disabled = data.last;
    qs("#ordersPrev", root).onclick = () => {
      window.location.hash = `#/orders?page=${Math.max(0, page - 1)}&size=${size}${status ? `&status=${encodeURIComponent(status)}` : ""}`;
    };
    qs("#ordersNext", root).onclick = () => {
      window.location.hash = `#/orders?page=${page + 1}&size=${size}${status ? `&status=${encodeURIComponent(status)}` : ""}`;
    };
  } catch (e) {
    tableHost.innerHTML = `<div class="text-muted">Error: ${e.message}</div>`;
  }

  qs("#btnOrderApply", root).onclick = () => {
    const newStatus = qs("#orderStatusFilter", root).value;
    const newSize = qs("#orderSize", root).value;
    window.location.hash = `#/orders?page=0&size=${newSize}${newStatus ? `&status=${encodeURIComponent(newStatus)}` : ""}`;
  };
  qs("#btnOrderReset", root).onclick = () => (window.location.hash = "#/orders");
}

export async function renderOrderCreate({ root, ui }) {
  root.innerHTML = `<div class="card"><div class="card-body text-center p-lg"><div class="text-muted">Loading carriers...</div></div></div>`;

  let carriers = [];
  try {
    const data = await apiGet("/api/meta/carriers");
    carriers = data.carriers || [];
  } catch (e) {
    ui.toastError("Carriers", e.message);
  }

  const linesState = [];
  const productState = {
    items: [],
    total: 0,
    query: "",
    pageSize: 200,
  };

  function getStockAvailable(product) {
    const onHand = Number(product.stockOnHand || 0);
    const allocated = Number(product.stockAllocated || 0);
    const available = Number(product.stockAvailable ?? (onHand - allocated));
    return Math.max(0, available);
  }

  function renderLines() {
    const host = qs("#linesHost", root);
    if (!host) return;

    const rows = linesState.map((l) => {
      const imageUrl = l.imageUrl ? encodeURI(l.imageUrl).replace(/'/g, "%27") : "";
      const available = Number(l.stockAvailable || 0);
      return `
        <tr>
          <td>
            <div class="row row--start">
              <div class="product-thumb" style="${imageUrl ? `background-image:url('${imageUrl}')` : ""}">
                ${imageUrl ? "" : `<span>${escapeHtml(l.name?.slice(0, 1) || "P")}</span>`}
              </div>
              <div>
                <div class="product-name">${escapeHtml(l.name)}</div>
                <div class="product-meta">
                  <span class="font-mono">${escapeHtml(l.sku)}</span>
                </div>
              </div>
            </div>
          </td>
          <td class="font-mono">${escapeHtml(l.locationCode || "-")}</td>
          <td class="font-mono">${available}</td>
          <td>
            <input class="input qty-input" type="number" min="1" max="${available}" value="${l.requestedQty}" data-line-qty="${l.productId}" />
          </td>
          <td><button class="btn btn-danger btn-sm" data-line-remove="${l.productId}">Remove</button></td>
        </tr>
      `;
    });

    host.innerHTML = `
      <div class="table-wrap">
        <table class="table">
          <thead>
            <tr>
              <th>Product</th>
              <th>Location</th>
              <th>Available</th>
              <th style="width:120px">Qty</th>
              <th style="width:80px"></th>
            </tr>
          </thead>
          <tbody>
            ${rows.join("") || `<tr><td colspan="5" class="text-muted text-center">Add at least one product line</td></tr>`}
          </tbody>
        </table>
      </div>
    `;

    qsa("[data-line-qty]", root).forEach((input) => {
      input.addEventListener("change", () => {
        const id = Number(input.getAttribute("data-line-qty"));
        const line = linesState.find((x) => x.productId === id);
        if (!line) return;
        const max = Number(line.stockAvailable || 0);
        let value = Number.parseInt(input.value || "1", 10) || 1;
        value = Math.max(1, value);
        if (max > 0 && value > max) {
          value = max;
          ui.toastError("Stock limit", `${line.sku} available: ${max}`);
        }
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

  function addLine(product, qty) {
    const available = getStockAvailable(product);
    if (available <= 0) {
      ui.toastError("Out of stock", `${product.sku} has 0 available`);
      return;
    }

    const existing = linesState.find((l) => l.productId === product.id);
    const safeQty = Math.max(1, qty || 1);
    if (existing) {
      const nextQty = existing.requestedQty + safeQty;
      if (nextQty > available) {
        existing.requestedQty = available;
        ui.toastError("Stock limit", `${product.sku} available: ${available}`);
      } else {
        existing.requestedQty = nextQty;
        ui.toastOk("Line updated", `${existing.sku} qty = ${existing.requestedQty}`);
      }
      renderLines();
      return;
    }

    linesState.push({
      productId: product.id,
      sku: product.sku,
      name: product.name,
      imageUrl: product.imageUrl,
      locationCode: product.locationCode,
      stockOnHand: product.stockOnHand,
      stockAllocated: product.stockAllocated,
      stockAvailable: available,
      requestedQty: Math.min(safeQty, available),
    });
    renderLines();
  }

  root.innerHTML = `
    <div class="grid grid-2">
      <div class="card">
        <div class="card-header">
          <div>
            <h3 class="card-title">Create Order</h3>
            <p class="card-subtitle">Shipping + carrier + product lines.</p>
          </div>
          <a class="btn btn-ghost btn-sm" href="#/orders">Back to list</a>
        </div>
        <div class="card-body">
          <div class="form-section">
            <div class="form-section-title">Order Info</div>
            <div class="form-row">
              <div class="form-group">
                <label class="label">External Ref (optional)</label>
                <input id="externalRef" class="input font-mono" placeholder="ERP-0001" />
              </div>
              <div class="form-group">
                <label class="label">Carrier</label>
                <select id="carrier" class="select">
                  ${carriers.map((c) => `<option value="${c}">${c}</option>`).join("")}
                </select>
              </div>
            </div>
          </div>

          <div class="form-section">
            <div class="form-section-title">Shipping Address</div>
            <div class="form-row">
              <div class="form-group">
                <label class="label">Name *</label>
                <input id="shipName" class="input" placeholder="John Doe" />
              </div>
              <div class="form-group">
                <label class="label">Phone</label>
                <input id="shipPhone" class="input" placeholder="+34 600 000 000" />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label class="label">Email</label>
                <input id="shipEmail" class="input" placeholder="john@example.com" />
              </div>
              <div class="form-group">
                <label class="label">Country</label>
                <input id="shipCountry" class="input" value="ES" />
              </div>
            </div>
            <div class="form-group">
              <label class="label">Address 1 *</label>
              <input id="shipAddr1" class="input" placeholder="Calle Mayor, 123" />
            </div>
            <div class="form-group">
              <label class="label">Address 2</label>
              <input id="shipAddr2" class="input" placeholder="Piso 2, Puerta B" />
            </div>
            <div class="form-row">
              <div class="form-group">
                <label class="label">Postal Code *</label>
                <input id="shipPostal" class="input" placeholder="28001" />
              </div>
              <div class="form-group">
                <label class="label">City *</label>
                <input id="shipCity" class="input" placeholder="Madrid" />
              </div>
              <div class="form-group">
                <label class="label">Province</label>
                <input id="shipProvince" class="input" placeholder="Madrid" />
              </div>
            </div>
          </div>

          <div class="mt-lg">
            <button class="btn btn-primary btn-lg" id="btnCreateOrder">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="20 6 9 17 4 12"></polyline></svg>
              Create Order
            </button>
          </div>
          <p class="help mt-sm">Fields marked with * are required.</p>
        </div>
      </div>

      <div class="card">
        <div class="card-header">
          <h3 class="card-title">Product Lines</h3>
        </div>
        <div class="card-body">
          <div class="form-group">
            <label class="label">Search products</label>
            <input id="productQuery" class="input" placeholder="Search by SKU, name, or barcode..." />
          </div>
          <div class="split mb-sm">
            <div id="productMeta" class="text-muted small"></div>
            <button class="btn btn-outline btn-sm" id="btnReloadProducts">Refresh</button>
          </div>
          <div id="productCatalog" class="table-wrap"></div>

          <div class="divider"></div>
          <h4 class="font-medium mb-md">Selected Lines</h4>
          <div id="linesHost"></div>
        </div>
      </div>
    </div>
  `;

  renderLines();

  function renderCatalog() {
    const host = qs("#productCatalog", root);
    if (!host) return;

    const query = productState.query.toLowerCase();
    const filtered = (productState.items || []).filter((p) => {
      if (!query) return true;
      const haystack = [p.sku, p.name, p.barcode].filter(Boolean).join(" ").toLowerCase();
      return haystack.includes(query);
    });

    const rows = filtered.map((p) => {
      const imageUrl = p.imageUrl ? encodeURI(p.imageUrl).replace(/'/g, "%27") : "";
      const available = getStockAvailable(p);
      const qtyValue = Number(qs(`[data-catalog-qty="${p.id}"]`, root)?.value || 1);
      const qty = Number.isNaN(qtyValue) ? 1 : qtyValue;
      return `
        <tr>
          <td>
            <div class="product-thumb product-thumb-sm" style="${imageUrl ? `background-image:url('${imageUrl}')` : ""}">
              ${imageUrl ? "" : `<span>${escapeHtml(p.name?.slice(0, 1) || "P")}</span>`}
            </div>
          </td>
          <td class="font-mono">${escapeHtml(p.sku)}</td>
          <td>${escapeHtml(p.name)}</td>
          <td class="font-mono">${escapeHtml(p.locationCode || "-")}</td>
          <td class="font-mono">${available}</td>
          <td>
            <input class="input qty-input" type="number" min="1" max="${available}" value="${qty}" data-catalog-qty="${p.id}" ${available <= 0 ? "disabled" : ""} />
          </td>
          <td>
            <button class="btn btn-primary btn-sm" data-add-product="${p.id}" ${available <= 0 ? "disabled" : ""}>Add</button>
          </td>
        </tr>
      `;
    });

    host.innerHTML = `
      <table class="table table-compact">
        <thead>
          <tr>
            <th>Photo</th>
            <th>SKU</th>
            <th>Name</th>
            <th>Location</th>
            <th>Available</th>
            <th style="width:120px">Qty</th>
            <th style="width:80px"></th>
          </tr>
        </thead>
        <tbody>
          ${rows.join("") || `<tr><td colspan="7" class="text-muted text-center">No products found</td></tr>`}
        </tbody>
      </table>
    `;

    qsa("[data-catalog-qty]", root).forEach((input) => {
      input.addEventListener("change", () => {
        const id = Number(input.getAttribute("data-catalog-qty"));
        const product = productState.items.find((x) => x.id === id);
        if (!product) return;
        const max = getStockAvailable(product);
        let value = Number.parseInt(input.value || "1", 10) || 1;
        value = Math.max(1, value);
        if (max > 0 && value > max) {
          value = max;
          ui.toastError("Stock limit", `${product.sku} available: ${max}`);
        }
        input.value = String(value);
      });
    });

    qsa("[data-add-product]", root).forEach((btn) => {
      btn.addEventListener("click", () => {
        const id = Number(btn.getAttribute("data-add-product"));
        const product = productState.items.find((x) => x.id === id);
        if (!product) return;
        const qtyInput = qs(`[data-catalog-qty="${id}"]`, root);
        const qty = Number.parseInt(qtyInput?.value || "1", 10) || 1;
        addLine(product, qty);
      });
    });
  }

  async function loadProducts() {
    const meta = qs("#productMeta", root);
    if (meta) meta.textContent = "Loading products...";
    try {
      const data = await apiGet(`/api/products?query=&page=0&size=${productState.pageSize}`);
      productState.items = data.content || [];
      productState.total = data.totalElements || productState.items.length;
      renderCatalog();
      if (meta) {
        const shown = productState.items.length;
        meta.textContent = productState.total > shown
          ? `Showing ${shown} of ${productState.total} products (use search to filter)`
          : `Showing ${shown} products`;
      }
    } catch (e) {
      if (meta) meta.textContent = `Error: ${e.message}`;
      qs("#productCatalog", root).innerHTML = `<div class="text-muted">Error: ${e.message}</div>`;
    }
  }

  let searchTimer = null;
  qs("#productQuery", root).addEventListener("input", () => {
    if (searchTimer) clearTimeout(searchTimer);
    searchTimer = setTimeout(() => {
      productState.query = qs("#productQuery", root).value.trim();
      renderCatalog();
    }, 200);
  });

  qs("#btnReloadProducts", root).addEventListener("click", loadProducts);
  await loadProducts();

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
  root.innerHTML = `<div class="card"><div class="card-body text-center p-lg"><div class="text-muted">Loading order...</div></div></div>`;

  try {
    const order = await apiGet(`/api/orders/${id}`);

    root.innerHTML = `
      <div class="card">
        <div class="card-header">
          <div>
            <h3 class="card-title">Order <span class="font-mono">#${order.id}</span></h3>
            <p class="card-subtitle">Ref: <span class="font-mono">${order.externalRef || "(none)"}</span></p>
          </div>
          <div class="row" style="gap: var(--space-sm)">
            <a class="btn btn-ghost btn-sm" href="#/orders">Back</a>
            <button class="btn btn-outline btn-sm" id="btnCopyId">Copy ID</button>
            <button class="btn btn-primary" id="btnRelease" ${order.status === "DRAFT" ? "" : "disabled"}>Release to Picking</button>
          </div>
        </div>
        <div class="card-body">
          <div class="row mb-lg" style="gap: var(--space-md)">
            ${badgeForOrderStatus(order.status)}
            <span class="badge badge-blue">Carrier: ${order.carrier || ""}</span>
            <span class="text-muted" style="font-size: 0.875rem">Updated: ${formatDateTime(order.updatedAt)}</span>
          </div>

          <div class="grid grid-2 mb-lg">
            <div class="form-section">
              <div class="form-section-title">Shipping Address</div>
              <div class="stack-sm">
                <div><strong>${order.shipping?.name || ""}</strong></div>
                <div>${order.shipping?.address1 || ""}</div>
                ${order.shipping?.address2 ? `<div class="text-muted">${order.shipping.address2}</div>` : ""}
                <div>${order.shipping?.postalCode || ""} ${order.shipping?.city || ""}</div>
                <div>${order.shipping?.province || ""} ${order.shipping?.country || ""}</div>
              </div>
            </div>
            <div class="form-section">
              <div class="form-section-title">Order Details</div>
              <div class="stack-sm">
                <div><span class="text-muted">Created:</span> <span class="font-mono">${formatDateTime(order.createdAt)}</span></div>
                <div><span class="text-muted">Order ID:</span> <span class="font-mono">${order.id}</span></div>
                ${order.externalRef ? `<div><span class="text-muted">External Ref:</span> <span class="font-mono">${order.externalRef}</span></div>` : ""}
              </div>
            </div>
          </div>

          <div class="divider"></div>

          <h4 class="font-medium mb-md">Order Lines</h4>
          <div class="table-wrap">
            <table class="table">
              <thead>
                <tr>
                  <th>Product</th>
                  <th>Location</th>
                  <th>Stock</th>
                  <th>Requested</th>
                  <th>Allocated</th>
                  <th>Picked</th>
                </tr>
              </thead>
              <tbody>
                ${(order.lines || [])
        .map((l) => {
          const imageUrl = l.imageUrl ? encodeURI(l.imageUrl).replace(/'/g, "%27") : "";
          const location = l.locationCode || "-";
          const stockOnHand = Number(l.stockOnHand || 0);
          const stockAllocated = Number(l.stockAllocated || 0);
          const stockAvailable = Number(l.stockAvailable ?? (stockOnHand - stockAllocated));
          return `
                        <tr>
                          <td>
                            <div class="row row--start">
                              <div class="product-thumb" style="${imageUrl ? `background-image:url('${imageUrl}')` : ""}">
                                ${imageUrl ? "" : `<span>${escapeHtml(l.productName?.slice(0, 1) || "P")}</span>`}
                              </div>
                              <div>
                                <div class="product-name">${escapeHtml(l.productName || "")}</div>
                                <div class="product-meta">
                                  <span class="font-mono">${escapeHtml(l.sku || "-")}</span>
                                </div>
                              </div>
                            </div>
                          </td>
                          <td class="font-mono">${escapeHtml(location)}</td>
                          <td>
                            <div class="stock-stack">
                              <div><span class="text-muted">Available</span> <strong>${stockAvailable}</strong></div>
                              <div class="text-muted small">On hand ${stockOnHand} - Alloc ${stockAllocated}</div>
                            </div>
                          </td>
                          <td class="font-mono">${l.requestedQty}</td>
                          <td class="font-mono">${l.allocatedQty}</td>
                          <td class="font-mono">${l.pickedQty}</td>
                        </tr>
                      `;
        })
        .join("") || `<tr><td colspan="6" class="text-muted">No lines</td></tr>`
      }
              </tbody>
            </table>
          </div>
        </div>
      </div>
    `;

    qs("#btnCopyId", root).addEventListener("click", async () => {
      await navigator.clipboard.writeText(String(order.id));
      ui.toastOk("Copied", "Order ID copied");
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
    root.innerHTML = `<div class="card"><div class="card-body"><h3 class="card-title text-err">Error</h3><p class="text-muted">${e.message}</p></div></div>`;
  }
}
