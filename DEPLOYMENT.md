# EnsureU - AWS EC2 Deployment Guide

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              AWS EC2 Instance                                │
│                         (t3.medium or larger recommended)                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────┐     ┌──────────────────────────────────────────────────┐   │
│  │   Nginx     │     │              Docker Network (ensureu-net)        │   │
│  │   (Port 80  │     │                                                  │   │
│  │    & 443)   │     │  ┌─────────────┐  ┌─────────────┐  ┌──────────┐ │   │
│  │             │────▶│  │   Next.js   │  │    Java     │  │  Python  │ │   │
│  │  SSL/TLS    │     │  │  Frontend   │  │   Backend   │  │    AI    │ │   │
│  │  Termination│     │  │  (Port 3000)│  │ (Port 8282) │  │(Port 8000│ │   │
│  └─────────────┘     │  └─────────────┘  └──────┬──────┘  └────┬─────┘ │   │
│                      │                          │               │       │   │
│                      │                          ▼               ▼       │   │
│                      │                   ┌─────────────────────────┐   │   │
│                      │                   │       MongoDB           │   │   │
│                      │                   │     (Port 27017)        │   │   │
│                      │                   │   Persistent Volume     │   │   │
│                      │                   └─────────────────────────┘   │   │
│                      └──────────────────────────────────────────────────┘   │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘

External Services:
┌─────────────┐  ┌─────────────┐  ┌─────────────┐
│  Anthropic  │  │   OpenAI    │  │   Ollama    │
│  Claude API │  │     API     │  │   (Local)   │
└─────────────┘  └─────────────┘  └─────────────┘
```

## Components

| Component | Technology | Port | Description |
|-----------|------------|------|-------------|
| Frontend | Next.js 14 | 3000 | React-based web application |
| Backend | Spring Boot 2.6 | 8282 | Java REST API server |
| AI Service | FastAPI (Python) | 8000 | AI/LLM integration service |
| Database | MongoDB 6.0 | 27017 | Document database |
| Reverse Proxy | Nginx | 80/443 | SSL termination, load balancing |

## Prerequisites

- AWS EC2 instance (minimum t3.medium, recommended t3.large)
- Ubuntu 22.04 LTS or Amazon Linux 2023
- Domain name (optional, for SSL)
- SSH access to EC2 instance

---

## Step 1: EC2 Instance Setup

### 1.1 Launch EC2 Instance

```bash
# Recommended specs:
# - Instance Type: t3.medium (2 vCPU, 4GB RAM) minimum
#                  t3.large (2 vCPU, 8GB RAM) recommended for AI workloads
# - Storage: 30GB+ EBS (gp3)
# - OS: Ubuntu 22.04 LTS
```

### 1.2 Security Group Rules

| Type | Port | Source | Description |
|------|------|--------|-------------|
| SSH | 22 | Your IP | SSH access |
| HTTP | 80 | 0.0.0.0/0 | Web traffic |
| HTTPS | 443 | 0.0.0.0/0 | Secure web traffic |
| Custom TCP | 8282 | VPC CIDR | Java backend (internal) |
| Custom TCP | 8000 | VPC CIDR | AI service (internal) |
| Custom TCP | 3000 | VPC CIDR | Next.js (internal) |

### 1.3 Connect and Update System

```bash
# Connect to EC2
ssh -i your-key.pem ubuntu@your-ec2-public-ip

# Update system
sudo apt update && sudo apt upgrade -y

# Install essential tools
sudo apt install -y git curl wget vim htop
```

---

## Step 2: Install Docker

```bash
# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Add user to docker group
sudo usermod -aG docker $USER

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Verify installation
docker --version
docker-compose --version

# Log out and back in for group changes
exit
# Reconnect via SSH
```

---

## Step 3: Project Structure

```bash
# Create project directory
sudo mkdir -p /opt/ensureu
sudo chown $USER:$USER /opt/ensureu
cd /opt/ensureu

# Clone repository (or upload files)
git clone https://github.com/your-repo/ensureu.git .

# Or create structure manually
mkdir -p {ensureu,ensureu-ui,ensureu-ai,ensureu-next,nginx,data/mongodb}
```

---

## Step 4: Docker Configuration Files

### 4.1 Main Docker Compose (`docker-compose.yml`)

```yaml
version: '3.8'

services:
  # MongoDB Database
  mongodb:
    image: mongo:6.0
    container_name: ensureu-mongodb
    restart: always
    environment:
      MONGO_INITDB_ROOT_USERNAME: ${MONGO_ROOT_USER:-admin}
      MONGO_INITDB_ROOT_PASSWORD: ${MONGO_ROOT_PASSWORD:-adminpassword}
      MONGO_INITDB_DATABASE: ensureu
    volumes:
      - mongodb_data:/data/db
      - ./mongo-init.js:/docker-entrypoint-initdb.d/mongo-init.js:ro
    ports:
      - "27017:27017"
    networks:
      - ensureu-net
    healthcheck:
      test: echo 'db.runCommand("ping").ok' | mongosh localhost:27017/test --quiet
      interval: 30s
      timeout: 10s
      retries: 5

  # Java Spring Boot Backend
  backend:
    build:
      context: ./ensureu
      dockerfile: Dockerfile
    container_name: ensureu-backend
    restart: always
    depends_on:
      mongodb:
        condition: service_healthy
    environment:
      - SPRING_DATA_MONGODB_URI=mongodb://${MONGO_USER:-ensureu}:${MONGO_PASSWORD:-Ensureu@india123}@mongodb:27017/ensureu?authSource=ensureu
      - SPRING_PROFILES_ACTIVE=production
      - AI_SERVICE_URL=http://ai-service:8000
      - AI_SERVICE_TIMEOUT=120000
      - JWT_SECRET=${JWT_SECRET:-mySecretKeyForProduction123!}
    ports:
      - "8282:8282"
    networks:
      - ensureu-net
    healthcheck:
      test: curl -f http://localhost:8282/api/actuator/health || exit 1
      interval: 30s
      timeout: 10s
      retries: 5

  # Python AI Service
  ai-service:
    build:
      context: ./ensureu-ai
      dockerfile: Dockerfile
    container_name: ensureu-ai
    restart: always
    depends_on:
      mongodb:
        condition: service_healthy
    environment:
      - DEBUG=false
      - LLM_PROVIDER=${LLM_PROVIDER:-claude}
      - ANTHROPIC_API_KEY=${ANTHROPIC_API_KEY}
      - OPENAI_API_KEY=${OPENAI_API_KEY:-}
      - OLLAMA_BASE_URL=${OLLAMA_BASE_URL:-http://host.docker.internal:11434}
      - MONGODB_URI=mongodb://${MONGO_USER:-ensureu}:${MONGO_PASSWORD:-Ensureu@india123}@mongodb:27017/ensureu?authSource=ensureu
      - MONGODB_DB=ensureu
      - JWT_SECRET=${JWT_SECRET:-mySecretKeyForProduction123!}
      - LLM_TIMEOUT=120
    ports:
      - "8000:8000"
    networks:
      - ensureu-net
    healthcheck:
      test: curl -f http://localhost:8000/health || exit 1
      interval: 30s
      timeout: 10s
      retries: 5

  # Next.js Frontend
  frontend:
    build:
      context: ./ensureu-next
      dockerfile: Dockerfile
      args:
        - NEXT_PUBLIC_API_URL=${API_URL:-http://localhost:8282/api}
    container_name: ensureu-frontend
    restart: always
    depends_on:
      - backend
    environment:
      - NODE_ENV=production
      - NEXT_PUBLIC_API_URL=${API_URL:-http://localhost:8282/api}
    ports:
      - "3000:3000"
    networks:
      - ensureu-net

  # Nginx Reverse Proxy
  nginx:
    image: nginx:alpine
    container_name: ensureu-nginx
    restart: always
    depends_on:
      - frontend
      - backend
      - ai-service
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./nginx/ssl:/etc/nginx/ssl:ro
      - ./nginx/logs:/var/log/nginx
    networks:
      - ensureu-net

networks:
  ensureu-net:
    driver: bridge

volumes:
  mongodb_data:
    driver: local
```

### 4.2 MongoDB Init Script (`mongo-init.js`)

```javascript
// Create application database and user
db = db.getSiblingDB('ensureu');

db.createUser({
  user: 'ensureu',
  pwd: 'Ensureu@india123',
  roles: [
    { role: 'readWrite', db: 'ensureu' }
  ]
});

// Create indexes for better performance
db.users.createIndex({ "userName": 1 }, { unique: true });
db.users.createIndex({ "email": 1 });
db.users.createIndex({ "mobileNumber": 1 });

db.papers.createIndex({ "paperCategory": 1, "paperStatus": 1 });
db.papers.createIndex({ "createdDate": -1 });

db.user_exam_analyses.createIndex({ "user_id": 1, "analyzed_at": -1 });
db.user_exam_analyses.createIndex({ "analysis_month": 1 });

print('MongoDB initialized successfully');
```

### 4.3 Java Backend Dockerfile (`ensureu/Dockerfile`)

```dockerfile
# Build stage
FROM maven:3.9-eclipse-temurin-22 AS builder
WORKDIR /app
COPY pom.xml .
COPY ensureu-commons/pom.xml ensureu-commons/
COPY ensureu-service/pom.xml ensureu-service/
RUN mvn dependency:go-offline -B

COPY ensureu-commons/src ensureu-commons/src
COPY ensureu-service/src ensureu-service/src
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:11-jre-jammy
WORKDIR /app

# Create non-root user
RUN groupadd -r ensureu && useradd -r -g ensureu ensureu

COPY --from=builder /app/ensureu-service/target/*.jar app.jar

# Set ownership
RUN chown -R ensureu:ensureu /app
USER ensureu

EXPOSE 8282

ENTRYPOINT ["java", "-jar", "-Xms512m", "-Xmx1024m", "-Djava.security.egd=file:/dev/./urandom", "app.jar"]
```

### 4.4 Python AI Service Dockerfile (`ensureu-ai/Dockerfile`)

```dockerfile
FROM python:3.11-slim

WORKDIR /app

# Install system dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Create non-root user
RUN groupadd -r ensureu && useradd -r -g ensureu ensureu

# Install Python dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy application
COPY app ./app

# Set ownership
RUN chown -R ensureu:ensureu /app
USER ensureu

EXPOSE 8000

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000", "--workers", "2"]
```

### 4.5 Next.js Frontend Dockerfile (`ensureu-next/Dockerfile`)

```dockerfile
# Build stage
FROM node:20-alpine AS builder
WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm ci

# Build argument for API URL
ARG NEXT_PUBLIC_API_URL
ENV NEXT_PUBLIC_API_URL=$NEXT_PUBLIC_API_URL

# Copy source and build
COPY . .
RUN npm run build

# Production stage
FROM node:20-alpine AS runner
WORKDIR /app

ENV NODE_ENV=production

# Create non-root user
RUN addgroup -g 1001 -S nodejs && adduser -S nextjs -u 1001

# Copy built assets
COPY --from=builder /app/public ./public
COPY --from=builder --chown=nextjs:nodejs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /app/.next/static ./.next/static

USER nextjs

EXPOSE 3000

CMD ["node", "server.js"]
```

### 4.6 Nginx Configuration (`nginx/nginx.conf`)

```nginx
user nginx;
worker_processes auto;
error_log /var/log/nginx/error.log warn;
pid /var/run/nginx.pid;

events {
    worker_connections 1024;
    use epoll;
    multi_accept on;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    # Logging
    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                    '$status $body_bytes_sent "$http_referer" '
                    '"$http_user_agent" "$http_x_forwarded_for"';
    access_log /var/log/nginx/access.log main;

    # Performance
    sendfile on;
    tcp_nopush on;
    tcp_nodelay on;
    keepalive_timeout 65;
    types_hash_max_size 2048;

    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_proxied any;
    gzip_comp_level 6;
    gzip_types text/plain text/css text/xml application/json application/javascript
               application/xml application/xml+rss text/javascript application/x-javascript;

    # Rate limiting
    limit_req_zone $binary_remote_addr zone=api_limit:10m rate=30r/s;
    limit_req_zone $binary_remote_addr zone=general_limit:10m rate=100r/s;

    # Upstream servers
    upstream frontend {
        server frontend:3000;
        keepalive 32;
    }

    upstream backend {
        server backend:8282;
        keepalive 32;
    }

    upstream ai_service {
        server ai-service:8000;
        keepalive 16;
    }

    # HTTP Server (redirect to HTTPS in production)
    server {
        listen 80;
        server_name _;

        # For Let's Encrypt certificate validation
        location /.well-known/acme-challenge/ {
            root /var/www/certbot;
        }

        # Redirect to HTTPS (uncomment in production with SSL)
        # return 301 https://$server_name$request_uri;

        # HTTP configuration (use when SSL is not configured)
        location / {
            proxy_pass http://frontend;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection 'upgrade';
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_cache_bypass $http_upgrade;

            limit_req zone=general_limit burst=50 nodelay;
        }

        # Java Backend API
        location /api/ {
            proxy_pass http://backend/api/;
            proxy_http_version 1.1;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;

            # Increased timeouts for AI operations
            proxy_connect_timeout 60s;
            proxy_send_timeout 120s;
            proxy_read_timeout 120s;

            # Rate limiting for API
            limit_req zone=api_limit burst=20 nodelay;

            # CORS headers
            add_header 'Access-Control-Allow-Origin' '*' always;
            add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS' always;
            add_header 'Access-Control-Allow-Headers' 'Authorization, Content-Type' always;

            if ($request_method = 'OPTIONS') {
                return 204;
            }
        }

        # Health checks
        location /health {
            access_log off;
            return 200 'OK';
            add_header Content-Type text/plain;
        }
    }

    # HTTPS Server (uncomment when SSL is configured)
    # server {
    #     listen 443 ssl http2;
    #     server_name your-domain.com;
    #
    #     ssl_certificate /etc/nginx/ssl/fullchain.pem;
    #     ssl_certificate_key /etc/nginx/ssl/privkey.pem;
    #     ssl_session_timeout 1d;
    #     ssl_session_cache shared:SSL:50m;
    #     ssl_protocols TLSv1.2 TLSv1.3;
    #     ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256;
    #     ssl_prefer_server_ciphers off;
    #
    #     # HSTS
    #     add_header Strict-Transport-Security "max-age=63072000" always;
    #
    #     # Same location blocks as HTTP server above...
    # }
}
```

---

## Step 5: Environment Configuration

### 5.1 Create Environment File (`.env`)

```bash
cd /opt/ensureu
cat > .env << 'EOF'
# MongoDB
MONGO_ROOT_USER=admin
MONGO_ROOT_PASSWORD=YourSecureAdminPassword123!
MONGO_USER=ensureu
MONGO_PASSWORD=YourSecureAppPassword123!

# JWT Secret (generate with: openssl rand -base64 32)
JWT_SECRET=YourSuperSecretJWTKey123!ChangeThisInProduction

# LLM Configuration
LLM_PROVIDER=claude
ANTHROPIC_API_KEY=sk-ant-your-api-key-here
OPENAI_API_KEY=sk-your-openai-key-here

# Application URLs
API_URL=http://your-domain.com/api
FRONTEND_URL=http://your-domain.com

# Optional: Ollama (if running locally)
OLLAMA_BASE_URL=http://host.docker.internal:11434
EOF

# Secure the file
chmod 600 .env
```

---

## Step 6: Build and Deploy

### 6.1 Build All Images

```bash
cd /opt/ensureu

# Build all services
docker-compose build --no-cache

# Or build individually
docker-compose build mongodb
docker-compose build backend
docker-compose build ai-service
docker-compose build frontend
docker-compose build nginx
```

### 6.2 Start Services

```bash
# Start all services
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f backend
docker-compose logs -f ai-service
```

### 6.3 Verify Deployment

```bash
# Check all containers are running
docker ps

# Test health endpoints
curl http://localhost/health
curl http://localhost/api/actuator/health
curl http://localhost:8000/health

# Test frontend
curl -I http://localhost
```

---

## Step 7: SSL/HTTPS Setup (Optional but Recommended)

### 7.1 Using Let's Encrypt with Certbot

```bash
# Install Certbot
sudo apt install -y certbot

# Stop nginx temporarily
docker-compose stop nginx

# Get certificate
sudo certbot certonly --standalone -d your-domain.com

# Copy certificates
sudo mkdir -p /opt/ensureu/nginx/ssl
sudo cp /etc/letsencrypt/live/your-domain.com/fullchain.pem /opt/ensureu/nginx/ssl/
sudo cp /etc/letsencrypt/live/your-domain.com/privkey.pem /opt/ensureu/nginx/ssl/
sudo chown -R $USER:$USER /opt/ensureu/nginx/ssl

# Update nginx.conf to enable HTTPS (uncomment SSL server block)

# Restart nginx
docker-compose up -d nginx
```

### 7.2 Auto-Renewal

```bash
# Add to crontab
(crontab -l 2>/dev/null; echo "0 0 1 * * certbot renew --quiet && docker-compose -f /opt/ensureu/docker-compose.yml restart nginx") | crontab -
```

---

## Step 8: Management Commands

### 8.1 Common Operations

```bash
# Start all services
docker-compose up -d

# Stop all services
docker-compose down

# Restart a specific service
docker-compose restart backend

# View logs
docker-compose logs -f --tail=100

# Scale services (if needed)
docker-compose up -d --scale backend=2

# Update and redeploy
git pull
docker-compose build --no-cache
docker-compose up -d
```

### 8.2 Database Backup

```bash
# Create backup script
cat > /opt/ensureu/backup.sh << 'EOF'
#!/bin/bash
BACKUP_DIR="/opt/ensureu/backups"
DATE=$(date +%Y%m%d_%H%M%S)
mkdir -p $BACKUP_DIR

docker exec ensureu-mongodb mongodump \
  --username ensureu \
  --password ${MONGO_PASSWORD} \
  --authenticationDatabase ensureu \
  --db ensureu \
  --out /tmp/backup_$DATE

docker cp ensureu-mongodb:/tmp/backup_$DATE $BACKUP_DIR/
docker exec ensureu-mongodb rm -rf /tmp/backup_$DATE

# Keep only last 7 days
find $BACKUP_DIR -type d -mtime +7 -exec rm -rf {} +

echo "Backup completed: $BACKUP_DIR/backup_$DATE"
EOF

chmod +x /opt/ensureu/backup.sh

# Add to crontab (daily backup at 2 AM)
(crontab -l 2>/dev/null; echo "0 2 * * * /opt/ensureu/backup.sh") | crontab -
```

### 8.3 Restore Database

```bash
# Restore from backup
docker cp /opt/ensureu/backups/backup_YYYYMMDD_HHMMSS ensureu-mongodb:/tmp/restore

docker exec ensureu-mongodb mongorestore \
  --username ensureu \
  --password ${MONGO_PASSWORD} \
  --authenticationDatabase ensureu \
  --db ensureu \
  /tmp/restore/ensureu
```

---

## Step 9: Monitoring

### 9.1 Basic Monitoring Script

```bash
cat > /opt/ensureu/monitor.sh << 'EOF'
#!/bin/bash

echo "=== EnsureU System Status ==="
echo ""

echo "Docker Containers:"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
echo ""

echo "Resource Usage:"
docker stats --no-stream --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}"
echo ""

echo "Disk Usage:"
df -h /opt/ensureu
echo ""

echo "Health Checks:"
echo -n "Frontend: "; curl -s -o /dev/null -w "%{http_code}" http://localhost:3000 || echo "FAIL"
echo -n "Backend:  "; curl -s -o /dev/null -w "%{http_code}" http://localhost:8282/api/actuator/health || echo "FAIL"
echo -n "AI:       "; curl -s -o /dev/null -w "%{http_code}" http://localhost:8000/health || echo "FAIL"
echo ""
EOF

chmod +x /opt/ensureu/monitor.sh
```

### 9.2 Log Rotation

```bash
cat > /etc/logrotate.d/ensureu << 'EOF'
/opt/ensureu/nginx/logs/*.log {
    daily
    missingok
    rotate 14
    compress
    delaycompress
    notifempty
    create 0640 root root
    sharedscripts
    postrotate
        docker exec ensureu-nginx nginx -s reload
    endscript
}
EOF
```

---

## Troubleshooting

### Common Issues

| Issue | Solution |
|-------|----------|
| Container won't start | Check logs: `docker-compose logs <service>` |
| MongoDB connection failed | Verify credentials in `.env` and wait for health check |
| AI service timeout | Increase `AI_SERVICE_TIMEOUT` in backend env |
| Out of memory | Increase EC2 instance size or add swap |
| Permission denied | Check file ownership and Docker socket permissions |

### Debug Commands

```bash
# Enter container shell
docker exec -it ensureu-backend bash
docker exec -it ensureu-ai bash
docker exec -it ensureu-mongodb mongosh

# Check container logs
docker logs ensureu-backend --tail 100 -f

# Inspect container
docker inspect ensureu-backend

# Check network
docker network inspect ensureu_ensureu-net

# Reset everything (CAUTION: destroys data)
docker-compose down -v
docker system prune -af
```

---

## Quick Start Summary

```bash
# 1. Connect to EC2
ssh -i key.pem ubuntu@your-ec2-ip

# 2. Install Docker
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker $USER && exit
# Reconnect

# 3. Clone/Upload project
cd /opt && sudo mkdir ensureu && sudo chown $USER:$USER ensureu
cd ensureu
# Upload or clone your code here

# 4. Configure environment
cp .env.example .env
nano .env  # Edit with your values

# 5. Deploy
docker-compose up -d

# 6. Verify
docker-compose ps
curl http://localhost/health
```

---

## Cost Estimation (AWS)

| Resource | Spec | Monthly Cost (approx) |
|----------|------|----------------------|
| EC2 t3.medium | 2 vCPU, 4GB RAM | ~$30 |
| EC2 t3.large | 2 vCPU, 8GB RAM | ~$60 |
| EBS Storage | 30GB gp3 | ~$3 |
| Data Transfer | 100GB out | ~$9 |
| **Total (t3.medium)** | | **~$42/month** |
| **Total (t3.large)** | | **~$72/month** |

*Note: Costs vary by region. AI API costs (Claude/OpenAI) are additional.*
