import { api } from "./api.js";
import { saveApiBaseUrl, getApiBaseUrl } from "./config.js";
import { isAuthenticated, getUser, logout } from "../shared/auth.js";

// Auth check - redirect to login if not authenticated
if (!isAuthenticated()) {
    window.location.href = './login.html';
    throw new Error('Not authenticated');
}

// Display user info in header
const user = getUser();
window.currentUser = user;

// Logout handler
window.handleLogout = () => {
    logout();
    window.location.href = './login.html';
};

// STATE
let state = {
    stationId: localStorage.getItem("wms_station_id") || null,
    operator: localStorage.getItem("wms_operator") || "Operator",
    sessionId: null,
    shipmentId: null,
    lines: [],
    toteInfo: {},
    packages: []
};

// DOM ELEMENTS
const views = {
    login: document.getElementById("viewLogin"),
    start: document.getElementById("viewStart"),
    packing: document.getElementById("viewPacking"),
    packages: document.getElementById("viewPackages"),
    complete: document.getElementById("viewComplete")
};

const inputs = {
    apiUrl: document.getElementById("apiUrlInput"),
    station: document.getElementById("stationSelect"),
    operator: document.getElementById("operatorName"),
    tote: document.getElementById("toteInput"),
    product: document.getElementById("productInput"),
    packageCount: document.getElementById("packageCountDisplay")
};

// INIT
function init() {
    inputs.apiUrl.value = getApiBaseUrl();

    if (state.stationId) {
        inputs.station.value = state.stationId;
        inputs.operator.value = state.operator;
        showView("start");
        updateHeader();
    } else {
        showView("login");
    }
}

// NAVIGATION
function showView(viewName) {
    Object.values(views).forEach(el => el.classList.add("hidden"));
    views[viewName].classList.remove("hidden");

    if (viewName === "start") {
        inputs.tote.value = "";
        setTimeout(() => inputs.tote.focus(), 100);
    } else if (viewName === "packing") {
        inputs.product.value = "";
        setTimeout(() => inputs.product.focus(), 100);
    }
}

function adjustPackages(delta) {
    const current = parseInt(inputs.packageCount.textContent, 10) || 1;
    let next = current + delta;
    if (next < 1) next = 1;
    if (next > 20) next = 20;
    inputs.packageCount.textContent = String(next);
}

window.showView = showView;
window.adjustPackages = adjustPackages;

function updateHeader() {
    const label = inputs.station.options[inputs.station.selectedIndex]?.text || "Unknown";
    document.getElementById("stationDisplay").textContent = `Station: ${label} | Op: ${state.operator}`;
}

function toast(msg, type = "info") {
    const el = document.getElementById("toast");
    el.textContent = msg;
    el.classList.add("show");
    el.style.backgroundColor = type === "error" ? "#dc2626" : (type === "success" ? "#16a34a" : "#374151");
    setTimeout(() => el.classList.remove("show"), 3000);
}

// LOGIC

// 1. LOGIN / SETUP
document.getElementById("btnInitStation").addEventListener("click", () => {
    const stationId = inputs.station.value;
    const operator = inputs.operator.value;
    const url = inputs.apiUrl.value.trim();

    saveApiBaseUrl(url);
    localStorage.setItem("wms_station_id", stationId);
    localStorage.setItem("wms_operator", operator);

    state.stationId = stationId;
    state.operator = operator;

    updateHeader();
    showView("start");
});

// 2. START SESSION
async function startSession() {
    const toteBarcode = inputs.tote.value.trim();
    if (!toteBarcode) return;

    const errorEl = document.getElementById("startError");
    errorEl.textContent = "";
    errorEl.classList.add("hidden");

    try {
        const res = await api.post("/api/packing/sessions/start", {
            toteBarcode: toteBarcode,
            stationId: parseInt(state.stationId),
            operator: state.operator
        });

        state.sessionId = res.sessionId;
        state.lines = res.lines;
        state.packages = [];
        state.shipmentId = res.shipmentId || null;
        state.toteInfo = {
            tote: res.toteBarcode,
            order: res.externalRef,
            carrier: res.carrier
        };

        renderPackingUI();

        // Determine View based on mode
        if (res.mode === "SET_PACKAGES") {
            showView("packages");
        } else if (res.mode === "READY_TO_COMPLETE") {
            await completeSession();
            await loadShipmentPackages();
            renderCompleteUI();
            showView("complete");
        } else {
            showView("packing");
        }

    } catch (err) {
        errorEl.textContent = err.message || "Failed to start session";
        errorEl.classList.remove("hidden");
        inputs.tote.value = "";
        inputs.tote.focus();
        toast("Error starting session", "error");
    }
}

document.getElementById("btnStartSession").addEventListener("click", startSession);
inputs.tote.addEventListener("keydown", (e) => {
    if (e.key === "Enter") startSession();
});

