# 验证码模块

## 当前实现

验证码模块支持图形、短信、邮件验证码，并支持两种调用方式：

- 兼容枚举场景：通过 `BusinessScenario` 枚举传入场景。
- 动态场景：通过 `base_sys_captcha_scene` 表维护场景配置，并通过场景编码调用。

实现位于 `io.github.opensabre.sysadmin.captcha` 包。

主要组件：

- `CaptchaController`：验证码发送和校验入口，路径前缀 `/captcha`
- `CaptchaSceneController`：验证码场景管理入口，路径前缀 `/captcha/scenes`
- `CaptchaService`：验证码服务抽象基类
- `ImageCaptchaService` / `SmsCaptchaService` / `EmailCaptchaService`
- `ImageCaptchaGenerator` / `SmsCaptchaGenerator` / `EmailCaptchaGenerator`
- `RedisICaptchaStorage`：验证码 Redis 存储
- `CaptchaNotificationSender`：短信/邮件验证码通知发送
- `CaptchaScene`：动态场景配置，表名 `base_sys_captcha_scene`

## API

验证码发送与校验：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST` | `/captcha/send/sms` | 按 `BusinessScenario` 发送短信验证码 |
| `POST` | `/captcha/send/email` | 按 `BusinessScenario` 发送邮件验证码 |
| `POST` | `/captcha/send/image` | 按 `BusinessScenario` 生成图形验证码 |
| `POST` | `/captcha/send/{sceneCode}` | 按动态场景发送验证码 |
| `POST` | `/captcha/verify` | 按 `BusinessScenario` 校验验证码 |
| `POST` | `/captcha/verify/{sceneCode}` | 按动态场景校验验证码 |

验证码场景管理：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/captcha/scenes` | 查询验证码场景列表 |
| `GET` | `/captcha/scenes/enabled` | 查询启用场景 |
| `GET` | `/captcha/scenes/{sceneCode}` | 获取验证码场景 |
| `POST` | `/captcha/scenes` | 创建验证码场景 |
| `PUT` | `/captcha/scenes/{sceneCode}` | 更新验证码场景 |
| `DELETE` | `/captcha/scenes/{sceneCode}` | 删除验证码场景 |

## 动态场景

`CaptchaScene` 字段：

- `sceneCode`
- `sceneName`
- `captchaType`
- `templateCode`
- `notificationTemplateId`
- `description`
- `captchaLength`
- `captchaExpireTime`
- `captchaAttempts`
- `minInterval`
- `maxLimitCount`
- `enabled`

初始数据位于 `src/main/resources/db/os-base-sysadmin-db.sql`：

- `LOGIN_IMAGE`
- `REGISTER_IMAGE`
- `LOGIN_SMS`
- `LOGIN_EMAIL`

## Redis 存储

验证码按类型、场景和验证码 ID 组成 Redis Key。过期时间来自场景配置的 `captchaExpireTime`。

校验逻辑：

- 验证码不存在或过期时返回失败。
- 验证成功后删除验证码。
- 验证失败会增加尝试次数。
- 达到 `captchaAttempts` 后删除验证码。

## 通知集成

短信和邮件验证码通过 `CaptchaNotificationSender` 调用通知模块。动态场景使用 `notificationTemplateId` 关联通知模板，例如：

- `LOGIN_SMS` -> `NOTIFY_TPL_LOGIN_SMS`
- `LOGIN_EMAIL` -> `NOTIFY_TPL_LOGIN_EMAIL`

## 配置

`src/main/resources/application.yml` 中包含验证码安全默认配置：

```yaml
captcha:
  security:
    ip:
      max-attempts: 500
      time-window: 3600
    device:
      max-attempts: 500
      time-window: 3600
    min-interval: 60
    max-attempts: 3
```

## 数据表

表结构由 `src/main/resources/db/os-base-sysadmin-ddl.sql` 维护：

- `base_sys_captcha_scene`
