# NexERP Backend

Backend de un ERP para pequenas y medianas empresas construido con Java 21 y Spring Boot 3. El proyecto implementa autenticacion JWT, control de roles y permisos, clientes, proveedores, catalogo de productos, categorias, almacenes, inventario, compras, ventas, dashboard y reportes.

> Proyecto de portfolio orientado a demostrar desarrollo backend profesional con monolito modular, seguridad stateless, persistencia con migraciones, cache Redis, Docker y reglas de negocio para operaciones ERP.

## Caracteristicas Tecnicas

El backend incluye componentes de infraestructura y reglas de negocio para operar un ERP modular:

- Monolito modular por dominio funcional.
- Seguridad con Spring Security, JWT, refresh tokens y BCrypt.
- Persistencia con PostgreSQL, JPA/Hibernate y migraciones Flyway.
- Cache controlado con Redis para dashboard y catalogos.
- Docker multi-stage y docker-compose para entorno local reproducible.
- Pruebas unitarias enfocadas en reglas criticas de negocio.
- Control de concurrencia para inventario mediante locking y versionado.
- Documentacion OpenAPI/Swagger habilitable por entorno.

## Stack

- Java 21
- Spring Boot 3.4
- Spring Security
- JWT
- Maven
- PostgreSQL 16
- Redis 7
- Flyway
- JPA/Hibernate
- MapStruct
- JUnit 5
- Mockito
- Docker

## Arquitectura

El backend sigue un monolito modular por dominio. Cada modulo contiene sus casos de uso, DTOs, mappers, controladores y persistencia.

```text
src/main/java/com/empresa/erp
├── auth
├── categorias
├── clientes
├── compras
├── dashboard
├── inventario
├── productos
├── proveedores
├── reportes
├── roles
├── usuarios
├── ventas
├── infrastructure
└── shared
```

La arquitectura prioriza separacion por dominio, casos de uso explicitos y dependencias simples entre modulos.

## Funcionalidades

- Login, refresh token, logout y cambio de contrasena.
- Recuperacion de contrasena mediante token seguro.
- Usuarios, roles y permisos.
- Clientes y proveedores.
- Productos, precios, imagenes y categorias.
- Inventario por almacen.
- Entradas, salidas, ajustes y transferencias.
- Carrito y checkout de ventas.
- Ordenes de compra y recepcion.
- Dashboard de KPIs.
- Reportes de ventas e inventario.

## Seguridad

- JWT con secreto obligatorio de al menos 32 bytes.
- Access token de vida corta.
- Refresh tokens hasheados en base de datos.
- Revocacion de refresh token en logout.
- Revocacion basica de access token por `jti`.
- BCrypt con fuerza 12.
- Autorizacion por permisos con `@PreAuthorize`.
- CORS configurable.
- Headers de seguridad HTTP.

## Base De Datos Y Migraciones

Flyway gestiona el schema `erp`.

Migraciones principales:

- `V1__initial_schema.sql`: crea tablas, claves primarias, foraneas, uniques y auditoria.
- `V2__seed_roles_permissions.sql`: inserta permisos y roles base idempotentes.
- `V3__indexes.sql`: agrega indices para busquedas, reportes, tokens, FKs e inventario.

Hibernate esta configurado con `ddl-auto=validate`, por lo que el esquema productivo depende de migraciones versionadas.

## Docker

Copiar variables:

```bash
cp .env.example .env
```

Editar `JWT_SECRET` con un valor real de al menos 32 bytes.

Levantar servicios:

```bash
docker compose up --build
```

Servicios incluidos:

- `backend`: API Spring Boot en `http://localhost:8081`
- `postgres`: base PostgreSQL con volumen persistente.
- `redis`: cache para dashboard/catalogos y revocacion de access tokens.
- `frontend`: aplicacion Angular cuando se ejecuta desde `docker-compose`.

## Ejecucion Local Sin Docker

Requisitos:

- Java 21
- Maven 3.9+
- PostgreSQL 16
- Redis opcional

Variables minimas:

```bash
JWT_SECRET=replace-with-a-random-secret-of-at-least-32-bytes
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/erp_pymes
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=replace-with-local-password
SPRING_CACHE_TYPE=simple
```

Ejecutar:

```bash
mvn spring-boot:run
```

## Testing

Ejecutar pruebas:

```bash
mvn test
```

Compilar sin tests:

```bash
mvn -DskipTests compile
```

Pruebas destacadas:

- Usuarios: validacion de duplicados y hash de contrasena.
- Productos: cambio de precio vigente e imagenes.
- Inventario: stock insuficiente y registro de movimientos.
- Ventas: checkout, descuento de inventario y limpieza de carrito.

## Endpoints Principales

Base URL:

```text
http://localhost:8081/api/v1
```

Modulos:

- `/auth`
- `/users`
- `/roles`
- `/permissions`
- `/clients`
- `/suppliers`
- `/categories`
- `/products`
- `/warehouses`
- `/inventory`
- `/purchase-orders`
- `/sales`
- `/dashboard`
- `/reports`

Swagger puede habilitarse con:

```text
APP_SWAGGER_ENABLED=true
```

URL Swagger local:

```text
http://localhost:8081/swagger-ui.html
```

## Integracion Frontend

Este backend esta pensado para trabajar con:

```text
https://github.com/YeremiTech/nexerp-frontend
```

En Docker Compose, el frontend se expone normalmente en:

```text
http://localhost:4300
```

## Variables De Entorno

| Variable | Descripcion |
| --- | --- |
| `SERVER_PORT` | Puerto HTTP del backend |
| `SPRING_DATASOURCE_URL` | URL JDBC PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | Usuario PostgreSQL |
| `SPRING_DATASOURCE_PASSWORD` | Password PostgreSQL |
| `SPRING_CACHE_TYPE` | `simple` o `redis` |
| `REDIS_HOST` | Host Redis |
| `REDIS_PORT` | Puerto Redis |
| `JWT_SECRET` | Secreto JWT minimo 32 bytes |
| `JWT_ACCESS_EXPIRATION` | Duracion access token en ms |
| `JWT_REFRESH_EXPIRATION` | Duracion refresh token en ms |
| `APP_CORS_ALLOWED_ORIGINS` | Origenes permitidos |
| `APP_SWAGGER_ENABLED` | Habilita Swagger |

## Capacidades De Infraestructura

- Migraciones reales en lugar de `ddl-auto=update`.
- Entorno reproducible con Docker.
- Seguridad JWT con revocacion basica.
- Redis aplicado donde aporta valor.
- Pruebas sobre reglas criticas.
- Control de concurrencia en inventario.
- Reportes optimizados con consultas agregadas.

## Mejoras Futuras

- Agregar Testcontainers para integracion PostgreSQL aislada.
- Incorporar Spring Boot Actuator.
- Agregar OWASP Dependency Check o CodeQL.
- Separar pruebas unitarias e integracion con Maven Failsafe.
- Ampliar cobertura de compras, dashboard y reportes.
- Agregar auditoria de usuario en movimientos de inventario.
- Agregar capturas o diagrama simple del flujo ventas/inventario.

## Licencia

MIT.
