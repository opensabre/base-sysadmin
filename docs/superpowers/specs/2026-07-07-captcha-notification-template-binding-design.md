# Captcha Notification Template Binding Design

## Goal

Upgrade the captcha module so SMS and email captcha scenes can be managed dynamically through database records and can bind directly to a notification template managed by the notification center.

## Scope

The captcha module already has database-backed captcha scenes and management APIs under `/captcha/scenes`. This upgrade keeps that model and adds an explicit template binding from a captcha scene to one notification template.

In scope:

- Add `notification_template_id` to `base_sys_captcha_scene`.
- Expose `notificationTemplateId` through existing captcha scene APIs.
- Send SMS and email captcha messages through notification center dynamic templates.
- Keep image captcha generation unchanged.
- Keep legacy `template_code` for compatibility, but stop using it as the primary send path.

Out of scope:

- Multi-template routing inside one captcha scene.
- New notification providers.
- Removing `BusinessScenario` legacy endpoints.

## Data Model

`base_sys_captcha_scene.notification_template_id` stores the exact `base_sys_notification_template.id` to use when the scene sends a captcha notification.

Rules:

- `IMAGE` captcha scenes do not require `notification_template_id`.
- `SMS` captcha scenes require a notification template whose `channel` is `SMS`.
- `EMAIL` captcha scenes require a notification template whose `channel` is `EMAIL`.
- The referenced template must exist and be enabled.
- The referenced notification scene should be enabled, because notification send still validates scene state.

Seed data binds:

- `LOGIN_SMS` to `NOTIFY_TPL_LOGIN_SMS`.
- `LOGIN_EMAIL` to `NOTIFY_TPL_LOGIN_EMAIL`.

## Runtime Flow

For SMS and email captcha generation:

1. `CaptchaController` resolves a `CaptchaScene` either from legacy `BusinessScenario` or from `/captcha/send/{sceneCode}`.
2. `CaptchaService` runs existing rate-limit checks and generates the captcha code.
3. The concrete SMS or email service resolves the scene's `notificationTemplateId`.
4. The service validates the template channel matches the captcha type.
5. The service calls notification center with:
   - target: captcha business key, such as phone number or email address
   - scene code: template's notification scene code
   - channel: template channel
   - args: `code` and `minutes`
6. Notification center renders the template, sends through the configured provider, and records the send result.

Image captcha returns image data directly and does not call notification center.

## API Behavior

Existing captcha scene management APIs remain:

- `GET /captcha/scenes`
- `GET /captcha/scenes/enabled`
- `GET /captcha/scenes/{sceneCode}`
- `POST /captcha/scenes`
- `PUT /captcha/scenes/{sceneCode}`
- `DELETE /captcha/scenes/{sceneCode}`

The request and response body includes `notificationTemplateId` for scenes that need notification delivery.

Existing send APIs remain compatible:

- `POST /captcha/send/sms`
- `POST /captcha/send/email`
- `POST /captcha/send/image`
- `POST /captcha/send/{sceneCode}`

The SMS and email send paths use `notificationTemplateId` when present. Legacy enum template sending is not used by the dynamic scene path.

## Error Handling

SMS and email captcha generation fails before returning a captcha response when:

- `notificationTemplateId` is blank.
- The referenced notification template does not exist.
- The referenced template is disabled.
- The template channel does not match the captcha type.
- Notification sending fails.

Failures are surfaced as runtime exceptions, matching the current captcha service style for rate-limit failures and unsupported scenes.

## Testing

Add focused unit tests for the captcha notification bridge:

- SMS captcha sends through a bound SMS notification template.
- Email captcha sends through a bound email notification template.
- Missing template binding fails for SMS/email scenes.
- Channel mismatch fails before sending.
- Image captcha still does not require a notification template.

Run:

- `mvn -Dtest=CaptchaNotificationTemplateTest,CaptchaSceneServiceTest test`
- `mvn clean package -DskipTests`
