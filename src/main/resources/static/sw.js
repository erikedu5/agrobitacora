const CACHE_NAME = 'agrobitacora-cache-v7';
let userRole = 'GUEST'; // valor por defecto

// Rutas comunes para todos los usuarios
const COMMON_ROUTES = [
  '/',
  '/home',
  '/auth',
  '/manifest.webmanifest',
  '/js/app.js',
  '/js/pwa.js',
  '/js/weather.js',
  '/js/common.js',
  '/js/notifications.js'
];

// Rutas específicas según el rol
const ROLE_ROUTES = {
  ADMIN: [
    '/admin',
    '/admin/users',
    '/admin/engineers'
  ],
  USER: [
    '/crop',
    '/bill',
    '/irrigation',
    '/fumigation',
    '/nutrition',
    '/production',
    '/labor',
    '/association',
    '/balance'
  ]
};

// Captura el rol del usuario desde el frontend
self.addEventListener('message', (event) => {
  if (event.data && event.data.type === 'ROLE') {
    userRole = event.data.value || 'GUEST';
  }
});

// Cacheo en instalación, basado en rol
self.addEventListener('install', (event) => {
  event.waitUntil((async () => {
    const cache = await caches.open(CACHE_NAME);
    const roleRoutes = ROLE_ROUTES[userRole] || [];
    const routesToCache = [...COMMON_ROUTES, ...roleRoutes];
    await cache.addAll(routesToCache);
  })());
});

// Limpieza de versiones anteriores
self.addEventListener('activate', (event) => {
  event.waitUntil(caches.keys().then(keys =>
    Promise.all(keys.filter(k => k !== CACHE_NAME).map(k => caches.delete(k)))
  ));
});

// Manejo de peticiones
function networkFirst(request) {
  return fetch(request)
    .then(resp => {
      if (resp && resp.status === 200) {
        const clone = resp.clone();
        caches.open(CACHE_NAME).then(cache => cache.put(request, clone));
      }
      return resp;
    })
    .catch(() => caches.match(request));
}

function cacheFirst(request) {
  return caches.match(request).then(cached => {
    return cached || fetch(request).then(resp => {
      if (resp && resp.status === 200) {
        const clone = resp.clone();
        caches.open(CACHE_NAME).then(cache => cache.put(request, clone));
      }
      return resp;
    }).catch(() => undefined);
  });
}

self.addEventListener('fetch', (event) => {
  const req = event.request;
  const url = new URL(req.url);

  if (req.method !== 'GET') return;

  // Bloqueo explícito a /admin si no tiene el rol
  if (url.pathname.startsWith('/admin') && userRole !== 'ADMIN') {
    event.respondWith(Response.redirect('/auth'));
    return;
  }

  if (req.mode === 'navigate') {
    event.respondWith(networkFirst(req));
    return;
  }

  // Solo usa cache-first para recursos estáticos (js, css, img, fonts)
  if (req.destination && ['script', 'style', 'image', 'font'].includes(req.destination)) {
    event.respondWith(cacheFirst(req));
    return;
  }

  // Para peticiones AJAX usa network-first
  event.respondWith(networkFirst(req));
});
