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

function loadPageModule() {
  const body = document.body || document.getElementsByTagName('body')[0];
  const pageAttr = body?.getAttribute('data-page') ||
    document.documentElement?.getAttribute('data-page');
  if (!pageAttr) return;
  import(`/js/pages/${pageAttr}/index.js`).then(m => m?.init?.()).catch(() => {
    import(`/js/pages/${pageAttr}.js`).then(m => m?.init?.()).catch(() => {});
  });
}

if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', loadPageModule);
} else {
  loadPageModule();
}
