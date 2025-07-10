App.registerEntity('bill', {
    url: '/bill/all?page=0&size=20',
    headers: () => ({ cropId: localStorage.getItem('cropId') }),
    buildRow: b => `<tr data-item="${App.enc(b)}"><td>${b.id}</td><td>${b.billDate}</td><td>${b.concept}</td><td>${b.cost}</td><td><button class='edit btn btn-sm btn-primary'>Editar</button> <button class='delete btn btn-sm btn-danger'>Eliminar</button></td></tr>`
});

(function () {
    async function loadSummary(start, end) {
        const cropId = localStorage.getItem('cropId');
        if (!cropId) return;
        const res = await fetch(`/bill/summary?start=${encodeURIComponent(start)}&end=${encodeURIComponent(end)}`, {
            headers: { cropId }
        });
        if (!res.ok) return;
        const data = await res.json();
        const $table = $('#bill-summary-table');
        const $tbody = $table.find('tbody');
        $tbody.empty();
        (data.bills || []).forEach(b => {
            $tbody.append(`<tr><td>${b.id}</td><td>${b.billDate}</td><td>${b.concept}</td><td>${b.cost}</td></tr>`);
        });
        $('#bill-summary-total').text(data.total.toFixed(2));
        $table.removeClass('d-none');
    }

    $(function () {
        const $form = $('#bill-summary-form');
        if ($form.length) {
            $form.on('submit', function (e) {
                e.preventDefault();
                const start = this.start.value;
                const end = this.end.value;
                if (start && end) loadSummary(start, end);
            });
        }
    });
})();
