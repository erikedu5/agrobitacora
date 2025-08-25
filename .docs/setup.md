# Agrobitácora — Convenciones Unificadas (Frontend + Backend)

> **Objetivo**: Un único documento para alinear *vibe coding* y desarrollo humano con la misma arquitectura, estilo y calidad en todo el proyecto.

---

## 0) Prompt “Sistema” para Vibe Coding (copiar tal cual al inicio de cada tarea)

```
Actúa como Staff Engineer full-stack para Agrobitácora.

Stack FIJO: Java 17, Spring Boot 3.2.x, Spring Data JPA, PostgreSQL, Thymeleaf (SSR), PWA (service worker + manifest.webmanifest), Spring Security con JWT, Bean Validation, springdoc-openapi, Lombok.

Estructura FIJA (paquetes):
com.meztlitech.agrobitacora
 ├─ config       (SecurityConfig, Swagger/OpenAPI, WebMvcConfig, GlobalExceptionHandler, JpaConfig)
 ├─ controller   (REST y MVC; REST bajo /api/v1/**)
 ├─ dto          (records Request/Response + ApiResponse)
 ├─ entity       (JPA @Entity)
 ├─ repository   (interfaces JPA)
 ├─ service      (@Service con lógica de negocio)
 └─ util         (helpers puntuales)

Convenciones OBLIGATORIAS:
- No exponer Entities en REST; usar DTOs (records) con Bean Validation.
- Respuesta REST estándar: ResponseEntity<ApiResponse<T>>.
- Errores centralizados en GlobalExceptionHandler.
- Seguridad: JWT stateless en /api/**. Permitir PWA: /manifest.webmanifest, /sw.js, /assets/**, /css/**, /js/**, /offline, /, /app/**. Swagger abierto.
- Thymeleaf: layout + fragments; no romper navegación offline (ruta /offline).
- Recursos estáticos con cache busting (content-based versioning).
- Commits: Conventional Commits.

Al responder SIEMPRE incluye:
1) Rutas de archivos NUEVOS/MODIFICADOS,
2) Código completo por archivo,
3) Notas de validación/seguridad/PWA,
4) (Si aplica) snippets Thymeleaf/JS y endpoints REST.
```

---

## 1) Backend — Convenciones

### 1.1 Stack y supuestos

* Java 17 / Spring Boot 3.2.x
* Spring Data JPA + PostgreSQL
* Spring Security (JWT, stateless en `/api/**`)
* Bean Validation (jakarta.validation)
* springdoc-openapi (Swagger UI)
* Lombok

### 1.2 Estructura y nombres

```
com.meztlitech.agrobitacora
 ├─ config
 ├─ controller
 ├─ dto
 ├─ entity
 ├─ repository
 ├─ service
 └─ util
```

* Entidades **singular**: `ThingEntity`.
* Repos: `ThingRepository` (extiende `JpaRepository<ThingEntity, Long>`).
* Servicios: `ThingService` (negocio; los controladores no tocan repos).
* REST: `ThingRestController` en `/api/v1/things`.
* MVC: `ThingPageController` en `/app/things`.
* DTOs: `ThingRequest`, `ThingResponse` (records).

### 1.3 Respuesta y errores

**`dto/ApiResponse.java`**

```java
package com.meztlitech.agrobitacora.dto;

public record ApiResponse<T>(boolean ok, String message, T data) {
  public static <T> ApiResponse<T> ok(T data){ return new ApiResponse<>(true, null, data); }
  public static <T> ApiResponse<T> ok(String msg, T data){ return new ApiResponse<>(true, msg, data); }
  public static <T> ApiResponse<T> error(String msg){ return new ApiResponse<>(false, msg, null); }
}
```

**`config/GlobalExceptionHandler.java`**

```java
package com.meztlitech.agrobitacora.config;

import com.meztlitech.agrobitacora.dto.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<String>> handleValidation(MethodArgumentNotValidException ex) {
    return ResponseEntity.badRequest().body(ApiResponse.error("Validación inválida: " + ex.getMessage()));
  }
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponse<String>> handleConstraint(ConstraintViolationException ex) {
    return ResponseEntity.badRequest().body(ApiResponse.error("Violación de restricción: " + ex.getMessage()));
  }
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<String>> handleGeneric(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error("Error inesperado: " + ex.getMessage()));
  }
}
```

### 1.4 Seguridad y recursos PWA

**`config/SecurityConfig.java`**

```java
package com.meztlitech.agrobitacora.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(
          "/", "/index", "/index.html", "/app/**",
          "/offline",
          "/manifest.webmanifest", "/sw.js",
          "/assets/**", "/css/**", "/js/**", "/webjars/**",
          "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"
        ).permitAll()
        .requestMatchers(HttpMethod.GET, "/api/public/**").permitAll()
        .anyRequest().authenticated()
      )
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .httpBasic(Customizer.withDefaults());
    return http.build();
  }
}
```

**`config/WebMvcConfig.java`**

