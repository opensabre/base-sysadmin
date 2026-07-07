# Captcha Notification Template Binding Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Bind captcha scenes directly to notification center templates so SMS and email captcha sends are fully database-managed.

**Architecture:** Add `notificationTemplateId` to the captcha scene persistence model and database scripts. Introduce a focused bridge service that validates the bound notification template and delegates send execution to `NotificationServiceManager`. Wire SMS and email captcha services through that bridge while keeping image captcha unchanged.

**Tech Stack:** Java 17, Spring Boot, MyBatis-Plus, Maven, JUnit 5, Mockito.

---

## File Structure

- Modify `src/main/java/io/github/opensabre/sysadmin/captcha/model/po/CaptchaScene.java`: add `notificationTemplateId`.
- Create `src/main/java/io/github/opensabre/sysadmin/captcha/service/CaptchaNotificationSender.java`: bridge from captcha scenes to notification center templates.
- Modify `src/main/java/io/github/opensabre/sysadmin/captcha/service/impl/SmsCaptchaService.java`: send SMS captcha through `CaptchaNotificationSender`.
- Modify `src/main/java/io/github/opensabre/sysadmin/captcha/service/impl/EmailCaptchaService.java`: send email captcha through `CaptchaNotificationSender`.
- Modify `src/main/resources/db/os-base-sysadmin-ddl.sql`: add `notification_template_id`.
- Modify `src/main/resources/db/os-base-sysadmin-db.sql`: seed template bindings for login SMS and email captcha scenes.
- Create `src/test/java/io/github/opensabre/sysadmin/captcha/service/CaptchaNotificationSenderTest.java`: validate bridge behavior.
- Modify `src/test/java/io/github/opensabre/sysadmin/captcha/service/impl/CaptchaSceneServiceTest.java`: assert model includes the new binding.

### Task 1: Captcha Scene Binding Model

**Files:**
- Modify: `src/main/java/io/github/opensabre/sysadmin/captcha/model/po/CaptchaScene.java`
- Modify: `src/main/resources/db/os-base-sysadmin-ddl.sql`
- Modify: `src/main/resources/db/os-base-sysadmin-db.sql`
- Test: `src/test/java/io/github/opensabre/sysadmin/captcha/service/impl/CaptchaSceneServiceTest.java`

- [ ] Add `private String notificationTemplateId;` to `CaptchaScene`.
- [ ] Add `notification_template_id varchar(64) DEFAULT NULL COMMENT '通知模板ID'` after `template_code` in `base_sys_captcha_scene`.
- [ ] Seed `LOGIN_SMS` with `NOTIFY_TPL_LOGIN_SMS` and `LOGIN_EMAIL` with `NOTIFY_TPL_LOGIN_EMAIL`.
- [ ] Update `CaptchaSceneServiceTest` to assert `notificationTemplateId`.

### Task 2: Captcha Notification Bridge

**Files:**
- Create: `src/main/java/io/github/opensabre/sysadmin/captcha/service/CaptchaNotificationSender.java`
- Test: `src/test/java/io/github/opensabre/sysadmin/captcha/service/CaptchaNotificationSenderTest.java`

- [ ] Create `CaptchaNotificationSender` as a Spring `@Service`.
- [ ] Inject `INotificationTemplateConfigService` and `NotificationServiceManager`.
- [ ] Add `sendCaptcha(CaptchaScene scene, String target, String code)` that:
  - rejects blank `notificationTemplateId`;
  - loads the notification template by ID;
  - rejects missing or disabled templates;
  - validates `SMS` scene uses `SMS` template and `EMAIL` scene uses `EMAIL` template;
  - sends `NotificationSendForm` with `code` and `minutes`.
- [ ] Add tests for success, missing binding, and channel mismatch.

### Task 3: Wire SMS and Email Captcha Sends

**Files:**
- Modify: `src/main/java/io/github/opensabre/sysadmin/captcha/service/impl/SmsCaptchaService.java`
- Modify: `src/main/java/io/github/opensabre/sysadmin/captcha/service/impl/EmailCaptchaService.java`
- Test: `src/test/java/io/github/opensabre/sysadmin/captcha/service/CaptchaNotificationSenderTest.java`

- [ ] Replace legacy enum-template send code in `SmsCaptchaService.afterGenerateCaptcha`.
- [ ] Replace legacy enum-template send code in `EmailCaptchaService.afterGenerateCaptcha`.
- [ ] Keep response body unchanged: `captchaId` and `expireTime`.

### Task 4: Verification

**Files:**
- All changed files.

- [ ] Run `mvn -Dtest=CaptchaNotificationSenderTest,CaptchaSceneServiceTest test`.
- [ ] Run `mvn clean package -DskipTests`.
- [ ] Check `git status -sb`.
- [ ] Commit with `feat: 动态绑定验证码通知模板`.
