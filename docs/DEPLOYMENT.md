# Guía de Despliegue en AWS — Serverless (Lambda + API Gateway)

## Arquitectura

La API se despliega como una función **AWS Lambda** (Java 21 + SnapStart) expuesta a través de **API Gateway HTTP API**. La infraestructura completa se define en `cloudformation/template-serverless.yaml` usando **AWS SAM**.

```
Cliente → API Gateway HTTP API (/prod) → Lambda (Spring Boot) → DynamoDB / SNS
```

## Prerrequisitos

| Herramienta | Versión mínima | Instalación |
|-------------|---------------|-------------|
| Java JDK | 21 | [Adoptium](https://adoptium.net/) |
| AWS CLI | 2.x | `winget install Amazon.AWSCLI` |
| AWS SAM CLI | 1.x | `winget install Amazon.SAM-CLI` |
| Gradle | 8.x | Incluido via `gradlew` |

Configurar credenciales AWS:

```bash
aws configure
# Ingresar: Access Key, Secret Key, Region (us-east-1), Output (json)
```

## Despliegue paso a paso

### 1. Compilar y generar el ZIP

```bash
./gradlew buildZip
```

Esto genera `build/distributions/fondos-api-1.0.0.zip` con el JAR y dependencias.

### 2. Build SAM (empaqueta el template y el artefacto)

```bash
sam build -t cloudformation/template-serverless.yaml
```

### 3. Desplegar

```bash
sam deploy \
  -t cloudformation/template-serverless.yaml \
  --resolve-s3 \
  --stack-name fondos-api-prod \
  --capabilities CAPABILITY_IAM \
  --parameter-overrides \
    "JwtSecret=<TU_JWT_SECRET_BASE64>" \
    "Environment=prod" \
  --no-confirm-changeset
```

> **Nota:** Reemplazar `<TU_JWT_SECRET_BASE64>` con un secreto codificado en Base64. Ejemplo para generar uno:
> ```bash
> echo -n "miClaveSecretaParaJWT2025" | base64
> ```

### 4. Obtener la URL de la API

```bash
aws cloudformation describe-stacks \
  --stack-name fondos-api-prod \
  --query 'Stacks[0].Outputs[?OutputKey==`ApiUrl`].OutputValue' \
  --output text
```

Resultado esperado: `https://<api-id>.execute-api.us-east-1.amazonaws.com/prod`

### 5. Verificar

```bash
# Health check
curl https://<api-id>.execute-api.us-east-1.amazonaws.com/prod/actuator/health

# Swagger UI (abrir en navegador)
https://<api-id>.execute-api.us-east-1.amazonaws.com/prod/swagger-ui/index.html
```

## Datos iniciales

Los 5 fondos del catálogo se crean automáticamente al arrancar la aplicación mediante `DataInitializer`.

## Actualizar la aplicación

```bash
./gradlew buildZip
sam build -t cloudformation/template-serverless.yaml
sam deploy \
  -t cloudformation/template-serverless.yaml \
  --resolve-s3 \
  --stack-name fondos-api-prod \
  --capabilities CAPABILITY_IAM \
  --parameter-overrides \
    "JwtSecret=<TU_JWT_SECRET_BASE64>" \
    "Environment=prod" \
  --no-confirm-changeset
```

## Eliminar la infraestructura

```bash
sam delete --stack-name fondos-api-prod
```

Esto elimina: Lambda, API Gateway, tablas DynamoDB, topic SNS y roles IAM.

## Costos estimados (Free Tier)

| Servicio | Free Tier | Costo estimado |
|----------|-----------|----------------|
| Lambda | 1M requests + 400K GB-s/mes | $0 |
| API Gateway HTTP | 1M requests/mes (12 meses) | $0 |
| DynamoDB | 25GB + 25 RCU/WCU | $0 |
| SNS | 1M publicaciones/mes | $0 |
| CloudWatch Logs | 5GB | $0 |
| S3 (artefacto SAM) | 5GB | $0 |

**Total estimado: $0/mes** (dentro de Free Tier)

## Características de producción incluidas

- **SnapStart**: Reduce el cold start de la Lambda (pre-inicializa el JVM snapshot)
- **CORS**: Configurado a nivel de API Gateway
- **Perfiles Spring**: `aws` se activa automáticamente en Lambda
- **Variables de entorno**: JWT secret, ARN de SNS, nombres de tablas — todo inyectado por CloudFormation
