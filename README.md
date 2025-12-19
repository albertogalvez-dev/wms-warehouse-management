# WMS - Warehouse Management System

Sistema de gestión de almacenes con backend Spring Boot y base de datos PostgreSQL.

## 📋 Requisitos

- **Java 17** o superior
- **Maven 3.8+** (o usar `C:\tools\apache-maven-3.9.6` si fue instalado automáticamente)
- **Docker** y **Docker Compose**

> ⚠️ **Nota**: Si tienes PostgreSQL local instalado en el puerto 5432, este proyecto usa el puerto **5433** para evitar conflictos.

## 🔧 Variables de Entorno

El backend soporta configuración mediante variables de entorno para facilitar el deploy:

| Variable | Descripción | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Puerto del servidor | `8080` |
| `DB_HOST` | Host de PostgreSQL | `127.0.0.1` |
| `DB_PORT` | Puerto de PostgreSQL | `5433` |
| `DB_NAME` | Nombre de la base de datos | `wms` |
| `DB_USER` | Usuario de PostgreSQL | `wms` |
| `DB_PASS` | Contraseña de PostgreSQL | `wms` |
| `SHOW_SQL` | Mostrar SQL en logs | `false` |
| `PRINTER_ENABLED` | Habilitar impresiÇün Zebra (TCP) | `false` |
| `PRINTER_HOST` | IP/host de la impresora | *(vacÇðo)* |
| `PRINTER_PORT` | Puerto de impresiÇün | `9100` |
| `PRINTER_FAIL_STRICT` | Si falla impresiÇün: abortar (500) | `false` |
| `PRINTER_TIMEOUT` | Timeout socket impresora (ms) | `5000` |

## 🚀 Inicio Rápido

### 1. Iniciar la base de datos PostgreSQL

```bash
cd docker
docker compose up -d
```

Esto levantará un contenedor PostgreSQL 16 con:
- **Host:** localhost
- **Puerto:** 5433 (⚠️ importante: no 5432)
- **Base de datos:** wms
- **Usuario:** wms
- **Contraseña:** wms

### 2. Iniciar el backend

```bash
cd backend
# Con Maven global
mvn spring-boot:run

# O con Maven portable (Windows)
C:\tools\apache-maven-3.9.6\bin\mvn.cmd spring-boot:run
```

El servidor arrancará en `http://localhost:8080`

## 🔗 Endpoints Disponibles

### Sistema
| Endpoint | Descripción |
|----------|-------------|
| `GET /api/ping` | Health check - devuelve `{"status": "ok"}` |
| `GET /actuator/health` | Estado del aplicativo (Actuator) |
| `GET /swagger-ui/index.html` | Documentación API (Swagger UI) |

### Products
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/products` | Crear producto |
| `GET` | `/api/products/{id}` | Obtener por ID |
| `GET` | `/api/products?query=&page=&size=` | Buscar paginado |
| `PUT` | `/api/products/{id}` | Actualizar |
| `DELETE` | `/api/products/{id}` | Soft delete |

### Locations
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/locations` | Crear ubicación |
| `GET` | `/api/locations/{id}` | Obtener por ID |
| `GET` | `/api/locations?query=&page=&size=` | Buscar paginado |
| `PUT` | `/api/locations/{id}` | Actualizar |
| `DELETE` | `/api/locations/{id}` | Soft delete |

### Stock
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/stock/adjust` | Ajustar stock (+/-) |
| `GET` | `/api/stock?productId=&locationId=&page=&size=` | Listar con filtros |

### Orders (M3)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/orders` | Crear pedido con líneas + shipping + carrier |
| `GET` | `/api/orders/{id}` | Obtener pedido con líneas |
| `GET` | `/api/orders?status=&page=&size=` | Buscar paginado por status |
| `POST` | `/api/orders/{id}/release` | Liberar pedido a picking (asigna stock) |
| `PUT` | `/api/orders/{id}/shipping` | Actualizar dirección y carrier (solo DRAFT/RELEASED) |

### Pick Tasks (M3)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/pick-tasks/{id}` | Obtener tarea con líneas de picking |
| `GET` | `/api/pick-tasks?status=&page=&size=` | Buscar paginado por status |
| `GET` | `/api/pick-tasks/{id}/handheld` | Resumen para terminal móvil |

### Meta (M3.1)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/meta/carriers` | Lista de transportistas disponibles |

### Pick Waves (M3.2)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/pick-waves` | Crear wave con múltiples pedidos |
| `GET` | `/api/pick-waves/{id}` | Obtener wave con pedidos y totes |
| `GET` | `/api/pick-waves?status=&page=&size=` | Buscar paginado |
| `POST` | `/api/pick-waves/{id}/start` | Iniciar wave (PLANNED→IN_PROGRESS) |
| `POST` | `/api/pick-waves/{id}/complete` | Completar wave (si todos PICKED) |
| `GET` | `/api/pick-waves/{id}/pick-list` | Lista de picking agrupada por ubicación |

