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
          <thead><tr><th>ID</th><th>Nombre</th><th></th></tr></thead>
          <tbody></tbody>
        </table>
      </div>
      <div class="col-md-6">
        <h5 class="mb-3" data-i18n="association.my.engineers">Mis ingenieros</h5>
        <table id="my-engineers" class="table table-striped">
          <thead><tr><th>ID</th><th>Nombre</th><th></th></tr></thead>
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
          <thead><tr><th>ID</th><th>Nombre</th><th></th></tr></thead>
          <tbody></tbody>
        </table>
      </div>
      <div class="col-md-6">
        <h5 class="mb-3" data-i18n="association.my.producers">Mis productores</h5>
        <table id="my-producers" class="table table-striped">
          <thead><tr><th>ID</th><th>Nombre</th><th></th></tr></thead>
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
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${e.id}</td><td>${e.name}</td><td><button class="btn btn-sm btn-primary" data-id="${e.id}" data-action="add-engineer">${i18n('association.add')}</button></td>`;
        tbody.appendChild(tr);
    });
    tbody.querySelectorAll('button[data-action="add-engineer"]').forEach(btn => {
        btn.addEventListener('click', async () => {
            await fetch(`/producer/engineers/${btn.dataset.id}`, {
                method: 'POST',
                headers: { Authorization: 'Bearer ' + App.getToken() }
            });
            loadMyEngineers();
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
        tr.innerHTML = `<td>${e.id}</td><td>${e.name}</td><td><button class="btn btn-sm btn-danger" data-id="${e.id}" data-action="del-engineer">${i18n('association.remove')}</button></td>`;
        tbody.appendChild(tr);
    });
    tbody.querySelectorAll('button[data-action="del-engineer"]').forEach(btn => {
        btn.addEventListener('click', async () => {
            await fetch(`/producer/engineers/${btn.dataset.id}`, {
                method: 'DELETE',
                headers: { Authorization: 'Bearer ' + App.getToken() }
            });
            loadMyEngineers();
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
        tr.innerHTML = `<td>${p.id}</td><td>${p.name}</td><td><button class="btn btn-sm btn-primary" data-id="${p.id}" data-action="add-producer">${i18n('association.add')}</button></td>`;
        tbody.appendChild(tr);
    });
    tbody.querySelectorAll('button[data-action="add-producer"]').forEach(btn => {
        btn.addEventListener('click', async () => {
            await fetch(`/engineer/producers/${btn.dataset.id}`, {
                method: 'POST',
                headers: { Authorization: 'Bearer ' + App.getToken() }
            });
            loadMyProducers();
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
        tr.innerHTML = `<td>${p.id}</td><td>${p.name}</td><td><button class="btn btn-sm btn-danger" data-id="${p.id}" data-action="del-producer">${i18n('association.remove')}</button></td>`;
        tbody.appendChild(tr);
    });
    tbody.querySelectorAll('button[data-action="del-producer"]').forEach(btn => {
        btn.addEventListener('click', async () => {
            await fetch(`/engineer/producers/${btn.dataset.id}`, {
                method: 'DELETE',
                headers: { Authorization: 'Bearer ' + App.getToken() }
            });
            loadMyProducers();
        });
    });
}

function i18n(key) {
    if (key === 'association.add') return document.getElementById('assoc-add').textContent.trim();
    if (key === 'association.remove') return document.getElementById('assoc-remove').textContent.trim();
    return key;
}
