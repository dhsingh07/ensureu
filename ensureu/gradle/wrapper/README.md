# EnsureU - Assessment Platform

Multi-module Java application for quiz/assessment management with JWT authentication, MongoDB storage, and multi-channel notifications.

## Quick Start

### Prerequisites
- Java 22 (for building) or Java 11+ (for running)
- Maven 3.9+
- MongoDB 4.0+

### Build and Run (Maven)

```bash
# Build entire project
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-22.jdk/Contents/Home
mvn clean install -DskipTests

# Run application
cd ensureu-service
mvn spring-boot:run
```

Application will start on `http://localhost:8282/api`

API Documentation: `http://localhost:8282/api/swagger-ui/index.html`

## Build Systems

This project supports both Maven and Gradle builds.

### Maven (Recommended)

Maven is the primary build tool with full support and testing.

**Build:**
```bash
mvn clean install -DskipTests
```

**Run:**
```bash
cd ensureu-service
mvn spring-boot:run
```

### Gradle (Alternative)

Gradle build files are provided. The wrapper requires Gradle 7.6 (compatibility issue with Gradle 9.0+).

**Setup Gradle wrapper (one-time):**

Install Gradle 7.6 from https://gradle.org/releases/ or use SDKMan:
```bash
sdk install gradle 7.6
sdk use gradle 7.6
gradle wrapper --gradle-version 7.6
```

**Build:**
```bash
./gradlew clean build -x test
```

**Run:**
```bash
./gradlew :ensureu-service:bootRun
```

## Project Structure

```
ensureu-parent/
├── pom.xml                    # Parent Maven POM
├── build.gradle               # Root Gradle build
├── ensureu-commons/           # Shared library
│   └── src/main/java/
│       └── com/ensureu/commons/
│           ├── notification/  # Email/SMS services
│           └── gcloud/        # Google Cloud Storage
└── ensureu-service/           # Main application
    └── src/main/java/
        └── com/book/ensureu/
            ├── admin/         # Admin APIs
            ├── api/           # User APIs
            └── security/      # JWT/OAuth2
```

## MongoDB Setup

```bash
# Start MongoDB
brew services start mongodb-community

# Create database
mongosh
> use assessu
> db.createUser({
    user: "appUser",
    pwd: "yourPassword",
    roles: [{ role: "readWrite", db: "assessu" }]
  })
```

Update connection in `application.properties`:
```properties
spring.data.mongodb.uri=mongodb://appUser:yourPassword@localhost:27017/assessu
```

## Technology Stack

- Java 11 (target), Java 22 (build)
- Spring Boot 2.6.0
- MongoDB (Spring Data)
- JWT + OAuth2 Security
- Maven/Gradle

For complete documentation, see [CLAUDE.md](./CLAUDE.md)
