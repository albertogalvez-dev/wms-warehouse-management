import { getApiBaseUrl } from "../config.js";
import { qs } from "../utils.js";

export async function renderDashboard({ root, ui }) {
  const api = getApiBaseUrl();
  root.innerHTML = `
    <div class="grid grid--3">
      <div class="card">
        <div class="card__title">Quick Start</div>
        <p class="card__subtitle">Operate the WMS end-to-end.</p>
        <div class="divider"></div>
        <div class="row row--start" style="gap:10px">
          <a class="btn btn--primary" href="#/orders/new">Create Order</a>
          <a class="btn" href="#/waves">Pick Waves</a>
        </div>
      </div>
      <div class="card">
        <div class="card__title">API</div>
        <p class="card__subtitle">Current Base URL</p>
        <div class="divider"></div>
        <div class="mono">${api}</div>
        <div class="divider"></div>
        <button class="btn btn--secondary" id="btnTestConnection">Test connection</button>
      </div>
      <div class="card">
        <div class="card__title">Swagger</div>
        <p class="card__subtitle">Useful for handheld flows and debugging.</p>
        <div class="divider"></div>
        <a class="btn btn--secondary" href="${api}/swagger-ui/index.html" target="_blank" rel="noreferrer">Open Swagger</a>
      </div>
    </div>
  `;

  qs("#btnTestConnection", root)?.addEventListener("click", () => ui.testConnection());
}

