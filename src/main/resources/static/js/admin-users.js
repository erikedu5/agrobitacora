App.registerEntity('adminusers', {
    url: '/api/admin/users?role=Productor',
    buildRow: u => `<tr data-item="${App.enc(u)}"><td>${u.id}</td><td>${u.name}</td><td>${u.username}</td><td>${u.whatsapp || ''}</td><td>${u.maxCrops || ''}</td><td><button class='edit btn btn-sm btn-primary'>Editar</button> <button class='delete btn btn-sm btn-danger'>Eliminar</button></td></tr>`,
    deleteUrl: id => `/api/admin/users/${id}`,
    onEdit: data => {
        const $form = $('#admin-user-form');
        if ($form.length) {
            App.fillForm($form[0], data);
            $form.attr('action', `/api/admin/users/${data.id}`);
            $form[0].dataset.method = 'PUT';
            $form.find('[name=password]').val('').removeAttr('required');
        }
    },
    skipFormEdit: true,
    afterLoad: App.loadAdminCounts
});

const $userForm = $('#admin-user-form');
$userForm.on('reset', () => {
    $userForm.attr('action', '/api/admin/users');
    delete $userForm[0].dataset.method;
    $userForm.find('[name=password]').attr('required', true);
});
