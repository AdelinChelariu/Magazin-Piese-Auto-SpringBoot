function $(id){ return document.getElementById(id); }
function setStatus(msg){ $("status").textContent = msg || ""; }

function escapeHtml(s){
    return String(s ?? "").replaceAll("&","&amp;").replaceAll("<","&lt;").replaceAll(">","&gt;")
        .replaceAll('"',"&quot;").replaceAll("'","&#039;");
}

function render(){
    const items = CartStore.load();
    const tbody = $("tbody");
    tbody.innerHTML = "";

    if (!items.length) {
        setStatus("Cart is empty.");
    } else {
        setStatus(`Items: ${items.length}`);
    }

    for (const it of items) {
        const line = Number(it.price) * (it.qty || 0);
        const tr = document.createElement("tr");
        tr.innerHTML = `
      <td>${escapeHtml(it.sku)}</td>
      <td>${escapeHtml(it.name)}</td>
      <td>${it.price}</td>
      <td>
        <input data-id="${it.productId}" class="qty" type="number" min="1" step="1" value="${it.qty}" />
      </td>
      <td>${line.toFixed(2)}</td>
      <td><button class="btn btn-secondary" data-action="remove" data-id="${it.productId}">Remove</button></td>
    `;
        tbody.appendChild(tr);
    }

    $("total").textContent = CartStore.total().toFixed(2);
}

async function checkout(){
    const items = CartStore.load();
    if (!items.length) { alert("Cart empty."); return; }

    const payload = {
        notes: $("notes").value.trim() || null,
        items: items.map(x => ({ productId: x.productId, quantity: x.qty }))
    };

    $("btnCheckout").disabled = true;
    try {
        const resp = await Api.request("/api/client/checkout", { method: "POST", json: payload });
        CartStore.clear();
        render();
        alert(`Order placed!\n${resp.orderNo}\nTotal: ${resp.total}`);
    } catch (e) {
        alert(String(e.message || e));
    } finally {
        $("btnCheckout").disabled = false;
    }
}

function wire(){
    $("btnLogout").addEventListener("click", Api.logout);

    $("btnClear").addEventListener("click", () => {
        if (!confirm("Clear cart?")) return;
        CartStore.clear();
        render();
    });

    $("tbody").addEventListener("click", (e) => {
        const btn = e.target.closest("button");
        if (!btn) return;
        if (btn.dataset.action === "remove") {
            CartStore.remove(Number(btn.dataset.id));
            render();
        }
    });

    $("tbody").addEventListener("change", (e) => {
        const inp = e.target.closest("input.qty");
        if (!inp) return;
        const id = Number(inp.dataset.id);
        const qty = Number(inp.value);
        CartStore.setQty(id, qty);
        render();
    });

    $("btnCheckout").addEventListener("click", checkout);
}

(async function main(){
    wire();
    render();
})();
