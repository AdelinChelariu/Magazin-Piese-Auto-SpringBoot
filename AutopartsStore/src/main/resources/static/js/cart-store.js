const CartStore = (() => {
    const KEY = "autoparts_cart_v1";

    function load() {
        try { return JSON.parse(localStorage.getItem(KEY) || "[]"); }
        catch { return []; }
    }

    function save(items) {
        localStorage.setItem(KEY, JSON.stringify(items));
    }

    function count() {
        return load().reduce((s, x) => s + (x.qty || 0), 0);
    }

    function clear() {
        save([]);
    }

    function add(product, qty = 1) {
        const items = load();
        const found = items.find(x => x.productId === product.id);
        if (found) found.qty += qty;
        else items.push({
            productId: product.id,
            sku: product.sku,
            name: product.name,
            price: Number(product.price),
            qty
        });
        save(items);
    }

    function setQty(productId, qty) {
        const items = load();
        const it = items.find(x => x.productId === productId);
        if (!it) return;
        it.qty = qty;
        if (it.qty <= 0) save(items.filter(x => x.productId !== productId));
        else save(items);
    }

    function remove(productId) {
        save(load().filter(x => x.productId !== productId));
    }

    function total() {
        return load().reduce((s, x) => s + (Number(x.price) * (x.qty || 0)), 0);
    }

    return { load, save, count, clear, add, setQty, remove, total };
})();
