export function init(){
App.registerEntity('fumigation', {
    url: '/fumigation/all?page=0&size=20',
    headers: () => ({ cropId: localStorage.getItem('cropId') }),
    buildRow: f => `<tr data-item="${App.enc(f)}"><td>${f.id}</td><td>${f.applicationDate}</td><td>${f.detail}</td><td>${(f.appDetails || []).map(d => d.productName).join(', ')}</td><td><button class='show-detail btn btn-sm btn-info'>Mostrar</button></td></tr>`,
    onPageLoad: () => {
        App.hideVisitDate();
        $('#add-product').on('click', () => App.addProductGroup());
        App.addProductGroup();
        App.attachProductAutoFill();
    },
    onEdit: data => { App.hideVisitDate(); App.setProductGroupCount((data.appDetails && data.appDetails.length) || 1); }
});
}
