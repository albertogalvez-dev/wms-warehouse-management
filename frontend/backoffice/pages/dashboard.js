import { getApiBaseUrl } from "../config.js";
import { qs } from "../utils.js";

export async function renderDashboard({ root, ui }) {
  const api = getApiBaseUrl();
  root.innerHTML = `
    <div class="stats-grid">
      <a href="#/guide" class="stat-box stat-box-link">
        <div class="stat-box-icon">📚</div>
        <div class="stat-box-label">Quick Start Guide</div>
      </a>
      <div class="stat-box" id="apiStatusBox">
        <div class="stat-box-icon">🔌</div>
        <div class="stat-box-label">API Status</div>
        <div class="stat-box-status" id="apiStatusIndicator">Checking...</div>
      </div>
      <a href="${api}/swagger-ui/index.html" target="_blank" rel="noreferrer" class="stat-box stat-box-link">
        <div class="stat-box-icon">📖</div>
        <div class="stat-box-label">API Documentation</div>
      </a>
    </div>

    <div class="grid grid-3">
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">Quick Actions</h3>
        </div>
        <div class="card-body">
          <p class="card-subtitle mb-md">Start your warehouse operations.</p>
          <div class="button-grid">
            <a class="action-btn action-btn-primary" href="#/orders/new">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="16"/><line x1="8" y1="12" x2="16" y2="12"/></svg>
              <span>Create Order</span>
            </a>
            <a class="action-btn action-btn-secondary" href="#/waves">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 12h-4l-3 9L9 3l-3 9H2"/></svg>
              <span>Pick Waves</span>
            </a>
            <a class="action-btn action-btn-secondary" href="#/totes">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 16V8a2 2 0 00-1-1.73l-7-4a2 2 0 00-2 0l-7 4A2 2 0 003 8v8a2 2 0 001 1.73l7 4a2 2 0 002 0l7-4A2 2 0 0021 16z"/></svg>
              <span>Totes</span>
            </a>
            <a class="action-btn action-btn-secondary" href="#/shipments">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="1" y="3" width="15" height="13"/><polygon points="16 8 20 8 23 11 23 16 16 16 16 8"/><circle cx="5.5" cy="18.5" r="2.5"/><circle cx="18.5" cy="18.5" r="2.5"/></svg>
              <span>Shipments</span>
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
          <div class="api-endpoint-box">
            <code class="font-mono">${api || '(default)'}</code>
          </div>
          <div class="mt-md">
            <button class="btn btn-primary btn-block" id="btnTestConnection">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="23 4 23 10 17 10"/><polyline points="1 20 1 14 7 14"/><path d="M3.51 9a9 9 0 0114.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0020.49 15"/></svg>
              Test Connection
            </button>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="card-header">
          <h3 class="card-title">Resources</h3>
        </div>
        <div class="card-body">
          <p class="card-subtitle mb-md">Documentation and tools.</p>
          <div class="resource-links">
            <a class="resource-link" href="${api}/swagger-ui/index.html" target="_blank" rel="noreferrer">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14.5 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V7.5L14.5 2z"/><polyline points="14 2 14 8 20 8"/></svg>
              Swagger UI
            </a>
            <a class="resource-link" href="#/guide">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M9.09 9a3 3 0 015.83 1c0 2-3 3-3 3"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
              Quick Start Guide
            </a>
            <a class="resource-link" href="#/settings">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 010 2.83 2 2 0 01-2.83 0l-.06-.06a1.65 1.65 0 00-2.82.94v.24a2 2 0 01-4 0v-.09a1.65 1.65 0 00-1.08-.95 1.65 1.65 0 00-1.81.33l-.06.06a2 2 0 01-2.83 0 2 2 0 010-2.83l.06-.06a1.65 1.65 0 00.33-1.82"/></svg>
              API Settings
            </a>
          </div>
        </div>
      </div>
    </div>

    <style>
      .stat-box-link {
        text-decoration: none;
        color: inherit;
        cursor: pointer;
        transition: transform 0.15s, box-shadow 0.15s;
      }
      .stat-box-link:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0,0,0,0.1);
      }
      .stat-box-icon {
        font-size: 2rem;
        margin-bottom: 0.25rem;
      }
      .stat-box-status {
        margin-top: 0.25rem;
        font-size: 0.75rem;
        padding: 0.125rem 0.5rem;
        border-radius: 4px;
        background: #f3f4f6;
      }
      .stat-box-status.ok { background: #d1fae5; color: #059669; }
      .stat-box-status.error { background: #fee2e2; color: #dc2626; }
      
      .button-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 0.75rem;
      }
      .action-btn {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 0.5rem;
        padding: 1rem;
        border-radius: 8px;
        text-decoration: none;
        font-weight: 500;
        font-size: 0.875rem;
        transition: all 0.15s;
      }
      .action-btn-primary {
        background: var(--color-primary);
        color: white;
      }
      .action-btn-primary:hover {
        background: var(--color-primary-dark);
      }
      .action-btn-secondary {
        background: var(--bg-light);
        color: var(--text-primary);
        border: 1px solid var(--border-color);
      }
      .action-btn-secondary:hover {
        background: var(--bg-soft);
        border-color: var(--color-primary);
      }
      
      .api-endpoint-box {
        background: var(--bg-soft);
        padding: 0.75rem;
        border-radius: 6px;
        word-break: break-all;
      }
      
      .resource-links {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
      }
      .resource-link {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.625rem 0.75rem;
        background: var(--bg-light);
        border-radius: 6px;
        text-decoration: none;
        color: var(--text-primary);
        font-size: 0.875rem;
        transition: all 0.15s;
      }
      .resource-link:hover {
        background: var(--bg-soft);
        color: var(--color-primary);
      }
    </style>
  `;

  // Test connection on load
  const statusEl = qs("#apiStatusIndicator", root);
  try {
    const res = await fetch(`${api}/actuator/health`);
    if (res.ok) {
      statusEl.textContent = "✓ Connected";
      statusEl.classList.add("ok");
    } else {
      statusEl.textContent = "✗ Error";
      statusEl.classList.add("error");
    }
  } catch {
    statusEl.textContent = "✗ Offline";
    statusEl.classList.add("error");
  }

  qs("#btnTestConnection", root)?.addEventListener("click", () => ui.testConnection());
}
