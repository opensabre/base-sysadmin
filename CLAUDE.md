# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**base-sysadmin** is a Spring Boot-based system administration platform built on the Opensabre framework. It provides foundational system services for auditing, captcha generation/validation, and notifications. The application runs on port 8020 by default (configurable via `SERVER_PORT` environment variable).

**Tech Stack:**
- Spring Boot 3.x (Java 17)
- Maven build system
- MyBatis-Plus for database access (table prefix: `base_sys_`)
- Redis for caching, captcha storage, and rate limiting
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

# Build Docker image to local Docker daemon
mvn jib:dockerBuild

# Build Docker image to remote registry
mvn jib:build
```

### Testing
```bash
# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=ClassName

# Run tests with H2 in-memory database (configured in pom.xml)
```

**Note**: Tests use H2 in-memory database. The test database configuration is in `pom.xml` dependencies.

## Architecture

The project follows a **modular architecture** with four core modules:

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

### 4. Rate Limit Module (`io.github.opensabre.sysadmin.ratelimit`)
- **Purpose**: Comprehensive rate limiting system supporting multiple dimensions and algorithms
- **Pattern**: Aspect-Oriented Programming (AOP) with Strategy pattern for algorithms
- **Key Features**:
  - Multi-dimensional rate limiting (IP, Device, Business)
  - Configurable algorithms (COUNTER currently, extensible for others)
  - Redis-based storage with TTL
  - SpEL expression support for business keys
  - Annotation-based and programmatic API
- **Key Components**:
  - `@RateLimit` annotation - Declarative rate limiting on methods
  - `@EnableRateLimit` - Enable module (add to main application class)
  - `IRateLimitService` - Programmatic API for custom logic
  - `RateLimitAspect` - AOP interceptor for annotation processing
  - Dimension extractors: `IpDimensionExtractor`, `DeviceDimensionExtractor`, `BusinessDimensionExtractor`
- **Enums**:
  - `RateLimitDimension` - IP, DEVICE, BUSINESS
  - `RateLimitAlgorithmType` - COUNTER (extensible)
- **Usage**:
  ```java
  @EnableRateLimit  // Add to main application class
  @RateLimit(
      algorithm = RateLimitAlgorithmType.COUNTER,
      dimensions = {RateLimitDimension.IP, RateLimitDimension.DEVICE},
      maxCount = 5,
      period = 60,
      message = "访问过于频繁，请1分钟后再试"
  )
  public Result<String> login(@RequestBody LoginForm form) { }
  ```
- **Configuration**: See `ratelimit-example.yml` for reference

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

### Environment Variables
Key environment variables for configuration:
- `SERVER_PORT` - Application port (default: 8020)
- `REDIS_HOST`, `REDIS_PORT` - Redis connection
- `DATASOURCE_HOST`, `DATASOURCE_PORT`, `DATASOURCE_USERNAME`, `DATASOURCE_PASSWORD` - Database connection
- `DATASOURCE_DBTYPE` - Database type (default: mysql)

### Database
- MySQL with MyBatis-Plus
- Table prefix: `base_sys_`
- Migration scripts in `src/main/resources/db/`

### Redis
- **Required** for captcha storage (`RedisICaptchaStorage`) and rate limiting
- Used for both captcha module and rate limit module
- Configured via `spring.data.redis` in `application.yml`

### Application Configuration
- `bootstrap.yml` - Server port and application name
- `application.yml` - Main application configuration including datasource, Redis, and captcha security settings

## Code Organization

```
io.github.opensabre.sysadmin
├── SysadminApplication.java          # Main application class
├── common/                            # Common utilities
│   └── utils/                        # HttpUtils for IP/device extraction
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
├── notification/                      # Notification module
│   ├── config/                       # Configuration
│   ├── enums/                        # NotificationType, NotificationTemplate
│   ├── model/                        # Data models
│   ├── provider/                     # ISmsProvider implementations
│   ├── rest/                         # REST controllers
│   └── service/                      # INotificationService implementations
└── ratelimit/                         # Rate limit module
    ├── annotations/                  # @RateLimit, @EnableRateLimit
    ├── aspect/                       # RateLimitAspect for AOP
    ├── algorithm/                    # Rate limit algorithms (Counter, etc.)
    ├── config/                       # Configuration properties
    ├── dimension/                     # Dimension extractors (IP, Device, Business)
    ├── enums/                        # RateLimitDimension, RateLimitAlgorithmType
    ├── exception/                    # RateLimitExceededException
    ├── model/                        # RateLimitConfig, RateLimitResult
    ├── service/                      # IRateLimitService and implementation
    └── storage/                      # Redis-based storage abstraction
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

### Enabling Rate Limit Module
1. Add `@EnableRateLimit` annotation to `SysadminApplication` class (currently not present - must be added manually)
2. Configure Redis for rate limit storage
3. Add rate limit configuration to `application.yml` (see `ratelimit-example.yml`)
4. Use `@RateLimit` annotation on methods or `IRateLimitService` for programmatic access

### Adding a New Rate Limit Algorithm
1. Add algorithm type to `RateLimitAlgorithmType` enum
2. Implement `RateLimitAlgorithm` interface with `@Component`
3. Configure algorithm name in annotation or `RateLimitConfig`

### Adding a Custom Dimension Extractor
1. Implement `DimensionExtractor` interface with `@Component`
2. Use `customDimensionExtractor` parameter in `@RateLimit` annotation to specify the bean name
3. Extractor receives `RateLimitConfig` and returns dimension value

## Rate Limit Usage Examples

### Common Scenarios

