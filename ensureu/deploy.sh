#!/bin/bash
# EnsureU Deployment Script for AWS EC2
# This script handles initial setup and updates

set -e

echo "=========================================="
echo "EnsureU Deployment Script"
echo "=========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if .env file exists
if [ ! -f .env ]; then
    print_warning ".env file not found. Creating from .env.example..."
    cp .env.example .env
    print_warning "Please edit .env file with your actual credentials before proceeding!"
    print_warning "Run: nano .env"
    exit 1
fi

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    print_error "Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
    print_error "Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

# Determine docker compose command
if command -v docker-compose &> /dev/null; then
    DOCKER_COMPOSE="docker-compose"
else
    DOCKER_COMPOSE="docker compose"
fi

# Pull latest code (if this is a git repo)
if [ -d .git ]; then
    print_status "Pulling latest code from git..."
    git pull || print_warning "Git pull failed or no changes"
fi

# Stop existing containers
print_status "Stopping existing containers..."
$DOCKER_COMPOSE down || true

# Build the application image
print_status "Building Docker images..."
$DOCKER_COMPOSE build --no-cache app

# Start the services
print_status "Starting services..."
$DOCKER_COMPOSE up -d

# Wait for services to be healthy
print_status "Waiting for services to start..."
sleep 10

# Check service health
print_status "Checking service health..."
$DOCKER_COMPOSE ps

# Show logs
print_status "Showing recent logs..."
$DOCKER_COMPOSE logs --tail=50

echo ""
echo "=========================================="
print_status "Deployment completed!"
echo "=========================================="
echo ""
echo "Service URLs:"
echo "  - Application: http://localhost:8282/api"
echo "  - Swagger UI: http://localhost:8282/api/swagger-ui.html"
echo "  - Health Check: http://localhost:8282/api/actuator/health"
echo "  - Nginx (port 80): http://localhost"
echo ""
echo "Useful commands:"
echo "  - View logs: $DOCKER_COMPOSE logs -f"
echo "  - View app logs: $DOCKER_COMPOSE logs -f app"
echo "  - Stop services: $DOCKER_COMPOSE down"
echo "  - Restart services: $DOCKER_COMPOSE restart"
echo ""
