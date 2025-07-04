(function () {
    window.App = window.App || {};
    const entities = {};

    App.registerEntity = (name, cfg) => entities[name] = cfg;
    App.enc = obj => encodeURIComponent(JSON.stringify(obj));

    App.notify = function (message, type = 'success') {
        const $container = $('#toast-container');
        if ($container.length === 0) return;
        const id = `toast-${Date.now()}`;
        const $toast = $(
            `<div id="${id}" class="toast align-items-center text-bg-${type} border-0" role="alert" aria-live="assertive" aria-atomic="true">` +
            '<div class="d-flex">' +
            `<div class="toast-body">${message}</div>` +
            '<button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>' +
            '</div></div>'
        );
        $container.append($toast);
        const toast = new bootstrap.Toast($toast[0], { delay: 3000 });
        toast.show();
        $toast.on('hidden.bs.toast', () => $toast.remove());
    };

    App.fillForm = function (form, data, prefix = '') {
        Object.entries(data).forEach(([k, v]) => {
            const name = prefix ? `${prefix}.${k}` : k;
            if (Array.isArray(v)) {
                v.forEach((item, idx) => App.fillForm(form, item, `${name}[${idx}]`));
            } else if (v && typeof v === 'object') {
                App.fillForm(form, v, name);
            } else {
                const input = form.querySelector(`[name="${name}"]`);
                if (input) {
                    let val = v;
                    if (input.type === 'datetime-local' && typeof v === 'string') {
                        val = v.substring(0, 19);
                    } else if (input.dataset.array === 'csv' && Array.isArray(v)) {
                        val = v.join(', ');
                    }
                    input.value = val ?? '';
                }
            }
        });
    };

    function assign(obj, keys, value) {
        let current = obj;
        for (let i = 0; i < keys.length; i++) {
            const key = keys[i];
            const prop = isNaN(Number(key)) ? key : Number(key);
            const last = i === keys.length - 1;
            if (last) {
                current[prop] = value;
            } else {
                if (current[prop] === undefined) {
                    const nextIsNum = !isNaN(Number(keys[i + 1]));
                    current[prop] = nextIsNum ? [] : {};
                }
                current = current[prop];
            }
        }
    }

    App.formDataToObject = function (form) {
        const data = {};
        for (const [key, value] of new FormData(form).entries()) {
            const input = form.querySelector(`[name="${key}"]`);
            let val = value;
            if (input) {
                if (input.type === 'number') {
                    val = value ? Number(value) : null;
                } else if (input.type === 'datetime-local') {
                    val = value ? new Date(value).toISOString().slice(0, 19) : null;
                } else if (input.dataset.array === 'csv') {
                    val = value ? value.split(',').map(v => v.trim()).filter(v => v) : [];
                }
            }
            const parts = key.split(/\.|\[|\]/).filter(Boolean);
            assign(data, parts, val);
        }
        return data;
    };

    let cropMap, cropMarker;
    App.initCropMap = function () {
        const $map = $('#map');
        if ($map.length === 0) return;
        const $lat = $('input[name="latitud"]');
        const $lng = $('input[name="longitud"]');
        const lat = parseFloat($lat.val()) || 19.4326;
        const lng = parseFloat($lng.val()) || -99.1332;
        if (!cropMap) {
            cropMap = L.map('map').setView([lat, lng], 13);
            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '&copy; OpenStreetMap contributors'
            }).addTo(cropMap);
            cropMarker = L.marker([lat, lng], { draggable: true }).addTo(cropMap);
            cropMap.on('click', e => {
                cropMarker.setLatLng(e.latlng);
                setCropLocation(e.latlng.lat, e.latlng.lng);
            });
            cropMarker.on('dragend', () => {
                const p = cropMarker.getLatLng();
                setCropLocation(p.lat, p.lng);
            });
        } else {
            cropMap.setView([lat, lng], 13);
            cropMarker.setLatLng([lat, lng]);
        }
        setCropLocation(lat, lng);
    };

    async function setCropLocation(lat, lng) {
        const $lat = $('input[name="latitud"]');
        const $lng = $('input[name="longitud"]');
        const $loc = $('input[name="location"]');
        $lat.val(lat.toFixed(6));
        $lng.val(lng.toFixed(6));
        try {
            const res = await fetch(`https://nominatim.openstreetmap.org/reverse?lat=${lat}&lon=${lng}&format=json`);
            if (res.ok) {
                const info = await res.json();
                $loc.val(info.display_name || '');
            }
        } catch (e) {}
    }

    App.renumberProductGroups = function () {
        const $container = $('#products');
        if ($container.length === 0) return;
        $container.find('.product-item').each(function (idx) {
            $(this).find('[data-field]').each(function () {
                const field = $(this).data('field');
                $(this).attr('name', `appDetails[${idx}].${field}`);
            });
        });
    };

    App.addProductGroup = function (data = {}) {
        const $template = $('#product-template');
        const $container = $('#products');
        if ($template.length === 0 || $container.length === 0) return;
        const $node = $($template.html());
        $container.append($node);
        $node.find('.remove-product').on('click', function () {
            $node.remove();
            App.renumberProductGroups();
        });
        App.renumberProductGroups();
        App.fillForm($node[0], data);
    };

    App.setProductGroupCount = function (count) {
        const $container = $('#products');
        if ($container.length === 0) return;
        while ($container.find('.product-item').length < count) {
            App.addProductGroup();
        }
        while ($container.find('.product-item').length > count) {
            $container.children().last().remove();
        }
        App.renumberProductGroups();
    };

    function saveLocalData(type, items) {
        console.log(`saveLocalData: storing ${items.length} ${type}s`);
        localStorage.setItem(`${type}s`, JSON.stringify(items));
    }

    function getLocalData(type) {
        const data = JSON.parse(localStorage.getItem(`${type}s`) || '[]');
        console.log(`getLocalData: loaded ${data.length} ${type}s`);
        return data;
    }

    function addPending(type, item) {
        const key = `pending-${type}`;
        const arr = JSON.parse(localStorage.getItem(key) || '[]');
        arr.push(item);
        console.log('addPending:', type, item);
        localStorage.setItem(key, JSON.stringify(arr));
    }

    async function syncPending() {
        if (!navigator.onLine) return;
        console.log('syncPending: online, start syncing');
        for (const type of ['fumigation', 'nutrition']) {
            const key = `pending-${type}`;
            let pending = JSON.parse(localStorage.getItem(key) || '[]');
            console.log(`syncPending: ${pending.length} pending ${type}(s)`);
            for (let i = 0; i < pending.length; i++) {
                try {
                    const res = await fetch(`/${type}`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(pending[i])
                    });
                    if (res.ok) {
                        console.log(`syncPending: sent one ${type}`);
                        pending.splice(i, 1);
                        i--;
                    }
                } catch (e) {
                    break;
                }
            }
            if (pending.length) {
                localStorage.setItem(key, JSON.stringify(pending));
            } else {
                localStorage.removeItem(key);
            }
        }
        if (['fumigation', 'nutrition'].includes(window.page)) {
            App.loadData(window.page);
        }
    }

    window.addEventListener('online', syncPending);

    App.loadAdminCounts = async function () {
        const $div = $('#admin-counts');
        if ($div.length === 0) return;
        const res = await fetch('/api/admin/counts');
        if (!res.ok) return;
        const data = await res.json();
        $div.text(`Productores: ${data.producers} | Ingenieros: ${data.engineers} | Admins: ${data.admins}`);
    };

    App.loadData = async function (page) {
        const config = entities[page];
        if (!config) return;
        if (typeof config.onPageLoad === 'function') config.onPageLoad();
        const headers = typeof config.headers === 'function' ? config.headers() : (config.headers || {});
        if (headers.cropId !== undefined && !headers.cropId) return;
        let res;
        try {
            res = await fetch(config.url, { headers });
        } catch (e) {
            console.log('loadData: offline when fetching', page);
            res = { ok: false, offline: true };
        }
        let items = [];
        if (res.ok) {
            const data = await res.json();
            items = Array.isArray(data) ? data : (data.content || []);
            if (page === 'fumigation' || page === 'nutrition') {
                saveLocalData(page, items);
            }
            console.log('loadData: loaded from network', page, items.length);
        } else if (!navigator.onLine || res.offline) {
            if (page === 'fumigation' || page === 'nutrition') {
                items = getLocalData(page);
            }
            console.log('loadData: using local data', page, items.length);
        } else {
            return;
        }
        const $tbody = $('table tbody');
        $tbody.empty();
        items.forEach(item => $tbody.append(config.buildRow(item)));
        $tbody.find('button.delete').on('click', async function () {
            const id = JSON.parse(decodeURIComponent($(this).closest('tr').data('item'))).id;
            const url = config.deleteUrl ? config.deleteUrl(id) : `/${page}/${id}`;
            const resp = await fetch(url, { method: 'DELETE' });
            if (resp.ok) {
                App.notify('Eliminado correctamente', 'success');
                App.loadData(page);
            } else {
                App.notify('Error al eliminar', 'danger');
                if (resp.status === 401) {
                    localStorage.removeItem('token');
                    location.href = '/auth';
                }
            }
        });
        $tbody.find('button.edit').on('click', function () {
            const item = JSON.parse(decodeURIComponent($(this).closest('tr').data('item')));
            if (typeof config.onEdit === 'function') config.onEdit(item);
            const $form = $('form.api');
            if ($form.length === 0 || config.skipFormEdit) return;
            App.fillForm($form[0], item);
            $form.attr('action', `${location.pathname}/${item.id}`);
            $form[0].dataset.method = 'PUT';
        });
        if (typeof config.afterLoad === 'function') config.afterLoad(items);
    };

    $(function () {
        if (location.pathname !== '/auth' && !localStorage.getItem('token')) {
            location.href = '/auth';
            return;
        }

        const originalFetch = window.fetch;
        window.fetch = async (input, init = {}) => {
            init.headers = {
                ...(init.headers || {}),
                ...(localStorage.getItem('token') ? { 'Authorization': 'Bearer ' + localStorage.getItem('token') } : {}),
                ...(localStorage.getItem('cropId') &&
                    ['Productor', 'Ingeniero'].includes(localStorage.getItem('role'))
                    ? { cropId: localStorage.getItem('cropId') }
                    : {})
            };
            return originalFetch(input, init);
        };

        $.ajaxSetup({
            beforeSend: function (xhr) {
                if (localStorage.getItem('token')) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.getItem('token'));
                }
                if (['Productor', 'Ingeniero'].includes(localStorage.getItem('role')) && localStorage.getItem('cropId')) {
                    xhr.setRequestHeader('cropId', localStorage.getItem('cropId'));
                }
            }
        });

        async function ensureRole() {
            if (!localStorage.getItem('role') && localStorage.getItem('token')) {
                const res = await fetch('/auth/verifySession', { method: 'POST' });
                if (res.ok) {
                    const info = await res.json();
                    if (info.role && info.role.name) {
                        localStorage.setItem('role', info.role.name);
                    }
                }
            }
        }

        async function loadCropSelect() {
            const role = localStorage.getItem('role');
            const $cropSelect = $('#crop-select');
            if ($cropSelect.length === 0) return;
            if (role === 'Productor') {
                const res = await fetch('/crop/all?page=0&size=20');
                if (!res.ok) return;
                const data = await res.json();
                const items = Array.isArray(data) ? data : (data.content || []);
                $cropSelect.empty();
                items.forEach(c => $cropSelect.append(`<option value="${c.id}">${c.alias}</option>`));
                if (!localStorage.getItem('cropId') && items.length) {
                    localStorage.setItem('cropId', items[0].id);
                }
                $cropSelect.val(localStorage.getItem('cropId'));
                $cropSelect.removeClass('d-none');
                $cropSelect.on('change', function () {
                    localStorage.setItem('cropId', this.value);
                    location.reload();
                });
            } else if (role === 'Ingeniero') {
                const $producerSelect = $('#producer-select');
                if ($producerSelect.length === 0) return;
                const resProd = await fetch('/engineer/producers');
                if (!resProd.ok) return;
                const producers = await resProd.json();
                $producerSelect.empty();
                producers.forEach(p => $producerSelect.append(`<option value="${p.id}">${p.name}</option>`));
                let producerId = localStorage.getItem('producerId');
                if (!producerId && producers.length) {
                    producerId = producers[0].id;
                    localStorage.setItem('producerId', producerId);
                }
                $producerSelect.val(producerId);
                async function loadCrops(pid) {
                    const res = await fetch(`/engineer/crops?producerId=${pid}&page=0&size=20`);
                    if (!res.ok) return;
                    const data = await res.json();
                    const items = Array.isArray(data) ? data : (data.content || []);
                    $cropSelect.empty();
                    items.forEach(c => $cropSelect.append(`<option value="${c.id}">${c.alias}</option>`));
                    if (!localStorage.getItem('cropId') && items.length) {
                        localStorage.setItem('cropId', items[0].id);
                    }
                    $cropSelect.val(localStorage.getItem('cropId'));
                    $cropSelect.removeClass('d-none');
                    $cropSelect.on('change', function () {
                        localStorage.setItem('cropId', this.value);
                        App.loadData(window.page);
                    });
                }
                if (producerId) {
                    await loadCrops(producerId);
                }
                $producerSelect.removeClass('d-none');
                $producerSelect.on('change', async function () {
                    localStorage.setItem('producerId', this.value);
                    await loadCrops(this.value);
                });
            }
        }

        function showMenus() {
            const role = localStorage.getItem('role');
            const all = ['#menu-bill','#menu-crop','#menu-fumigation','#menu-irrigation','#menu-labor','#menu-nutrition','#menu-production'];
            all.forEach(sel => $(sel).addClass('d-none'));
            $('#admin-menu').addClass('d-none');
            if (role === 'Admin') {
                $('#admin-menu').removeClass('d-none');
                if (location.pathname === '/' || location.pathname === '/home') {
                    location.href = '/admin';
                }
                return;
            }
            let allow = all;
            if (role === 'Ingeniero') {
                allow = ['#menu-fumigation','#menu-nutrition'];
            }
            allow.forEach(sel => $(sel).removeClass('d-none'));
        }

       $('form.api').on('submit', async function (e) {
           e.preventDefault();
            const role = localStorage.getItem('role');
            if (role === 'Ingeniero' && this.action.includes('/fumigation') && !localStorage.getItem('cropId')) {
                App.notify('Debe seleccionar un cultivo', 'danger');
                return;
            }
           const data = App.formDataToObject(this);
           const method = this.dataset.method || $(this).attr('method') || this.method;
        let res;
        try {
            res = await fetch(this.action, {
                method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
        } catch (e) {
            console.log('form submit offline, will store locally', this.action);
            res = { ok: false, offline: true };
        }
        let info = null;
        try { info = await res.json(); } catch (e) {}
        if (res.ok) {
                if (this.id === 'sign-in-form' || this.id === 'sign-up-form') {
                    if (info && info.token) {
                        localStorage.setItem('token', info.token);
                        if (info.role && info.role.name) {
                            localStorage.setItem('role', info.role.name);
                        }
                        App.notify('Autenticado correctamente', 'success');
                        setTimeout(() => location.href = '/', 1000);
                    } else {
                        App.notify('Credenciales inválidas', 'danger');
                    }
                    return;
                }
                App.notify('Guardado correctamente', 'success');
                App.loadData(page);
                this.reset();
            } else {
            if (!navigator.onLine || res.offline) {
                const type = this.action.includes('/fumigation') ? 'fumigation' :
                              (this.action.includes('/nutrition') ? 'nutrition' : null);
                if (type) {
                    addPending(type, data);
                    console.log('stored offline entry', type, data);
                    const list = getLocalData(type);
                    list.push(Object.assign({ id: Date.now() }, data));
                    saveLocalData(type, list);
                    App.notify('Guardado localmente. Se enviará cuando haya conexión.', 'warning');
                    App.loadData(type);
                    this.reset();
                    return;
                }
            }
            App.notify((info && (info.description || info.message)) || 'Error', 'danger');
            if (res.status === 401) {
                localStorage.removeItem('token');
                location.href = '/auth';
            }
        }
        });

        const $btnLogout = $('#logout');
        if ($btnLogout.length) {
            $btnLogout.on('click', async function () {
                await fetch('/auth/logout', { method: 'POST' });
                localStorage.removeItem('token');
                location.href = '/auth';
            });
        }

        (async () => {
            await ensureRole();
            await loadCropSelect();
            showMenus();
            const page = location.pathname.substring(1).replace(/\//g, '');
            window.page = page;
            console.log('initial syncPending');
            await syncPending();
            App.loadData(page);
        })();
    });
})();
