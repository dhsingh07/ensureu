# EnsureU AWS Deployment Guide (Testing Environment)

This guide walks you through deploying EnsureU on a single AWS EC2 instance using Docker for testing with a group of users.

## Architecture Overview

```
AWS EC2 Instance (t3.medium)
├── Docker: Nginx (reverse proxy on port 80)
├── Docker: EnsureU App (Spring Boot on port 8282)
└── Docker: MongoDB (database on port 27017)
```

**Estimated Cost:** ~$30-40/month

---

## Prerequisites

- AWS Account
- Basic knowledge of SSH and command line
- Domain name (optional, but recommended for testing)

---

## Step 1: Launch EC2 Instance

### 1.1 Create EC2 Instance

1. Log in to AWS Console → EC2 → Launch Instance
2. **Configuration:**
   - **Name:** `ensureu-testing`
   - **AMI:** Ubuntu Server 22.04 LTS (Free Tier eligible)
   - **Instance Type:** `t3.medium` (2 vCPU, 4 GB RAM)
   - **Key Pair:** Create new or select existing (SAVE THE .pem FILE!)
   - **Network Settings:**
     - Allow SSH (port 22) from your IP
     - Allow HTTP (port 80) from anywhere (0.0.0.0/0)
     - Allow HTTPS (port 443) from anywhere (0.0.0.0/0)
     - Allow Custom TCP (port 8282) from anywhere (for testing)
   - **Storage:** 30 GB gp3 SSD

3. Click **Launch Instance**

### 1.2 Allocate Elastic IP (Recommended)

1. EC2 → Elastic IPs → Allocate Elastic IP address
2. Associate with your instance
3. This ensures your IP doesn't change on restart

### 1.3 Update Security Group (After Launch)

Go to EC2 → Security Groups → Select your instance's security group → Edit inbound rules:

```
Type            Protocol    Port    Source          Description
SSH             TCP         22      Your IP/32      SSH access
HTTP            TCP         80      0.0.0.0/0       Public web access
HTTPS           TCP         443     0.0.0.0/0       Public web access (future)
Custom TCP      TCP         8282    0.0.0.0/0       Direct app access (testing)
```

---

## Step 2: Connect to EC2 Instance

```bash
# Download your .pem file and set permissions
chmod 400 your-key.pem

# Connect to EC2 instance
ssh -i your-key.pem ubuntu@YOUR_EC2_PUBLIC_IP
```

---

## Step 3: Install Docker on EC2

Run these commands on your EC2 instance:

```bash
# Update system
sudo apt-get update
sudo apt-get upgrade -y

# Install Docker
sudo apt-get install -y ca-certificates curl gnupg lsb-release

# Add Docker's official GPG key
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg

# Set up Docker repository
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Install Docker Engine
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Add current user to docker group (so you don't need sudo)
sudo usermod -aG docker ubuntu

# Verify installation
docker --version
docker compose version

# IMPORTANT: Log out and log back in for group changes to take effect
exit
```

**Reconnect to EC2:**
```bash
ssh -i your-key.pem ubuntu@YOUR_EC2_PUBLIC_IP
```

---

## Step 4: Install Git and Clone Repository

```bash
# Install Git
sudo apt-get install -y git

# Clone your repository (replace with your repo URL)
git clone https://github.com/YOUR_USERNAME/ensureu.git
cd ensureu

# OR if you have the code locally, you can SCP it
# From your local machine:
# scp -i your-key.pem -r /path/to/ensureu ubuntu@YOUR_EC2_PUBLIC_IP:~/
```

---

## Step 5: Configure Environment

```bash
# Copy environment template
cp .env.example .env

# Edit environment file
nano .env
```

**Update these values in `.env`:**

```bash
# MongoDB Passwords (CHANGE THESE!)
MONGO_ROOT_PASSWORD=YourSecureRootPassword123!
MONGO_APP_PASSWORD=YourSecureAppPassword123!

# JWT Secret (CHANGE THIS!)
JWT_SECRET=YourVeryLongRandomJWTSecretKeyForProduction123456789

# Email Configuration (for Gmail)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-gmail-app-password

# For testing without real email, use Mailtrap:
# Sign up at https://mailtrap.io (free tier available)
# MAIL_HOST=smtp.mailtrap.io
# MAIL_PORT=2525
# MAIL_USERNAME=your-mailtrap-username
# MAIL_PASSWORD=your-mailtrap-password
```

**Save and exit:** Press `Ctrl+X`, then `Y`, then `Enter`

---

## Step 6: Deploy Application

```bash
# Make deployment script executable
chmod +x deploy.sh

# Run deployment
./deploy.sh
```

The script will:
1. Build the Docker images
2. Start MongoDB, App, and Nginx containers
3. Show service status and logs

**Wait 1-2 minutes** for the application to fully start.

---

## Step 7: Verify Deployment

### Check Service Status
```bash
docker compose ps
```

You should see 3 containers running:
- ensureu-mongodb
- ensureu-app
- ensureu-nginx

### Check Application Logs
```bash
# View all logs
docker compose logs -f

# View only app logs
docker compose logs -f app

# Press Ctrl+C to exit logs
```

### Test Endpoints

**From your local machine:**

```bash
# Replace YOUR_EC2_PUBLIC_IP with your actual IP

# Health check
curl http://YOUR_EC2_PUBLIC_IP/api/actuator/health

# Swagger UI (open in browser)
http://YOUR_EC2_PUBLIC_IP/api/swagger-ui.html

# Direct app access
http://YOUR_EC2_PUBLIC_IP:8282/api/actuator/health
```

---

## Step 8: Testing with Users

### Access URLs

Share these with your test users:

