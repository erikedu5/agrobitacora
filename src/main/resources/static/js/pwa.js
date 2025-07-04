if ('serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    navigator.serviceWorker.register('/sw.js')
      .then(() => console.log('Service worker registrado'))
      .catch(err => console.error('Error al registrar el service worker', err));
  });
}
