# Notification Center Design

## Goal

Build a database-managed notification center for `base-sysadmin` so operators can manage notification scenes, channel templates, send records, and manual retries through APIs.

## Scope

The feature covers:

- Notification scenes, such as `LOGIN_CAPTCHA` or `ORDER_CREATED`.
- Channel templates for each scene and channel, such as SMS and email.
- Runtime send API based on `sceneCode` and optional `channel`.
- Send records with template snapshots, arguments, status, message id, and failure reason.
- Manual retry of failed records.

The first implementation does not include scheduled automatic retry. The table keeps `retry_count` and `next_retry_time` so automatic retry can be added later without changing the main data model.

## Data Model

`base_sys_notification_scene` stores a scene code, scene name, description, enabled flag, and audit columns.

`base_sys_notification_template` stores one template per scene and channel. It includes title, content, parameter schema text, sort order, enabled flag, and audit columns. A unique key on `scene_code + channel` prevents duplicate active channel definitions for a scene.

`base_sys_notification_record` stores every send attempt. It stores scene code, channel, target, template id, title snapshot, content snapshot, argument JSON, status, message id, failure reason, retry count, next retry time, sent time, and audit columns.

## API Design

Management APIs follow the existing `captcha` and `ratelimit` controller style:

- `GET /notification/scenes`
- `GET /notification/scenes/enabled`
- `GET /notification/scenes/{sceneCode}`
- `POST /notification/scenes`
- `PUT /notification/scenes/{sceneCode}`
- `DELETE /notification/scenes/{sceneCode}`
- `GET /notification/templates`
- `GET /notification/templates/{id}`
- `POST /notification/templates`
- `PUT /notification/templates/{id}`
- `DELETE /notification/templates/{id}`
- `POST /notification/send`
- `GET /notification/records`
- `GET /notification/records/{id}`
- `POST /notification/records/{id}/retry`

## Send Flow

The caller sends `target`, `sceneCode`, optional `channel`, and optional named `args`.

If `channel` is provided, the service uses the enabled template for that scene and channel. If `channel` is absent, it selects the first enabled template by `sort`.

Template rendering uses named placeholders such as `{code}`. Missing values are replaced with an empty string. The rendered content is passed to the existing channel service layer. The service records success or failure before returning.

## Compatibility

The existing enum template path is replaced as the main path by database templates. Existing channel implementations remain in place but gain content-based send support so runtime templates can be used without introducing provider-specific code.

## Validation And Errors

Scene code, scene name, channel, target, and template content are required. Disabled scenes or missing templates return clear argument errors. Unsupported channels return an argument error before any record is written.

Send failures are recorded with `FAILED` status and failure reason, then returned to the caller as a failed response. Manual retry only accepts records in `FAILED` status.

## Tests

Focused unit tests cover template rendering, default channel selection, send record creation, failed send retry behavior, and invalid scene/template errors. Model tests cover request and record defaults.
