export function init(){
App.registerEntity('irrigation', {
    url: '/irrigation/all?page=0&size=20',
    headers: () => ({ cropId: localStorage.getItem('cropId') }),
    buildRow: i => `<tr data-item="${App.enc(i)}"><td>${i.id}</td><td>${i.date}</td><td>${i.type}</td><td><button class='edit btn btn-sm btn-primary'>Editar</button> <button class='delete btn btn-sm btn-danger'>Eliminar</button></td></tr>`
});
}
