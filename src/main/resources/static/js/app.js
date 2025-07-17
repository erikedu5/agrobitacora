if ('serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    navigator.serviceWorker.register('/sw.js').then((registration) => {
      navigator.serviceWorker.ready.then(() => {
        const token = localStorage.getItem('token');
        if (token) {
          const payload = JSON.parse(atob(token.split('.')[1]));
          const roleName = (payload.role?.name || 'GUEST').toUpperCase();

          if (registration.active) {
            registration.active.postMessage({
              type: 'ROLE',
              value: roleName  // 👈 por ejemplo: 'ADMIN'
            });
          }
        }
      });
    });
  });
}
