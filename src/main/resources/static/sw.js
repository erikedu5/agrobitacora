const CACHE_NAME = 'agrobitacora-cache-v4';
const URLS_TO_CACHE = [
  '/',
  '/home',
  '/auth',
  '/bill',
  '/crop',
  '/fumigation',
  '/irrigation',
  '/labor',
  '/nutrition',
  '/production',
  '/weather/details',
  '/admin',
  '/admin/users',
  '/admin/engineers',
  '/manifest.webmanifest',
  '/js/app.js',
  '/js/pwa.js',
  '/js/weather.js',
  '/js/weather-page.js',
  '/js/common.js',
  '/js/notifications.js',
  // icons are embedded in the manifest as data URIs
];
self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then(cache => cache.addAll(URLS_TO_CACHE))
  );
});
self.addEventListener('fetch', event => {
  if (event.request.method !== 'GET') {
    return;
  }

  const dest = event.request.destination;

  if (event.request.mode !== 'navigate' && dest === '') {
    // network first for API/AJAX requests
    event.respondWith(
      fetch(event.request)
        .then(resp => {
          if (resp && resp.status === 200) {
            const clone = resp.clone();
            caches.open(CACHE_NAME).then(c => c.put(event.request, clone));
          }
          return resp;
        })
        .catch(() => caches.match(event.request))
    );
    return;
  }

  event.respondWith(
    caches.match(event.request).then(cached => {
      if (cached) {
        return cached;
      }
      if (!self.navigator.onLine) {
        if (event.request.mode === 'navigate') {
          return caches.match('/');
        }
        return;
      }
      return fetch(event.request)
        .then(resp => {
          if (resp && resp.status === 200) {
            const clone = resp.clone();
            caches.open(CACHE_NAME).then(c => c.put(event.request, clone));
          }
          return resp;
        })
        .catch(() => {
          if (event.request.mode === 'navigate') {
            return caches.match('/');
          }
        });
    })
  );
});
self.addEventListener('activate', event => {
  event.waitUntil(
    caches.keys().then(keys => Promise.all(
      keys.filter(k => k !== CACHE_NAME).map(k => caches.delete(k))
    ))
  );
});
