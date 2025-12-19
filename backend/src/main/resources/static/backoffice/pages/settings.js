import { getApiBaseUrl, getStoredApiBaseUrlRaw, setApiBaseUrl } from "../config.js";
import { qs } from "../utils.js";

export async function renderSettings({ root, ui }) {
  const stored = getStoredApiBaseUrlRaw();
  const current = getApiBaseUrl();

  root.innerHTML = `
    <div class="grid grid--2">
      <div class="card">
        <div class="card__title">API Settings</div>
        <p class="card__subtitle">Used by all API calls in this backoffice.</p>
        <div class="divider"></div>

        <div class="field">
          <label for="apiBaseUrl">API Base URL</label>
          <input id="apiBaseUrl" class="input mono" placeholder="http://localhost:8080" value="${stored || current}" />
          <div class="help">Saved in localStorage. Leave empty to use default.</div>
        </div>

        <div class="row" style="margin-top:12px">
          <button class="btn btn--primary" id="btnSaveApi">Save</button>
          <button class="btn btn--secondary" id="btnTestApi">Test connection</button>
        </div>
      </div>

      <div class="card">
        <div class="card__title">Tips</div>
        <ul class="muted" style="margin:0; padding-left:18px; line-height:1.6">
          <li>Local: <span class="mono">http://localhost:8080</span></li>
          <li>Oracle VPS: set to your public host (e.g. <span class="mono">http://1.2.3.4:8080</span>)</li>
          <li>Serve this UI via Nginx as static files.</li>
        </ul>
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