**Login API (IP dimension)**:
```java
@PostMapping("/login")
@RateLimit(
    dimensions = {RateLimitDimension.IP},
    maxCount = 5,
    period = 60,
    message = "登录过于频繁，请1分钟后再试"
)
public Result<String> login(@RequestBody LoginForm form) { }
```

**SMS sending (phone number dimension)**:
```java
@PostMapping("/sms/send")
@RateLimit(
    dimensions = {RateLimitDimension.BUSINESS},
    maxCount = 3,
    period = 3600,
    key = "#request.phone",  // Extract phone from request
    message = "短信发送过于频繁，请1小时后再试"
)
public Result<String> sendSms(@RequestBody SmsRequest request) { }
```

**Multi-dimensional (IP + Device)**:
```java
@PostMapping("/api/data")
@RateLimit(
    dimensions = {RateLimitDimension.IP, RateLimitDimension.DEVICE},
    maxCount = 10,
    period = 60,
    message = "调用过于频繁，请1分钟后再试"
)
public Result<Data> getData(@RequestBody DataRequest request) { }
```

### Programmatic Usage
```java
@Resource
private IRateLimitService rateLimitService;

public void doSomething() {
    RateLimitConfig config = RateLimitConfig.builder()
            .key("user:123")
            .algorithm(RateLimitAlgorithmType.COUNTER)
            .dimensions(List.of(RateLimitDimension.BUSINESS))
            .maxCount(10)
            .period(600)
            .build();

    RateLimitResult result = rateLimitService.checkLimit(config);
    if (!result.isAllowed()) {
        throw new RateLimitExceededException(result.getErrorMessage());
    }
    // Execute business logic
}
```

## SpEL Expressions in Rate Limiting

The rate limit module supports Spring Expression Language (SpEL) for extracting business keys dynamically from method parameters:

### Common SpEL Patterns

```java
// Extract from request body
@RateLimit(key = "#request.phone")
public Result<String> sendSms(@RequestBody SmsRequest request) { }

// Extract from path variable
@GetMapping("/user/{userId}")
@RateLimit(key = "#userId")
public Result<String> getUser(@PathVariable String userId) { }

// Extract from request header
@RateLimit(key = "#request.getHeader('X-User-ID')")
public Result<String> doSomething(HttpServletRequest request) { }

// Complex expression combining multiple fields
@RateLimit(key = "#request.userId + ':' + #request.operationType")
public Result<String> doOperation(@RequestBody OperationRequest request) { }
```

### Dimension-Specific SpEL Usage

- **BUSINESS dimension**: Use SpEL via `key` parameter to extract business identifiers (phone, email, userId, etc.)
- **IP/DEVICE dimensions**: Automatically extracted from request headers; SpEL not needed
- **Multi-dimensional**: Combine BUSINESS with IP/DEVICE for layered rate limiting

## Testing

### Manual Testing via REST Controller
The project includes `RateLimitControllerTest` at `/test/ratelimit/*` for manual testing of rate limit functionality:

```bash
# IP dimension rate limit
curl http://localhost:8020/test/ratelimit/ip

# Device dimension rate limit
curl http://localhost:8020/test/ratelimit/device

# Multi-dimensional rate limit
curl http://localhost:8020/test/ratelimit/multi

# Business key rate limit (POST with phone number)
curl -X POST http://localhost:8020/test/ratelimit/business \
  -H "Content-Type: application/json" \
  -d '{"phone": "13800138000"}'

# Programmatic rate limit check
curl http://localhost:8020/test/ratelimit/programmatic?key=test123

# Get remaining count
curl http://localhost:8020/test/ratelimit/remaining?key=test123

# Reset rate limit
curl -X POST http://localhost:8020/test/ratelimit/reset?key=test123
```

**Note**: `RateLimitControllerTest` is a REST controller for manual testing, not a JUnit test class.

### Unit Testing
- Tests use H2 in-memory database
- Run with `mvn test`
- Individual test classes: `mvn test -Dtest=ClassName`

## Important Notes

- **Framework Dependencies**: This project depends on the opensabre framework. If working on the framework itself, build `opensabre-framework/` first with `mvn clean install`
- **Maven Compiler Plugin**: Configured with `<parameters>true</parameters>` allowing `@RequestParam` annotations without explicit `value` parameter
- **Git Commit ID Plugin**: Generates git properties during build for version tracking
- **Database Prefix**: All tables use `base_sys_` prefix
- **Redis Required**: Both captcha module and rate limit module require Redis for storage and state persistence
- **Rate Limiting**:
  - Captcha module enforces IP, device, and business-level rate limits configured per `BusinessScenario`
  - Rate limit module provides comprehensive rate limiting with multiple dimensions (IP, Device, Business)
  - Rate limit uses AOP via `@RateLimit` annotation and supports SpEL expressions for business keys
  - **Must add `@EnableRateLimit` to `SysadminApplication` class to enable rate limit functionality**
- **Template Parameters**: Notification templates support dynamic parameter replacement using Map or varargs
- **HTTP Utilities**: `HttpUtils` class in `common/utils/` provides helper methods for:
  - `getClientIpAddress()` - Extracts IP from X-Forwarded-For, X-Real-IP, or RemoteAddr headers
  - `getDeviceId()` - Generates device fingerprint using User-Agent + IP hash
  - `getCurrentRequest()` / `getCurrentResponse()` - Get current HTTP context

## Rate Limit Best Practices

1. **Reasonable limits**: Login 5/min, SMS 3/hour, queries 100/min, writes 10/min
2. **Multi-dimensional**: Combine IP + DEVICE for stricter controls
3. **User-friendly messages**: "您操作过于频繁，请{n}秒后再试"
4. **Monitoring**: Log rate limit exceeded events for analysis
5. **Testing**: Use test endpoints and validate with load testing
