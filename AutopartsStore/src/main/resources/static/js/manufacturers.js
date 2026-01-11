let all = [];
let editingId = null;

function el(...ids) {
    for (const id of ids) {
        const x = document.getElementById(id);
        if (x) return x;
    }
    return null;
}
function setText(node, msg){ if (node) node.textContent = msg || ""; }
function esc(s){
    return String(s ?? "")
        .replaceAll("&","&amp;").replaceAll("<","&lt;").replaceAll(">","&gt;")
        .replaceAll('"',"&quot;").replaceAll("'","&#039;");
}

async function requestFirst(paths, options){
    let lastErr = null;
    for (const p of paths) {
        try { return await Api.request(p, options); }
        catch (e) {
            lastErr = e;
            const m = String(e?.message || e);
            if (m.includes("HTTP 404")) continue;
            throw e;
        }
    }
    throw lastErr || new Error("No endpoint matched.");
}

async function loadManufacturers(){
    const status = el("status");
    setText(status, "Loading...");

    const qEl = el("f_q", "search", "txtSearch");
    const q = (qEl?.value || "").trim().toLowerCase();

    try {
        const data = await Api.request(`/api/manufacturers?q=${encodeURIComponent(q)}`);
        all = Array.isArray(data) ? data : (data.content || []);
    } catch {
        const data = await Api.request(`/api/manufacturers`);
        all = Array.isArray(data) ? data : (data.content || []);
    }

    let view = all;
    if (q) view = all.filter(x =>
        String(x.name ?? "").toLowerCase().includes(q) ||
        String(x.country ?? "").toLowerCase().includes(q)
    );

    const tbody = el("tbody", "tableBody", "manufacturersBody");
    if (!tbody) throw new Error("Nu găsesc <tbody> (id='tbody' / 'tableBody' / 'manufacturersBody').");

    tbody.innerHTML = "";
    for (const m of view) {
        const tr = document.createElement("tr");
        tr.innerHTML = `
      <td>${m.id ?? ""}</td>
      <td>${esc(m.name)}</td>
      <td>${esc(m.country ?? "")}</td>
      <td class="row gap">
        <button type="button" class="btn btn-secondary" data-action="edit" data-id="${m.id}">Edit</button>
        <button type="button" class="btn btn-secondary" data-action="del" data-id="${m.id}">Delete</button>
      </td>
    `;
        tbody.appendChild(tr);
    }

    setText(status, `Total: ${view.length}`);
}

function resetForm(){
    editingId = null;
    setText(el("formTitle", "titleForm"), "Add Manufacturer");
    const btn = el("btnSave", "btnAdd", "btnSubmit");
    if (btn) btn.textContent = "Add";

    const name = el("m_name", "name", "manufacturerName");
    if (name) name.value = "";
    const country = el("m_country", "country", "manufacturerCountry");
    if (country) country.value = "";
}

async function saveManufacturer(){
    const nameEl = el("m_name", "name", "manufacturerName");
    const countryEl = el("m_country", "country", "manufacturerCountry");

    const name = (nameEl?.value || "").trim();
    const country = (countryEl?.value || "").trim() || null;

    if (!name) throw new Error("Name is required.");
    const payload = { name, country };

    if (editingId == null) {
        await requestFirst(
            ["/api/manufacturers/manage", "/api/manufacturers"],
            { method: "POST", json: payload }
        );
    } else {
        await requestFirst(
            [`/api/manufacturers/manage/${editingId}`, `/api/manufacturers/${editingId}`],
            { method: "PUT", json: payload }
        );
    }

    resetForm();
    await loadManufacturers();
}

async function editManufacturer(id){
    let m = null;
    try { m = await Api.request(`/api/manufacturers/${id}`); }
    catch { m = all.find(x => Number(x.id) === Number(id)); }
    if (!m) throw new Error("Manufacturer not found.");

    editingId = m.id;
    setText(el("formTitle", "titleForm"), "Edit Manufacturer");
    const btn = el("btnSave", "btnAdd", "btnSubmit");
    if (btn) btn.textContent = "Save";

    const name = el("m_name", "name", "manufacturerName");
    if (name) name.value = m.name ?? "";
    const country = el("m_country", "country", "manufacturerCountry");
    if (country) country.value = m.country ?? "";
}

async function deleteManufacturer(id){
    if (!confirm("Delete this manufacturer?")) return;

    await requestFirst(
        [`/api/manufacturers/manage/${id}`, `/api/manufacturers/${id}`],
        { method: "DELETE" }
    );
    await loadManufacturers();
}

function wire(){
    el("btnLogout")?.addEventListener("click", Api.logout);
    el("form", "manufacturerForm")?.addEventListener("submit", (e) => { e.preventDefault(); });

    el("btnSave", "btnAdd", "btnSubmit")?.addEventListener("click", async () => {
        try { await saveManufacturer(); }
        catch (e) { alert(String(e.message || e)); }
    });

    el("btnCancel", "btnReset", "btnClear")?.addEventListener("click", () => resetForm());

    el("btnApply", "btnSearch")?.addEventListener("click", async () => {
        try { await loadManufacturers(); }
        catch (e) { alert(String(e.message || e)); }
    });

    el("btnResetFilters")?.addEventListener("click", async () => {
        const qEl = el("f_q", "search", "txtSearch");
        if (qEl) qEl.value = "";
        await loadManufacturers();
    });

    el("tbody", "tableBody", "manufacturersBody")?.addEventListener("click", async (e) => {
        const btn = e.target.closest("button[data-action]");
        if (!btn) return;
        const id = Number(btn.dataset.id);
        try {
            if (btn.dataset.action === "edit") await editManufacturer(id);
            if (btn.dataset.action === "del") await deleteManufacturer(id);
        } catch (err) {
            alert(String(err.message || err));
        }
    });
}

(async function main(){
    await Api.me();
    wire();
    resetForm();
    await loadManufacturers();
})();
