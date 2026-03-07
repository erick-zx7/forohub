# ForoHub API 🗣️

ForoHub es una API REST diseñada para gestionar los tópicos de un foro educativo. Permite a los usuarios crear, consultar, actualizar y eliminar tópicos, con autenticación segura mediante JWT y un manejo robusto de errores.

---

## 🛠️ Tecnologías Utilizadas

| Tecnología | Versión | Descripción |
|---|---|---|
| ☕ Java | 21 | Lenguaje principal |
| 🍃 Spring Boot | 3.x | Framework principal |
| 🔒 Spring Security | 3.x | Autenticación y autorización |
| 🗄️ Spring Data JPA | 3.x | Capa de persistencia |
| 🐘 PostgreSQL | 15+ | Base de datos relacional |
| 🔄 Flyway | 9.x | Migraciones de base de datos |
| 🔑 Auth0 Java JWT | 4.4.0 | Generación y validación de tokens JWT |
| ✅ Bean Validation | 3.x | Validación de datos de entrada |
| ⚙️ Lombok | 1.18+ | Reducción de código boilerplate |
| 📦 Maven | 3.x | Gestión de dependencias |

---

## 📐 Arquitectura del Proyecto

El proyecto sigue una **Arquitectura en Capas**, donde cada capa tiene una responsabilidad única y bien definida, aplicando el principio de **Separación de Responsabilidades (SRP)** de SOLID.

```
com.forohub
│
├── controller/          → Capa de Presentación (HTTP)
│   ├── AuthController
│   └── TopicoController
│
├── domain/
│   ├── topico/          → Dominio principal
│   │   ├── dto/
│   │   ├── Topico
│   │   ├── TopicoRepository
│   │   ├── TopicoService
│   │   └── EstadoTopico
│   │
│   └── usuario/         → Dominio de seguridad
│       ├── dto/
│       ├── Usuario
│       └── UsuarioRepository
│
└── infra/               → Infraestructura transversal
    ├── security/
    │   ├── SecurityConfig
    │   ├── SecurityFilter
    │   └── TokenService
    └── exception/
        ├── GlobalExceptionHandler
        ├── EntityNotFoundException
        ├── BusinessException
        ├── ErrorResponse
        └── ValidationErrorResponse
```

---

## ⚙️ Configuración del Entorno

### Prerrequisitos
- Java 21
- Maven 3.x
- PostgreSQL 15+

### Variables de Entorno

El proyecto utiliza variables de entorno para proteger las credenciales sensibles. **Nunca se hardcodean valores en el código fuente.**

```properties
DB_USERNAME=tu_usuario_postgres
DB_PASSWORD=tu_password_postgres
JWT_SECRET=unaClaveLargaYSeguraParaFirmarTokens
```

### Configuración en IntelliJ IDEA
1. Ve a `Run > Edit Configurations`
2. Selecciona tu configuración de Spring Boot
3. En `Environment Variables` agrega las tres variables mencionadas

### Base de Datos
Crea la base de datos en PostgreSQL antes de ejecutar la aplicación:
```sql
CREATE DATABASE forohub;
```

> Las tablas son creadas automáticamente por **Flyway** al iniciar la aplicación.

---

## 🗃️ Migraciones de Base de Datos

El proyecto utiliza **Flyway** para versionar el esquema de la base de datos. Las migraciones se ejecutan automáticamente al iniciar la aplicación en el orden de su versión.

### V1 — Tabla `topicos`
Crea la tabla principal con restricciones de integridad:
- `UNIQUE` en `titulo` para garantizar unicidad
- `CHECK CONSTRAINT` en `estado` para valores válidos
- `DEFAULT NOW()` en `fecha_creacion` como red de seguridad

### V2 — Tabla `usuarios`
Crea la tabla de usuarios para autenticación:
- `UNIQUE` en `email` para garantizar un usuario por correo
- `contrasena` almacenada con hash **BCrypt**

---

## 🔐 Autenticación y Seguridad

ForoHub implementa seguridad **Stateless** con tokens **JWT (JSON Web Token)**. Esto significa que el servidor no guarda sesiones: cada petición es autenticada de forma independiente mediante el token enviado en el header.

### Flujo de Autenticación

```
1. Cliente envía credenciales → POST /auth/login
2. Spring Security valida email y contraseña contra la BD
3. Si son válidas → se genera y devuelve un JWT firmado
4. Cliente incluye el token en peticiones posteriores
5. SecurityFilter intercepta cada request y valida el token
6. Si el token es válido → la petición llega al Controller
```

### Características de Seguridad Implementadas

- **BCrypt** para el hash de contraseñas. Nunca se almacena la contraseña en texto plano.
- **HMAC256** como algoritmo de firma del JWT.
- **Expiración del token** configurada a 2 horas.
- **Variables de entorno** para credenciales y secreto JWT.
- **Política STATELESS** — sin cookies ni sesiones HTTP.
- El `SecurityFilter` extiende `OncePerRequestFilter` garantizando que se ejecuta exactamente una vez por request.

