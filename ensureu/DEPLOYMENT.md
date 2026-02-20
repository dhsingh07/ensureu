# EnsureU AWS Deployment Guide

This guide walks you through deploying EnsureU (Backend + Frontend) on AWS EC2.

## Architecture Overview

```
AWS EC2 Instance (t3.medium or t3.large)
├── Docker: Nginx (reverse proxy on port 80/443)
├── Docker: EnsureU Backend (Spring Boot on port 8282)
├── Docker: MongoDB (database on port 27017)
├── Docker: Next.js Frontend (on port 3000)
└── AWS S3: File Storage (CSV uploads, images)
```

**Estimated Cost:** ~$40-60/month

---

## Prerequisites

- AWS Account
- S3 Bucket created for file storage
- Domain name (optional, but recommended)
- SSL Certificate (for HTTPS)

---

## Step 1: AWS S3 Setup (File Storage)

### 1.1 Create S3 Bucket

1. Go to AWS Console → S3 → Create Bucket
2. **Configuration:**
   - **Bucket name:** `ensureu-assets-prod` (or your preferred name)
   - **Region:** `ap-south-1` (Mumbai) or nearest to your users
   - **Block Public Access:** Keep enabled (we'll use signed URLs)
   - **Versioning:** Enable (recommended)

### 1.2 Create IAM User for S3 Access

1. Go to IAM → Users → Add User
2. **User name:** `ensureu-s3-user`
3. **Access type:** Programmatic access
4. **Permissions:** Attach policy `AmazonS3FullAccess` (or create custom policy)
5. **Save credentials:** Download Access Key ID and Secret Access Key

### 1.3 S3 Bucket Policy (Optional - for public read access to images)

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "PublicReadGetObject",
            "Effect": "Allow",
            "Principal": "*",
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::ensureu-assets-prod/uploads/*"
        }
    ]
}
```

---

## Step 2: Launch EC2 Instance

### 2.1 Create EC2 Instance

1. Log in to AWS Console → EC2 → Launch Instance
2. **Configuration:**
   - **Name:** `ensureu-prod`
   - **AMI:** Ubuntu Server 22.04 LTS
   - **Instance Type:** `t3.medium` (2 vCPU, 4 GB RAM) or `t3.large` for production
   - **Key Pair:** Create new or select existing (SAVE THE .pem FILE!)
   - **Network Settings:**
     - Allow SSH (port 22) from your IP
     - Allow HTTP (port 80) from anywhere (0.0.0.0/0)
     - Allow HTTPS (port 443) from anywhere (0.0.0.0/0)
     - Allow Custom TCP (port 8282) from anywhere (for API)
     - Allow Custom TCP (port 3000) from anywhere (for Frontend dev)
   - **Storage:** 50 GB gp3 SSD

### 2.2 Allocate Elastic IP

1. EC2 → Elastic IPs → Allocate Elastic IP address
2. Associate with your instance
3. This ensures your IP doesn't change on restart

---

## Step 3: Connect & Setup EC2

```bash
# Connect to EC2
chmod 400 your-key.pem
ssh -i your-key.pem ubuntu@YOUR_EC2_PUBLIC_IP

# Update system
sudo apt-get update && sudo apt-get upgrade -y

# Install Docker
sudo apt-get install -y ca-certificates curl gnupg lsb-release
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Add user to docker group
sudo usermod -aG docker ubuntu

# Install Git
sudo apt-get install -y git

# Install Node.js (for frontend)
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# Verify installations
docker --version
docker compose version
node --version
npm --version

# Log out and back in for docker group to take effect
exit
```

---

## Step 4: Clone Repository

```bash
ssh -i your-key.pem ubuntu@YOUR_EC2_PUBLIC_IP

# Clone repositories
git clone https://github.com/YOUR_USERNAME/ensureu.git
git clone https://github.com/YOUR_USERNAME/ensureu-next.git

cd ensureu
```

---

## Step 5: Configure Environment

### 5.1 Backend Configuration

```bash
cd ~/ensureu
cp .env.example .env
nano .env
```

**Update `.env` with production values:**

```bash
# ===========================================
# MongoDB Configuration
# ===========================================
MONGO_ROOT_PASSWORD=YourSecureRootPassword123!
MONGO_APP_PASSWORD=YourSecureAppPassword123!

# ===========================================
# JWT Security
# ===========================================
JWT_SECRET=YourVeryLongRandomJWTSecretKeyForProduction123456789

# ===========================================
# S3 Storage Configuration (REQUIRED)
# ===========================================
STORAGE_TYPE=S3
S3_BUCKET_NAME=ensureu-assets-prod
S3_REGION=ap-south-1
S3_ACCESS_KEY=YOUR_AWS_ACCESS_KEY_ID
S3_SECRET_KEY=YOUR_AWS_SECRET_ACCESS_KEY
S3_PREFIX=uploads

# ===========================================
# Email Configuration (Gmail or Mailtrap)
# ===========================================
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-gmail-app-password

# ===========================================
# Google OAuth
# ===========================================
GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
```

### 5.2 Update application-prod.properties

```bash
nano ensureu-service/src/main/resources/application-prod.properties
```

Ensure S3 configuration is set:

```properties
# File Storage - S3
storage.type=S3
storage.s3.bucket-name=${S3_BUCKET_NAME:ensureu-assets-prod}
storage.s3.region=${S3_REGION:ap-south-1}
storage.s3.access-key=${S3_ACCESS_KEY}
storage.s3.secret-key=${S3_SECRET_KEY}
storage.s3.prefix=${S3_PREFIX:uploads}
```

### 5.3 Frontend Configuration

```bash
cd ~/ensureu-next
cp .env.example .env.local
nano .env.local
```

**Update `.env.local`:**

```bash
NEXT_PUBLIC_API_URL=https://your-domain.com/api/
# OR for IP-based deployment:
# NEXT_PUBLIC_API_URL=http://YOUR_EC2_PUBLIC_IP:8282/api/
```

---

## Step 6: Deploy Backend

```bash
cd ~/ensureu

# Make deploy script executable
chmod +x deploy.sh

# Deploy with production profile
./deploy.sh

# Verify containers are running
docker compose ps

# Check logs
docker compose logs -f app
```

**Expected output:**
```
[S3Storage] Initialized S3 client for bucket: ensureu-assets-prod in region: ap-south-1
[FileStorage] Using S3 storage
```

---

## Step 7: Deploy Frontend

```bash
cd ~/ensureu-next

# Install dependencies
npm install

# Build for production
npm run build

# Option 1: Run with PM2 (recommended)
sudo npm install -g pm2
pm2 start npm --name "ensureu-frontend" -- start
pm2 save
pm2 startup

# Option 2: Run with Docker
docker build -t ensureu-frontend .
docker run -d -p 3000:3000 --name ensureu-frontend ensureu-frontend
```

---

## Step 8: Configure Nginx (Reverse Proxy)

Update nginx configuration to serve both backend and frontend:

```bash
nano ~/ensureu/nginx/default.conf
```

```nginx
upstream backend {
    server app:8282;
}

upstream frontend {
    server host.docker.internal:3000;
}

server {
    listen 80;
    server_name your-domain.com;

    # API routes → Backend
    location /api/ {
        proxy_pass http://backend/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # File upload size
        client_max_body_size 50M;
    }

    # All other routes → Frontend
    location / {
        proxy_pass http://frontend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Restart nginx:
```bash
docker compose restart nginx
```

---

## Step 9: Verify Deployment

### Test Backend
```bash
# Health check
curl http://YOUR_EC2_PUBLIC_IP/api/actuator/health

# Swagger UI
http://YOUR_EC2_PUBLIC_IP/api/swagger-ui.html
```

### Test Frontend
```bash
http://YOUR_EC2_PUBLIC_IP:3000
```

### Test S3 Upload (CSV Paper Upload)
1. Login to admin panel: `http://YOUR_EC2_PUBLIC_IP:3000/admin`
2. Go to Paper Management
3. Click "Upload CSV"
4. Upload a sample CSV paper
5. Verify CSV appears in S3 bucket: `s3://ensureu-assets-prod/uploads/csv-uploads/`

---

## Step 10: SSL/HTTPS Setup (Production)

### Using Let's Encrypt with Certbot

```bash
# Install Certbot
sudo apt-get install -y certbot python3-certbot-nginx

# Stop nginx temporarily
docker compose stop nginx

# Get certificate
sudo certbot certonly --standalone -d your-domain.com -d www.your-domain.com

# Copy certificates
sudo cp /etc/letsencrypt/live/your-domain.com/fullchain.pem ~/ensureu/nginx/ssl/
sudo cp /etc/letsencrypt/live/your-domain.com/privkey.pem ~/ensureu/nginx/ssl/

# Update nginx config for HTTPS
# Then restart
docker compose up -d nginx
```

---

## New Features Deployment Notes

### CSV Paper Upload
- CSV files are automatically uploaded to S3 before processing
- Path: `s3://{bucket}/uploads/csv-uploads/{timestamp}_{filename}.csv`
- Papers are saved as DRAFT status
- Admin can review and activate papers

### S3 Storage Structure
```
s3://ensureu-assets-prod/
├── uploads/
│   ├── csv-uploads/          # Uploaded CSV papers
│   ├── papers/               # Paper images
│   │   └── {category}/
│   │       └── {paperId}/
│   ├── question-bank/        # Question images
│   └── blog/                 # Blog images
```

### Role-Based Access
- **SUPERADMIN**: Full access (users, config, papers, CSV upload)
- **ADMIN**: Dashboard, paper management, CSV upload
- **TEACHER**: Paper editing, question management
- **USER**: Take tests only

---

## Common Operations

### View Logs
```bash
# All services
docker compose logs -f

# Backend only
docker compose logs -f app

# Frontend (if using PM2)
pm2 logs ensureu-frontend
```

### Restart Services
```bash
# Backend
docker compose restart app

# Frontend
pm2 restart ensureu-frontend
```

### Update Deployment
```bash
# Backend
cd ~/ensureu
git pull
./deploy.sh

# Frontend
cd ~/ensureu-next
git pull
npm run build
pm2 restart ensureu-frontend
```

### MongoDB Backup
```bash
# Backup
docker exec ensureu-mongodb mongodump --out=/tmp/backup --db=ensureu
docker cp ensureu-mongodb:/tmp/backup ~/backups/backup-$(date +%Y%m%d)

# Restore
docker cp ~/backups/backup-20240115 ensureu-mongodb:/tmp/restore
docker exec ensureu-mongodb mongorestore --db=ensureu /tmp/restore/ensureu
```

---

## Troubleshooting

### S3 Upload Fails
```bash
# Check S3 configuration
docker compose logs app | grep S3

# Verify credentials
aws s3 ls s3://ensureu-assets-prod/ --region ap-south-1
```

### CSV Upload Error
- Ensure CSV is properly formatted (see `/docs/SSC_PAPER_CSV_FORMAT.md`)
- Check for embedded newlines in question text
- All fields with commas must be quoted

### Frontend Not Loading
```bash
# Check if Next.js is running
pm2 status

# Check frontend logs
pm2 logs ensureu-frontend

# Verify API URL in .env.local
cat ~/ensureu-next/.env.local
```

### MongoDB Connection Issues
```bash
docker compose logs mongodb
docker exec -it ensureu-mongodb mongosh
```

---

## Cost Optimization

| Resource | Cost/Month |
|----------|-----------|
| EC2 t3.medium (On-Demand) | ~$30-35 |
| EC2 t3.medium (Reserved 1yr) | ~$20 |
| EBS 50 GB gp3 | ~$4 |
| S3 (10 GB) | ~$0.25 |
| Data Transfer (10 GB) | ~$1 |
| **Total** | **~$35-40** |

---

## Quick Reference

```bash
# Deploy backend
cd ~/ensureu && ./deploy.sh

# Deploy frontend
cd ~/ensureu-next && npm run build && pm2 restart ensureu-frontend

# View logs
docker compose logs -f app
pm2 logs ensureu-frontend

# Restart all
docker compose restart
pm2 restart all

# Check S3
aws s3 ls s3://ensureu-assets-prod/uploads/ --recursive

# MongoDB backup
docker exec ensureu-mongodb mongodump --out=/tmp/backup --db=ensureu
```

---

## Support

For issues:
1. Check logs: `docker compose logs -f` and `pm2 logs`
2. Verify S3 credentials and bucket permissions
3. Check security group allows required ports
4. Verify `.env` and `.env.local` configurations

Good luck with your deployment! 🚀
