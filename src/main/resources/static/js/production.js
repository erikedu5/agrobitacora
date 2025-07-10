App.registerEntity('production', {
    url: '/production/all?page=0&size=20',
    headers: () => ({ cropId: localStorage.getItem('cropId') }),
    buildRow: p => `<tr data-item="${App.enc(p)}"><td>${p.id}</td><td>${p.productionDate}</td><td></td><td><button class='edit btn btn-sm btn-primary'>Editar</button> <button class='delete btn btn-sm btn-danger'>Eliminar</button></td></tr>`
});
