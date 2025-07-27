App.registerEntity('adminlicenses', {
    url: '/license/all',
    buildRow: l => `<tr><td>${l.id}</td><td>${l.schoolName}</td><td>${l.studentTotal}</td><td>${l.expirationDate}</td></tr>`
});
