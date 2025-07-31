App.registerEntity('adminlicenses', {
    url: '/license/all',
    buildRow: l => `<tr data-item="${App.enc(l)}">
        <td>${l.id}</td>
        <td>${l.cipherText}</td>
        <td>${l.key}</td>
        <td>${l.iv}</td>
        <td>${l.schoolName}</td>
        <td>${l.studentTotal}</td>
        <td>${l.decryptedText}</td>
        <td>${l.expirationDate}</td>
        <td>
            <button class="btn btn-sm btn-success send-wa">Enviar Erik</button>
            <button class="btn btn-sm btn-success send-wa1">Enviar Jesus</button>
        </td>
    </tr>`,
    afterLoad: () => {
        const tbody = document.querySelector('table tbody');
        const rows = Array.from(tbody.querySelectorAll('tr'));
        rows.sort((a, b) => parseInt(b.children[0].textContent) - parseInt(a.children[0].textContent));
        rows.forEach(r => tbody.appendChild(r));
        document.querySelectorAll('.send-wa').forEach(btn => {
            btn.addEventListener('click', () => {
                const item = JSON.parse(decodeURIComponent(btn.closest('tr').dataset.item));
                const payload = JSON.stringify({ C: item.cipherText, I: item.iv, K: item.key, F: item.expirationDate });
                window.open(`https://wa.me/5217228259581?text=${encodeURIComponent(payload)}`, '_blank');
            });
        });
        document.querySelectorAll('.send-wa1').forEach(btn => {
            btn.addEventListener('click', () => {
                const item = JSON.parse(decodeURIComponent(btn.closest('tr').dataset.item));
                const payload = JSON.stringify({ C: item.cipherText, I: item.iv, K: item.key, F: item.expirationDate });
                window.open(`https://wa.me/5217221833593?text=${encodeURIComponent(payload)}`, '_blank');
            });
        });
    }
});

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('license-form');
    if (!form) return;
    form.addEventListener('submit', async e => {
        e.preventDefault();
        const data = App.formDataToObject(form);
        const res = await fetch(form.action, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        if (res.ok) {
            const modalEl = document.getElementById('licenseModal');
            const modal = bootstrap.Modal.getInstance(modalEl) || new bootstrap.Modal(modalEl);
            modal.hide();
            form.reset();
            App.notify('Guardado correctamente', 'success');
            App.loadData('adminlicenses');
        } else {
            App.notify('Error', 'danger');
        }
    });
});
