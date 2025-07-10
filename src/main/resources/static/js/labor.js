App.registerEntity('labor', {
    url: '/labor/all?page=0&size=20',
    headers: () => ({ cropId: localStorage.getItem('cropId') }),
    buildRow: l => `<tr data-item="${App.enc(l)}"><td>${l.id}</td><td>${l.laborDate}</td><td>${l.kindLabor}</td><td><button class='edit btn btn-sm btn-primary'>Editar</button> <button class='delete btn btn-sm btn-danger'>Eliminar</button></td></tr>`
});
