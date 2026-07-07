# Notification Center Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build database-managed notification scenes, channel templates, send records, and manual retry APIs in `base-sysadmin`.

**Architecture:** Follow the existing Spring Boot and MyBatis-Plus feature package style. Add focused `po`, `form`, `vo`, `dao`, `service`, and `rest` classes under `notification`, then route sends through database templates and existing SMS/email providers.

**Tech Stack:** Java 17, Spring Boot, MyBatis-Plus, JUnit 5, AssertJ, Mockito, MySQL DDL scripts.

---

### Task 1: Notification Persistence Model

**Files:**
- Create: `src/main/java/io/github/opensabre/sysadmin/notification/model/po/NotificationScene.java`
- Create: `src/main/java/io/github/opensabre/sysadmin/notification/model/po/NotificationTemplateConfig.java`
- Create: `src/main/java/io/github/opensabre/sysadmin/notification/model/po/NotificationRecord.java`
- Create: `src/main/java/io/github/opensabre/sysadmin/notification/enums/NotificationSendStatus.java`
- Create: `src/main/java/io/github/opensabre/sysadmin/notification/dao/NotificationSceneMapper.java`
- Create: `src/main/java/io/github/opensabre/sysadmin/notification/dao/NotificationTemplateMapper.java`
- Create: `src/main/java/io/github/opensabre/sysadmin/notification/dao/NotificationRecordMapper.java`
- Modify: `src/main/resources/db/os-base-sysadmin-ddl.sql`
- Modify: `src/main/resources/db/os-base-sysadmin-db.sql`

- [ ] Add PO classes with `@TableName`, Lombok builders, and fields matching the DDL.
- [ ] Add mapper interfaces extending `BaseMapper`.
- [ ] Add DDL for scene, template, and record tables.
- [ ] Add seed rows for login captcha SMS/email scene templates.
- [ ] Run `mvn -DskipTests compile`.

### Task 2: Notification Management Services

**Files:**
- Create: `src/main/java/io/github/opensabre/sysadmin/notification/service/INotificationSceneService.java`
- Create: `src/main/java/io/github/opensabre/sysadmin/notification/service/INotificationTemplateConfigService.java`
- Create: `src/main/java/io/github/opensabre/sysadmin/notification/service/INotificationRecordService.java`
- Create: `src/main/java/io/github/opensabre/sysadmin/notification/service/impl/NotificationSceneService.java`
- Create: `src/main/java/io/github/opensabre/sysadmin/notification/service/impl/NotificationTemplateConfigService.java`
- Create: `src/main/java/io/github/opensabre/sysadmin/notification/service/impl/NotificationRecordService.java`

- [ ] Implement scene CRUD by `sceneCode`, enabled listing, and validation.
- [ ] Implement template CRUD by id and lookup by `sceneCode + channel`.
- [ ] Implement record creation, query by id, page query, and retry counter updates.
- [ ] Run focused service tests.

### Task 3: Runtime Send Flow

**Files:**
- Create: `src/main/java/io/github/opensabre/sysadmin/notification/model/form/NotificationSendForm.java`
- Create: `src/main/java/io/github/opensabre/sysadmin/notification/model/vo/NotificationSendResponse.java`
- Create: `src/main/java/io/github/opensabre/sysadmin/notification/model/vo/NotificationPageData.java`
- Modify: `src/main/java/io/github/opensabre/sysadmin/notification/service/INotificationService.java`
- Modify: `src/main/java/io/github/opensabre/sysadmin/notification/service/NotificationServiceManager.java`
- Modify: `src/main/java/io/github/opensabre/sysadmin/notification/service/impl/SmsNotificationService.java`
- Modify: `src/main/java/io/github/opensabre/sysadmin/notification/service/impl/EmailNotificationService.java`

- [ ] Add content-based `sendContent(target, title, content)` support to channel services.
- [ ] Add template rendering with named placeholders `{name}`.
- [ ] Add send-by-scene logic that selects explicit channel or first enabled template by sort.
- [ ] Persist success and failure records.
- [ ] Implement manual retry for failed records.

### Task 4: REST APIs

**Files:**
- Create: `src/main/java/io/github/opensabre/sysadmin/notification/rest/NotificationSceneController.java`
- Create: `src/main/java/io/github/opensabre/sysadmin/notification/rest/NotificationTemplateController.java`
- Create: `src/main/java/io/github/opensabre/sysadmin/notification/rest/NotificationRecordController.java`
- Modify: `src/main/java/io/github/opensabre/sysadmin/notification/rest/NotificationController.java`

- [ ] Add scene CRUD endpoints.
- [ ] Add template CRUD endpoints.
- [ ] Add send endpoint using `NotificationSendForm`.
- [ ] Add record page/detail/retry endpoints.
- [ ] Keep controller naming consistent with existing modules.

### Task 5: Verification

**Files:**
- Create: `src/test/java/io/github/opensabre/sysadmin/notification/service/NotificationServiceManagerTest.java`
- Create: `src/test/java/io/github/opensabre/sysadmin/notification/model/NotificationModelTest.java`

- [ ] Test named placeholder rendering.
- [ ] Test default template selection by sort.
- [ ] Test failure record creation when the channel service throws.
- [ ] Test retry rejects non-failed records.
- [ ] Run `mvn -Dtest=NotificationServiceManagerTest,NotificationModelTest test`.
- [ ] Run `mvn clean package -DskipTests`.
