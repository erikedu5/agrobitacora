(function () {
    App.loadWeather = async function () {
        const el = document.getElementById('weather');
        if (!el) return;
        const cropId = localStorage.getItem('cropId');
        if (!cropId || !navigator.onLine) return;
        try {
            const res = await fetch('/weather', { headers: { cropId } });
            if (!res.ok) return;
            const data = await res.json();
            if (data && data.temperature !== undefined) {
                const t = data.temperature.toFixed(1);
                const w = data.windspeed != null ? data.windspeed.toFixed(1) : '';
                el.textContent = `${t}°C` + (w ? ` | ${w} km/h` : '');
            }
        } catch (e) {
            console.log('weather error', e);
        }
    };

    document.addEventListener('DOMContentLoaded', () => App.loadWeather());
})();
