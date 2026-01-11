let page = 0;
const size = 20;
let selectedOrderId = null;

function $(id){ return document.getElementById(id); }
function setStatus(msg){ $("status").textContent = msg || ""; }

function toIsoOrNull(dtLocal){
    if (!dtLocal) return null;
    return dtLocal.length === 16 ? dtLocal + ":00" : dtLocal;
}

function esc(s){
    return String(s ?? "")
        .replaceAll("&","&amp;").replaceAll("<","&lt;").replaceAll(">","&gt;")
        .replaceAll('"',"&quot;").replaceAll("'","&#039;");
}

function buildQuery(){
    const p = new URLSearchParams();
    const q = $("f_q").value.trim();
    const st = $("f_status").value;
    const from = toIsoOrNull($("f_from").value);
    const to = toIsoOrNull($("f_to").value);

    if (q) p.set("q", q);
    if (st) p.set("status", st);
    if (from) p.set("from", from);
    if (to) p.set("to", to);

    p.set("page", String(page));
    p.set("size", String(size));
    p.set("sort", "orderDate");
    p.set("dir", "desc");
    return p.toString();
}

function fmtTime(iso){
    if (!iso) return "";
    return iso.replace("T"," ").slice(0,19);
}

function renderRows(items){
    const tb = $("tbody");
    tb.innerHTML = "";
    for (const o of items) {
        const tr = document.createElement("tr");
        tr.innerHTML = `
      <td>${esc(o.orderNo)}</td>
      <td>${esc(fmtTime(o.orderDate))}</td>
      <td>${esc(o.status)}</td>
      <td>${esc((o.customerName ?? "") + " (" + (o.customerEmail ?? "") + ")")}</td>
      <td>${o.total ?? ""}</td>
      <td><button class="btn btn-secondary" data-id="${o.id}">Open</button></td>
    `;
        tb.appendChild(tr);
    }
}

async function loadList(){
    setStatus("Loading...");
    const data = await Api.request(`/api/orders/manage?${buildQuery()}`);
    renderRows(data.content || []);
    $("pageInfo").textContent = `Page ${data.number + 1} / ${data.totalPages || 1}`;
    setStatus(`Total: ${data.totalElements}`);
    $("prevPage").disabled = data.first;
    $("nextPage").disabled = data.last;
}

function resetFilters(){
    $("f_q").value = "";
    $("f_status").value = "";
    $("f_from").value = "";
    $("f_to").value = "";
    page = 0;
}

async function openDetails(id){
    const d = await Api.request(`/api/orders/manage/${id}`);
    selectedOrderId = d.id;

    $("detailsCard").style.display = "";
    $("d_status").value = d.status;

    $("detailsMeta").textContent =
        `${d.orderNo} | ${fmtTime(d.orderDate)} | ${d.customerName} (${d.customerEmail}) | Total: ${d.total}`;

    const body = $("itemsBody");
    body.innerHTML = "";
    for (const it of (d.items || [])) {
        const tr = document.createElement("tr");
        tr.innerHTML = `
      <td>${esc(it.sku)}</td>
      <td>${esc(it.name)}</td>
      <td>${it.quantity}</td>
      <td>${it.unitPrice}</td>
      <td>${it.lineTotal}</td>
    `;
        body.appendChild(tr);
    }
}

async function saveStatus(){
    if (!selectedOrderId) return;
    const st = $("d_status").value;
    await Api.request(`/api/orders/manage/${selectedOrderId}/status`, {
        method: "PUT",
        json: { status: st }
    });
    await loadList();
    await openDetails(selectedOrderId);
    alert("Status updated.");
}

function wire(){
    $("btnLogout").addEventListener("click", Api.logout);

    $("btnApply").addEventListener("click", async () => { page = 0; await loadList(); });
    $("btnReset").addEventListener("click", async () => { resetFilters(); await loadList(); });

    $("prevPage").addEventListener("click", async () => { if (page > 0) page--; await loadList(); });
    $("nextPage").addEventListener("click", async () => { page++; await loadList(); });

    $("tbody").addEventListener("click", async (e) => {
        const btn = e.target.closest("button[data-id]");
        if (!btn) return;
        await openDetails(Number(btn.dataset.id));
    });

    $("btnSaveStatus").addEventListener("click", saveStatus);
}

(async function main(){
    wire();
    await loadList();
})();