- **API Base URL:** `http://YOUR_EC2_PUBLIC_IP/api`
- **Swagger Documentation:** `http://YOUR_EC2_PUBLIC_IP/api/swagger-ui.html`

### Create Test Users

You can use the API endpoints or Swagger UI to:
1. Register new users
2. Create test papers/questions
3. Test authentication flow

---

## Common Operations

### View Logs
```bash
# All services
docker compose logs -f

# Specific service
docker compose logs -f app
docker compose logs -f mongodb
docker compose logs -f nginx
```

### Restart Services
```bash
# Restart all
docker compose restart

# Restart specific service
docker compose restart app
```

### Stop Services
```bash
docker compose down
```

### Start Services
```bash
docker compose up -d
```

### Update Application
```bash
# Pull latest code (if using git)
git pull

# Redeploy
./deploy.sh
```

### Access MongoDB Shell
```bash
# Connect to MongoDB container
docker exec -it ensureu-mongodb mongosh

# Inside mongosh:
use assessu
show collections
db.users.find().limit(5)
exit
```

### Backup MongoDB Data
```bash
# Create backup directory
mkdir -p ~/backups

# Backup MongoDB data
docker exec ensureu-mongodb mongodump --out=/tmp/backup --db=assessu

# Copy backup from container to host
docker cp ensureu-mongodb:/tmp/backup ~/backups/backup-$(date +%Y%m%d-%H%M%S)
```

### Restore MongoDB Data
```bash
# Copy backup to container
docker cp ~/backups/backup-20240115 ensureu-mongodb:/tmp/restore

# Restore data
docker exec ensureu-mongodb mongorestore --db=assessu /tmp/restore/assessu
```

---

## Monitoring

### Check Resource Usage
```bash
# System resources
htop  # Install: sudo apt-get install htop

# Docker stats
docker stats

# Disk usage
df -h
docker system df
```

### Set Up CloudWatch (Optional)

Install CloudWatch agent for monitoring:

```bash
# Download and install CloudWatch agent
wget https://s3.amazonaws.com/amazoncloudwatch-agent/ubuntu/amd64/latest/amazon-cloudwatch-agent.deb
sudo dpkg -i -E ./amazon-cloudwatch-agent.deb

# Configure and start (requires IAM role with CloudWatch permissions)
# Follow: https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/install-CloudWatch-Agent-on-EC2-Instance.html
```

---

## Troubleshooting

### Application won't start

**Check logs:**
```bash
docker compose logs app
```

**Common issues:**
- MongoDB not ready: Wait 30 seconds and check again
- Port already in use: `sudo lsof -i :8282`
- Out of memory: Check with `free -h`, consider upgrading instance

### Cannot connect to application

**Check if containers are running:**
```bash
docker compose ps
```

**Check security group:**
- Ensure ports 80, 443, 8282 are open
- Verify source is 0.0.0.0/0 for public access

**Check nginx:**
```bash
docker compose logs nginx
```

### MongoDB connection issues

**Check MongoDB logs:**
```bash
docker compose logs mongodb
```

**Verify credentials in .env match docker/mongo-init.js:**
```bash
cat .env | grep MONGO
```

### Out of disk space

**Check disk usage:**
```bash
df -h
docker system df
```

**Clean up:**
```bash
# Remove unused images
docker image prune -a

# Remove unused volumes (BE CAREFUL!)
docker volume prune
```

---

## Security Best Practices

### For Testing Environment

1. **Change default passwords** in `.env`
2. **Don't expose port 27017** publicly (already handled in docker-compose)
3. **Restrict SSH access** to your IP only
4. **Use strong JWT secret**
5. **Enable HTTPS** before going to production (see next section)

### For Production Migration

When moving to production:

1. **Get SSL certificate** (Let's Encrypt or ACM)
2. **Use managed database** (MongoDB Atlas or DocumentDB)
3. **Enable backups** (automated daily backups)
4. **Set up monitoring** (CloudWatch, Datadog, etc.)
5. **Use environment-specific secrets** (AWS Secrets Manager)
6. **Enable auto-scaling** (move to ECS or Elastic Beanstalk)
7. **Set up CI/CD** (GitHub Actions, Jenkins, CodePipeline)
8. **Configure proper logging** (CloudWatch Logs, ELK stack)

---

## Estimated Costs (Monthly)

```
EC2 t3.medium (744 hours): ~$30-35
EBS Storage (30 GB gp3):   ~$2.40
Elastic IP:                 Free (when attached)
Data Transfer (5 GB out):  ~$0.45
-------------------------------------------
Total:                     ~$33-38/month
```

**Cost Savings:**
- Use Reserved Instances: Save 30-40%
- Stop instance when not in use: Hourly billing
- Use t3.small for very light testing: ~$15/month

---

## Next Steps

1. **Test all API endpoints** using Swagger UI
2. **Create test data** (users, papers, questions)
3. **Invite test users** and gather feedback
4. **Monitor performance** and resource usage
5. **Plan for scaling** based on user feedback

---

## Support

For issues:
1. Check logs: `docker compose logs -f`
2. Verify configuration: `cat .env`
3. Check service health: `docker compose ps`
4. Review application.properties in container

---

## Quick Reference

```bash
# Deploy/Update
./deploy.sh

# View logs
docker compose logs -f app

# Restart
docker compose restart

# Stop
docker compose down

# Start
docker compose up -d

# MongoDB backup
docker exec ensureu-mongodb mongodump --out=/tmp/backup --db=assessu

# Check resources
docker stats
```

Good luck with your testing! 🚀


RUN Locally:
export JAVA_HOME=$(/usr/libexec/java_home -v 11)
cd /Users/dharmendrasingh/Documents/en/ensureu/ensureu/ensureu-service
mvn spring-boot:run

