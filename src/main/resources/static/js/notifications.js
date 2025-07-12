(function () {
    async function loadNotifications() {
        const token = App.getToken();
        if (!token) return;
        let res;
        try {
            res = await fetch('/notification', { headers: { Authorization: token } });
        } catch (e) { return; }
        if (!res.ok) return;
        const list = await res.json();
        const $list = $('#notification-list');
        $list.empty();
        let unread = 0;
        list.forEach(n => {
            const item = `<li class="list-group-item d-flex justify-content-between align-items-start${n.read ? '' : ' fw-bold'}">
                <span>${n.message}</span>
                ${n.read ? '' : `<button data-id="${n.id}" class="btn btn-sm btn-link mark-read">Marcar</button>`}
            </li>`;
            $list.append(item);
            if (!n.read) unread++;
        });
        $('#notification-count').text(unread);
        $('.mark-read').on('click', async function () {
            const id = $(this).data('id');
            await fetch(`/notification/${id}/read`, { method: 'POST', headers: { Authorization: token } });
            loadNotifications();
        });
    }

    document.addEventListener('DOMContentLoaded', function () {
        $('#notificationsButton').on('click', loadNotifications);
    });
})();
