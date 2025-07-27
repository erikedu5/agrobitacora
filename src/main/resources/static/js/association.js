document.addEventListener('DOMContentLoaded', () => {
    const role = App.getRole();
    if (role === 'Productor') {
        initProducer();
    } else if (role === 'Ingeniero') {
        initEngineer();
    }
});

async function initProducer() {
    const container = document.getElementById('association-content');
    container.innerHTML = `
    <div class="row">
      <div class="col-md-6">
        <h5 class="mb-3" data-i18n="association.available.engineers">Ingenieros disponibles</h5>
        <table id="all-engineers" class="table table-striped">
          <thead><tr><th>ID</th><th>Nombre</th><th>Whatsapp</th><th>Ranking</th><th></th></tr></thead>
          <tbody></tbody>
        </table>
      </div>
      <div class="col-md-6">
        <h5 class="mb-3" data-i18n="association.my.engineers">Mis ingenieros</h5>
        <table id="my-engineers" class="table table-striped">
          <thead><tr><th>ID</th><th>Nombre</th><th>Whatsapp</th><th></th><th></th></tr></thead>
          <tbody></tbody>
        </table>
      </div>
    </div>`;
    await loadAllEngineers();
    await loadMyEngineers();
}

async function initEngineer() {
    const container = document.getElementById('association-content');
    container.innerHTML = `
    <div class="row">
      <div class="col-md-6">
        <h5 class="mb-3" data-i18n="association.available.producers">Productores disponibles</h5>
        <table id="all-producers" class="table table-striped">
          <thead><tr><th>ID</th><th>Nombre</th><th>Whatsapp</th><th></th></tr></thead>
          <tbody></tbody>
        </table>
      </div>
      <div class="col-md-6">
        <h5 class="mb-3" data-i18n="association.my.producers">Mis productores</h5>
        <table id="my-producers" class="table table-striped">
          <thead><tr><th>ID</th><th>Nombre</th><th>Whatsapp</th><th></th></tr></thead>
          <tbody></tbody>
        </table>
      </div>
    </div>`;
    await loadAllProducers();
    await loadMyProducers();
}

async function loadAllEngineers() {
    const res = await fetch('/producer/engineers', {
        headers: { Authorization: 'Bearer ' + App.getToken() }
    });
    if (!res.ok) return;
    const data = await res.json();
    const tbody = document.querySelector('#all-engineers tbody');
    tbody.innerHTML = '';
    data.forEach(e => {
        const rank = e.ranking ? e.ranking.toFixed(1) : '0';
        const tr = document.createElement('tr');
        const whatsapp = e.whatsapp != null ? `https://wa.me/521${e.whatsapp}?text=Hola!,%20Me%20gustaria%20que%20asesores,%20contactame!!!` : '';
        tr.innerHTML = `<td>${e.id}</td><td>${e.name}</td><td><a target="_blank" href=${whatsapp}>${e.whatsapp}</a></td><td>${rank}</td><td><button class="btn btn-sm btn-primary" data-id="${e.id}" data-action="add-engineer">${i18n('association.add')}</button></td>`;
        tbody.appendChild(tr);
    });
    tbody.querySelectorAll('button[data-action="add-engineer"]').forEach(btn => {
        btn.addEventListener('click', async () => {
            await fetch(`/producer/engineers/${btn.dataset.id}`, {
                method: 'POST',
                headers: { Authorization: 'Bearer ' + App.getToken() }
            });
            await loadMyEngineers();
            await loadAllEngineers();
        });
    });
}

async function loadMyEngineers() {
    const res = await fetch('/producer/my-engineers', {
        headers: { Authorization: 'Bearer ' + App.getToken() }
    });
    if (!res.ok) return;
    const data = await res.json();
    const tbody = document.querySelector('#my-engineers tbody');
    tbody.innerHTML = '';
    data.forEach(e => {
        const tr = document.createElement('tr');
        const whatsapp = e.whatsapp != null ? `https://wa.me/521${e.whatsapp}` : '';
        tr.innerHTML = `<td>${e.id}</td><td>${e.name}</td><td><a target="_blank" href=${whatsapp}>${e.whatsapp}</a></td><td><button class="btn btn-sm btn-secondary" data-id="${e.id}" data-action="rate-engineer">${i18n('association.rate')}</button></td><td><button class="btn btn-sm btn-danger" data-id="${e.id}" data-action="del-engineer">${i18n('association.remove')}</button></td>`;
        tbody.appendChild(tr);
    });
    tbody.querySelectorAll('button[data-action="rate-engineer"]').forEach(btn => {
        btn.addEventListener('click', () => rateEngineer(btn.dataset.id));
    });
    tbody.querySelectorAll('button[data-action="del-engineer"]').forEach(btn => {
        btn.addEventListener('click', async () => {
            await fetch(`/producer/engineers/${btn.dataset.id}`, {
                method: 'DELETE',
                headers: { Authorization: 'Bearer ' + App.getToken() }
            });
            await loadMyEngineers();
            await loadAllEngineers();
        });
    });
}

