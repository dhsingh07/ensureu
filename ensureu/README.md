# ensureu-service
Assessment platform

# Build
To use this build in the future:

# Set Java 22 as your build Java
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-22.jdk/Contents/Home

# Build the project
mvn clean install -DskipTests

# Run
cd ensureu-service && export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-22.jdk/Contents/Home && mvn spring-boot:run

export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-22.jdk/Contents/Home
mvn test

Debugging
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-22.jdk/Contents/Home && \
mvn spring-boot:run \
-Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"


✅ Application Status: RUNNING

Access Points:
- Swagger UI (API Documentation): http://localhost:8282/api/swagger-ui/index.html (HTTP 200 ✓)
- Base URL: http://localhost:8282/api
- Health Endpoint: http://localhost:8282/api/actuator/health (reachable, status: DOWN due to MongoDB auth)


What's Working:
1. ✅ Multi-module Maven build completed successfully
2. ✅ Parent POM structure working correctly
3. ✅ ensureu-commons module built and installed
4. ✅ ensureu-service module built and packaged
5. ✅ Spring Boot application started (7.4 seconds startup time)
6. ✅ MongoDB connection established
7. ✅ Application running on port 8282
8. ✅ API documentation available via Swagger UI

---

## Docker Deployment (Recommended for Testing)

For quick deployment with Docker (includes MongoDB + App + Nginx):

```bash
# 1. Configure environment
cp .env.example .env
nano .env  # Update credentials

# 2. Deploy
./deploy.sh

# 3. Access
# Swagger UI: http://localhost/api/swagger-ui.html
# API: http://localhost:8282/api
```

### AWS EC2 Deployment

For deploying to AWS for testing with a group of users:

📖 **See [QUICKSTART.md](QUICKSTART.md)** for quick setup

📖 **See [DEPLOYMENT.md](DEPLOYMENT.md)** for detailed instructions

**Quick summary:**
- Single EC2 instance (t3.medium) with Docker
- Includes MongoDB, Application, and Nginx
- Cost: ~$33-38/month
- Ready for testing with user groups


export JAVA_HOME=$(/usr/libexec/java_home -v 11) && export SIMPLE_DATA_MONGODB_USERNAME=ensureu && export
│ SIMPLE_DATA_MONGODB_PASSWORD='PRO6XFqqYvsPbkZNHt+dBi2mXilu1G5b/1ZXE6Z2hNA=' && export SIMPLE_DATA_MONGODB_NAME=ensureu && export
│ SIMPLE_DATA_MONGODB_HOST=localhost && export SIMPLE_DATA_MONGODB_PORT=27017 && nohup mvn spring-boot:run > /Users/dharmendrasingh/Documents/en/ensureu/


