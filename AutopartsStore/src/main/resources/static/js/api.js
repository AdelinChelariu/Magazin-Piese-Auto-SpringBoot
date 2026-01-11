const Api = (() => {
    async function request(path, options = {}) {
        const headers = options.headers ? {...options.headers} : {};
        headers["Accept"] = "application/json";

        if (options.json) {
            headers["Content-Type"] = "application/json";
            options.body = JSON.stringify(options.json);
            delete options.json;
        }

        const res = await fetch(path, { ...options, headers, credentials: "same-origin" });

        if (res.status === 401) {
            window.location.href = "/login.html";
            return;
        }

        if (!res.ok) {
            const text = await res.text().catch(() => "");
            throw new Error(`HTTP ${res.status} ${res.statusText}\n${text}`);
        }

        if (res.status === 204) return null;

        const ct = res.headers.get("content-type") || "";
        if (ct.includes("application/json")) return await res.json();
        return await res.text();
    }

    async function me() {
        return await request("/api/me");
    }

    async function logout() {
        await fetch("/logout", { method: "POST", credentials: "same-origin" });
        window.location.href = "/login.html?logout=1";
    }

    return { request, me, logout };
})();
