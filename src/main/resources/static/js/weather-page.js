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

    let historyChart;

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
            const labels = (data || []).map(r => r.date);
            const tmin = (data || []).map(r => r.temperatureMin);
            const tmax = (data || []).map(r => r.temperatureMax);
            const ctx = document.getElementById('historyChart');
            if (!ctx) return;
            if (historyChart) historyChart.destroy();
            historyChart = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels,
                    datasets: [
                        {
                            label: ctx.dataset.tmin,
                            backgroundColor: 'rgba(54, 162, 235, 0.5)',
                            data: tmin
                        },
                        {
                            label: ctx.dataset.tmax,
                            backgroundColor: 'rgba(255, 99, 132, 0.5)',
                            data: tmax
                        }
                    ]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: { y: { beginAtZero: false } }
                }
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
