# Repository Guidelines

## Project Structure & Module Organization

This is a Java 17 Maven project packaged as a Spring Boot jar. Application code lives under `src/main/java/io/github/opensabre/sysadmin`, with feature packages for `audit`, `captcha`, `notification`, `ratelimit`, and shared helpers under `common`. The entry point is `SysadminApplication`.

Configuration files are in `src/main/resources`, including `application.yml`, `bootstrap.yml`, rate-limit examples, and database scripts in `src/main/resources/db`. Tests and test-only controllers belong in `src/test/java`; keep package names aligned with the production package being exercised.

## Build, Test, and Development Commands

- `mvn clean package`: compile, run tests, and build the executable jar in `target/`.
- `mvn test`: run the test suite with Spring Boot test support and H2 test dependencies.
- `mvn spring-boot:run`: start the service locally using the current resource configuration.
- `mvn jib:build`: build/publish a container image when Jib is configured by the parent or environment.

Use `-DskipTests` only for local packaging when tests have already passed.

## Coding Style & Naming Conventions

Use 4-space indentation and UTF-8 source files. Follow the existing package structure: `rest` for controllers, `service` and `service.impl` for service contracts and implementations, `model.form` for request forms, `model.vo` for response objects, `model.po` for persistence objects, `config` for Spring configuration, and `enums` for enums.

Prefer clear Java names such as `AuditLogService`, `NotificationController`, and `RateLimitConfig`. Interfaces in this repository commonly use an `I` prefix, for example `IAuditLogService` and `IRateLimitService`; keep that pattern for new service interfaces.

## Testing Guidelines

Use JUnit/Spring Boot testing from `spring-boot-starter-test`. Place tests under `src/test/java` with names ending in `Test`. For persistence-dependent behavior, use the existing H2 test dependency where practical. Add focused tests for service logic, annotations/aspects, and controller behavior before changing shared rate-limit, captcha, notification, or audit flows.

## Commit & Pull Request Guidelines

Recent commits use short, imperative Chinese summaries, for example `限次数据储存接口与实现`. Keep commits concise and scoped to one change. Pull requests should include a brief description, affected module paths, test results such as `mvn test`, and any configuration or database script changes. Link related issues when available and include API examples or screenshots for REST-facing behavior changes.

## Security & Configuration Tips

Do not commit secrets or environment-specific credentials in `application.yml` or `bootstrap.yml`. Keep reusable schema/data changes in `src/main/resources/db`, and document new runtime settings in the relevant design or quick-start Markdown file.
