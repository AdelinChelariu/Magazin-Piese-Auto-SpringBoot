let page = 0;
const size = 10;

let categories = [];
let manufacturers = [];
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

function buildQuery(){
    const p = new URLSearchParams();

    const q = (el("f_q","search","txtSearch")?.value || "").trim();
    const cat = el("f_category","f_categoryId")?.value || "";
    const man = el("f_manufacturer","f_manufacturerId")?.value || "";
    const active = el("f_active")?.value || "";

    if (q) p.set("q", q);
    if (cat) p.set("categoryId", cat);
    if (man) p.set("manufacturerId", man);
    if (active) p.set("active", active);

    p.set("page", String(page));
    p.set("size", String(size));
    p.set("sort", "name");
    p.set("dir", "asc");
    return p.toString();
}

function fillSelect(selectEl, items, allowEmpty){
    if (!selectEl) return;
    selectEl.innerHTML = "";
    if (allowEmpty) {
        const opt = document.createElement("option");
        opt.value = "";
        opt.textContent = "(all)";
        selectEl.appendChild(opt);
    }
    for (const it of items) {
        const opt = document.createElement("option");
        opt.value = it.id;
        opt.textContent = it.name;
        selectEl.appendChild(opt);
    }
}

function renderTable(items){
    const tbody = el("tbody","tableBody","productsBody");
    if (!tbody) throw new Error("Nu găsesc <tbody> (id='tbody' / 'tableBody' / 'productsBody').");

    tbody.innerHTML = "";
    for (const p of items) {
        const tr = document.createElement("tr");
        tr.innerHTML = `
      <td>${esc(p.sku)}</td>
      <td>${esc(p.name)}</td>
      <td>${esc(p.categoryName ?? "")}</td>
      <td>${esc(p.manufacturerName ?? "")}</td>
      <td>${p.price ?? ""}</td>
      <td>${p.stockQty ?? ""}</td>
      <td>${String(!!p.active)}</td>
      <td class="row gap">
        <button type="button" class="btn btn-secondary" data-action="edit" data-id="${p.id}">Edit</button>
        <button type="button" class="btn btn-secondary" data-action="del" data-id="${p.id}">Delete</button>
      </td>
    `;
        tbody.appendChild(tr);
    }
}

async function loadLookups(){
    categories = await Api.request("/api/categories");
    manufacturers = await Api.request("/api/manufacturers");

    fillSelect(el("p_categoryId","categoryId"), categories, false);
    fillSelect(el("p_manufacturerId","manufacturerId"), manufacturers, false);

    fillSelect(el("f_category","f_categoryId"), categories, true);
    fillSelect(el("f_manufacturer","f_manufacturerId"), manufacturers, true);
}

async function loadProducts(){
    setText(el("status"), "Loading...");
    const data = await Api.request(`/api/products?${buildQuery()}`);
    renderTable(data.content || []);
    setText(el("pageInfo"), `Page ${data.number + 1} / ${data.totalPages || 1}`);
    setText(el("status"), `Total: ${data.totalElements}`);

    const prev = el("prevPage");
    const next = el("nextPage");
    if (prev) prev.disabled = data.first;
    if (next) next.disabled = data.last;
}

function resetForm(){
    editingId = null;
    setText(el("formTitle","titleForm"), "Add Product");
    const btn = el("btnSave","btnAdd","btnSubmit");
    if (btn) btn.textContent = "Add";

    const sku = el("p_sku","sku");
    const name = el("p_name","name","productName");
    const price = el("p_price","price");
    const stock = el("p_stockQty","stockQty");
    const vat = el("p_vatRate","vatRate");
    const active = el("p_active","active");

    if (sku) sku.value = "";
    if (name) name.value = "";
    if (price) price.value = "";
    if (stock) stock.value = "";
    if (vat) vat.value = "19";
    if (active) active.value = "true";

    const cat = el("p_categoryId","categoryId");
    const man = el("p_manufacturerId","manufacturerId");
    if (cat && categories[0]) cat.value = categories[0].id;
    if (man && manufacturers[0]) man.value = manufacturers[0].id;
}