### Picking Sessions (M4.0)
| MÇ¸todo | Endpoint | DescripciÇün |
|--------|----------|-------------|
| `POST` | `/api/picking/sessions/start` | Iniciar sesiÇün de picking para una wave |
| `POST` | `/api/picking/sessions/{sessionId}/scan` | Escanear (location/product/tote) y aplicar picking |
| `GET` | `/api/picking/sessions/{sessionId}` | Consultar sesiÇün (reanudar handheld) |
| `POST` | `/api/picking/sessions/{sessionId}/complete` | Completar sesiÇün (solo si no quedan picklines OPEN) |

### Totes (M3.2)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/totes/{barcode}` | Obtener tote por barcode |
| `POST` | `/api/totes/{barcode}/assign-station` | Asignar tote a estación de packing |
| `POST` | `/api/totes/{barcode}/close` | Cerrar tote (solo si pedido PICKED) |

### Packing Stations (M3.2)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/packing-stations` | Listar estaciones activas |
| `POST` | `/api/packing-stations` | Crear estación |
| `PUT` | `/api/packing-stations/{id}` | Actualizar estación |

## 📝 Ejemplos con cURL

### Crear un producto
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"sku":"NUEVO-001","name":"Producto Nuevo","barcode":"1234567890123"}'
```

### Listar productos
```bash
curl "http://localhost:8080/api/products?query=widget&page=0&size=10"
```

### Crear una ubicación
```bash
curl -X POST http://localhost:8080/api/locations \
  -H "Content-Type: application/json" \
  -d '{"code":"D-01-01","zone":"D","description":"Nueva ubicación"}'
```

### Ajustar stock (añadir 50 unidades)
```bash
curl -X POST http://localhost:8080/api/stock/adjust \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"locationId":1,"delta":50,"reason":"Recepción de mercancía"}'
```

### Ajustar stock (restar 10 unidades)
```bash
curl -X POST http://localhost:8080/api/stock/adjust \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"locationId":1,"delta":-10,"reason":"Venta"}'
```

### Ver stock
```bash
curl "http://localhost:8080/api/stock?page=0&size=20"
```

### Crear un pedido con shipping (M3.1)
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "externalRef":"ERP-NEW-001",
    "carrier":"DHL",
    "shipping":{
      "name":"Cliente Ejemplo",
      "phone":"+34600000000",
      "email":"cliente@example.com",
      "address1":"Calle Principal 123",
      "address2":"Piso 2",
      "postalCode":"28001",
      "city":"Madrid",
      "province":"Madrid",
      "country":"ES"
    },
    "lines":[
      {"productId":1,"requestedQty":10},
      {"productId":2,"requestedQty":5}
    ]
  }'
```

### Actualizar shipping de pedido (M3.1)
```bash
curl -X PUT http://localhost:8080/api/orders/1/shipping \
  -H "Content-Type: application/json" \
  -d '{
    "carrier":"GLS",
    "shipping":{
      "name":"Nuevo Destinatario",
      "address1":"Avenida Nueva 456",
      "postalCode":"18001",
      "city":"Granada",
      "country":"ES"
    }
  }'
```

### Ver transportistas disponibles (M3.1)
```bash
curl http://localhost:8080/api/meta/carriers
```

### Liberar pedido a picking (M3)
```bash
curl -X POST http://localhost:8080/api/orders/1/release
```

### Ver tarea de picking (M3)
```bash
curl http://localhost:8080/api/pick-tasks/1
```

### Resumen handheld (M3)
```bash
curl http://localhost:8080/api/pick-tasks/1/handheld
```

### Crear PickWave con varios pedidos (M3.2)
```bash
curl -X POST http://localhost:8080/api/pick-waves \
  -H "Content-Type: application/json" \
  -d '{"orderIds":[1,2]}'
```

### Iniciar wave (M3.2)
```bash
curl -X POST http://localhost:8080/api/pick-waves/1/start
```

### Ver pick-list de la wave (M3.2)
```bash
curl http://localhost:8080/api/pick-waves/1/pick-list
```

### Picking handheld (M4.0)
1) Start session
```bash
curl -X POST http://localhost:8080/api/picking/sessions/start \
  -H "Content-Type: application/json" \
  -d '{"waveId":1}'
```

2) Scan location
```bash
curl -X POST http://localhost:8080/api/picking/sessions/{sessionId}/scan \
  -H "Content-Type: application/json" \
  -d '{"code":"A-01-01","qty":1}'
```

