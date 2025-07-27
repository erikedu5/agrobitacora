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
        document.querySelectorAll('.send-wa').forEach(btn => {
            btn.addEventListener('click', () => {
                const item = JSON.parse(decodeURIComponent(btn.closest('tr').dataset.item));
                const payload = JSON.stringify({ CipherText: item.cipherText, Iv: item.iv, Key: item.key });
                window.open(`https://wa.me/5217228259581?text=${encodeURIComponent(payload)}`, '_blank');
            });
        });
        document.querySelectorAll('.send-wa1').forEach(btn => {
            btn.addEventListener('click', () => {
                const item = JSON.parse(decodeURIComponent(btn.closest('tr').dataset.item));
                const payload = JSON.stringify({ CipherText: item.cipherText, Iv: item.iv, Key: item.key });
                window.open(`https://wa.me/5217221833593?text=${encodeURIComponent(payload)}`, '_blank');
            });
        });
    }
});
