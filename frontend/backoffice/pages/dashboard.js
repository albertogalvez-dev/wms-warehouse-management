import { getApiBaseUrl } from "../config.js";
import { qs } from "../utils.js";

export async function renderDashboard({ root, ui }) {
  const api = getApiBaseUrl();
  root.innerHTML = `
    <div class="stats-grid">
      <div class="stat-box">
        <div class="stat-box-value">📦</div>
        <div class="stat-box-label">Quick Start</div>
      </div>
      <div class="stat-box">
        <div class="stat-box-value">🔌</div>
        <div class="stat-box-label">API Status</div>
      </div>
      <div class="stat-box">
        <div class="stat-box-value">📄</div>
        <div class="stat-box-label">Documentation</div>
      </div>
    </div>

    <div class="grid grid-3">
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">Quick Start</h3>
        </div>
        <div class="card-body">
          <p class="card-subtitle mb-md">Operate the WMS end-to-end workflow.</p>
          <div class="stack-sm">
            <a class="btn btn-primary btn-block" href="#/orders/new">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="16"/><line x1="8" y1="12" x2="16" y2="12"/></svg>
              Create Order
            </a>
            <a class="btn btn-outline btn-block" href="#/waves">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 12h-4l-3 9L9 3l-3 9H2"/></svg>
              Pick Waves
            </a>
            <a class="btn btn-ghost btn-block" href="#/shipments">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="1" y="3" width="15" height="13"/><polygon points="16 8 20 8 23 11 23 16 16 16 16 8"/></svg>
              Shipments
            </a>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="card-header">
          <h3 class="card-title">API Connection</h3>
        </div>
        <div class="card-body">
          <p class="card-subtitle mb-md">Current API endpoint</p>
          <div class="card" style="background:var(--bg-soft); border:none; padding:var(--space-md)">
            <code class="font-mono" style="word-break:break-all">${api || '(default)'}</code>
          </div>
          <div class="mt-md">
            <button class="btn btn-primary btn-block" id="btnTestConnection">
              Test Connection
            </button>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="card-header">
          <h3 class="card-title">Documentation</h3>
        </div>
        <div class="card-body">
          <p class="card-subtitle mb-md">API reference and testing tools.</p>
          <div class="stack-sm">
            <a class="btn btn-outline btn-block" href="${api}/swagger-ui/index.html" target="_blank" rel="noreferrer">
              Open Swagger UI
            </a>
            <a class="btn btn-ghost btn-block" href="#/settings">
              API Settings
            </a>
          </div>
        </div>
      </div>
    </div>
  `;

  qs("#btnTestConnection", root)?.addEventListener("click", () => ui.testConnection());
}
