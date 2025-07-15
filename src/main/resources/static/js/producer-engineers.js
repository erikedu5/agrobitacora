document.addEventListener('DOMContentLoaded', async () => {
    const list = document.getElementById('engineer-list');
    const resAll = await fetch('/producer/engineers/all');
    const all = resAll.ok ? await resAll.json() : [];
    const resSel = await fetch('/producer/engineers');
    const selected = resSel.ok ? (await resSel.json()).map(e => e.id) : [];
    list.innerHTML = all.map(e => {
        const checked = selected.includes(e.id) ? 'checked' : '';
        return `<div class="form-check"><input class="form-check-input" type="checkbox" value="${e.id}" id="eng-${e.id}" ${checked}><label class="form-check-label" for="eng-${e.id}">${e.name}</label></div>`;
    }).join('');

    document.getElementById('producer-engineers-form').addEventListener('submit', async ev => {
        ev.preventDefault();
        const ids = Array.from(list.querySelectorAll('input[type="checkbox"]:checked')).map(c => parseInt(c.value));
        await fetch('/producer/engineers', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(ids)
        });
        App.notify('Guardado');
    });
});
