# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**base-sysadmin** is a Spring Boot-based system administration platform built on the Opensabre framework. It provides foundational system services for auditing, captcha generation/validation, and notifications. The application runs on port 8020 by default (configurable via `SERVER_PORT` environment variable).

**Tech Stack:**
- Spring Boot 3.x (Java 17)
- Maven build system
- MyBatis-Plus for database access (table prefix: `base_sys_`)
- Redis for caching and captcha storage
- Opensabre framework dependencies

## Common Commands

### Build and Run
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

# Package for deployment
mvn clean package

# Build Docker image using Jib
mvn compile jib:dockerBuild
```

### Testing
```bash
# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=ClassName

# Run tests with H2 in-memory database (configured in pom.xml)
```

## Architecture

The project follows a **modular architecture** with three core modules:

### 1. Audit Module (`io.github.opensabre.sysadmin.audit`)
- **Purpose**: Comprehensive logging and tracking of system operations
- **Pattern**: Standard MVC with MyBatis-Plus
- **Components**: `AuditLogController` → `IAuditLogService` → `AuditLogMapper`
- **Table**: `base_sys_audit_log`

### 2. Captcha Module (`io.github.opensabre.sysadmin.captcha`)
- **Purpose**: Highly extensible security verification system supporting Image, SMS, and Email captcha types
- **Pattern**: Strategy pattern with abstract base class (`CaptchaService`)
- **Key Features**:
  - Multi-dimensional rate limiting (IP, device, business level)
  - Redis-based storage with TTL
  - Business scenario support (login, registration, etc.)
  - Configurable expiration, attempt limits, and minimum intervals
- **Key Interfaces**:
  - `ICaptchaGenerator` - Captcha generation logic
  - `ICaptchaStorageService` - Storage abstraction (Redis implementation)
  - `IRateLimitService` - Security and rate limiting
- **Services**: Extend `CaptchaService` and implement `beforeGenerateCaptcha()`, `afterGenerateCaptcha()`, and `customValidateCaptcha()` hooks

### 3. Notification Module (`io.github.opensabre.sysadmin.notification`)
- **Purpose**: Universal messaging system for multiple channels (SMS, Email, WeChat)
- **Pattern**: Provider pattern with service manager
- **Key Components**:
  - `NotificationServiceManager` - Manages and retrieves notification services by enum
  - `INotificationService` interface - Universal notification contract
  - `ISmsProvider` interface - SMS provider abstraction
  - `SimulateSmsProvider` - Default simulation implementation
- **Enums**:
  - `NotificationType` - SMS, EMAIL, WECHAT
  - `NotificationTemplate` - CAPTCHA and other templates

## Key Design Patterns

### Interface-Based Programming
Extensive use of interfaces throughout the codebase for loose coupling and extensibility. When adding new functionality, implement the appropriate interface rather than modifying existing code.

### Strategy Pattern (Captcha Module)
- `CaptchaService` is an abstract base class
- Concrete implementations (`ImageCaptchaService`, `SmsCaptchaService`, `EmailCaptchaService`) extend it
- Each captcha type has its own `ICaptchaGenerator` implementation

### Provider Pattern (Notification Module)
- `NotificationServiceManager` uses enums to retrieve services
- Services use provider interfaces (`ISmsProvider`) to abstract third-party implementations
- Easy to add new notification channels or switch providers

### Service Manager Pattern
`NotificationServiceManager` retrieves notification services by type using enum values, providing a clean alternative to string-based service lookup.

## Configuration

### Database
- MySQL with MyBatis-Plus
- Table prefix: `base_sys_`
- Migration scripts in `src/main/resources/db/`

### Redis
- Used for captcha storage (`RedisICaptchaStorage`) and rate limiting
- Required for captcha functionality

### Application Configuration
- `bootstrap.yml` - Server port and application name
- `application.yml` - Main application configuration
- Captcha security settings: rate limits, attempt counts, expiration times

## Code Organization

```
io.github.opensabre.sysadmin
├── SysadminApplication.java          # Main application class
├── audit/                             # Audit module
│   ├── dao/                          # Data Access Objects (MyBatis-Plus)
│   ├── model/                        # Form, Param, PO objects
│   ├── rest/                         # REST controllers
│   └── service/                      # Service implementations
├── captcha/                           # Captcha module
│   ├── config/                       # Configuration classes
│   ├── enums/                        # BusinessScenario, CaptchaType
│   ├── model/                        # CaptchaInfo, ClientInfo, CaptchaVo
│   ├── rest/                         # CaptchaController
│   ├── service/                      # Service layer with interfaces
│   └── utils/                        # Utility classes
└── notification/                      # Notification module
    ├── config/                       # Configuration
    ├── enums/                        # NotificationType, NotificationTemplate
    ├── model/                        # Data models
    ├── provider/                     # ISmsProvider implementations
    ├── rest/                         # REST controllers
    └── service/                      # INotificationService implementations
```

## Extending the System

### Adding a New Captcha Type
1. Add type to `CaptchaType` enum
2. Implement `ICaptchaGenerator` interface with `@Component`
3. Create service extending `CaptchaService` with the generator as dependency
4. Implement hook methods: `beforeGenerateCaptcha()`, `afterGenerateCaptcha()`, `customValidateCaptcha()`

### Adding a New Notification Channel
1. Add type to `NotificationType` enum
2. Implement `INotificationService` interface with `@Service`
3. Implement corresponding provider interface (e.g., `IEmailProvider`)
4. `NotificationServiceManager` will automatically discover and manage the service

### Adding a New Notification Template
1. Add template to `NotificationTemplate` enum with code, name, content pattern, type, and description

## Important Notes

- **Maven Compiler Plugin**: Configured with `<parameters>true</parameters>` allowing `@RequestParam` annotations without explicit `value` parameter
- **Git Commit ID Plugin**: Generates git properties during build for version tracking
- **Database Prefix**: All tables use `base_sys_` prefix
- **Redis Required**: Captcha module requires Redis for storage and rate limiting
- **Rate Limiting**: Captcha module enforces IP, device, and business-level rate limits configured per `BusinessScenario`
- **Template Parameters**: Notification templates support dynamic parameter replacement using Map or varargs
