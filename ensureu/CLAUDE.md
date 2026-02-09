# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

EnsureU is an assessment/quiz platform built as a multi-module Java project. It consists of:
- **ensureu-service**: Main Spring Boot 2.6.0 application providing REST APIs for quiz/assessment management
- **ensureu-commons**: Shared library module providing notification services (email/SMS), Google Cloud Storage utilities, and common data models

The platform supports user authentication via JWT and OAuth2 (Google/Facebook), test paper management, subscriptions, and analytics.

## Build Commands

This is a multi-module project supporting both **Maven** and **Gradle** builds. Maven is the primary build tool with full support.

### Maven Build (Primary)

**Build entire project from root:**
```bash
# Use Java 22 for building (Lombok compatibility)
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-22.jdk/Contents/Home
mvn clean install -DskipTests
```

**Build and run the application:**
```bash
# From root directory
mvn clean install -DskipTests
cd ensureu-service
mvn spring-boot:run
```

**Run tests:**
```bash
mvn test
```

**Build individual modules (if needed):**
```bash
# Build commons first (it has no dependencies)
cd ensureu-commons
mvn clean install

# Then build service (depends on commons)
cd ../ensureu-service
mvn clean package
```

### Gradle Build (Alternative)

Gradle build files are provided as an alternative to Maven.

**Build with Gradle:**
```bash
# Generate wrapper first (if not present)
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-22.jdk/Contents/Home
gradle wrapper --gradle-version 7.6

# Build project
export JAVA_HOME=/opt/homebrew/Cellar/openjdk@11/11.0.28/libexec/openjdk.jdk/Contents/Home
./gradlew clean build -x test
```

**Run with Gradle:**
```bash
./gradlew :ensureu-service:bootRun
```

**Note:** Gradle wrapper generation requires Java 17+ but the wrapper can then be used with Java 11 for builds.

### Important Build Notes

**Lombok + Java Compatibility:**
- **Recommended**: Use Java 22 (or JDK 17+) for Maven builds with Lombok 1.18.32
- Java 11 (Homebrew version) has known compatibility issues with Lombok
- If you encounter Lombok errors, switch to Oracle JDK 11, AdoptOpenJDK 11, or use Java 22

The application runs on **port 8282** with context path **/api**.

## Database Setup

**MongoDB Configuration:**
- Database name: `assessu`
- Default connection: `mongodb://localhost:27017/assessu`
- Configuration in: `ensureu-service/src/main/resources/application.properties`

**Start MongoDB:**
```bash
# macOS with Homebrew
brew services start mongodb-community

# Manual start
mongod --dbpath /path/to/data
```

**Create database and user:**
```javascript
use assessu
db.createUser({
  user: "appUser",
  pwd: "yourPassword",
  roles: [{ role: "readWrite", db: "assessu" }]
})
```

Update connection string in application.properties:
```properties
spring.data.mongodb.uri=mongodb://appUser:yourPassword@localhost:27017/assessu
```

## Project Architecture

This is a **multi-module Maven project** with distinct responsibilities:

### Module: ensureu-commons
Shared library providing:
- **Notification services** - Multi-channel communication framework supporting email (via SMTP and ClickSend) and SMS (via ClickSend)
- **Google Cloud Storage utilities** - File upload/download from GCS buckets
- **Strategy pattern implementation** - `CommunicationChanelStrategy` for pluggable notification providers
- **Common data models** - `Notification`, `Message`, email/SMS DTOs

Package structure:
- `com.ensureu.commons.notification.service` - Service interfaces and abstract classes
- `com.ensureu.commons.notification.service.impl` - Concrete implementations (ClickSend, Email, SMS, Push)
- `com.ensureu.commons.notification.util` - Email and provider utilities
- `com.ensureu.commons.gcloud.util` - Google Cloud Storage integration
- `com.ensureu.commons.constant` - Enums (NotificationType, EmailType, SmsType, PlatformType)

This module has no pom.xml at root (uses minimal Maven structure) and is referenced as a dependency in ensureu-service.

### Module: ensureu-service
Main Spring Boot application with layered architecture:

**Package Structure:**
- `com.book.ensureu` - Root application package
- `admin/` - Administrative bounded context
  - `admin/api/` - Admin REST endpoints for paper upload, collection management
  - `admin/service/` - Admin business logic
  - `admin/model/` - Admin-specific domain entities
