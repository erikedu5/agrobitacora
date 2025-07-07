const CACHE_NAME = 'agrobitacora-cache-v3';
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
  '/admin',
  '/admin/users',
  '/admin/engineers',
  '/manifest.webmanifest',
  '/js/app.js',
  '/js/pwa.js',
  '/js/common.js',
  '/js/farm.js',
  '/js/admin.js',
  // icons are embedded in the manifest as data URIs
];
self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then(cache => cache.addAll(URLS_TO_CACHE))
  );
});
self.addEventListener('fetch', event => {
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
            const respClone = resp.clone();
            caches.open(CACHE_NAME).then(cache => cache.put(event.request, respClone));
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
