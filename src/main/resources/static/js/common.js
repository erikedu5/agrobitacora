(function () {
    window.App = window.App || {};
    const entities = {};

    App.registerEntity = (name, cfg) => entities[name] = cfg;
    App.enc = obj => encodeURIComponent(JSON.stringify(obj));

    function getStored(key) {
        return localStorage.getItem(key);
    }

    function clearAuth() {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        localStorage.removeItem('roleId');
        localStorage.removeItem('storeId');
        localStorage.removeItem('storeName');
    }

    function setAuth(token, role, roleId, storeId, storeName) {
        localStorage.setItem('token', token);
        if (role) localStorage.setItem('role', role);
        if (roleId !== undefined) localStorage.setItem('roleId', roleId);
        if (storeId !== undefined && storeId !== null) localStorage.setItem('storeId', storeId);
        if (storeName !== undefined && storeName !== null) localStorage.setItem('storeName', storeName);
    }

    App.getToken = () => getStored('token');
    App.getRole = () => getStored('role');
    App.getRoleId = () => getStored('roleId');
    App.getStoreId = () => getStored('storeId');
    App.getStoreName = () => getStored('storeName');
    App.clearAuth = clearAuth;

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
                } else if (input.type === 'checkbox') {
                    val = input.checked;
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
        const $loc = $('input[name="location"]');
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
            $loc.off('blur.geocode').on('blur.geocode', async function () {
                const query = $loc.val().trim();
                if (!query) return;
                try {
                    const res = await fetch(`https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(query)}&limit=1`);
                    if (res.ok) {
                        const [info] = await res.json();
                        if (info) {
                            const lt = parseFloat(info.lat);
                            const lg = parseFloat(info.lon);
                            cropMarker.setLatLng([lt, lg]);
                            cropMap.setView([lt, lg], 13);
                            setCropLocation(lt, lg);
                        }
                    }
                } catch (e) {}
            });
        } else {
            cropMap.setView([lat, lng], 13);
            cropMarker.setLatLng([lat, lng]);
        }
        if (!$lat.val() && !$lng.val() && navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(pos => {
                const lt = pos.coords.latitude;
                const lg = pos.coords.longitude;
                cropMarker.setLatLng([lt, lg]);
                cropMap.setView([lt, lg], 13);
                setCropLocation(lt, lg);
            }, () => setCropLocation(lat, lng));
        } else {
            setCropLocation(lat, lng);
        }
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

    App.hideVisitDate = function () {
        if (App.getRole() === 'Productor') {
            const $visit = $('input[name="visitDate"]');
            $visit.closest('.col-md-6').addClass('d-none');
            $visit.prop('required', false);
            const appVal = $('input[name="applicationDate"]').val();
            if (appVal && !$visit.val()) {
                $visit.val(appVal);
            }
        }
    };

    App.showApplicationDetails = function (app) {
        const $modal = $('#detailModal');
        if ($modal.length === 0) return;
        $modal.find('#app-info').text(`${app.applicationDate || ''} - ${app.detail || ''}`);
        const $tbody = $modal.find('#detail-table');
        $tbody.empty();
        (app.appDetails || []).forEach(d => {
            $tbody.append(
                `<tr data-name="${d.productName}">
                   <td><input type="checkbox" class="order-check"></td>
                   <td>${d.productName}</td>
                   <td>${d.activeIngredient}</td>
                   <td>${d.dosis}</td>
                   <td>${d.unit}</td>
                   <td>${(d.condiciones || []).join(', ')}</td>
                 </tr>`);
        });
        const $btnOrder = $modal.find('#make-order');
        const storeId = localStorage.getItem('storeId');
        if (!storeId) {
            $btnOrder.prop('disabled', true);
        } else {
            $btnOrder.prop('disabled', false);
        }
        $btnOrder.off('click').on('click', async function () {
            if (!localStorage.getItem('storeId')) return;
            const selected = [];
            $tbody.find('tr').each(function () {
                const $row = $(this);
                const chk = $row.find('.order-check')[0];
                if (chk && chk.checked) {
                    selected.push($row.data('name'));
                }
            });
            if (!selected.length) return;
            const items = [];
            for (const name of selected) {
                const product = await App.searchStoreProduct(name);
                if (product && product.id) {
                    items.push({ producto_id: product.id, cantidad: 1 });
                }
            }
            if (items.length) {
                const storeName = localStorage.getItem('storeName') || '';
                const msg = `¿Enviar pedido a ${storeName || 'la tienda'} con los productos: ${selected.join(', ')}?`;
                if (!confirm(msg)) return;
                await App.placeOrder(items);
            }
        });
        const modal = new bootstrap.Modal($modal[0]);
        modal.show();
    };

    App.searchStoreProduct = async function (name) {
        const storeId = localStorage.getItem('storeId');
        if (!storeId || !name) return null;
        try {
            const res = await fetch(`/store/${storeId}/products?q=${encodeURIComponent(name)}`);
            if (res.ok) {
                const data = await res.json();
                return Array.isArray(data) ? data[0] : null;
            }
        } catch (e) {
            console.log('searchStoreProduct error', e);
        }
        return null;
    };

    App.placeOrder = async function (items) {
        try {
            await fetch('/store/order', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ items })
            });
            App.notify('Pedido enviado');
        } catch (e) {
            console.log('placeOrder error', e);
        }
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

    let isOffline = !navigator.onLine;

    async function syncPending(reload = false) {
        if (isOffline) return;
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
        if (reload && ['fumigation', 'nutrition'].includes(window.page)) {
            App.loadData(window.page);
        }
    }

    window.addEventListener('online', () => { isOffline = false; syncPending(true); });
    window.addEventListener('offline', () => { isOffline = true; });

    App.loadAdminCounts = async function () {
        const $div = $('#admin-counts');
        if ($div.length === 0 || isOffline) return;
        try {
            const res = await fetch('/api/admin/counts');
            if (!res.ok) return;
            const data = await res.json();
            $div.text(`Productores: ${data.producers} | Ingenieros: ${data.engineers} | Admins: ${data.admins}`);
        } catch (e) {
            console.log('loadAdminCounts: offline');
        }
    };

    App.loadData = async function (page) {
        const config = entities[page];
        if (!config) return;
        if (typeof config.onPageLoad === 'function') config.onPageLoad();
        const headers = typeof config.headers === 'function' ? config.headers() : (config.headers || {});
        if (headers.cropId !== undefined && !headers.cropId) return;
        let res;
        if (isOffline) {
            res = { ok: false, offline: true };
        } else {
            try {
                res = await fetch(config.url, { headers });
            } catch (e) {
                console.log('loadData: offline when fetching', page);
                isOffline = true;
                res = { ok: false, offline: true };
            }
        }
        let items = [];
        if (res.ok) {
            const data = await res.json();
            items = Array.isArray(data) ? data : (data.content || []);
            if (page === 'fumigation' || page === 'nutrition') {
                saveLocalData(page, items);
            }
            console.log('loadData: loaded from network', page, items.length);
        } else if (isOffline || res.offline) {
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
                    App.clearAuth();
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
        $tbody.find('button.show-detail').on('click', function () {
            const item = JSON.parse(decodeURIComponent($(this).closest('tr').data('item')));
            App.showApplicationDetails(item);
        });
        if (typeof config.afterLoad === 'function') config.afterLoad(items);
    };

    $(function () {
        if (location.pathname !== '/auth' && !App.getToken()) {
            location.href = '/auth';
            return;
        }

        const originalFetch = window.fetch;
        window.fetch = async (input, init = {}) => {
            init.headers = {
                ...(init.headers || {}),
                ...(App.getToken() ? { 'Authorization': 'Bearer ' + App.getToken() } : {}),
                ...(localStorage.getItem('cropId') &&
                    ['Productor', 'Ingeniero'].includes(App.getRole())
                    ? { cropId: localStorage.getItem('cropId') }
                    : {})
            };
            return originalFetch(input, init);
        };

        $.ajaxSetup({
            beforeSend: function (xhr) {
                if (App.getToken()) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + App.getToken());
                }
                if (['Productor', 'Ingeniero'].includes(App.getRole()) && localStorage.getItem('cropId')) {
                    xhr.setRequestHeader('cropId', localStorage.getItem('cropId'));
                }
            }
        });

        App.attachProductAutoFill = function () {
            $(document).off('blur.productSearch')
                .on('blur.productSearch', '[data-field="productName"]', async function () {
                    const $input = $(this);
                    const $group = $input.closest('.product-item');
                    const $ingredient = $group.find('[data-field="activeIngredient"]');
                    const $unit = $group.find('[data-field="unit"]');
                    const val = $input.val().trim();
                    if (!val) return;
                    try {
                        const res = await fetch(`/product/search?name=${encodeURIComponent(val)}`);
                        if (res.ok) {
                            const data = await res.json();
                            if ($ingredient.val().trim() === '') $ingredient.val(data.activeIngredient || '');
                            if ($unit.val().trim() === '') $unit.val(data.unit || '');
                        }
                    } catch (e) {
                        console.log('product search error', e);
                    }
                });
        };

        App.attachProductAutoFill();

        async function ensureRole() {
            if (isOffline || App.getRole() || !App.getToken()) {
                return;
            }
            try {
                const res = await fetch('/auth/verifySession', { method: 'POST' });
                if (res.ok) {
                    const info = await res.json();
                    if (info.role && info.role.name) {
                        localStorage.setItem('role', info.role.name);
                    }
                }
            } catch (e) {
                console.log('ensureRole: offline');
            }
        }

        async function loadCropSelect() {
            const role = App.getRole();
            const $cropSelect = $('#crop-select');
            if ($cropSelect.length === 0 || isOffline) return;
            if (role === 'Productor') {
                let res;
                try { res = await fetch('/crop/all?page=0&size=20'); } catch (e) { return; }
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
                const $producerLabel = $('#producer-label');
                if ($producerSelect.length === 0) return;
                let resProd;
                try { resProd = await fetch('/engineer/producers'); } catch (e) { return; }
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
                    if (isOffline) return;
                    let res;
                    try { res = await fetch(`/engineer/crops?producerId=${pid}&page=0&size=20`); } catch (e) { return; }
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
                $producerLabel.removeClass('d-none');
                $producerSelect.on('change', async function () {
                    localStorage.setItem('producerId', this.value);
                    await loadCrops(this.value);
                });
            }
        }

        function showMenus() {
            const role = App.getRole();
            const all = ['#menu-association','#menu-bill','#menu-crop','#menu-fumigation','#menu-irrigation','#menu-labor','#menu-nutrition','#menu-production','#menu-balance','#menu-store'];
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
                allow = ['#menu-association','#menu-fumigation','#menu-nutrition'];
            }
            const hasCrop = !!localStorage.getItem('cropId');
            const roleId = App.getRoleId();
            if (!hasCrop && App.getToken() && String(roleId) === '3') {
                allow = ['#menu-crop'];
                if (location.pathname !== '/crop') location.href = '/crop';
            }
            allow.forEach(sel => $(sel).removeClass('d-none'));
        }

       $('form.api').on('submit', async function (e) {
           e.preventDefault();
            const role = App.getRole();
            if (role === 'Ingeniero' && this.action.includes('/fumigation') && !localStorage.getItem('cropId')) {
                App.notify('Debe seleccionar un cultivo', 'danger');
                return;
            }
           const data = App.formDataToObject(this);
           const method = this.dataset.method || $(this).attr('method') || this.method;
        let res;
        if (isOffline) {
            console.log('form submit offline, will store locally', this.action);
            res = { ok: false, offline: true };
        } else {
            try {
                res = await fetch(this.action, {
                    method,
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(data)
                });
            } catch (e) {
                console.log('form submit offline, will store locally', this.action);
                isOffline = true;
                res = { ok: false, offline: true };
            }
        }
        let info = null;
        try { info = await res.json(); } catch (e) {}
        if (res.ok) {
                if (this.id === 'sign-in-form' || this.id === 'sign-up-form') {
                    if (info && info.token) {
                        setAuth(info.token, info.role && info.role.name, info.role && info.role.id, info.branchId);
                        App.notify('Autenticado correctamente', 'success');
                        const needsCrop = info.cropCount !== undefined && info.cropCount === 0;
                        const roleId = info.role && info.role.id;
                        const needsRoleCrop = String(roleId) === '3';
                        const target = ((this.id === 'sign-up-form' && needsRoleCrop) ||
                                        (needsCrop && needsRoleCrop)) ? '/crop' : '/';
                        setTimeout(() => location.href = target, 1000);
                    } else {
                        App.notify('Credenciales inválidas', 'danger');
                    }
                    return;
                } else if (this.id === 'recover-form') {
                    App.notify('Contraseña actualizada', 'success');
                    this.reset();
                    return;
                }
                App.notify('Guardado correctamente', 'success');
                App.loadData(page);
                this.reset();
            } else {
            if (isOffline || res.offline) {
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
                App.clearAuth();
                if (this.id !== 'sign-in-form') {
                    location.href = '/auth';
                }
            }
        }
        });

        const $btnLogout = $('#logout');
        if ($btnLogout.length) {
            $btnLogout.on('click', async function () {
                if (!isOffline) {
                    try { await fetch('/auth/logout', { method: 'POST' }); } catch (e) {}
                }
                App.clearAuth();
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
