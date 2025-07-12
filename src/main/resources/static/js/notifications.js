(function () {
    let notifications = [];

    async function loadNotifications() {
        const token = App.getToken();
        if (!token) return;
        let res;
        try {
            res = await fetch('/notification', { headers: { Authorization: token } });
        } catch (e) { return; }
        if (!res.ok) return;
        notifications = await res.json();
        render();
    }

    async function markAsRead(id) {
        const token = App.getToken();
        if (!token) return;
        try { await fetch(`/notification/${id}/read`, { method: 'POST', headers: { Authorization: token } }); } catch (e) {}
    }

    function render() {
        const $list = $('#notification-list');
        $list.empty();
        let unread = 0;
        notifications.forEach(n => {
            const item = $(
                `<li class="list-group-item notification-item${n.read ? '' : ' fw-bold'}" data-id="${n.id}">
                    <span>${n.message}</span>
                </li>`
            );
            $list.append(item);
            if (!n.read) unread++;
        });
        $('#notification-count').text(unread);
    }

    $(document).on('click', '.notification-item', async function () {
        const id = $(this).data('id');
        const notif = notifications.find(n => n.id === id);
        if (notif) {
            App.notify(notif.message, 'info');
            if (!notif.read) {
                await markAsRead(id);
                notif.read = true;
                render();
            }
        }
    });

    document.addEventListener('DOMContentLoaded', loadNotifications);
})();
