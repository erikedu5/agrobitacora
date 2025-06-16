(function () {
    window.App = window.App || {};
    const entities = {};

    App.registerEntity = (name, cfg) => entities[name] = cfg;
    App.enc = obj => encodeURIComponent(JSON.stringify(obj));

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

    App.loadAdminCounts = async function () {
        const $div = $('#admin-counts');
        if ($div.length === 0) return;
        const res = await fetch('/api/admin/counts');
        if (!res.ok) return;
        const data = await res.json();
        $div.text(`Usuarios: ${data.users} | Cultivos: ${data.crops}`);
    };

    App.loadData = async function (page) {
        const config = entities[page];
        if (!config) return;
        if (typeof config.onPageLoad === 'function') config.onPageLoad();
        const headers = typeof config.headers === 'function' ? config.headers() : (config.headers || {});
        if (headers.cropId !== undefined && !headers.cropId) return;
        const res = await fetch(config.url, { headers });
        if (!res.ok) return;
        const data = await res.json();
        const items = Array.isArray(data) ? data : (data.content || []);
        const $tbody = $('table tbody');
        $tbody.empty();
        items.forEach(item => $tbody.append(config.buildRow(item)));
        $tbody.find('button.delete').on('click', async function () {
            const id = JSON.parse(decodeURIComponent($(this).closest('tr').data('item'))).id;
            const url = config.deleteUrl ? config.deleteUrl(id) : `/${page}/${id}`;
            const resp = await fetch(url, { method: 'DELETE' });
            if (resp.ok) App.loadData(page);
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
                ...(localStorage.getItem('role') === 'Productor' && localStorage.getItem('cropId') ? { cropId: localStorage.getItem('cropId') } : {})
            };
            return originalFetch(input, init);
        };

        $.ajaxSetup({
            beforeSend: function (xhr) {
                if (localStorage.getItem('token')) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.getItem('token'));
                }
                if (localStorage.getItem('role') === 'Productor' && localStorage.getItem('cropId')) {
                    xhr.setRequestHeader('cropId', localStorage.getItem('cropId'));
                }
            }
        });

        async function ensureRole() {
            if (!localStorage.getItem('role') && localStorage.getItem('token')) {
                const res = await fetch('/auth/verify', { method: 'POST' });
                if (res.ok) {
                    const info = await res.json();
                    if (info.role && info.role.name) {
                        localStorage.setItem('role', info.role.name);
                    }
                }
            }
        }

        async function loadCropSelect() {
            if (localStorage.getItem('role') !== 'Productor') return;
            const $select = $('#crop-select');
            if ($select.length === 0) return;
            const res = await fetch('/crop/all?page=0&size=20');
            if (!res.ok) return;
            const data = await res.json();
            const items = Array.isArray(data) ? data : (data.content || []);
            $select.empty();
            items.forEach(c => $select.append(`<option value="${c.id}">${c.alias}</option>`));
            if (!localStorage.getItem('cropId') && items.length) {
                localStorage.setItem('cropId', items[0].id);
            }
            $select.val(localStorage.getItem('cropId'));
            $select.removeClass('d-none');
            $select.on('change', function () {
                localStorage.setItem('cropId', this.value);
                location.reload();
            });
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
            const data = App.formDataToObject(this);
            const method = this.dataset.method || $(this).attr('method') || this.method;
            const res = await fetch(this.action, {
                method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
            if (res.ok) {
                if (this.id === 'sign-in-form' || this.id === 'sign-up-form') {
                    const info = await res.json();
                    localStorage.setItem('token', info.token);
                    if (info.role && info.role.name) {
                        localStorage.setItem('role', info.role.name);
                    }
                    location.href = '/';
                    return;
                }
                App.loadData(page);
                this.reset();
            } else if (res.status === 401) {
                localStorage.removeItem('token');
                location.href = '/auth';
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
            App.loadData(page);
        })();
    });
})();
