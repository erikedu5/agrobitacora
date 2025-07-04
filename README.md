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

Las páginas de gestión ahora muestran automáticamente los registros existentes.
Se obtiene la lista con solicitudes `fetch` que incluyen el token almacenado y se
puebla la tabla de cada vista. Al visitar la sección de cultivos, el primer
registro disponible se guarda como `cropId` en `localStorage`, lo cual se usa
para consultar la información asociada en las demás páginas. Si el usuario es
Productor, ese `cropId` también se envía como cabecera HTTP en todas las
peticiones para que el servidor pueda identificar el cultivo seleccionado.

## Integración con WhatsApp

Se añadió un nuevo servicio y controlador para enviar mensajes mediante la API de WhatsApp Business. Para habilitarlo es necesario definir las variables de entorno `WHATSAPP_TOKEN` y `WHATSAPP_PHONE_NUMBER_ID` o establecer los valores correspondientes en `application.properties`.

El endpoint `/whatsapp/send` permite enviar un mensaje simple especificando los parámetros `to` (número de destino) y `message`.

## PWA

La aplicación incluye soporte básico como [Progressive Web App](https://developer.mozilla.org/es/docs/Web/Progressive_web_apps). Se proporciona un `manifest.webmanifest` (con los iconos en formato base64) y se registra un *service worker* para almacenar en caché los recursos estáticos principales y permitir su uso sin conexión.

### Modo sin conexión

Cuando no haya conexión a internet las fumigaciones y nutriciones creadas se guardarán localmente en el navegador. Al recuperar la red se enviarán automáticamente al servidor. Si se inicia sesión sin conexión, la aplicación mostrará los registros almacenados de forma local.

## Ejecución local

1. Instala JDK 17 (o superior) y clona este repositorio.
2. Desde la raíz ejecuta `./mvnw spring-boot:run` para iniciar la aplicación. La primera vez se descargarán las dependencias de Maven.
3. La aplicación quedará disponible en [http://localhost:8081](http://localhost:8081).
4. Si deseas probar la integración con WhatsApp define las variables de entorno `WHATSAPP_TOKEN` y `WHATSAPP_PHONE_NUMBER_ID` antes de iniciar.
5. Al abrir la página principal tu navegador ofrecerá la opción de instalar la aplicación como PWA.
   El *service worker* solo se registra en contextos seguros. `localhost` se
   considera seguro, por lo que basta con abrir
   [http://localhost:8081](http://localhost:8081) para probarlo.
   Si necesitas acceder desde otro dispositivo, genera un certificado
   autofirmado y habilita HTTPS en Spring Boot:

   ```bash
   keytool -genkeypair -alias agrobitacora -keyalg RSA -keysize 2048 \
          -storetype PKCS12 -keystore keystore.p12 -validity 365
   ```

   Luego agrega en `application.properties` las siguientes líneas y ejecuta la
   aplicación de nuevo:

   ```properties
   server.ssl.enabled=true
   server.ssl.key-store=classpath:keystore.p12
   server.ssl.key-store-password=<contraseña>
   ```

