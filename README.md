# NexERP Backend

Backend de un ERP para pequenas y medianas empresas construido con Java 21 y Spring Boot 3. El proyecto implementa autenticacion JWT, control de roles y permisos, clientes, proveedores, productos, almacenes, inventario, compras, ventas, dashboard y reportes.

## Funcionalidades

- Login, refresh token, logout y cambio de contrasena.
- Recuperacion de contrasena mediante token seguro.
- Usuarios, roles y permisos.
- Clientes y proveedores.
- Categorias, productos, precios e imagenes.
- Inventario por almacen, kardex y movimientos.
- Entradas, salidas, ajustes y transferencias.
- Ordenes de compra y recepcion.
- Carrito, checkout y ventas.
- Dashboard de KPIs y reportes de ventas e inventario.

## Stack Tecnico

- Java 21
- Spring Boot 3.4
- Spring Web, Spring Data JPA, Spring Security y Bean Validation
- PostgreSQL 16, Redis 7 y Flyway
- JWT, BCrypt y refresh tokens
- JPA/Hibernate y MapStruct
- OpenAPI/Swagger con Springdoc
- JUnit 5, Mockito y JaCoCo
- Maven, Docker y Docker Compose

## Arquitectura

El backend sigue un monolito modular por dominio funcional. Cada modulo contiene sus controladores, casos de uso, DTOs, mappers, persistencia y reglas principales.

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

La arquitectura prioriza separacion por dominio, casos de uso explicitos, validaciones de negocio y dependencias simples entre modulos.

## Seguridad

- API stateless protegida con Spring Security.
- JWT con secreto obligatorio de al menos 32 bytes.
- Access token de vida corta.
- Refresh tokens hasheados en base de datos.
- Revocacion de refresh token en logout.
- Revocacion basica de access token por `jti`.
- BCrypt con fuerza 12.
- Autorizacion por permisos con `@PreAuthorize`.
- CORS configurable por entorno.
- Headers HTTP de seguridad.

## Base De Datos Y Migraciones

Flyway gestiona el schema `erp` y Hibernate esta configurado con `ddl-auto=validate`.

Migraciones principales:

- `V1__initial_schema.sql`: crea tablas, claves primarias, foraneas, uniques y auditoria.
- `V2__seed_roles_permissions.sql`: inserta permisos y roles base idempotentes.
- `V3__indexes.sql`: agrega indices para busquedas, reportes, tokens, FKs e inventario.

## Requisitos

- Java 21
- Maven 3.9+
- PostgreSQL 16+
- Redis 7+
- Docker y Docker Compose opcional

## Configuracion

Copiar el archivo de variables de entorno:

```bash
cp .env.example .env
```

Variables principales:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `JWT_SECRET`
- `SPRING_CACHE_TYPE`
- `REDIS_HOST`
- `APP_CORS_ALLOWED_ORIGINS`
- `APP_SWAGGER_ENABLED`

No subir secretos reales al repositorio.

## Ejecucion Local

```bash
mvn spring-boot:run
```

API local:

```text
http://localhost:8081/api/v1
```

## Docker Compose

El `docker-compose.yml` levanta backend, PostgreSQL, Redis y frontend:

```bash
docker compose --env-file .env up --build
```

Servicios principales:

- Backend: `http://localhost:8081`
- Frontend: `http://localhost:4300`
- PostgreSQL: puerto configurado por `POSTGRES_PORT`
- Redis: puerto configurado por `REDIS_PORT`

## Pruebas

Ejecutar pruebas:

```bash
mvn test
```

Ejecutar verificacion con JaCoCo:

```bash
mvn verify
```

Pruebas destacadas:

- Validacion de usuarios duplicados y hash de contrasena.
- Cambio de precio vigente en productos.
- Control de stock insuficiente y movimientos de inventario.
- Checkout de ventas, descuento de inventario y limpieza de carrito.

## Documentacion API

Swagger puede habilitarse en entorno local:

```text
APP_SWAGGER_ENABLED=true
```

URL:

```text
http://localhost:8081/swagger-ui.html
```

Modulos principales:

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

## Repositorio Relacionado

Frontend Angular:

```text
https://github.com/YeremiTech/nexerp-frontend
```

## Licencia

MIT.
