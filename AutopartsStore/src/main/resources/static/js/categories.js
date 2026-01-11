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
            if (m.includes("HTTP 404")) continue; // încearcă următorul
            throw e; // altă eroare => o arătăm
        }
    }
    throw lastErr || new Error("No endpoint matched.");
}

async function loadCategories() {
    const status = el("status");
    setText(status, "Loading...");

    const qEl = el("f_q", "search", "txtSearch");
    const q = (qEl?.value || "").trim().toLowerCase();

    // 1) încearcă server-side (dacă ai paginare / q)
    try {
        const data = await Api.request(`/api/categories?q=${encodeURIComponent(q)}`);
        // dacă serverul întoarce listă
        all = Array.isArray(data) ? data : (data.content || []);
    } catch {
        // 2) fallback: fără parametri (cele mai multe implementări așa sunt)
        const data = await Api.request(`/api/categories`);
        all = Array.isArray(data) ? data : (data.content || []);
    }

    let view = all;
    if (q) view = all.filter(x => String(x.name ?? "").toLowerCase().includes(q));

    const tbody = el("tbody", "tableBody", "categoriesBody");
    if (!tbody) throw new Error("Nu găsesc <tbody> (id='tbody' / 'tableBody' / 'categoriesBody').");

    tbody.innerHTML = "";
    for (const c of view) {
        const tr = document.createElement("tr");
        tr.innerHTML = `
      <td>${c.id ?? ""}</td>
      <td>${esc(c.name)}</td>
      <td class="row gap">
        <button type="button" class="btn btn-secondary" data-action="edit" data-id="${c.id}">Edit</button>
        <button type="button" class="btn btn-secondary" data-action="del" data-id="${c.id}">Delete</button>
      </td>
    `;
        tbody.appendChild(tr);
    }

    setText(status, `Total: ${view.length}`);
}

function resetForm() {
    editingId = null;
    setText(el("formTitle", "titleForm"), "Add Category");
    const btn = el("btnSave", "btnAdd", "btnSubmit");
    if (btn) btn.textContent = "Add";
    const name = el("c_name", "name", "categoryName");
    if (name) name.value = "";
}

async function saveCategory() {
    const nameEl = el("c_name", "name", "categoryName");
    const name = (nameEl?.value || "").trim();
    if (!name) throw new Error("Name is required.");

    const payload = { name };

    if (editingId == null) {
        await requestFirst(
            ["/api/categories/manage", "/api/categories"],
            { method: "POST", json: payload }
        );
    } else {
        await requestFirst(
            [`/api/categories/manage/${editingId}`, `/api/categories/${editingId}`],
            { method: "PUT", json: payload }
        );
    }

    resetForm();
    await loadCategories();
}

async function editCategory(id) {
    // încearcă GET /{id}, altfel caută din listă
    let c = null;
    try { c = await Api.request(`/api/categories/${id}`); }
    catch { c = all.find(x => Number(x.id) === Number(id)); }

    if (!c) throw new Error("Category not found.");

    editingId = c.id;
    setText(el("formTitle", "titleForm"), "Edit Category");
    const btn = el("btnSave", "btnAdd", "btnSubmit");
    if (btn) btn.textContent = "Save";

    const name = el("c_name", "name", "categoryName");
    if (name) name.value = c.name ?? "";
}

async function deleteCategory(id) {
    if (!confirm("Delete this category?")) return;

    await requestFirst(
        [`/api/categories/manage/${id}`, `/api/categories/${id}`],
        { method: "DELETE" }
    );
    await loadCategories();
}

function wire() {
    el("btnLogout")?.addEventListener("click", Api.logout);

    // dacă ai form, evită submit refresh
    el("form", "categoryForm")?.addEventListener("submit", (e) => { e.preventDefault(); });

    el("btnSave", "btnAdd", "btnSubmit")?.addEventListener("click", async () => {
        try { await saveCategory(); }
        catch (e) { alert(String(e.message || e)); }
    });

    el("btnCancel", "btnReset", "btnClear")?.addEventListener("click", () => resetForm());

    el("btnApply", "btnSearch")?.addEventListener("click", async () => {
        try { await loadCategories(); }
        catch (e) { alert(String(e.message || e)); }
    });

    el("btnResetFilters")?.addEventListener("click", async () => {
        const qEl = el("f_q", "search", "txtSearch");
        if (qEl) qEl.value = "";
        await loadCategories();
    });

    el("tbody", "tableBody", "categoriesBody")?.addEventListener("click", async (e) => {
        const btn = e.target.closest("button[data-action]");
        if (!btn) return;
        const id = Number(btn.dataset.id);
        try {
            if (btn.dataset.action === "edit") await editCategory(id);
            if (btn.dataset.action === "del") await deleteCategory(id);
        } catch (err) {
            alert(String(err.message || err));
        }
    });
}

(async function main(){
    await Api.me();     // dacă nu ești logat -> redirect la login
    wire();
    resetForm();
    await loadCategories();
})();
