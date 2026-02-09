# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

EnsureU Service is a Spring Boot 2.6.0 microservice for an assessment/quiz platform. It's built with Java 11 and uses MongoDB for data persistence. The application provides APIs for managing quiz papers, user assessments, subscriptions, and analytics.

## Build and Development Commands

**Build the project:**
```bash
mvn clean compile
```

**Run the application:**
```bash
mvn spring-boot:run
```

**Package the application:**
```bash
mvn clean package
```

**Run tests:**
```bash
mvn test
```

**Note:** This project currently has no test suite. When creating tests, use JUnit with Spring Boot Test starter.

The application runs on port 8282 with context path `/api`.

**API Documentation:** Available via SpringDoc OpenAPI at `/api/swagger-ui.html` when the application is running.

## Database Setup

**MongoDB Configuration:**
- Database name: `assessu`
- Default connection: `mongodb://localhost:27017/assessu`
- Enable authentication in MongoDB configuration
- Create application user with readWrite role for the `assessu` database

**Start MongoDB locally:**
```bash
# macOS with Homebrew
brew services start mongodb-community

# Or manually with auth
mongod --dbpath ${dbpath} --auth
```

**Create application user:**
```javascript
use assessu
db.createUser({
  user: "appUser",
  pwd: "appPassword123",
  roles: [{ role: "readWrite", db: "assessu" }]
})
```

Update `application.properties` with credentials:
```properties
spring.data.mongodb.uri=mongodb://appUser:appPassword123@localhost:27017/assessu
```

## Architecture

**Package Structure:**
- `com.book.ensureu` - Main application package
- `admin/` - Administrative functionality with separate API controllers and services
  - `admin/api/` - Admin REST endpoints (paper upload, collections management)
  - `admin/service/` - Admin business logic
  - `admin/model/` - Admin-specific domain entities
- `api/` - User-facing REST API controllers
- `service/` - Business logic layer
  - `service/impl/` - Service implementations
  - `service/provider/` - OAuth provider integrations
  - `service/audit/` - Audit trail services
- `repository/` - MongoDB repositories (Spring Data)
- `model/` - Core domain entities (User, Paper, Question, Subscription, etc.)
- `dto/` - Data transfer objects
- `flow/analytics/` - Analytics module (separate bounded context)
  - User growth tracking, percentile calculations, time series data
- `security/` - JWT authentication, filters, and user principal services
- `configuration/` - Spring configuration (MongoDB, Swagger, WebSecurity)
- `aop/` - AOP aspects for cross-cutting concerns
- `advice/` - Global exception handlers
- `transformer/` - Model-to-DTO transformers

**Key Architectural Patterns:**
- Layered architecture (API → Service → Repository → Database)
- Domain-driven design with separate admin and analytics bounded contexts
- MongoDB repository pattern with Spring Data (no JPA)
- JWT-based authentication with OAuth2 integration (Google, Facebook)
- AOP for cross-cutting concerns:
  - `UserAuditLoginAspect` - Track user login audit trails
  - `ServiceCallLimitAspect` - Rate limiting and API usage tracking
  - `PurchaseSubscriptionAspect` - Transaction logging for purchases
- Global exception handling via `@ControllerAdvice`

**Dependencies:**
- Spring Boot 2.6.0 with Web, MongoDB, Security, AOP, Mail, Actuator
- MongoDB with Spring Data (no SQL/JPA)
- JWT authentication (jjwt 0.9.0) with OAuth2
- Lombok 1.18.34 for boilerplate reduction (requires annotation processing)
- SpringDoc OpenAPI 1.6.14 for API documentation
- Jackson 2.15.3 for JSON processing
- Jasypt 1.9.2 for encryption/decryption
- OpenCSV 4.1 for CSV file handling
- **Custom `ensureu-commons` module** (version 0.0.1-SNAPSHOT) - Required dependency for shared utilities and Google Cloud integration

**Important Build Configuration:**
- Java 11 source/target with explicit release flag
- Lombok annotation processor must be configured in maven-compiler-plugin
- Spring Boot DevTools enabled for hot reload during development

**Configuration:**
- Main config in `application.properties`
- JWT settings with custom secret (`spring.jwt.secret`) and 1-hour expiration
- MongoDB connection with optional authentication
- Email integration via Gmail SMTP
- File upload limits (10MB max)
- OAuth token validation URLs for Google and Facebook
- File storage paths:
  - CSV files: `spring.file.csv` (Windows: `C://eutest//csv/`, Linux: `/var/ensureu/csv/`)
  - Free CSV files: `spring.free.file.csv`
  - Images: `spring.static.image`
- Google Cloud Storage integration for content (bucket path in comments)
- AES encryption configured with salt, IV, and passphrase for sensitive data

**API Structure:**
- Admin APIs (under `/admin/*`) - Require admin authentication
  - Paper upload and management
  - Question collections
  - Subscription management
  - Test series collections
- User APIs (under `/api/*`) - User-facing endpoints
  - Authentication and OTP
  - Paper browsing and attempts
  - User subscriptions and enrollments
  - Blog and courses

The application follows Spring Boot conventions with clear separation between admin functionality, core user features, and analytics capabilities. All entities use MongoDB's document model with `@Document` annotations rather than JPA.