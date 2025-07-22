document.addEventListener('DOMContentLoaded', () => {
    loadStores();
    document.getElementById('save-store').addEventListener('click', saveStore);
});

async function loadStores() {
    const select = document.getElementById('store-select');
    select.innerHTML = '<option>Cargando...</option>';
    try {
        const res = await fetch('/store/all', {
            headers: { Authorization: 'Bearer ' + App.getToken() }
        });
        if (!res.ok) return;
        const data = await res.json();
        select.innerHTML = '';
        data.forEach(s => {
            const opt = document.createElement('option');
            opt.value = s.id || s.ID || s.codigo || '';
            opt.textContent = s.nombre || s.name || ('Sucursal ' + opt.value);
            opt.dataset.phone = s.whatsapp || s.telefono || s.phone || '';
            select.appendChild(opt);
        });
        const saved = localStorage.getItem('storeId');
        if (saved) {
            select.value = saved;
            const opt = select.options[select.selectedIndex];
            if (opt) {
                localStorage.setItem('storeName', opt.textContent);
                localStorage.setItem('storePhone', opt.dataset.phone || '');
            }
        }
    } catch (e) {
        select.innerHTML = '<option>Error</option>';
    }
}

async function saveStore() {
    const select = document.getElementById('store-select');
    const id = select.value;
    if (!id) return;
    try {
        await fetch('/store/' + id, {
            method: 'PUT',
            headers: { Authorization: 'Bearer ' + App.getToken() }
        });
        localStorage.setItem('storeId', id);
        const opt = select.options[select.selectedIndex];
        const name = opt.textContent;
        localStorage.setItem('storeName', name);
        localStorage.setItem('storePhone', opt.dataset.phone || '');
        App.notify('Tienda guardada');
    } catch (e) {
        console.log('save store error', e);
    }
}