// 3. PACKING (SCAN)
function renderPackingUI() {
    document.getElementById("packToteRef").textContent = `Tote: ${state.toteInfo.tote}`;
    document.getElementById("packOrderRef").textContent = `Order: ${state.toteInfo.order} (${state.toteInfo.carrier})`;

    const listEl = document.getElementById("packingList");
    listEl.innerHTML = "";

    let totalReq = 0;
    let totalPacked = 0;

    state.lines.forEach(line => {
        totalReq += line.requiredQty;
        totalPacked += line.packedQty;

        const isDone = line.packedQty >= line.requiredQty;

        const div = document.createElement("div");
        div.className = `hh-item ${isDone ? "complete" : ""}`;
        div.innerHTML = `
      <div class="hh-item-info">
        <div class="hh-item-sku">${line.sku}</div>
        <div class="hh-item-name">${line.productName}</div>
      </div>
      <div class="hh-item-qty">${line.packedQty}/${line.requiredQty}</div>
    `;
        listEl.appendChild(div);
    });

    const percent = totalReq ? Math.round((totalPacked / totalReq) * 100) : 0;
    document.getElementById("packProgress").textContent = `${percent}%`;

    const isAllDone = totalReq > 0 && totalPacked >= totalReq;
    const btnNext = document.getElementById("btnFinishScan");

    if (isAllDone) {
        btnNext.classList.remove("hidden");
        const scanMsg = document.getElementById("scanMsg");
        scanMsg.textContent = "All items packed! Proceed to box setup.";
        scanMsg.classList.remove("text-err");
        scanMsg.classList.add("text-ok");
    } else {
        btnNext.classList.add("hidden");
    }
}

async function scanProduct() {
    const code = inputs.product.value.trim();
    if (!code) return;

    const msgEl = document.getElementById("scanMsg");
    msgEl.textContent = "";
    msgEl.classList.remove("text-err", "text-ok");

    try {
        const res = await api.post(`/api/packing/sessions/${state.sessionId}/scan`, {
            code: code,
            qty: 1
        });

        state.lines = res.lines;
        renderPackingUI();

        if (res.lastScanResult) {
            msgEl.textContent = res.lastScanResult.message || "";
            if (res.lastScanResult.status === "ERROR") {
                msgEl.classList.add("text-err");
                toast("Scan Error", "error");
                inputs.product.select();
            } else {
                msgEl.classList.add("text-ok");
                inputs.product.value = "";
            }
        }

    } catch (err) {
        msgEl.textContent = err.message || "Scan failed";
        msgEl.classList.add("text-err");
        inputs.product.select();
        toast(err.message || "Scan failed", "error");
    }
}

inputs.product.addEventListener("keydown", (e) => {
    if (e.key === "Enter") scanProduct();
});

document.getElementById("btnFinishScan").addEventListener("click", () => showView("packages"));


// 4. SET PACKAGES
document.getElementById("btnConfirmPackages").addEventListener("click", async () => {
    const count = parseInt(inputs.packageCount.textContent);

    try {
        const res = await api.post(`/api/packing/sessions/${state.sessionId}/set-packages`, {
            packageCount: count
        });

        state.packages = res.packages || [];
        state.shipmentId = res.shipmentId || state.shipmentId;

        // Auto-complete session after generating labels
        await completeSession();

        // Show success view
        if (!state.packages.length) {
            await loadShipmentPackages();
        }
        renderCompleteUI();
        showView("complete");

    } catch (err) {
        toast(err.message, "error");
    }
});

// 5. COMPLETE
async function completeSession() {
    await api.post(`/api/packing/sessions/${state.sessionId}/complete`, {});
}

async function loadShipmentPackages() {
    if (!state.shipmentId) return;
    try {
        const shipment = await api.get(`/api/shipments/${state.shipmentId}`);
        state.packages = shipment.packages || [];
    } catch (err) {
        toast(err.message || "Failed to load labels", "error");
    }
}

function renderCompleteUI() {
    document.getElementById("doneOrderRef").textContent = state.toteInfo.order || "";
    const list = document.getElementById("labelsList");
    list.innerHTML = "";

    if (!state.packages || state.packages.length === 0) {
        const empty = document.createElement("div");
        empty.style.textAlign = "center";
        empty.style.color = "var(--muted)";
        empty.textContent = "No labels available.";
        list.appendChild(empty);
        return;
    }

    state.packages.forEach((pkg) => {
        const packageId = pkg.packageId || pkg.id;
        const card = document.createElement("div");
        card.className = "hh-card hh-label-card";

        const body = document.createElement("div");
        body.className = "hh-card-body";

        const row = document.createElement("div");
        row.className = "hh-label-row";

        const title = document.createElement("div");
        title.className = "hh-label-title";
        title.textContent = `Box ${pkg.packageNo}`;

        const tracking = document.createElement("div");
        tracking.className = "hh-label-tracking";
        tracking.textContent = pkg.trackingCode || "";

        row.appendChild(title);
        row.appendChild(tracking);
        body.appendChild(row);

        if (pkg.labelZplPreview) {
            const preview = document.createElement("textarea");
            preview.className = "hh-label-preview";
            preview.readOnly = true;
            preview.value = pkg.labelZplPreview;
            body.appendChild(preview);
        }

        const meta = document.createElement("div");
        meta.className = "hh-label-meta";
        meta.textContent = `Format: ${pkg.labelFormat || "ZPL"}`;
        body.appendChild(meta);

        const btn = document.createElement("button");
        btn.className = "hh-btn hh-btn-ghost hh-btn-block";
        btn.textContent = "Download ZPL";
        if (!state.shipmentId || !packageId) {
            btn.disabled = true;
        } else {
            btn.addEventListener("click", () => {
                const url = `${getApiBaseUrl()}/api/shipments/${state.shipmentId}/packages/${packageId}/label.zpl`;
                window.open(url, "_blank");
            });
        }

        body.appendChild(btn);
        card.appendChild(body);
        list.appendChild(card);
    });
}

document.getElementById("btnNextTote").addEventListener("click", () => {
    state.sessionId = null;
    state.shipmentId = null;
    state.lines = [];
    state.toteInfo = {};
    state.packages = [];
    showView("start");
});


// RUN
init();
