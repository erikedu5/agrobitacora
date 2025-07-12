const urlMap = {
        'FUMIGATION': '/fumigation/catalog',
        'IRRIGATION': '/irrigation/catalog',
        'LABOR': '/labor/catalog',
        'NUTRITION': '/nutrition/catalog'
    };

async function loadAssociated(kind) {
        const url = urlMap[kind];
        const $container = $('#bill-associated-container');
        const $select = $('#bill-associated');
        if (!url) {
            $select.empty();
            $container.addClass('d-none');
            return;
        }
        let res;
        try { res = await fetch(url); } catch (e) { return; }
        if (!res.ok) return;
        const data = await res.json();
        const items = Array.isArray(data) ? data : (data.content || []);
        $select.empty();
        items.forEach(i => {
            const text = i.description ? `${i.id} - ${i.description}` : i.id;
            $select.append(`<option value="${i.id}">${text}</option>`);
        });
        $container.removeClass('d-none');
    }

App.registerEntity('bill', {
    url: '/bill/all?page=0&size=20',
    headers: () => ({ cropId: localStorage.getItem('cropId') }),
    buildRow: b => `<tr data-item="${App.enc(b)}"><td>${b.id}</td><td>${b.billDate}</td><td>${b.concept}</td><td>${b.cost}</td><td><button class='edit btn btn-sm btn-primary'>Editar</button> <button class='delete btn btn-sm btn-danger'>Eliminar</button></td></tr>`,
    onEdit: data => {
        if (data.kindBillAssociated) {
            loadAssociated(data.kindBillAssociated).then(() => {
                $('#bill-associated').val(data.idBillAssociated || '');
            });
        } else {
            $('#bill-associated-container').addClass('d-none');
        }
    }
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
        const $kind = $('#kindBillAssociated');
        if ($kind.length) {
            $kind.on('change', function () { loadAssociated(this.value); });
        }
    });
})();
