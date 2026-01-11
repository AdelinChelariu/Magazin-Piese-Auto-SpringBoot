let catChart = null;

function $(id){ return document.getElementById(id); }
function setStatus(msg){ $("status").textContent = msg || ""; }

function toIso(dtLocal){
    // datetime-local: "YYYY-MM-DDTHH:mm" -> "YYYY-MM-DDTHH:mm:00"
    if (!dtLocal) return "";
    return dtLocal.length === 16 ? (dtLocal + ":00") : dtLocal;
}

function setLast30Days(){
    const now = new Date();
    const to = new Date(now);
    const from = new Date(now);
    from.setDate(from.getDate() - 30);

    // format pentru datetime-local (fără secunde)
    const fmt = (d) => {
        const pad = (n) => String(n).padStart(2, "0");
        return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
    };

    $("from").value = fmt(from);
    $("to").value = fmt(to);
}

function renderSummary(s){
    $("s_orders").textContent = s.ordersCount ?? 0;
    $("s_subtotal").textContent = s.subtotal ?? 0;
    $("s_vat").textContent = s.vatTotal ?? 0;
    $("s_total").textContent = s.total ?? 0;
}

function esc(s){
    return String(s ?? "")
        .replaceAll("&","&amp;").replaceAll("<","&lt;").replaceAll(">","&gt;")
        .replaceAll('"',"&quot;").replaceAll("'","&#039;");
}

function renderCategoryTable(rows){
    const body = $("catBody");
    body.innerHTML = "";
    for (const r of rows) {
        const tr = document.createElement("tr");
        tr.innerHTML = `
      <td>${esc(r.categoryName)}</td>
      <td>${r.qty ?? 0}</td>
      <td>${r.subtotal ?? 0}</td>
      <td>${r.vat ?? 0}</td>
      <td>${r.total ?? 0}</td>
    `;
        body.appendChild(tr);
    }
}

function renderCategoryChart(rows){
    const labels = rows.map(r => r.categoryName);
    const values = rows.map(r => Number(r.total ?? 0));

    const ctx = $("catChart").getContext("2d");
    if (catChart) catChart.destroy();

    catChart = new Chart(ctx, {
        type: "bar",
        data: {
            labels,
            datasets: [{ label: "Total sales", data: values }]
        },
        options: {
            responsive: true,
            plugins: { legend: { display: true } },
            scales: { y: { beginAtZero: true } }
        }
    });
}

function download(url){
    window.location.href = url; // browserul descarcă pentru ResponseEntity attachment
}

function qs(){
    const from = toIso($("from").value);
    const to = toIso($("to").value);
    return new URLSearchParams({from,to}).toString();
}


async function generate(){
    const from = toIso($("from").value);
    const to = toIso($("to").value);

    if (!from || !to) {
        alert("Selectează From și To.");
        return;
    }

    setStatus("Loading...");
    const qs = new URLSearchParams({ from, to }).toString();

    const summary = await Api.request(`/api/reports/sales-summary?${qs}`);
    const byCat = await Api.request(`/api/reports/sales-by-category?${qs}`);

    renderSummary(summary);
    renderCategoryTable(byCat);
    renderCategoryChart(byCat);

    setStatus("Done.");
}

function wire(){
    $("btnXlsx").addEventListener("click", () => download(`/api/reports/sales-by-category.xlsx?${qs()}`));
    $("btnPdf").addEventListener("click", () => download(`/api/reports/sales-by-category.pdf?${qs()}`));
    $("btnLogout").addEventListener("click", Api.logout);
    $("btnGenerate").addEventListener("click", generate);
    $("btnLast30").addEventListener("click", () => { setLast30Days(); });
}

(async function main(){
    await Api.me();
    wire();
    setLast30Days();
    await generate();
})();
