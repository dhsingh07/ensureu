# EnsureU Notification Service

A product of **GrayscaleLabs AI Pvt Ltd.**

## Overview

Notification microservice for the EnsureU assessment platform. Handles Email, SMS, and Push notifications.

## Tech Stack

- Java 17+
- Spring Boot 3.2.5
- MongoDB
- Gradle

## Build & Run

```bash
# Build
./gradlew build -x test

# Run
./gradlew bootRun

# Or run JAR
java -jar build/libs/notification-service.jar
```

## Configuration

Set environment variables or update `application.properties`:

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Server port | 8383 |
| `MONGODB_URI` | MongoDB connection URI | mongodb://localhost:27017/ensureu |
| `MSG91_AUTH_KEY` | MSG91 SMS API key | - |
| `MAIL_USERNAME` | SMTP username | - |
| `MAIL_PASSWORD` | SMTP password | - |

## API Endpoints

### Send Notification

```
POST /notification/api/notifications
```

**Request:**
```json
{
  "channel": "EMAIL",
  "recipient": "user@example.com",
  "subject": "Welcome",
  "message": "Hello from EnsureU!"
}
```

**Channels:** `EMAIL`, `SMS`, `PUSH`

## License

Copyright (c) 2024 GrayscaleLabs AI Pvt Ltd. All rights reserved.