### Endpoint Público

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/auth/login` | Obtener token JWT |

### Ejemplo de Login

**Request:**
```json
POST /auth/login
Content-Type: application/json

{
    "email": "admin@forohub.com",
    "contrasena": "tu_password"
}
```

**Response `200 OK`:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

<img width="544" height="239" alt="imagen" src="https://github.com/user-attachments/assets/60c04429-2ce2-4bc7-849d-d12eb17a0d8a" />

---

## 📋 Funcionalidades de la API

Todos los endpoints de tópicos requieren autenticación. Debes incluir el token en el header de cada petición:

```
Authorization: Bearer <tu_token_jwt>
```

---

### ✅ Crear Tópico

**`POST /topicos`**

Crea un nuevo tópico en el foro. Implementa las siguientes validaciones y buenas prácticas:

- **Validación de campos**: Todos los campos son obligatorios (`@NotBlank`) y tienen límite de caracteres (`@Size`). La validación ocurre en la capa de presentación antes de llegar al Service.
- **Validación de duplicados**: El Service verifica que no exista un tópico activo con el mismo `titulo` y `mensaje`. Los tópicos eliminados (soft delete) no cuentan como duplicados, permitiendo recrear tópicos previamente eliminados.
- **Estado inicial**: Todo tópico se crea automáticamente con estado `ABIERTO`.
- **Fecha automática**: `fecha_creacion` es asignada por Hibernate via `@CreationTimestamp`, sin depender del cliente.
- **Respuesta `201 Created`**: Incluye el header `Location` con la URL del recurso creado, siguiendo el estándar REST.

**Request:**

<img width="487" height="204" alt="imagen" src="https://github.com/user-attachments/assets/1ca4fe9e-6407-4fdd-9755-4119a3bf093f" />

**Response `201 Created`:**

<img width="547" height="329" alt="imagen" src="https://github.com/user-attachments/assets/a0c0996a-4cd1-45b9-b97c-155555489dfd" />

---

### 📄 Listar Tópicos

**`GET /topicos`**

Retorna todos los tópicos activos (no eliminados) con **paginación automática**. Buenas prácticas implementadas:

- **Paginación por defecto**: 10 tópicos por página, ordenados por `fechaCreacion` descendente (más recientes primero). Retornar una lista sin paginar es un problema de escalabilidad que se evita desde el diseño.
- **Soft delete transparente**: Los tópicos con estado `ELIMINADO` son filtrados automáticamente. El usuario nunca los verá.
- **`readOnly = true`**: La transacción se marca como solo lectura, optimizando el rendimiento al desactivar el dirty checking de Hibernate.
- **Parámetros opcionales**: El cliente puede personalizar la paginación con query params.

**Parámetros de paginación opcionales:**
```
GET /topicos?page=0&size=5&sort=titulo,asc
```

**Response `200 OK`:**

<img width="535" height="456" alt="imagen" src="https://github.com/user-attachments/assets/618285b9-1389-41e5-8acb-64e0caaade7e" />

<img width="168" height="50" alt="imagen" src="https://github.com/user-attachments/assets/1220dd3f-d3c4-4dea-9063-dbdb84e13690" />

---

### 🔍 Buscar Tópico por ID

**`GET /topicos/{id}`**

Retorna un tópico específico. Buenas prácticas implementadas:

- **Validación doble**: Verifica que el ID exista en la BD y que el tópico no esté eliminado, lanzando errores descriptivos diferenciados para cada caso.
- **Método privado reutilizable**: La lógica de `obtenerTopicoActivo()` es compartida entre buscar, actualizar y eliminar, aplicando el principio **DRY (Don't Repeat Yourself)**.
- **DTOs como contrato**: La entidad `Topico` nunca se expone directamente. `TopicoResponse` define exactamente qué información sale de la API.

**Response `200 OK`:**

<img width="325" height="43" alt="imagen" src="https://github.com/user-attachments/assets/beb579e6-533b-437a-a05a-f14975154bb0" />

<img width="542" height="313" alt="imagen" src="https://github.com/user-attachments/assets/20a2cd38-0cef-420f-8d5b-92276b79b834" />

---

### ✏️ Actualizar Tópico

**`PUT /topicos/{id}`**

Actualiza parcialmente un tópico existente. Buenas prácticas implementadas:

- **Actualización parcial**: Todos los campos son opcionales en `TopicoUpdateRequest`. Solo se actualizan los campos que el cliente envíe con valor no nulo y no vacío.
- **Rich Domain Model**: La lógica de actualización vive en el método `actualizarDatos()` de la entidad `Topico`, no en el Service. La entidad conoce sus propias reglas (principio **Tell, Don't Ask**).
- **Dirty Checking de Hibernate**: No se llama a `repository.save()`. Hibernate detecta los cambios en la entidad automáticamente dentro de la transacción y ejecuta el `UPDATE` al finalizar.
- **Protección contra eliminados**: No es posible actualizar un tópico con estado `ELIMINADO`.

**Request (todos los campos son opcionales):**

<img width="480" height="197" alt="imagen" src="https://github.com/user-attachments/assets/bba2c920-6f5a-4241-883e-58078433250b" />

**Response `200 OK`:**

<img width="544" height="300" alt="imagen" src="https://github.com/user-attachments/assets/977d35f3-5ab7-47f6-85d1-b3cb6b661b57" />

---

### 🗑️ Eliminar Tópico

**`DELETE /topicos/{id}`**

Elimina un tópico del foro. Implementa **Soft Delete**, una de las mejores prácticas más importantes en sistemas productivos.

- **Soft Delete**: El registro **nunca se borra físicamente** de la base de datos. En su lugar, el estado cambia a `ELIMINADO`. Esto preserva el historial, permite auditorías y hace la operación reversible.
- **Tres capas de seguridad para el estado**: La restricción `CHECK CONSTRAINT` en la BD, la validación en el Service y el método `eliminar()` en la entidad garantizan que `estado` siempre tenga un valor válido.
- **Respuesta `204 No Content`**: Sin cuerpo en la respuesta, siguiendo el estándar REST para operaciones DELETE exitosas.
- **Transparencia**: Los tópicos eliminados desaparecen automáticamente de todos los listados sin necesidad de lógica adicional.

**Response `204 No Content`** (sin cuerpo)

<img width="1020" height="162" alt="imagen" src="https://github.com/user-attachments/assets/d9520a65-0200-40ff-87fa-5dc6cc9f3dd5" />

---

## ⚠️ Manejo de Errores

ForoHub implementa un manejo de errores centralizado mediante `@RestControllerAdvice`. Todas las respuestas de error siguen un **formato consistente**, lo que facilita el manejo en el cliente.

### Formato Estándar de Error

```json
{
    "status": 404,
    "error": "Not Found",
    "mensaje": "Tópico no encontrado con id: 99",
    "path": "/topicos/99",
    "timestamp": "2024-01-15T10:30:00"
}
```

### Catálogo de Errores

| Código | Tipo | Cuándo Ocurre |
|---|---|---|
| `400 Bad Request` | Validación | Campos obligatorios ausentes o con formato inválido |
| `401 Unauthorized` | Autenticación | Request sin token JWT |
| `403 Forbidden` | Autorización | Token JWT inválido o expirado |
| `404 Not Found` | Recurso | ID de tópico inexistente o tópico eliminado |
| `409 Conflict` | Regla de negocio | Tópico duplicado (mismo título y mensaje activos) |

### Error de Validación `400` — Formato Especial

Cuando un request falla validación, se devuelven **todos los errores de campo juntos**:

<img width="1023" height="553" alt="imagen" src="https://github.com/user-attachments/assets/febff651-15ee-4bb7-915c-486a361fc9ca" />

<img width="1023" height="291" alt="imagen" src="https://github.com/user-attachments/assets/c07737aa-8c9f-454d-8bd4-d9d5d7702748" />

<img width="1018" height="239" alt="imagen" src="https://github.com/user-attachments/assets/952931e8-b745-4b6e-9a74-bf63788e83d5" />

---

## 🧪 Pruebas con Insomnia

### Colección de Endpoints

| # | Método | Endpoint | Autenticación |
|---|---|---|---|
| 1 | `POST` | `/auth/login` | ❌ No requerida |
| 2 | `POST` | `/topicos` | ✅ Bearer Token |
| 3 | `GET` | `/topicos` | ✅ Bearer Token |
| 4 | `GET` | `/topicos/{id}` | ✅ Bearer Token |
| 5 | `PUT` | `/topicos/{id}` | ✅ Bearer Token |
| 6 | `DELETE` | `/topicos/{id}` | ✅ Bearer Token |

---

## 📌 Buenas Prácticas Aplicadas

| Práctica | Descripción |
|---|---|
| **SOLID — SRP** | Cada clase tiene una única responsabilidad bien definida |
| **SOLID — DIP** | Las capas dependen de abstracciones, no de implementaciones concretas |
| **DRY** | Lógica reutilizada mediante métodos privados y Factory Methods |
| **Rich Domain Model** | La lógica de negocio vive en la entidad, no en el Service |
| **DTOs** | La entidad nunca se expone directamente en la API |
| **Java Records** | DTOs inmutables y concisos sin boilerplate |
| **Soft Delete** | Borrado lógico para preservar historial e integridad |
| **Paginación** | Todos los listados son paginados para evitar problemas de escalabilidad |
| **Variables de entorno** | Credenciales y secretos nunca hardcodeados en el código |
| **Validación en capas** | Bean Validation → Service → Base de datos |
| **Conventional Commits** | Historial de Git limpio y semántico |
