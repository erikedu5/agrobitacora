App.registerEntity('crop', {
    url: '/crop/all?page=0&size=20',
    buildRow: c => `<tr data-item="${App.enc(c)}"><td>${c.id}</td><td>${c.alias}</td><td>${c.location}</td><td><button class='edit btn btn-sm btn-primary'>Editar</button> <button class='delete btn btn-sm btn-danger'>Eliminar</button></td></tr>`,
    onPageLoad: () => App.initCropMap(),
    onEdit: () => App.initCropMap(),
    afterLoad: items => {
        if (!localStorage.getItem('cropId') && items.length) {
            localStorage.setItem('cropId', items[0].id);
        }
    }
});

App.registerEntity('irrigation', {
    url: '/irrigation/all?page=0&size=20',
    headers: () => ({ cropId: localStorage.getItem('cropId') }),
    buildRow: i => `<tr data-item="${App.enc(i)}"><td>${i.id}</td><td>${i.date}</td><td>${i.type}</td><td><button class='edit btn btn-sm btn-primary'>Editar</button> <button class='delete btn btn-sm btn-danger'>Eliminar</button></td></tr>`
});

App.registerEntity('labor', {
    url: '/labor/all?page=0&size=20',
    headers: () => ({ cropId: localStorage.getItem('cropId') }),
    buildRow: l => `<tr data-item="${App.enc(l)}"><td>${l.id}</td><td>${l.laborDate}</td><td>${l.kindLabor}</td><td><button class='edit btn btn-sm btn-primary'>Editar</button> <button class='delete btn btn-sm btn-danger'>Eliminar</button></td></tr>`
});

App.registerEntity('production', {
    url: '/production/all?page=0&size=20',
    headers: () => ({ cropId: localStorage.getItem('cropId') }),
    buildRow: p => `<tr data-item="${App.enc(p)}"><td>${p.id}</td><td>${p.productionDate}</td><td></td><td><button class='edit btn btn-sm btn-primary'>Editar</button> <button class='delete btn btn-sm btn-danger'>Eliminar</button></td></tr>`
});

App.registerEntity('nutrition', {
    url: '/nutrition/all?page=0&size=20',
    headers: () => ({ cropId: localStorage.getItem('cropId') }),
    buildRow: n => `<tr data-item="${App.enc(n)}"><td>${n.id}</td><td>${n.applicationDate}</td><td>${n.detail}</td><td><button class='show-detail btn btn-sm btn-info'>Mostrar</button></td><td><button class='edit btn btn-sm btn-primary'>Editar</button> <button class='delete btn btn-sm btn-danger'>Eliminar</button></td></tr>`
});

App.registerEntity('fumigation', {
    url: '/fumigation/all?page=0&size=20',
    headers: () => ({ cropId: localStorage.getItem('cropId') }),
    buildRow: f => `<tr data-item="${App.enc(f)}"><td>${f.id}</td><td>${f.applicationDate}</td><td>${f.detail}</td><td>${(f.appDetails || []).map(d => d.productName).join(', ')}</td><td><button class='show-detail btn btn-sm btn-info'>Mostrar</button></td><td><button class='edit btn btn-sm btn-primary'>Editar</button> <button class='delete btn btn-sm btn-danger'>Eliminar</button></td></tr>`,
    onPageLoad: () => { $('#add-product').on('click', () => App.addProductGroup()); App.addProductGroup(); },
    onEdit: data => { App.setProductGroupCount((data.appDetails && data.appDetails.length) || 1); }
});
