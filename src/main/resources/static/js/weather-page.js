(function () {
    async function loadDetails() {
        const cropId = localStorage.getItem('cropId');
        const token = localStorage.getItem('token');
        if (!cropId || !token) return;
        try {
            const res = await fetch('/weather', {
                headers: { cropId, Authorization: 'Bearer ' + token }
            });
            if (!res.ok) return;
            const data = await res.json();
            $('#wd-location').text(data.location || '');
            if (data.evapotranspiration != null) $('#wd-et').text(data.evapotranspiration.toFixed(1));
            if (data.temperatureMin != null) $('#wd-tmin').text(data.temperatureMin.toFixed(1));
            if (data.temperatureMax != null) $('#wd-tmax').text(data.temperatureMax.toFixed(1));
            if (data.precipitation != null) $('#wd-rain').text(data.precipitation.toFixed(1));
            if (data.windspeed != null) $('#wd-wind').text(data.windspeed.toFixed(1));
            if (data.relativeHumidity != null) $('#wd-rh').text(data.relativeHumidity.toFixed(1));
            if (data.solarRadiation != null) $('#wd-rad').text(data.solarRadiation.toFixed(1));
            if (data.soilHumidity != null) $('#wd-soil').text(data.soilHumidity.toFixed(1));
        } catch (e) {
            console.log('weather page error', e);
        }
    }

    async function loadHistory() {
        const cropId = localStorage.getItem('cropId');
        const token = localStorage.getItem('token');
        if (!cropId || !token) return;
        try {
            const res = await fetch('/weather/history', {
                headers: { cropId, Authorization: 'Bearer ' + token }
            });
            if (!res.ok) return;
            const data = await res.json();
            const $tbody = $('#history tbody');
            $tbody.empty();
            (data || []).forEach(r => {
                const tmin = r.temperatureMin != null ? r.temperatureMin.toFixed(1) : '';
                const tmax = r.temperatureMax != null ? r.temperatureMax.toFixed(1) : '';
                $tbody.append(`<tr><td>${r.date}</td><td>${tmin}</td><td>${tmax}</td></tr>`);
            });
        } catch (e) {
            console.log('weather history error', e);
        }
    }

    $(function () {
        loadDetails();
        loadHistory();
    });
})();
