# agrobitacora

Esta aplicación incluye ahora una interfaz sencilla basada en Thymeleaf.
Todas las plantillas se encuentran en `src/main/resources/templates` y
pueden consultarse desde la ruta raíz (`/`). Se usa el tema
[Bootswatch Flatly](https://bootswatch.com/flatly/) para dar estilo a las
páginas y se agregaron formularios básicos para crear registros de cada
controlador disponible.

El ingreso a la aplicación ahora requiere autenticarse desde `/auth`. Se debe
utilizar como usuario el correo electrónico con el que se registró. Tras un
inicio de sesión o registro exitoso, el token JWT se guarda en `localStorage` y
se envía automáticamente en cada petición mediante JavaScript.
