App.registerEntity('adminengineers', {
    url: '/api/admin/users?role=Ingeniero',
    buildRow: u => `<tr data-item="${App.enc(u)}"><td>${u.id}</td><td>${u.name}</td><td>${u.username}</td><td>${u.maxCrops || ''}</td><td><button class='edit btn btn-sm btn-primary'>Editar</button> <button class='delete btn btn-sm btn-danger'>Eliminar</button></td></tr>`,
    deleteUrl: id => `/api/admin/users/${id}`,
    onEdit: data => {
        const $form = $('#admin-engineer-form');
        if ($form.length) {
            App.fillForm($form[0], data);
            // API returns username but form expects email field
            $form.find('[name=email]').val(data.username || data.email || '');
            $form.attr('action', `/api/admin/users/${data.id}`);
            $form[0].dataset.method = 'PUT';
            $form.find('[name=password]').val('').removeAttr('required');
        }
        const limit = prompt('Límite de cultivos', data.maxCrops || '');
        if (limit !== null) {
            fetch(`/api/admin/users/${data.id}/limit`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ maxCrops: parseInt(limit) })
            }).then(() => App.loadData(window.page));
        }
    },
    skipFormEdit: true,
    afterLoad: App.loadAdminCounts
});

const $engineerForm = $('#admin-engineer-form');
$engineerForm.on('reset', () => {
    $engineerForm.attr('action', '/api/admin/engineers');
    delete $engineerForm[0].dataset.method;
    $engineerForm.find('[name=password]').attr('required', true);
});
