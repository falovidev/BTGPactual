# BTG Pactual - API de Fondos de Inversión

API REST para la gestión de fondos de inversión que permite a los clientes suscribirse, cancelar suscripciones y consultar su historial de transacciones.

## Stack Tecnológico

| Componente | Tecnología |
|-----------|------------|
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.5.0 |
| Build | Gradle 8.11 |
| Base de datos | Amazon DynamoDB |
| Autenticación | JWT (JSON Web Tokens) |
| Notificaciones | AWS SNS (Email/SMS) |
| Documentación API | Swagger / OpenAPI 3 |
| Contenedores | Docker |
| Infraestructura | AWS CloudFormation (ECS Fargate) |

## Arquitectura

El proyecto implementa **Arquitectura Hexagonal (Ports & Adapters)**:

```
src/main/java/com/btg/fondos/
├── domain/                  # Capa de dominio (sin dependencias externas)
│   ├── model/               # Entidades de negocio
│   ├── exception/           # Excepciones de dominio
│   └── port/                # Puertos (interfaces)
│       ├── in/              # Puertos de entrada (use cases)
│       └── out/             # Puertos de salida (repositorios, notificaciones)
├── application/             # Capa de aplicación
│   └── service/             # Servicios que implementan la lógica de negocio
└── infrastructure/          # Capa de infraestructura
    ├── adapter/
    │   ├── in/web/          # Adaptadores de entrada (Controllers REST)
    │   └── out/
    │       ├── persistence/ # Adaptadores de persistencia (DynamoDB)
    │       └── notification/# Adaptadores de notificación (SNS/Mock)
    ├── config/              # Configuraciones (DynamoDB, Security, OpenAPI)
    └── security/            # JWT (filtro, proveedor)
```

## Modelo de Datos (DynamoDB)

| Tabla | Partition Key | Sort Key | GSI |
|-------|--------------|----------|-----|
| Clients | clientId | - | email-index |
| Funds | fundId | - | - |
| Transactions | transactionId | - | clientId-index |
| Subscriptions | clientId | fundId | - |

## Fondos Disponibles

| ID | Nombre | Monto Mínimo | Categoría |
|----|--------|-------------|-----------|
| 1 | FPV_BTG_PACTUAL_RECAUDADORA | COP $75.000 | FPV |
| 2 | FPV_BTG_PACTUAL_ECOPETROL | COP $125.000 | FPV |
| 3 | DEUDAPRIVADA | COP $50.000 | FIC |
| 4 | FDO-ACCIONES | COP $250.000 | FIC |
| 5 | FPV_BTG_PACTUAL_DINAMICA | COP $100.000 | FPV |

## API Endpoints

### Autenticación (públicos)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/register` | Registrar nuevo cliente |
| POST | `/api/auth/login` | Iniciar sesión (retorna JWT) |

### Fondos (requieren autenticación)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/funds` | Listar fondos disponibles (público) |
| POST | `/api/funds/{fundId}/subscribe` | Suscribirse a un fondo |
| DELETE | `/api/funds/{fundId}/cancel` | Cancelar suscripción |
| GET | `/api/funds/subscriptions` | Ver suscripciones activas |

### Transacciones
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/transactions` | Ver historial de transacciones |

### Cliente
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/clients/me` | Ver perfil y saldo actual |

## Reglas de Negocio

- Saldo inicial del cliente: **COP $500.000**
- Cada fondo tiene un monto mínimo de vinculación
- Si el saldo es insuficiente: *"No tiene saldo disponible para vincularse al fondo {nombre}"*
- Al cancelar, el monto se retorna al saldo del cliente
- No se permiten suscripciones duplicadas al mismo fondo
- Se envía notificación (email o SMS) al suscribirse exitosamente

## Seguridad

- **Autenticación**: JWT con expiración de 24 horas
- **Autorización**: Roles USER/ADMIN con `@PreAuthorize`
- **Encriptación**: BCrypt para contraseñas
- **CORS**: Configurado para permitir orígenes específicos
- **Stateless**: Sin sesiones del lado del servidor

## Ejecutar Localmente

### Opción 1: Con Docker Compose (recomendado)

```bash
docker-compose up --build
```

### Opción 2: Sin Docker

1. Iniciar DynamoDB Local:
```bash
docker run -p 8000:8000 amazon/dynamodb-local
```

2. Ejecutar la aplicación:
```bash
./gradlew bootRun
```

### Acceder a la API

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

## Ejemplo de Uso

```bash
# 1. Registrar un cliente
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Pérez",
    "email": "juan@example.com",
    "phone": "+573001234567",
    "password": "password123",
    "notificationPreference": "EMAIL"
  }'

# 2. Iniciar sesión
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "juan@example.com", "password": "password123"}'

# 3. Suscribirse a un fondo (usar el token del login)
curl -X POST http://localhost:8080/api/funds/1/subscribe \
  -H "Authorization: Bearer <TOKEN>"

# 4. Ver historial de transacciones
curl http://localhost:8080/api/transactions \
  -H "Authorization: Bearer <TOKEN>"

# 5. Cancelar suscripción
curl -X DELETE http://localhost:8080/api/funds/1/cancel \
  -H "Authorization: Bearer <TOKEN>"
```

## Pruebas

```bash
./gradlew test
```

## Despliegue en AWS

Ver la [guía de despliegue completa](docs/DEPLOYMENT.md).

Resumen:
1. Construir y subir imagen Docker a ECR
2. Desplegar con CloudFormation: `aws cloudformation create-stack ...`
3. La infraestructura incluye: VPC, ECS Fargate, ALB, DynamoDB, SNS, IAM

## Estructura de Costos AWS (Free Tier)

| Servicio | Estimado/mes |
|----------|-------------|
| DynamoDB (25GB free) | $0 |
| SNS (1M free) | $0 |
| ECS Fargate | ~$10 |
| ALB (primer año) | $0 |
| **Total** | **~$10/mes** |
