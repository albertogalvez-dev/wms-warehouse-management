import { apiGet, apiPost, apiPut, apiDelete } from "../api.js";
import { qs, qsa, escapeHtml, formatDateTime } from "../utils.js";

export async function renderProducts({ root, ui }) {
  const state = {
    page: 0,
    size: Number(qs("#productPageSize", root)?.value || 20),
    query: "",
    status: "all",
    data: null
  };

  function renderTable(data) {
    const host = qs("#productsTable", root);
    if (!data) {
      host.innerHTML = `<div class="text-muted">No data</div>`;
      return;
    }

    const statusFilter = state.status;
    let rows = data.content || [];
    if (statusFilter === "active") {
      rows = rows.filter((p) => p.active);
    } else if (statusFilter === "inactive") {
      rows = rows.filter((p) => !p.active);
    }

    const rowHtml = rows.map((p) => {
      const description = p.description ? escapeHtml(p.description) : "";
      const shortDesc = description.length > 120 ? `${description.slice(0, 120)}...` : description;
      const badge = p.active ? "badge-ok" : "badge-neutral";
      const status = p.active ? "Active" : "Inactive";
      const imageUrl = p.imageUrl ? encodeURI(p.imageUrl).replace(/'/g, "%27") : "";
      const location = p.locationCode || "-";
      const stockOnHand = Number(p.stockOnHand || 0);
      const stockAllocated = Number(p.stockAllocated || 0);
      const stockAvailable = Number(p.stockAvailable ?? (stockOnHand - stockAllocated));
      return `
        <tr>
          <td>
            <div class="product-thumb" style="${imageUrl ? `background-image:url('${imageUrl}')` : ""}">
              ${imageUrl ? "" : `<span>${escapeHtml(p.name?.slice(0, 1) || "P")}</span>`}
            </div>
          </td>
          <td>
            <div class="product-name">${escapeHtml(p.name)}</div>
            <div class="product-meta">
              <span class="font-mono">${escapeHtml(p.sku)}</span>
              <span class="text-muted">${escapeHtml(p.barcode || "No barcode")}</span>
            </div>
            <div class="product-desc">${shortDesc || "No description"}</div>
          </td>
          <td class="font-mono">${escapeHtml(location)}</td>
          <td>
            <div class="stock-stack">
              <div><span class="text-muted">Available</span> <strong>${stockAvailable}</strong></div>
              <div class="text-muted small">On hand ${stockOnHand} - Alloc ${stockAllocated}</div>
            </div>
          </td>
          <td><span class="badge ${badge}">${status}</span></td>
          <td class="font-mono">${formatDateTime(p.updatedAt || p.createdAt)}</td>
          <td>
            <div class="row row--start product-actions">
              <button class="btn btn-outline btn-sm" data-edit-id="${p.id}">Edit</button>
              <button class="btn btn-ghost btn-sm" data-toggle-id="${p.id}">
                ${p.active ? "Disable" : "Enable"}
              </button>
            </div>
          </td>
        </tr>
      `;
    });

    host.innerHTML = `
      <table class="table">
        <thead>
          <tr>
            <th>Image</th>
            <th>Product</th>
            <th>Location</th>
            <th>Stock</th>
            <th>Status</th>
            <th>Updated</th>
            <th></th>
          </tr>
        </thead>
        <tbody>${rowHtml.join("") || `<tr><td colspan="6" class="text-muted">No products found</td></tr>`}</tbody>
      </table>
    `;

    qsa("[data-edit-id]", root).forEach((btn) => {
      btn.addEventListener("click", () => {
        const id = Number(btn.getAttribute("data-edit-id"));
        const product = rows.find((item) => item.id === id);
        if (product) openProductModal(product);
      });
    });

    qsa("[data-toggle-id]", root).forEach((btn) => {
      btn.addEventListener("click", async () => {
        const id = Number(btn.getAttribute("data-toggle-id"));
        const product = rows.find((item) => item.id === id);
        if (!product) return;
        const action = product.active ? "disable" : "enable";
        if (!confirm(`Confirm ${action} ${product.sku}?`)) return;
        try {
          const payload = {
            sku: product.sku,
            name: product.name,
            description: product.description,
            barcode: product.barcode,
            imageUrl: product.imageUrl,
            locationCode: product.locationCode,
            stockOnHand: product.stockOnHand,
            active: !product.active
          };
          await apiPut(`/api/products/${product.id}`, payload);
          ui.toastOk("Product updated", `${product.sku} is now ${!product.active ? "Active" : "Inactive"}`);
          await loadProducts();
        } catch (e) {
          ui.toastError("Update failed", e.message);
        }
      });
    });
  }

  async function loadProducts() {
    const query = qs("#productQuery", root)?.value.trim() || "";
    const size = Number(qs("#productPageSize", root)?.value || 20);
    const status = qs("#productStatus", root)?.value || "all";

    state.query = query;
    state.size = size;
    state.status = status;

    qs("#productsTable", root).innerHTML = `<div class="text-muted">Loading products...</div>`;

    try {
      const data = await apiGet(`/api/products?query=${encodeURIComponent(query)}&page=${state.page}&size=${size}`);
      state.data = data;
      renderTable(data);
      const meta = qs("#productsMeta", root);
      const total = data.totalElements || 0;
      meta.textContent = `Showing ${Math.min(size, (data.content || []).length)} of ${total} products`;
    } catch (e) {
      qs("#productsTable", root).innerHTML = `<div class="text-muted">Error: ${e.message}</div>`;
    }
  }

  function openProductModal(product = null) {
    const isEdit = Boolean(product);
    const title = isEdit ? `Edit Product - ${product.sku}` : "Create Product";
    const bodyHtml = `
      <form id="productForm">
        <div class="form-group">
          <label class="label" for="productSku">SKU</label>
          <input class="input" id="productSku" value="${escapeHtml(product?.sku || "")}" required />
        </div>
        <div class="form-group">
          <label class="label" for="productName">Name</label>
          <input class="input" id="productName" value="${escapeHtml(product?.name || "")}" required />
        </div>
        <div class="form-group">
          <label class="label" for="productDescription">Description</label>
          <textarea class="textarea" id="productDescription" rows="3" placeholder="Short product description...">${escapeHtml(product?.description || "")}</textarea>
        </div>
        <div class="form-row">
          <div>
            <label class="label" for="productBarcode">Barcode</label>
            <input class="input" id="productBarcode" value="${escapeHtml(product?.barcode || "")}" />
          </div>
          <div>
            <label class="label" for="productImageUrl">Image URL</label>
            <input class="input" id="productImageUrl" value="${escapeHtml(product?.imageUrl || "")}" placeholder="https://..." />
          </div>
        </div>
        <div class="form-row">
          <div>
            <label class="label" for="productLocation">Location Code</label>
            <input class="input font-mono" id="productLocation" value="${escapeHtml(product?.locationCode || "")}" placeholder="A-01-01" />
          </div>
          <div>
            <label class="label" for="productStock">Stock On Hand</label>
            <input class="input" id="productStock" type="number" min="0" value="${product?.stockOnHand ?? ""}" placeholder="0" />
          </div>
        </div>
        <div class="row row--start mt-md">
          <input type="checkbox" id="productActive" ${product?.active === false ? "" : "checked"} />
          <label class="label" for="productActive" style="margin:0">Active product</label>
        </div>
      </form>
    `;

    const footerHtml = `
      <button type="button" class="btn btn-ghost" data-close-modal>Cancel</button>
      <button type="submit" class="btn btn-primary" form="productForm">
        ${isEdit ? "Save Changes" : "Create Product"}
      </button>
    `;

    ui.openModal({ title, bodyHtml, footerHtml });

    const form = qs("#productForm");
    form.addEventListener("submit", async (e) => {
      e.preventDefault();
      const stockValue = qs("#productStock").value;
      const stockOnHand = stockValue === "" ? null : Number(stockValue);
      if (stockValue !== "" && Number.isNaN(stockOnHand)) {
        ui.toastError("Invalid stock", "Stock on hand must be a number");
        return;
      }

      const payload = {
        sku: qs("#productSku").value.trim(),
        name: qs("#productName").value.trim(),
        description: qs("#productDescription").value.trim(),
        barcode: qs("#productBarcode").value.trim(),
        imageUrl: qs("#productImageUrl").value.trim(),
        locationCode: qs("#productLocation").value.trim() || null,
        stockOnHand,
        active: qs("#productActive").checked
      };

      if (!payload.sku || !payload.name) {
        ui.toastError("Missing fields", "SKU and Name are required");
        return;
      }

      try {
        if (isEdit) {
          await apiPut(`/api/products/${product.id}`, payload);
          ui.toastOk("Product updated", payload.sku);
        } else {
          await apiPost("/api/products", payload);
          ui.toastOk("Product created", payload.sku);
        }
        ui.closeModal();
        await loadProducts();
      } catch (err) {
        ui.toastError("Save failed", err.message);
      }
    });
  }

  qs("#btnCreateProduct", root)?.addEventListener("click", () => openProductModal());
  qs("#btnProductSearch", root)?.addEventListener("click", () => {
    state.page = 0;
    loadProducts();
  });
  qs("#productQuery", root)?.addEventListener("keydown", (e) => {
    if (e.key === "Enter") {
      state.page = 0;
      loadProducts();
    }
  });
  qs("#productStatus", root)?.addEventListener("change", () => loadProducts());
  qs("#productPageSize", root)?.addEventListener("change", () => {
    state.page = 0;
    loadProducts();
  });

  await loadProducts();
}
