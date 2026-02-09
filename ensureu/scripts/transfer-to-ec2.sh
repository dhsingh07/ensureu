#!/bin/bash
# Script to transfer project to EC2 instance
# Usage: ./scripts/transfer-to-ec2.sh your-key.pem ec2-user@YOUR_EC2_IP

set -e

# Check arguments
if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <path-to-key.pem> <user@ec2-ip>"
    echo "Example: $0 ~/.ssh/my-key.pem ubuntu@54.123.45.67"
    exit 1
fi

KEY_FILE=$1
EC2_HOST=$2

echo "=========================================="
echo "Transferring EnsureU to EC2"
echo "=========================================="

# Verify key file exists
if [ ! -f "$KEY_FILE" ]; then
    echo "Error: Key file not found: $KEY_FILE"
    exit 1
fi

# Set correct permissions on key
chmod 400 "$KEY_FILE"

echo "Creating project directory on EC2..."
ssh -i "$KEY_FILE" "$EC2_HOST" "mkdir -p ~/ensureu"

echo "Transferring project files..."

# Transfer only necessary files
rsync -avz \
    --progress \
    -e "ssh -i $KEY_FILE" \
    --exclude '.git' \
    --exclude 'target/' \
    --exclude '.gradle/' \
    --exclude 'build/' \
    --exclude '.idea/' \
    --exclude '*.iml' \
    --exclude '.DS_Store' \
    --exclude '*.log' \
    --exclude '.env' \
    --exclude 'personal.local' \
    ./ "$EC2_HOST:~/ensureu/"

echo ""
echo "=========================================="
echo "Transfer completed!"
echo "=========================================="
echo ""
echo "Next steps:"
echo "1. SSH to EC2: ssh -i $KEY_FILE $EC2_HOST"
echo "2. Go to project: cd ~/ensureu"
echo "3. Copy environment: cp .env.example .env"
echo "4. Edit credentials: nano .env"
echo "5. Deploy: ./deploy.sh"
echo ""
