(function () {
    let notifications = [];
    let tray;

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
    function ensureTrayExists() {
        tray = document.getElementById('notificationTray');
        if (!tray) {
            tray = document.createElement('div');
            tray.id = 'notificationTray';
            tray.className = 'offcanvas offcanvas-end';
            tray.tabIndex = -1;
            tray.setAttribute('aria-labelledby', 'notificationTrayLabel');
            tray.innerHTML =
                '<div class="offcanvas-header">' +
                '<h5 id="notificationTrayLabel">Notificaciones</h5>' +
                '<button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>' +
                '</div>' +
                '<div class="offcanvas-body">' +
                '<ul id="notification-list" class="list-group"></ul>' +
                '</div>';
            document.body.appendChild(tray);
        }
    }

    function init() {
        ensureTrayExists();
        if (tray) {
            bootstrap.Offcanvas.getOrCreateInstance(tray);
        }
        loadNotifications();
    }

    if (document.readyState !== 'loading') {
        init();
    } else {
        document.addEventListener('DOMContentLoaded', init);
    }
})();