- `api/` - User-facing REST controllers
- `service/` - Business logic layer
  - `service/impl/` - Service implementations
  - `service/provider/` - OAuth provider integrations (Google, Facebook)
  - `service/audit/` - Audit trail services
- `repository/` - Spring Data MongoDB repositories
- `model/` - Core domain entities (User, Paper, Question, Subscription, etc.)
- `dto/` - Data transfer objects
- `flow/analytics/` - Analytics module (user growth, percentile calculations, time series)
- `security/` - JWT authentication, filters, user principal services
- `configuration/` - Spring configuration (MongoDB, Swagger, WebSecurity)
- `aop/` - Cross-cutting concerns via aspects
- `advice/` - Global exception handlers
- `transformer/` - Model-to-DTO transformers
- `util/` - Utilities (encryption, date handling, CSV conversion, image upload)

**Key Architectural Patterns:**
- Layered architecture: API → Service → Repository → MongoDB
- Domain-driven design with separate bounded contexts (admin, analytics)
- MongoDB document model (Spring Data MongoDB, **not JPA**)
- JWT + OAuth2 authentication
- Strategy pattern for communication channels (from commons module)
- AOP for cross-cutting concerns:
  - `UserAuditLoginAspect` - User login audit trails
  - `ServiceCallLimitAspect` - API rate limiting and usage tracking
  - `PurchaseSubscriptionAspect` - Purchase transaction logging

**Component Scanning:**
The Application class scans both `com.book.ensureu` and `com.ensureu.commons.gcloud` packages to include Google Cloud utilities from the commons module.

## Technology Stack

**Core:**
- Java 11 (target), Java 22 recommended for builds
- Spring Boot 2.6.0 (Web, MongoDB, Security, AOP, Mail, Actuator)
- Spring Data MongoDB (not JPA/Hibernate)
- Maven 3.9+ (primary build tool)
- Gradle 7.6 (alternative build tool)

**Authentication & Security:**
- JWT (jjwt 0.9.0) with 1-hour token expiration
- Spring Security OAuth2
- OAuth providers: Google, Facebook
- Jasypt 1.9.2 for encryption

**Documentation:**
- SpringDoc OpenAPI 1.6.14
- API docs available at `/api/swagger-ui.html`

**Other:**
- Lombok 1.18.34 (requires annotation processor configuration)
- Jackson 2.15.3
- OpenCSV 4.1
- MySQL connector (runtime)
- Spring DevTools (development hot reload)

## Configuration Notes

**Important Build Settings (pom.xml):**
- Java 11 source/target with explicit `<release>11</release>`
- Lombok annotation processor must be configured in maven-compiler-plugin
- ensureu-commons is a required dependency (version 0.0.1-SNAPSHOT)

**Application Properties:**
Located in `ensureu-service/src/main/resources/application.properties`:
- MongoDB connection string and credentials
- JWT secret and expiration
- Email SMTP configuration (Gmail)
- OAuth token validation URLs
- File upload limits (10MB max)
- CSV and image file paths (platform-specific: Windows/Linux)
- AES encryption settings (salt, IV, passphrase)
- ClickSend notification API credentials
- Google Cloud Storage bucket configuration

**Security Note:**
The application.properties contains sensitive credentials. Ensure this file is never committed or use environment-specific configuration with externalized secrets.

## API Structure

**Admin APIs** (require admin role):
- Paper upload and management
- Question collection management
- Subscription configuration
- Test series collections

**User APIs:**
- Authentication (JWT, OAuth, OTP)
- Paper browsing and attempts
- User subscriptions and enrollments
- Analytics and progress tracking
- Blog and courses

**OAuth Flow:**
Token validation endpoints configured for Google and Facebook. The `service/provider/` package contains provider-specific implementations.

## Development Notes

**Hot Reload:**
Spring DevTools is enabled. Changes to Java files will trigger automatic restart during development.

**Circular References:**
The application allows circular bean references (`spring.main.allow-circular-references=true`). This should be refactored for cleaner dependency management.

**File Storage:**
- Local file paths for CSV/images configured in application.properties
- Google Cloud Storage integration via commons module for production content
- Bucket URL pattern: `https://storage.googleapis.com/ensureu-content/{category}/{imageId}`

**MongoDB Document Model:**
All entities use `@Document` annotations (not JPA `@Entity`). Repositories extend Spring Data MongoDB interfaces, not JPA repositories.