if ('serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    navigator.serviceWorker.register('/sw.js').then((registration) => {
      navigator.serviceWorker.ready.then(() => {
        const token = localStorage.getItem('token');
        if (token && registration.active) {
          const payload = JSON.parse(atob(token.split('.')[1]));
          const roleName = (payload.role?.name || 'GUEST').toUpperCase();
          registration.active.postMessage({ type: 'ROLE', value: roleName });
        }
      });
    });
  });
}

document.addEventListener('DOMContentLoaded', () => {
  const page = document.body?.getAttribute('data-page');
  if (page) {
    import(`/js/pages/${page}/index.js`).then(m => m?.init?.()).catch(() => {
      import(`/js/pages/${page}.js`).then(m => m?.init?.()).catch(() => {});
    });
  }
});
