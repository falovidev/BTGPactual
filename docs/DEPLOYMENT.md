# Guía de Despliegue en AWS

## Prerrequisitos

- AWS CLI configurado con credenciales
- Docker instalado
- Cuenta de AWS con acceso a ECR, ECS, DynamoDB, SNS, CloudFormation

## 1. Crear repositorio ECR

```bash
aws ecr create-repository --repository-name fondos-api --region us-east-1
```

## 2. Construir y subir imagen Docker

```bash
# Autenticarse en ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com

# Construir imagen
docker build -t fondos-api .

# Etiquetar
docker tag fondos-api:latest <ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/fondos-api:latest

# Subir
docker push <ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/fondos-api:latest
```

## 3. Desplegar con CloudFormation

```bash
aws cloudformation create-stack \
  --stack-name fondos-api-prod \
  --template-body file://cloudformation/template.yaml \
  --parameters \
    ParameterKey=Environment,ParameterValue=prod \
    ParameterKey=DockerImage,ParameterValue=<ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/fondos-api:latest \
    ParameterKey=JwtSecret,ParameterValue=<TU_JWT_SECRET_BASE64> \
  --capabilities CAPABILITY_IAM \
  --region us-east-1
```

## 4. Verificar despliegue

```bash
# Ver estado del stack
aws cloudformation describe-stacks --stack-name fondos-api-prod --query 'Stacks[0].StackStatus'

# Obtener URL de la API
aws cloudformation describe-stacks --stack-name fondos-api-prod \
  --query 'Stacks[0].Outputs[?OutputKey==`ApiUrl`].OutputValue' --output text
```

## 5. Inicializar datos de fondos

Los fondos se inicializan automáticamente al arrancar la aplicación mediante `DataInitializer`.

## Costos estimados (Free Tier)

| Servicio | Free Tier | Costo estimado |
|----------|-----------|----------------|
| DynamoDB | 25GB + 25 RCU/WCU | $0 |
| SNS | 1M publicaciones | $0 |
| ECS Fargate | No tiene free tier | ~$10/mes (256 CPU, 512MB) |
| ALB | 750 horas/mes primer año | $0 primer año |
| CloudWatch Logs | 5GB | $0 |
| ECR | 500MB | $0 |

**Total estimado primer año: ~$10/mes** (solo por Fargate)

## Actualizar la aplicación

```bash
# Construir nueva imagen
docker build -t fondos-api .
docker tag fondos-api:latest <ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/fondos-api:latest
docker push <ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/fondos-api:latest

# Forzar nuevo despliegue en ECS
aws ecs update-service --cluster fondos-api-cluster-prod --service <SERVICE_NAME> --force-new-deployment
```

## Eliminar la infraestructura

```bash
aws cloudformation delete-stack --stack-name fondos-api-prod
```
