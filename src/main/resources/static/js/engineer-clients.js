document.addEventListener('DOMContentLoaded', async () => {
    const list = document.getElementById('producer-list');
    const resAll = await fetch('/engineer/producers/all');
    const all = resAll.ok ? await resAll.json() : [];
    const resSel = await fetch('/engineer/producers');
    const selected = resSel.ok ? (await resSel.json()).map(e => e.id) : [];
    list.innerHTML = all.map(e => {
        const checked = selected.includes(e.id) ? 'checked' : '';
        return `<div class="form-check"><input class="form-check-input" type="checkbox" value="${e.id}" id="prod-${e.id}" ${checked}><label class="form-check-label" for="prod-${e.id}">${e.name}</label></div>`;
    }).join('');

    document.getElementById('engineer-clients-form').addEventListener('submit', async ev => {
        ev.preventDefault();
        const ids = Array.from(list.querySelectorAll('input[type="checkbox"]:checked')).map(c => parseInt(c.value));
        await fetch('/engineer/producers', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(ids)
        });
        App.notify('Guardado');
    });
});