3) Scan product (barcode o SKU) y ver candidates
```bash
curl -X POST http://localhost:8080/api/picking/sessions/{sessionId}/scan \
  -H "Content-Type: application/json" \
  -d '{"code":"7501234567890","qty":1}'
```

4) Scan tote (repite hasta completar)
```bash
curl -X POST http://localhost:8080/api/picking/sessions/{sessionId}/scan \
  -H "Content-Type: application/json" \
  -d '{"code":"TOTE-20251218-0001-01","qty":1}'
```

5) Complete session (marca wave DONE si todo está completo)
```bash
curl -X POST http://localhost:8080/api/picking/sessions/{sessionId}/complete
```

### Asignar tote a estación de packing (M3.2)
```bash
curl -X POST http://localhost:8080/api/totes/TOTE-20251218-0001-01/assign-station \
  -H "Content-Type: application/json" \
  -d '{"stationId":1}'
```

### Ver estaciones de packing (M3.2)
```bash
curl http://localhost:8080/api/packing-stations
```

### Packing handheld (M4)
Requisitos:
- La tote debe estar en estado `AT_PACKING` y asignada a la estaci╟№n (`/api/totes/{barcode}/assign-station`).
- El pedido asociado debe estar en estado `PICKED` (o el PickTask en `DONE`).

### Start packing session (scan tote)
```bash
curl -X POST http://localhost:8080/api/packing/sessions/start \
  -H "Content-Type: application/json" \
  -d '{"toteBarcode":"TOTE-20251218-0001-01","stationId":1,"operator":"packer-1"}'
```

### Scan productos (barcode o SKU)
Ejemplos (de datos seed): `7501234567890` o `PROD-001`
```bash
curl -X POST http://localhost:8080/api/packing/sessions/{sessionId}/scan \
  -H "Content-Type: application/json" \
  -d '{"code":"7501234567890","qty":1}'
```
Repite hasta que la respuesta indique `mode=SET_PACKAGES`.

### Definir bultos (packages) + generar etiquetas
```bash
curl -X POST http://localhost:8080/api/packing/sessions/{sessionId}/set-packages \
  -H "Content-Type: application/json" \
  -d '{"packageCount":2}'
```

### Completar packing
```bash
curl -X POST http://localhost:8080/api/packing/sessions/{sessionId}/complete
```

### Consultar shipment + descargar ZPL
```bash
curl http://localhost:8080/api/shipments/{shipmentId}
curl http://localhost:8080/api/shipments/{shipmentId}/packages/{packageId}/label.zpl
```

### (Opcional) Imprimir en Zebra (TCP 9100)
Configura `PRINTER_ENABLED=true` y `PRINTER_HOST=<ip>` y luego:
```bash
curl -X POST http://localhost:8080/api/shipments/{shipmentId}/print
```

## 📁 Estructura del Proyecto

```
wms/
├── backend/
│   ├── src/main/
│   │   ├── java/com/wms/
│   │   │   ├── config/          # Configuraciones (CORS)
│   │   │   ├── controller/      # REST Controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── entity/          # JPA Entities
│   │   │   ├── exception/       # Custom Exceptions
│   │   │   ├── repository/      # JPA Repositories
│   │   │   └── service/         # Business Logic
│   │   └── resources/
│   │       ├── db/migration/    # Flyway migrations
│   │       └── application.yml
│   └── pom.xml
├── frontend/                    # Frontend (próximamente)
├── docker/
│   └── docker-compose.yml
└── README.md
```

## 🛠️ Tecnologías

- **Backend:** Spring Boot 3.2, Java 17
- **Base de datos:** PostgreSQL 16
- **Migraciones:** Flyway
- **Documentación API:** SpringDoc OpenAPI (Swagger)
- **Validación:** Bean Validation (jakarta.validation)
- **Contenedores:** Docker Compose

## 📝 Comandos Útiles

```bash
# Ver logs de PostgreSQL
docker logs wms-postgres

# Parar PostgreSQL
cd docker && docker compose down

# Parar y eliminar volumen (¡borra datos!)
cd docker && docker compose down -v

# Compilar backend sin tests
cd backend && mvn clean package -DskipTests

# Ejecutar con variables de entorno personalizadas
DB_HOST=mi-servidor DB_PORT=5432 mvn spring-boot:run
```

## 🚀 Deploy en Oracle VPS

Para desplegar en un VPS Oracle (Always Free):

1. Instala Docker y Docker Compose en el VPS
2. Clona el repositorio
3. Configura las variables de entorno en el servidor:
   ```bash
   export DB_HOST=localhost
   export DB_PORT=5432
   export DB_NAME=wms
   export DB_USER=wms_prod
   export DB_PASS=contraseña_segura
   ```
4. Ejecuta `docker compose up -d` para PostgreSQL
5. Ejecuta el backend con `java -jar target/wms-backend-1.0.0-SNAPSHOT.jar`
