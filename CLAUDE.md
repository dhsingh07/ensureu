# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

EnsureU is an assessment/quiz platform monorepo with two main components:
- **ensureu/** - Java Spring Boot 2.6.0 backend (multi-module Maven project)
- **ensureu-ui/** - Angular 6.1.0 frontend (package.json names it `assessu-ui`)

MongoDB is the database. Authentication uses JWT + OAuth2 (Google, Facebook).

## Build Commands

### Backend (Java/Maven)

```bash
# Set Java 22 for builds (Lombok 1.18.32 compatibility — Homebrew Java 11 has issues)
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-22.jdk/Contents/Home

# Build all modules from ensureu/ root
cd ensureu
mvn clean install -DskipTests

# Run the application (port 8282, context /api)
cd ensureu-service && mvn spring-boot:run

# Run all tests
mvn test

# Run a single test class
cd ensureu-service && mvn test -Dtest=SomeClassTest

# Run with debugger on port 5005
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
```

Build order matters: `ensureu-commons` must build before `ensureu-service` (parent POM handles this).

### Frontend (Angular 6)

```bash
cd ensureu-ui
npm install

# Required with Node.js 17+ (OpenSSL 3 breaks old webpack)
export NODE_OPTIONS=--openssl-legacy-provider

ng serve              # Dev server on port 4200
ng build --prod       # Production build (output: dist/assessu-ui)
ng test               # Unit tests (Karma/Jasmine)
ng e2e                # E2E tests (Protractor)
ng lint               # Lint
```

Three build configurations exist in angular.json: development, staging, production.

Set `TEST_MODE = true` in `ensureu-ui/src/app/utils/constants/main.ts` to use local mock data (bypasses API calls in many services).

### Docker (Full Stack: MongoDB + App + Nginx)

```bash
cd ensureu
cp .env.example .env   # Then edit .env with real credentials
./deploy.sh
```

Docker Compose runs three services: `mongodb` (port 27017), `app` (port 8282), `nginx` (ports 80/443). The Dockerfile uses a multi-stage build: Maven 3.9 + Java 22 for compilation, Eclipse Temurin JRE 11 for runtime.

## Architecture

### Backend Modules

**ensureu-commons** (`com.ensureu.commons`):
- `notification/` - Multi-channel notification framework (email via SMTP, SMS via ClickSend) using strategy pattern (`CommunicationChanelStrategy`)
- `gcloud/` - Google Cloud Storage file upload/download
- `constant/` - Shared enums (NotificationType, EmailType, SmsType)

**ensureu-service** (`com.book.ensureu`):
- `admin/` - Admin bounded context with its own api/, service/, model/, dao/
- `api/` - User-facing REST controllers
- `service/impl/` - Business logic implementations
- `service/provider/` - OAuth provider integrations (Google, Facebook)
- `repository/` - Spring Data MongoDB repositories
- `model/` - Domain entities (`@Document`, not `@Entity`)
- `dto/` and `transformer/` - DTOs and model-to-DTO mappers
- `flow/analytics/` - Analytics bounded context (user growth, percentiles, time series)
- `security/` - JWT authentication and filters
- `aop/` - Cross-cutting aspects: `UserAuditLoginAspect`, `ServiceCallLimitAspect`, `PurchaseSubscriptionAspect`
- `configuration/` - Spring config (MongoDB, Swagger, WebSecurity)
- `advice/` - Global exception handlers

Note: Component scanning includes both `com.book.ensureu` and `com.ensureu.commons.gcloud`.

### Key Patterns

- **Layered architecture**: API → Service → Repository → MongoDB
- **Spring Data MongoDB** (not JPA) - all entities use `@Document`
- **Strategy pattern** for pluggable notification channels in commons
- **AOP** for rate limiting, audit trails, and transaction logging
- **Separate bounded contexts**: admin and analytics modules have their own layered structure

### Frontend Structure (ensureu-ui/src/app/)

Key feature modules: `admin/`, `exam/`, `practice/`, `dashboard/`, `test-paper-editor/`, `home/`, `profile/`, `user-progress/`, `blog/`, `previous-papers/`.

Shared code: `services/` (API clients), `shared/` (components), `utils/` (constants, helpers).

Environments: `environment.ts` (dev), `environment.staging.ts`, `environment.prod.ts`.

## Configuration

**Application properties** (`ensureu/ensureu-service/src/main/resources/application.properties`):
- Server: port 8282, context path `/api`
- MongoDB URI with auth (database name varies: `ensureu` in properties, `assessu` in Docker init script)
- JWT: secret `mySecret`, 1-hour expiration
- Email: Gmail SMTP (smtp.gmail.com:587)
- File upload limit: 10MB
- OAuth token validation URLs for Google and Facebook

**API endpoints**:
- Swagger UI: `http://localhost:8282/api/swagger-ui/index.html`
- Health: `http://localhost:8282/api/actuator/health`
- Admin APIs: `/admin/*` (require admin role)

## Development Notes

- **Circular references** are allowed via `spring.main.allow-circular-references=true`
- **Hot reload**: Spring DevTools enabled for backend
- **MongoDB auth**: Health endpoint shows DOWN if MongoDB auth is not configured locally
- **File paths**: application.properties has Windows-style paths (`C://eutest//`) that need overriding on macOS/Linux

## Coding Standards

- Java: 4-space indentation, `UpperCamelCase` classes, `lowerCamelCase` methods/fields
- Use Lombok consistently with surrounding code
- Keep Spring layers in established packages (api, service, repository, model)
- Tests: JUnit 5, name with `*Test` suffix, prefer module-scoped runs (`cd ensureu-service && mvn test`)
- Commits: Short, lowercase, imperative summaries