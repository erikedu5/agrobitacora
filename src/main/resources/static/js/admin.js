window.registerEntity('adminusers', {
    url: '/api/admin/users?role=Productor',
    buildRow: u => `<tr data-item="${App.enc(u)}"><td>${u.id}</td><td>${u.name}</td><td>${u.userName}</td><td>${u.maxCrops || ''}</td><td><button class='edit btn btn-sm btn-primary'>Editar</button> <button class='delete btn btn-sm btn-danger'>Eliminar</button></td></tr>`,
    deleteUrl: id => `/api/admin/users/${id}`,
    onEdit: data => {
        const $form = $('#admin-user-form');
        if ($form.length) {
            App.fillForm($form[0], data);
            $form.attr('action', `/api/admin/users/${data.id}`);
            $form[0].dataset.method = 'PUT';
        }
    },
    afterLoad: App.loadAdminCounts
});

window.registerEntity('adminengineers', {
    url: '/api/admin/users?role=Ingeniero',
    buildRow: u => `<tr data-item="${App.enc(u)}"><td>${u.id}</td><td>${u.name}</td><td>${u.userName}</td><td>${u.maxCrops || ''}</td><td><button class='edit btn btn-sm btn-primary'>Editar</button> <button class='delete btn btn-sm btn-danger'>Eliminar</button></td></tr>`,
    deleteUrl: id => `/api/admin/users/${id}`,
    onEdit: data => {
        const $form = $('#admin-engineer-form');
        if ($form.length) {
            App.fillForm($form[0], data);
            $form.attr('action', `/api/admin/users/${data.id}`);
            $form[0].dataset.method = 'PUT';
        }
        const limit = prompt('Límite de cultivos', data.maxCrops || '');
        if (limit !== null) {
            fetch(`/admin/users/${data.id}/limit`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ maxCrops: parseInt(limit) })
            }).then(() => App.loadData(window.page));
        }
    },
    afterLoad: App.loadAdminCounts
});
