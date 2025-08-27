export function init(){
App.registerEntity('crop', {
    url: '/crop/all?page=0&size=20',
    buildRow: c => `<tr data-item="${App.enc(c)}"><td>${c.id}</td><td>${c.alias}</td><td>${c.location}</td><td><button class='edit btn btn-sm btn-primary'>Editar</button> <button class='delete btn btn-sm btn-danger'>Eliminar</button></td></tr>`,
    onPageLoad: () => {
        const modalEl = document.getElementById('cropModal');
        if (modalEl) {
            modalEl.addEventListener('shown.bs.modal', () => App.initCropMap(), { once: true });
        }
    },
    onEdit: () => App.initCropMap(),
    afterLoad: items => {
        if (!localStorage.getItem('cropId') && items.length) {
            localStorage.setItem('cropId', items[0].id);
        }
    }
});
}
