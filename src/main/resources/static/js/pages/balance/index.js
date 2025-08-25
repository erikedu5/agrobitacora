export function init(){
(function () {
    async function loadBalance(start, end) {
        const cropId = localStorage.getItem('cropId');
        if (!cropId) return;
        const params = new URLSearchParams({ start, end });
        const res = await fetch(`/balance/general?${params.toString()}`, { headers: { cropId } });
        if (!res.ok) return;
        const data = await res.json();
        $('#balance-bills').text(data.billsTotal.toFixed(2));
        $('#balance-sales').text(data.salesTotal.toFixed(2));
        $('#balance-total').text(data.balance.toFixed(2));
        $('#balance-table').removeClass('d-none');
    }

    $(function () {
        const $form = $('#balance-form');
        if ($form.length) {
            $form.on('submit', function (e) {
                e.preventDefault();
                const start = this.start.value;
                const end = this.end.value;
                if (start && end) loadBalance(start, end);
            });
        }
    });
})();
}
