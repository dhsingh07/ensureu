# Repository Guidelines

## Project Structure & Module Organization
- Multi-module Java workspace with `ensureu-commons` (shared utilities/integrations) and `ensureu-service` (Spring Boot REST API).
- Java sources: `ensureu-commons/src/main/java` and `ensureu-service/src/main/java`.
- Resources: `*/src/main/resources`, with API assets/data in `ensureu-service/src/main/resources/static`, `csv`, and `data`.
- Tests: `ensureu-commons/src/test/java` and `ensureu-service/src/test/java`.

## Build, Test, and Development Commands
- `mvn clean install -DskipTests` (repo root): build all modules.
- `mvn test`: run the full test suite.
- `cd ensureu-service && mvn spring-boot:run`: start the API (default port 8282, base path `/api`).
- Docker for local testing: `cp .env.example .env` then `./deploy.sh` (see `QUICKSTART.md`).
- Gradle files exist; use `gradle build` only if you have Gradle installed (no wrapper script in repo).

## Coding Style & Naming Conventions
- Java 4-space indentation; `UpperCamelCase` classes, `lowerCamelCase` methods/fields, and lowercase packages (e.g., `com.ensureu`).
- Keep Spring layers in established packages (e.g., `api`, `service`, `repository`, `model`).
- Use Lombok consistently with surrounding code.
- Project targets Java 11 in `pom.xml`/`build.gradle`; align your JDK accordingly.

## Testing Guidelines
- Tests run on JUnit 5 via `spring-boot-starter-test`.
- Name tests with a `*Test` suffix and keep them module-local.
- Prefer module-scoped runs when iterating: `cd ensureu-service && mvn test`.

## Commit & Pull Request Guidelines
- Commit messages are short, lowercase summaries (e.g., "update claude md"). Keep them scoped and imperative.
- PRs should include: a concise summary, affected module(s), and test commands run.
- Add screenshots only for API response changes or UI/static asset updates.

## Configuration & Security Notes
- Copy `.env.example` to `.env` for Docker deployments; do not commit real secrets.
- App config lives in `ensureu-service/src/main/resources/application.properties`; use environment overrides for local credentials.
