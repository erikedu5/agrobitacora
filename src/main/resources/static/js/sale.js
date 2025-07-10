(function () {
    async function loadSummary(date) {
        const cropId = localStorage.getItem('cropId');
        if (!cropId) return;
        const res = await fetch(`/sale/summary?date=${encodeURIComponent(date)}`, {
            headers: { cropId }
        });
        if (!res.ok) return;
        const data = await res.json();
        const $table = $('#sales-table');
        const $tbody = $table.find('tbody');
        $tbody.empty();
        (data.sales || []).forEach(s => {
            $tbody.append(`<tr><td>${s.id}</td><td>${s.saleDate}</td><td>${s.packages}</td><td>${s.price}</td><td>${s.flowerName || ''}</td></tr>`);
        });
        $('#sales-total').text(data.total.toFixed(2));
        $table.removeClass('d-none');
    }

    $(function () {
        const $form = $('#summary-form');
        if ($form.length) {
            $form.on('submit', function (e) {
                e.preventDefault();
                const date = this.date.value;
                if (date) loadSummary(date);
            });
        }
    });
})();
