let page = 0;
const size = 25;

function $(id) { return document.getElementById(id); }
function setStatus(msg) { $("status").textContent = msg || ""; }

function escapeHtml(s) {
    return String(s ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

function toIsoOrNull(datetimeLocalValue) {
    // input datetime-local: "2026-01-07T18:05"
    // API expects ISO: "2026-01-07T18:05:00"
    if (!datetimeLocalValue) return null;
    return datetimeLocalValue.length === 16 ? (datetimeLocalValue + ":00") : datetimeLocalValue;
}

function buildQuery() {
    const p = new URLSearchParams();

    const username = $("f_username").value.trim();
    const action = $("f_action").value.trim();
    const entity = $("f_entity").value.trim();
    const success = $("f_success").value;
    const from = $("f_from").value;
    const to = $("f_to").value;

    if (username) p.set("username", username);
    if (action) p.set("action", action);
    if (entity) p.set("entity", entity);
    if (success) p.set("success", success);

    const fromIso = toIsoOrNull(from);
    const toIso = toIsoOrNull(to);
    if (fromIso) p.set("from", fromIso);
    if (toIso) p.set("to", toIso);

    p.set("page", String(page));
    p.set("size", String(size));

    p.set("sort", $("sortField").value);
    p.set("dir", $("sortDir").value);

    return p.toString();
}

function formatTime(iso) {
    if (!iso) return "";
    // iso e deja string; îl afișăm scurt
    return iso.replace("T", " ").slice(0, 19);
}

function renderTable(items) {
    const tbody = $("tbody");
    tbody.innerHTML = "";

    for (const x of items) {
        const tr = document.createElement("tr");
        tr.innerHTML = `
      <td>${x.id}</td>
      <td>${escapeHtml(formatTime(x.createdAt))}</td>
      <td>${escapeHtml(x.username ?? "")}</td>
      <td>${escapeHtml(x.roleSnapshot ?? "")}</td>
      <td>${escapeHtml(x.action ?? "")}</td>
      <td>${escapeHtml(x.entity ?? "")}</td>
      <td>${x.entityId ?? ""}</td>
      <td>${x.success}</td>
      <td>${escapeHtml(x.ipAddress ?? "")}</td>
      <td title="${escapeHtml(x.errorMessage ?? "")}">
        ${escapeHtml((x.errorMessage ?? "").slice(0, 60))}
      </td>
    `;
        tbody.appendChild(tr);
    }
}

async function loadLogs() {
    setStatus("Loading...");
    const qs = buildQuery();
    const data = await Api.request(`/api/activity-log?${qs}`);

    renderTable(data.content || []);
    $("pageInfo").textContent = `Page ${data.number + 1} / ${data.totalPages || 1}`;
    setStatus(`Total: ${data.totalElements}`);

    $("prevPage").disabled = data.first;
    $("nextPage").disabled = data.last;
}

function resetFilters() {
    $("f_username").value = "";
    $("f_action").value = "";
    $("f_entity").value = "";
    $("f_success").value = "";
    $("f_from").value = "";
    $("f_to").value = "";
    page = 0;
}

async function ensureLogin() {
    if (Api.getToken()) return true;
    return await Api.loginPrompt();
}

function wireEvents() {
    $("btnLogout").addEventListener("click", Api.logout);

    $("btnApply").addEventListener("click", async () => {
        page = 0;
        await loadLogs();
    });

    $("btnReset").addEventListener("click", async () => {
        resetFilters();
        await loadLogs();
    });

    $("prevPage").addEventListener("click", async () => {
        if (page > 0) page--;
        await loadLogs();
    });

    $("nextPage").addEventListener("click", async () => {
        page++;
        await loadLogs();
    });

    $("sortField").addEventListener("change", async () => {
        page = 0;
        await loadLogs();
    });

    $("sortDir").addEventListener("change", async () => {
        page = 0;
        await loadLogs();
    });
}

(async function main() {
    wireEvents();
    const ok = await ensureLogin();
    if (ok) await loadLogs();
    else setStatus("Not logged in. Click Login.");
})();
