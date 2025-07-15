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
            const unreadClass = n.read ? '' : ' fw-bold bg-warning-subtle';
            const item = $(`<li class="list-group-item notification-item${unreadClass}" data-id="${n.id}"><span>${n.message}</span></li>`);
            $list.append(item);
            if (!n.read) unread++;
        });
        $('#notification-count').text(unread);
    }

    $(document).on('click', '.notification-item', async function () {
        const id = $(this).data('id');
        const notif = notifications.find(n => n.id === id);
        if (notif) {
            if (!notif.read) {
                await markAsRead(id);
                notif.read = true;
                render();
            }
            if (notif.link) {
                window.location.href = notif.link;
            }
        }
    });
    function ensureTrayExists() {
        tray = document.getElementById('notificationTray');
        if (!tray) {
            tray = document.createElement('div');
            tray.id = 'notificationTray';
            tray.className = 'modal fade';
            tray.tabIndex = -1;
            tray.setAttribute('aria-labelledby', 'notificationTrayLabel');
            tray.setAttribute('aria-hidden', 'true');
            tray.innerHTML =
                '<div class="modal-dialog modal-dialog-scrollable">' +
                '<div class="modal-content">' +
                '<div class="modal-header">' +
                '<h5 id="notificationTrayLabel" class="modal-title">Notificaciones</h5>' +
                '<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>' +
                '</div>' +
                '<div class="modal-body">' +
                '<ul id="notification-list" class="list-group"></ul>' +
                '</div>' +
                '</div></div>';
            document.body.appendChild(tray);
        }
    }

    function init() {
        ensureTrayExists();
        if (tray) {
            bootstrap.Modal.getOrCreateInstance(tray);
        }
        loadNotifications();
    }

    if (document.readyState !== 'loading') {
        init();
    } else {
        document.addEventListener('DOMContentLoaded', init);
    }
})();
