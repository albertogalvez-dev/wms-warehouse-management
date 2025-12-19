import { getApiBaseUrl, getStoredApiBaseUrlRaw, setApiBaseUrl } from "../config.js";
import { qs } from "../utils.js";

export async function renderSettings({ root, ui }) {
  const stored = getStoredApiBaseUrlRaw();
  const current = getApiBaseUrl();

  root.innerHTML = `
    <div class="grid grid-2">
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">API Settings</h3>
        </div>
        <div class="card-body">
          <p class="card-subtitle mb-lg">Configure the API endpoint used by this backoffice.</p>
          
          <div class="form-group">
            <label class="label">API Base URL</label>
            <input id="apiBaseUrl" class="input font-mono" placeholder="http://localhost:8080" value="${stored || current}" />
            <p class="help">Saved in localStorage. Leave empty to use default.</p>
          </div>

          <div class="row" style="gap: var(--space-sm)">
            <button class="btn btn-primary" id="btnSaveApi">Save Changes</button>
            <button class="btn btn-outline" id="btnTestApi">Test Connection</button>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="card-header">
          <h3 class="card-title">Configuration Tips</h3>
        </div>
        <div class="card-body">
          <div class="stack">
            <div class="card" style="background: var(--bg-soft); border: none; padding: var(--space-md)">
              <div class="font-medium mb-sm">🖥️ Local Development</div>
              <code class="font-mono text-muted">http://localhost:8080</code>
            </div>
            <div class="card" style="background: var(--bg-soft); border: none; padding: var(--space-md)">
              <div class="font-medium mb-sm">☁️ Oracle VPS</div>
              <code class="font-mono text-muted">http://your-server-ip:8080</code>
            </div>
            <div class="card" style="background: var(--bg-soft); border: none; padding: var(--space-md)">
              <div class="font-medium mb-sm">🔒 Production (via Nginx)</div>
              <code class="font-mono text-muted">(leave empty for relative /api/)</code>
            </div>
          </div>
        </div>
      </div>
    </div>
  `;

  qs("#btnSaveApi", root)?.addEventListener("click", () => {
    const value = qs("#apiBaseUrl", root)?.value || "";
    const newValue = setApiBaseUrl(value);
    ui.updateApiLabel();
    ui.toastOk("Saved", `API Base URL: ${newValue}`);
    ui.testConnection({ silent: true });
  });

  qs("#btnTestApi", root)?.addEventListener("click", () => ui.testConnection());
}
