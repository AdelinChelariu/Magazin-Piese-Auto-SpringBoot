let page = 0;
const size = 10;
let categories = [];
let manufacturers = [];

function $(id){ return document.getElementById(id); }
function setStatus(msg){ $("status").textContent = msg || ""; }

function escapeHtml(s){
    return String(s ?? "").replaceAll("&","&amp;").replaceAll("<","&lt;").replaceAll(">","&gt;")
        .replaceAll('"',"&quot;").replaceAll("'","&#039;");
}

function fillSelect(selectEl, items, allowEmpty) {
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

function updateCartCount() {
    $("cartCount").textContent = String(CartStore.count());
}

function buildQuery(){
    const p = new URLSearchParams();
    const q = $("f_q").value.trim();
    if (q) p.set("q", q);

    const categoryId = $("f_category").value;
    if (categoryId) p.set("categoryId", categoryId);

    const manufacturerId = $("f_manufacturer").value;
    if (manufacturerId) p.set("manufacturerId", manufacturerId);

    const minPrice = $("f_minPrice").value;
    if (minPrice) p.set("minPrice", minPrice);

    const maxPrice = $("f_maxPrice").value;
    if (maxPrice) p.set("maxPrice", maxPrice);

    const minStock = $("f_minStock").value;
    if (minStock) p.set("minStock", minStock);

    p.set("active", "true"); // în shop arătăm doar active

    p.set("page", String(page));
    p.set("size", String(size));
    p.set("sort", "name");
    p.set("dir", "asc");
    return p.toString();
}

function renderTable(items){
    const tbody = $("tbody");
    tbody.innerHTML = "";

    for (const p of items) {
        const tr = document.createElement("tr");
        const canAdd = p.stockQty > 0;

        tr.innerHTML = `
      <td>${escapeHtml(p.sku)}</td>
      <td>${escapeHtml(p.name)}</td>
      <td>${escapeHtml(p.categoryName ?? "")}</td>
      <td>${escapeHtml(p.manufacturerName ?? "")}</td>
      <td>${p.price}</td>
      <td>${p.stockQty}</td>
      <td>
        <button class="btn btn-primary" data-action="add" data-id="${p.id}" ${canAdd ? "" : "disabled"}>
          Add
        </button>
      </td>
    `;
        tbody.appendChild(tr);
    }
}

async function loadLookups(){
    categories = await Api.request("/api/categories");
    manufacturers = await Api.request("/api/manufacturers");
    fillSelect($("f_category"), categories, true);
    fillSelect($("f_manufacturer"), manufacturers, true);
}

async function loadProducts(){
    setStatus("Loading...");
    const data = await Api.request(`/api/products?${buildQuery()}`);
    renderTable(data.content || []);
    $("pageInfo").textContent = `Page ${data.number + 1} / ${data.totalPages || 1}`;
    setStatus(`Total: ${data.totalElements}`);
    $("prevPage").disabled = data.first;
    $("nextPage").disabled = data.last;
}

function resetFilters(){
    $("f_q").value = "";
    $("f_category").value = "";
    $("f_manufacturer").value = "";
    $("f_minPrice").value = "";
    $("f_maxPrice").value = "";
    $("f_minStock").value = "";
    page = 0;
}

async function applyRoleUi(){
    const me = await Api.me();
    const roles = me.roles || [];
    const isAdmin = roles.includes("ROLE_ADMIN");
    const isEmp = roles.includes("ROLE_EMPLOYEE");

    $("lnkProducts").style.display = (isAdmin || isEmp) ? "" : "none";
    $("lnkCategories").style.display = (isAdmin || isEmp) ? "" : "none";
    $("lnkManufacturers").style.display = (isAdmin || isEmp) ? "" : "none";
    $("lnkLog").style.display = isAdmin ? "" : "none";
}

function wire(){
    $("btnLogout").addEventListener("click", Api.logout);
    $("btnApply").addEventListener("click", async () => { page = 0; await loadProducts(); });
    $("btnReset").addEventListener("click", async () => { resetFilters(); await loadProducts(); });

    $("prevPage").addEventListener("click", async () => { if (page > 0) page--; await loadProducts(); });
    $("nextPage").addEventListener("click", async () => { page++; await loadProducts(); });

    $("tbody").addEventListener("click", async (e) => {
        const btn = e.target.closest("button");
        if (!btn) return;
        if (btn.dataset.action !== "add") return;

        const id = Number(btn.dataset.id);
        const p = await Api.request(`/api/products/${id}`);
        CartStore.add(p, 1);
        updateCartCount();
    });
}

(async function main(){
    wire();
    await applyRoleUi();
    await loadLookups();
    updateCartCount();
    await loadProducts();
})();
