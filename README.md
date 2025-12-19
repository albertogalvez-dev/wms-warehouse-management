# WMS - Warehouse Management System

A complete Warehouse Management System built with **Java 17 + Spring Boot 3 + PostgreSQL**.  
Designed as a production-ready portfolio project demonstrating real warehouse operations.

```
┌─────────────────────────────────────────────────────────────────────┐
│                        WMS ARCHITECTURE                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐     │
│   │ FRONTEND │    │  NGINX   │    │ BACKEND  │    │POSTGRESQL│     │
│   │ Backoffice│───▶│  Proxy   │───▶│Spring Boot│───▶│   DB     │     │
│   │ Handheld │    │  :80     │    │  :8080   │    │  :5432   │     │
│   └──────────┘    └──────────┘    └──────────┘    └──────────┘     │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

## 📦 Order Flow

```
  ┌─────────┐     ┌──────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐
  │  DRAFT  │────▶│ RELEASED │────▶│ PICKING │────▶│ PACKING │────▶│ SHIPPED │
  └─────────┘     └──────────┘     └─────────┘     └─────────┘     └─────────┘
       │               │                │               │               │
       ▼               ▼                ▼               ▼               ▼
   Create order   Allocate stock   Wave picking    Scan & pack    Print labels
   Add lines      → RELEASED       Zebra device    Validate qty   Ship to carrier
```

## ⚡ Quick Start

### Prerequisites
- **Java 17** or higher
- **Maven 3.8+**
- **Docker** and **Docker Compose**

### Local Development (Docker Compose)

```bash
# 1. Clone the repository
git clone https://github.com/albertogalvez/wms-warehouse-management.git
cd wms-warehouse-management

# 2. Copy environment file
cp .env.example .env

# 3. Start all services
docker compose up -d

# 4. Wait for startup (~60s), then open:
```

| Service | URL |
|---------|-----|
| 🏠 Backoffice | http://localhost/ |
| 📱 Handheld | http://localhost/handheld/ |
| 📖 Swagger API | http://localhost/swagger-ui/index.html |
| 💚 Health Check | http://localhost/actuator/health |

### Manual Backend (without Docker)

```bash
# Start PostgreSQL
cd docker && docker compose up -d

# Run backend
cd backend
mvn spring-boot:run
```

## 🔐 Authentication

JWT-based authentication with role-based access control.

### Default Admin
- **Username:** `admin`
- **Password:** `admin123`

### Login Example
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "role": "ADMIN",
  "expiresIn": 86400000
}
```

### Roles

| Role | Permissions |
|------|-------------|
| `ADMIN` | Full access to all operations |
| `MANAGER` | Manage products, locations, orders, waves |
| `PICKER` | Picking operations only |
| `PACKER` | Packing operations only |

## 🔧 Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active profile (dev/prod) | `dev` |
| `DB_HOST` | PostgreSQL host | `127.0.0.1` |
| `DB_PORT` | PostgreSQL port | `5433` |
| `DB_NAME` | Database name | `wms` |
| `DB_USER` | Database user | `wms` |
| `DB_PASS` | Database password | `wms` |
| `JWT_SECRET` | JWT signing key (256-bit) | dev default |
| `JWT_EXPIRATION_MS` | Token expiration (ms) | `86400000` |
| `PRINTER_ENABLED` | Enable Zebra printing | `false` |
| `PRINTER_HOST` | Zebra printer IP | - |

## 📡 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/login` | Login, returns JWT |
| POST | `/auth/register` | Register user (Admin) |
| GET | `/auth/me` | Current user info |

### Products
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products` | List products (paginated) |
| GET | `/api/products/{id}` | Get product by ID |
| POST | `/api/products` | Create product |
| PUT | `/api/products/{id}` | Update product |

### Orders
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/orders` | List orders |
| POST | `/api/orders` | Create order |
| POST | `/api/orders/{id}/release` | Release to picking |
| GET | `/api/orders/{id}` | Get order details |

### Waves
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/waves` | List waves |
| POST | `/api/waves` | Create wave |
| GET | `/api/waves/{id}/picklist` | Get pick list |

### Picking (Handheld)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/picking/tasks/{waveId}` | Get pick tasks |
| POST | `/api/picking/complete` | Complete pick |

### Packing
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/packing/start` | Start packing session |
| POST | `/api/packing/scan` | Scan product |
| POST | `/api/packing/set-packages` | Set package count |
| POST | `/api/packing/complete` | Complete packing |

### Shipments
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/shipments/{id}` | Get shipment |
| GET | `/api/shipments/{id}/labels` | Get ZPL labels |

## 🏗️ Project Structure

```
wms/
├── backend/                 # Spring Boot application
│   ├── src/main/java/com/wms/
│   │   ├── controller/      # REST controllers
│   │   ├── service/         # Business logic
│   │   ├── repository/      # JPA repositories
│   │   ├── entity/          # JPA entities
│   │   ├── dto/             # Data transfer objects
│   │   ├── config/          # Configuration
│   │   ├── security/        # JWT authentication
│   │   └── exception/       # Error handling
│   └── src/main/resources/
│       └── db/migration/    # Flyway migrations
├── frontend/
│   ├── backoffice/          # Admin web app
│   ├── handheld/            # Mobile packing app
│   ├── landing/             # Portfolio landing page
│   └── shared/              # Shared theme & utilities
├── nginx/                   # Nginx configuration
├── docker/                  # PostgreSQL setup
├── docker-compose.yml       # Full stack deployment
└── .github/workflows/       # CI/CD pipelines
```

## 🧪 Testing

```bash
# Run all tests
cd backend
mvn test

# Run with coverage
mvn test jacoco:report
```

## 🚀 Deployment (Oracle VPS)

```bash
# 1. SSH to your VPS
ssh opc@your-vps-ip

# 2. Install Docker
sudo dnf install -y docker docker-compose
sudo systemctl enable --now docker

# 3. Clone and deploy
git clone https://github.com/albertogalvez/wms-warehouse-management.git
cd wms-warehouse-management
cp .env.example .env

# 4. Edit .env with production values
nano .env  # Set SPRING_PROFILES_ACTIVE=prod, strong JWT_SECRET

# 5. Start
docker compose up -d
```

## 📝 License

MIT License - free to use for educational and commercial purposes.

---

**Built with ❤️ as a portfolio project.**