async function loadAllProducers() {
    const res = await fetch('/engineer/all-producers', {
        headers: { Authorization: 'Bearer ' + App.getToken() }
    });
    if (!res.ok) return;
    const data = await res.json();
    const tbody = document.querySelector('#all-producers tbody');
    tbody.innerHTML = '';
    data.forEach(p => {
        const tr = document.createElement('tr');
        const whatsapp = p.whatsapp != null ? `https://wa.me/521${p.whatsapp}?text=Hola!,%20Ingresa%20a%20https://bitacora.pixka.com.mx%20para%20que%20te%20asesore` : '';
        tr.innerHTML = `<td>${p.id}</td><td>${p.name}</td><td><a target="_blank" href=${whatsapp}>${p.whatsapp}</a></td><td><button class="btn btn-sm btn-primary" data-id="${p.id}" data-action="add-producer">${i18n('association.add')}</button></td>`;
        tbody.appendChild(tr);
    });
    tbody.querySelectorAll('button[data-action="add-producer"]').forEach(btn => {
        btn.addEventListener('click', async () => {
            await fetch(`/engineer/producers/${btn.dataset.id}`, {
                method: 'POST',
                headers: { Authorization: 'Bearer ' + App.getToken() }
            });
            await loadMyProducers();
            await loadAllProducers();
        });
    });
}

async function loadMyProducers() {
    const res = await fetch('/engineer/producers', {
        headers: { Authorization: 'Bearer ' + App.getToken() }
    });
    if (!res.ok) return;
    const data = await res.json();
    const tbody = document.querySelector('#my-producers tbody');
    tbody.innerHTML = '';
    data.forEach(p => {
        const tr = document.createElement('tr');
        const whatsapp = p.whatsapp != null ? `https://wa.me/521${p.whatsapp}` : '';
        tr.innerHTML = `<td>${p.id}</td><td>${p.name}</td><td><a target="_blank" href=${whatsapp}>${p.whatsapp}</a></td><td><button class="btn btn-sm btn-danger" data-id="${p.id}" data-action="del-producer">${i18n('association.remove')}</button></td>`;
        tbody.appendChild(tr);
    });
    tbody.querySelectorAll('button[data-action="del-producer"]').forEach(btn => {
        btn.addEventListener('click', async () => {
            await fetch(`/engineer/producers/${btn.dataset.id}`, {
                method: 'DELETE',
                headers: { Authorization: 'Bearer ' + App.getToken() }
            });
            await loadMyProducers();
            await loadAllProducers();
        });
    });
}

function i18n(key) {
    if (key === 'association.add') return document.getElementById('assoc-add').textContent.trim();
    if (key === 'association.remove') return document.getElementById('assoc-remove').textContent.trim();
    if (key === 'association.rate') return document.getElementById('assoc-rate').textContent.trim();
    if (key === 'association.ratingPrompt') return document.getElementById('assoc-rating-prompt').textContent.trim();
    if (key === 'association.reviewPrompt') return document.getElementById('assoc-review-prompt').textContent.trim();
    return key;
}

async function rateEngineer(id) {
    const rating = prompt(i18n('association.ratingPrompt') || 'Rating (1-5)');
    if (!rating) return;
    const review = prompt(i18n('association.reviewPrompt') || 'Review') || '';
    await fetch(`/producer/engineers/${id}/rating`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            Authorization: 'Bearer ' + App.getToken()
        },
        body: JSON.stringify({ rating: parseInt(rating), review })
    });
    await loadMyEngineers();
    await loadAllEngineers();
}