```java
package com.meztlitech.agrobitacora.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/sw.js").addResourceLocations("classpath:/static/sw.js");
  }
}
```

### 1.5 JPA Auditing (opcional)

**`config/JpaConfig.java`**

```java
package com.meztlitech.agrobitacora.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaConfig {}
```

*(Si usas auditoría temporal, define un `BaseEntity` con `@CreatedDate`, `@LastModifiedDate`, `@Version` y extiéndelo en tus entidades.)*

### 1.6 Convenciones REST

* Base path: **`/api/v1`**.
* Nunca devolver Entities, solo **Response DTO**.
* **Request DTO** con Bean Validation; Controllers reciben `@Valid`.
* Responder `ResponseEntity<ApiResponse<T>>`.
* Paginación (`Pageable`): `page`, `size`, `sort`.
* Búsquedas con `?q=` o filtros explícitos.

**Ejemplo**

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ThingRestController {
  private final ThingService service;

  @GetMapping("/things")
  public ResponseEntity<ApiResponse<List<ThingResponse>>> list(){
    return ResponseEntity.ok(ApiResponse.ok(service.list()));
  }
}
```

### 1.7 Swagger / OpenAPI

* Usar `springdoc-openapi-starter-webmvc-ui`.
* UI abierta en `/swagger-ui`.

### 1.8 Testing

* Unit tests a Services; integración a Repositories (opcional Testcontainers PostgreSQL).
* Validar estructura `ApiResponse`.

### 1.9 Maven a asegurar

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

*(Opcional MapStruct para mapeos.*)

### 1.10 Checklist Backend (PR)

* [ ] Paquetes y nombres según convención.
* [ ] DTOs (records) con Bean Validation.
* [ ] Endpoints bajo `/api/v1/**`.
* [ ] Respuestas envuelven `ApiResponse<T>`.
* [ ] Errores centralizados (GlobalExceptionHandler).
* [ ] Swagger muestra endpoints nuevos.
* [ ] Tests mínimos y/o guía para agregarlos.
* [ ] Commits con Conventional Commits.

---

## 2) Frontend — Convenciones (Thymeleaf + PWA)

### 2.1 Estructura de carpetas

```
src/main/resources/
  templates/
    layouts/
      main.html            # layout base
      admin.html           # (opcional) layout admin
    pages/
      admin/
        users/
          index.html
          form.html
      crop/
        index.html
        form.html
    fragments/
      shared/
        _navbar.html
        _footer.html
        _flash.html
        _pagination.html
        _breadcrumbs.html
      users/
        _table.html
        _form.html
  static/
    css/
      app.css
      pages/
        admin/users.css
    js/
      app.js               # entrypoint único (ESM)
      api.js               # fetch wrappers
      ui.js
      components/
        modal.js
      pages/
        admin/users/index.js
        admin/users/form.js
    manifest.webmanifest
    sw.js
```

> Puedes migrar gradualmente desde la estructura actual; todo **nuevo** debe seguir este patrón.

### 2.2 Layout y Páginas

**`templates/layouts/main.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="es">
<head th:fragment="head(title)">
  <meta charset="utf-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <link rel="manifest" href="/manifest.webmanifest"/>
  <title th:text="${title} ?: 'Agrobitácora'">Agrobitácora</title>
  <link rel="stylesheet" href="/css/app.css"/>
</head>
<body th:with="pageId=${pageId}" th:attr="data-page=${pageId}">
  <header th:replace="~{fragments/shared/_navbar :: navbar}"></header>
  <main>
    <div th:replace="~{fragments/shared/_flash :: flash}"></div>
    <div th:fragment="content">[CONTENT]</div>
  </main>
  <footer th:replace="~{fragments/shared/_footer :: footer}"></footer>
  <script>if ('serviceWorker' in navigator) { navigator.serviceWorker.register('/sw.js').catch(()=>{}); }</script>
  <script type="module" src="/js/app.js"></script>
</body>
</html>
```

**Página ejemplo `templates/pages/admin/users/index.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" th:replace="~{layouts/main :: head('Usuarios')}">
<body>
<main th:replace="~{layouts/main :: content}">
  <section class="page page--admin-users">
    <h1>Usuarios</h1>
    <form method="get" th:action="@{/app/admin/users}" class="filters">
      <input type="text" name="q" th:value="${q}" placeholder="Buscar..."/>
      <button type="submit">Buscar</button>
    </form>
    <div th:replace="~{fragments/users/_table :: table(${users})}"></div>
    <div th:replace="~{fragments/shared/_pagination :: pagination(${page})}"></div>
  </section>
</main>
</body>
</html>
```

### 2.3 Fragmentos compartidos

**`templates/fragments/shared/_flash.html`**

```html
<div th:fragment="flash">
  <div class="flash" th:if="${flash_success}" th:text="${flash_success}"></div>
  <div class="flash flash--error" th:if="${flash_error}" th:text="${flash_error}"></div>
</div>
```

**`templates/fragments/shared/_pagination.html`**

```html
<nav th:fragment="pagination(page)">
  <ul class="pagination" th:if="${page != null}">
    <li th:each="i : ${#numbers.sequence(0, page.totalPages-1)}">
      <a th:href="${'?' + 'page=' + i}" th:text="${i+1}" th:classappend="${page.number==i}? 'is-active' : ''"></a>
    </li>
  </ul>
</nav>
```

### 2.4 JavaScript modular

**`static/js/app.js`**

```js
const page = document.body.getAttribute('data-page');
if (page) {
  import(`/js/pages/${page}/index.js`).then(m => m?.init?.()).catch(() => {
    import(`/js/pages/${page}.js`).then(m => m?.init?.()).catch(()=>{});
  });
}
```

**`static/js/api.js`**

```js
function headers(extra = {}) {
  const h = { 'Content-Type': 'application/json' };
  // Si usas JWT: const t = localStorage.getItem('token'); if (t) h['Authorization'] = `Bearer ${t}`;
  return { ...h, ...extra };
}
async function handle(res) {
  const json = await res.json().catch(() => ({}));
  if (!res.ok || json?.ok === false) {
    throw new Error(json?.message || `HTTP ${res.status}`);
  }
  return json?.data ?? json;
}
export const apiGet = (u) => fetch(u, { headers: headers() }).then(handle);
export const apiPost = (u,b) => fetch(u,{method:'POST',headers:headers(),body:JSON.stringify(b)}).then(handle);
export const apiPut = (u,b) => fetch(u,{method:'PUT',headers:headers(),body:JSON.stringify(b)}).then(handle);
export const apiDelete = (u) => fetch(u,{method:'DELETE',headers:headers()}).then(handle);
```

**`static/js/components/modal.js`**

```js
export function confirmDialog(message){ return Promise.resolve(window.confirm(message)); }
```

### 2.5 MVC Controllers (pasar `pageId`)

```java
@GetMapping("/app/admin/users")
public String users(Model model, @RequestParam(required=false) String q) {
  model.addAttribute("pageId", "admin/users"); // -> /js/pages/admin/users/index.js
  model.addAttribute("q", q);
  model.addAttribute("users", userService.list(q));
  model.addAttribute("page", userService.page(q));
  return "pages/admin/users/index";
}
```

### 2.6 CSS (BEM + variables)

```css
:root{ --primary:#0ea5e9; --bg:#fff; --text:#111; }
.page{ padding:1.25rem; }
.btn{ padding:.5rem .75rem; border-radius:.5rem; }
.btn--primary{ background:var(--primary); color:#fff; }
```

### 2.7 PWA

* Manifest en `/manifest.webmanifest` y SW en `/sw.js`.
* Estrategias recomendadas: **Network-First** para navegación (fallback `/offline`) y **Stale-While-Revalidate** para estáticos.
* SSR primero; JS como *progressive enhancement*.

### 2.8 Accesibilidad e i18n

* `<label>` unido a inputs (`th:field`).
* Botones con texto o `aria-label`.
* Textos que deban traducirse: evitar hardcode en JS; leer del DOM o properties.

### 2.9 Checklist Frontend (PR)

* [ ] Página en `templates/pages/**` que **extiende** `layouts/main.html`.
* [ ] Sin scripts inline; `data-page` correcto + módulo `init()`.
* [ ] Fragmentos en `templates/fragments/**` (tabla, formulario, paginación).
* [ ] Estilos mínimos, BEM + variables.
* [ ] Funciona sin JS (SSR) y mejora con JS.
* [ ] Registro de SW y fallback `/offline` operando.

---

## 3) Plantilla de Tarea (para usar con el prompt)

```
Tarea:
[Describir qué crear/modificar].

Requisitos:
- REST bajo /api/v1/[recurso]; DTOs Request/Response (records) con Bean Validation.
- Service con mapeo Entity <-> DTO; respuesta con ApiResponse<T>.
- (Si aplica) vista Thymeleaf en templates/pages/** y fragmentos en templates/fragments/**.
- (Si aplica) JS modular (pages/<area>/<recurso>/index.js) con init().
- Paginación y búsqueda (?q=) cuando aplique.

Entregables:
1) Archivos con rutas exactas (NUEVOS/MODIFICADOS).
2) Código completo por archivo.
3) Notas de seguridad/validación/PWA.
4) (Si aplica) snippets Thymeleaf/JS y endpoints REST.
```

---

## 4) Roadmap de migración (si tu código actual no separa pantallas)

1. Crear `templates/layouts/main.html` y mover navbar/footer a `templates/fragments/shared/`.
2. Nuevas pantallas en `templates/pages/**` y JS modular en `static/js/pages/**`.
3. Extraer tablas y formularios a `templates/fragments/**`.
4. Añadir `data-page` y `static/js/app.js` (dynamic import) en el layout.
5. Centralizar llamadas REST en `static/js/api.js`.
6. Habilitar cache busting de estáticos (config en YAML) y verificar PWA offline.

---