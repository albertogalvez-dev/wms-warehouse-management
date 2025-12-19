export function parseHash() {
  const raw = window.location.hash || "";
  const hash = raw.startsWith("#") ? raw.slice(1) : raw;
  const value = hash || "/dashboard";

  const [pathPart, queryPart] = value.split("?");
  const path = pathPart.startsWith("/") ? pathPart : `/${pathPart}`;

  const query = {};
  if (queryPart) {
    const params = new URLSearchParams(queryPart);
    for (const [k, v] of params.entries()) query[k] = v;
  }

  return { path, query, raw: value };
}

export function createRouter({ routes, onRoute }) {
  async function handleRoute() {
    const current = parseHash();
    for (const route of routes) {
      const match = route.pattern ? current.path.match(route.pattern) : current.path === route.path ? [] : null;
      if (!match) continue;

      const params = route.getParams ? route.getParams(match) : {};
      await onRoute({ ...current, route, params });
      return;
    }

    window.location.hash = "#/dashboard";
  }

  function start() {
    window.addEventListener("hashchange", handleRoute);
    handleRoute();
  }

  return { start, handleRoute };
}