function formPayload(){
    const sku = (el("p_sku","sku")?.value || "").trim();
    const name = (el("p_name","name","productName")?.value || "").trim();
    const categoryId = Number(el("p_categoryId","categoryId")?.value);
    const manufacturerId = Number(el("p_manufacturerId","manufacturerId")?.value);
    const price = Number(el("p_price","price")?.value);
    const stockQty = Number(el("p_stockQty","stockQty")?.value);
    const vatRate = Number(el("p_vatRate","vatRate")?.value || 0);
    const active = (el("p_active","active")?.value || "true") === "true";

    if (!sku) throw new Error("SKU is required.");
    if (!name) throw new Error("Name is required.");
    if (!Number.isFinite(categoryId)) throw new Error("Category is required.");
    if (!Number.isFinite(manufacturerId)) throw new Error("Manufacturer is required.");
    if (!Number.isFinite(price)) throw new Error("Price is required.");
    if (!Number.isFinite(stockQty)) throw new Error("Stock is required.");

    return { sku, name, categoryId, manufacturerId, price, stockQty, vatRate, active };
}

async function saveProduct(){
    const payload = formPayload();
    if (editingId == null) {
        await requestFirst(
            ["/api/products/manage", "/api/products"],
            { method: "POST", json: payload }
        );
    } else {
        await requestFirst(
            [`/api/products/manage/${editingId}`, `/api/products/${editingId}`],
            { method: "PUT", json: payload }
        );
    }
    resetForm();
    await loadProducts();
}

async function editProduct(id){
    const p = await Api.request(`/api/products/${id}`);
    editingId = p.id;

    setText(el("formTitle","titleForm"), "Edit Product");
    const btn = el("btnSave","btnAdd","btnSubmit");
    if (btn) btn.textContent = "Save";

    el("p_sku","sku").value = p.sku ?? "";
    el("p_name","name","productName").value = p.name ?? "";
    el("p_categoryId","categoryId").value = p.categoryId ?? "";
    el("p_manufacturerId","manufacturerId").value = p.manufacturerId ?? "";
    el("p_price","price").value = p.price ?? "";
    el("p_stockQty","stockQty").value = p.stockQty ?? "";
    const vat = el("p_vatRate","vatRate"); if (vat) vat.value = p.vatRate ?? 19;
    const act = el("p_active","active"); if (act) act.value = String(!!p.active);
}

async function deleteProduct(id){
    if (!confirm("Delete this product?")) return;
    await requestFirst(
        [`/api/products/manage/${id}`, `/api/products/${id}`],
        { method: "DELETE" }
    );
    await loadProducts();
}

function wire(){
    el("btnLogout")?.addEventListener("click", Api.logout);

    // evită refresh dacă ai <form>
    el("form","productForm")?.addEventListener("submit", (e) => { e.preventDefault(); });

    el("btnSave","btnAdd","btnSubmit")?.addEventListener("click", async () => {
        try { await saveProduct(); }
        catch (e) { alert(String(e.message || e)); }
    });

    el("btnCancel","btnReset","btnClear")?.addEventListener("click", () => resetForm());

    el("btnApply","btnSearch")?.addEventListener("click", async () => { page = 0; await loadProducts(); });
    el("btnResetFilters")?.addEventListener("click", async () => {
        const q = el("f_q","search","txtSearch"); if (q) q.value = "";
        const c = el("f_category","f_categoryId"); if (c) c.value = "";
        const m = el("f_manufacturer","f_manufacturerId"); if (m) m.value = "";
        const a = el("f_active"); if (a) a.value = "";
        page = 0;
        await loadProducts();
    });

    el("prevPage")?.addEventListener("click", async () => { if (page > 0) page--; await loadProducts(); });
    el("nextPage")?.addEventListener("click", async () => { page++; await loadProducts(); });

    el("tbody","tableBody","productsBody")?.addEventListener("click", async (e) => {
        const btn = e.target.closest("button[data-action]");
        if (!btn) return;
        const id = Number(btn.dataset.id);
        try {
            if (btn.dataset.action === "edit") await editProduct(id);
            if (btn.dataset.action === "del") await deleteProduct(id);
        } catch (err) {
            alert(String(err.message || err));
        }
    });
}

(async function main(){
    await Api.me();
    wire();
    await loadLookups();
    resetForm();
    await loadProducts();
})();
