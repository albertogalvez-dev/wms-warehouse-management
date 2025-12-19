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

## 🚀 Deploy to Oracle VPS (ARM64/aarch64)

This project is optimized for Oracle Cloud Free Tier with ARM A1.Flex instances.

### Prerequisites on VPS

```bash
# SSH to your VPS
ssh opc@YOUR_VPS_IP

# Install Docker (Oracle Linux 9)
sudo dnf config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
sudo dnf install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
sudo systemctl enable --now docker
sudo usermod -aG docker opc
# Log out and log back in for group changes
```

### Deployment Steps

```bash
# 1. Clone repository
git clone https://github.com/albertogalvez/wms-warehouse-management.git
cd wms-warehouse-management

# 2. Create and configure .env
cp .env.example .env
nano .env
```

### Required .env Configuration for Production

```bash
# === REQUIRED: Change these values ===
DB_PASS=your_secure_database_password_here
JWT_SECRET=your_32_char_secure_random_string_here

# === IMPORTANT: Set your domain or IP ===
# With domain (auto HTTPS):
DOMAIN=wms.yourdomain.com

# Without domain (HTTP only, use IP):
DOMAIN=:80

# === Production profile ===
SPRING_PROFILES_ACTIVE=prod

# === Optional: CORS for external access ===
CORS_ALLOWED_ORIGINS=*
```

### Start Production Services

```bash
# Build and start with production compose file
docker compose -f docker-compose.prod.yml up -d --build

# Check logs
docker compose -f docker-compose.prod.yml logs -f backend

# Verify health
curl http://localhost/actuator/health
curl http://localhost/api/ping
```

### Firewall (Oracle Cloud)

Open these ports in your VCN Security List:
- **80** (HTTP)
- **443** (HTTPS, if using domain)

```bash
# Also in iptables (Oracle Linux)
sudo firewall-cmd --permanent --add-port=80/tcp
sudo firewall-cmd --permanent --add-port=443/tcp
sudo firewall-cmd --reload
```

### Update Deployment

```bash
cd wms-warehouse-management
git pull origin master
docker compose -f docker-compose.prod.yml up -d --build
```

### Files Modified for ARM Compatibility

| File | Change |
|------|--------|
| `backend/Dockerfile` | Uses `eclipse-temurin:17-jre-jammy` (Ubuntu) instead of Alpine |
| `docker-compose.prod.yml` | Includes `SPRING_DATASOURCE_*` environment variables |
| `.env.example` | Full documentation of all required variables |

## 📝 License

MIT License - free to use for educational and commercial purposes.

---

**Built with ❤️ as a portfolio project.**
