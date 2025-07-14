(function () {
    App.loadWeather = async function () {
        const el = document.getElementById('weather');
        if (!el) return;

        const cropId = localStorage.getItem('cropId');
        const token = localStorage.getItem('token');
        if (!cropId || !token || !navigator.onLine) return;

        try {
            const res = await fetch('/weather', {
                headers: {
                    cropId,
                    Authorization: 'Bearer ' + token
                }
            });

            if (!res.ok) return;
            const data = await res.json();

            if (data && data.temperature !== undefined) {
                const t = data.temperature.toFixed(1); // °C
                const w = data.windspeed != null ? data.windspeed.toFixed(1) : null; // km/h
                const l = data.location || 'Unknown';

                el.textContent = `${l}  |°|  🌡️ Temp: ${t}°C` + (w ? `  |°|  🌬️ Viento: ${w} km/h` : '');
            }
        } catch (e) {
            console.log('weather error', e);
        }
    };

    document.addEventListener('DOMContentLoaded', () => App.loadWeather());
})();
