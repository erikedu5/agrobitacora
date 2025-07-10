App.registerEntity('nutrition', {
    url: '/nutrition/all?page=0&size=20',
    headers: () => ({ cropId: localStorage.getItem('cropId') }),
    buildRow: n => `<tr data-item="${App.enc(n)}"><td>${n.id}</td><td>${n.applicationDate}</td><td>${n.detail}</td><td>${(n.appDetails || []).map(d => d.productName).join(', ')}</td><td><button class='show-detail btn btn-sm btn-info'>Mostrar</button></td></tr>`,
    onPageLoad: () => {
        App.hideVisitDate();
        $('#add-product').on('click', () => App.addProductGroup());
        App.addProductGroup();
        App.attachProductAutoFill();
    },
    onEdit: data => { App.hideVisitDate(); App.setProductGroupCount((data.appDetails && data.appDetails.length) || 1); }
});
