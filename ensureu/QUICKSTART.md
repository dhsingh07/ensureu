# EnsureU Quick Start Guide

This is a **condensed version** of the deployment guide. For detailed instructions, see [DEPLOYMENT.md](DEPLOYMENT.md).

## Local Testing (Your Machine)

### Prerequisites
- Docker installed
- Docker Compose installed

### Steps

```bash
# 1. Copy environment file
cp .env.example .env

# 2. Edit .env with your credentials
nano .env

# 3. Deploy
./deploy.sh

# 4. Access application
# Swagger UI: http://localhost/api/swagger-ui.html
# Direct API: http://localhost:8282/api
# Health: http://localhost/api/actuator/health
```

---

## AWS EC2 Deployment (Testing Environment)

### 1. Launch EC2 Instance
- **Instance Type:** t3.medium
- **AMI:** Ubuntu 22.04 LTS
- **Storage:** 30 GB
- **Security Group Ports:** 22, 80, 443, 8282

### 2. Connect to EC2
```bash
chmod 400 your-key.pem
ssh -i your-key.pem ubuntu@YOUR_EC2_IP
```

### 3. Install Docker
```bash
sudo apt-get update
sudo apt-get install -y ca-certificates curl gnupg

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Install Docker Compose plugin
sudo apt-get install -y docker-compose-plugin

# Add user to docker group
sudo usermod -aG docker ubuntu

# Log out and log back in
exit
```

### 4. Transfer Project

**Option A: Using Git**
```bash
git clone https://github.com/YOUR_USERNAME/ensureu.git
cd ensureu
```

**Option B: Using SCP (from your local machine)**
```bash
./scripts/transfer-to-ec2.sh your-key.pem ubuntu@YOUR_EC2_IP
```

### 5. Configure and Deploy
```bash
# On EC2 instance
cd ensureu
cp .env.example .env
nano .env  # Update credentials

# Deploy
./deploy.sh
```

### 6. Verify
```bash
# Check services
docker compose ps

# View logs
docker compose logs -f

# Test (from your browser)
# http://YOUR_EC2_IP/api/swagger-ui.html
```

---

## Essential Commands

```bash
# View logs
docker compose logs -f app

# Restart services
docker compose restart

# Stop services
docker compose down

# Start services
docker compose up -d

# Update application
git pull && ./deploy.sh

# Backup database
docker exec ensureu-mongodb mongodump --out=/tmp/backup --db=assessu
```

---

## Access URLs

After deployment, share these with your users:

- **Swagger UI:** `http://YOUR_EC2_IP/api/swagger-ui.html`
- **API Base:** `http://YOUR_EC2_IP/api`
- **Health Check:** `http://YOUR_EC2_IP/api/actuator/health`

---

## Cost Estimate

**~$33-38/month** for t3.medium with 30GB storage

---

## Troubleshooting

### Container not starting?
```bash
docker compose logs app
```

### Can't connect to API?
- Check security group has ports 80, 8282 open
- Verify EC2 public IP is correct
- Check containers are running: `docker compose ps`

### MongoDB issues?
```bash
docker compose logs mongodb
# Verify .env has correct MONGO_APP_PASSWORD
```

### Out of space?
```bash
df -h
docker system prune
```

---

## Next Steps

1. ✅ Deploy to EC2
2. ✅ Test all endpoints via Swagger
3. ✅ Share URL with test users
4. 📊 Monitor usage and performance
5. 🚀 Plan for production scaling

---

For detailed instructions, see **[DEPLOYMENT.md](DEPLOYMENT.md)**
