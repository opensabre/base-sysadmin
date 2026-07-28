# 通知模块

## 当前实现

通知模块支持按通知场景和渠道发送消息，并记录发送结果。当前运行时模板来自数据库表，不再依赖旧文档中的 `templateCode` 请求字段。

实现位于 `io.github.opensabre.sysadmin.notification` 包。

主要组件：

- `NotificationController`：发送通知入口，路径前缀 `/notification`
- `NotificationSceneController`：通知场景管理，路径前缀 `/notification/scenes`
- `NotificationTemplateController`：通知模板管理，路径前缀 `/notification/templates`
- `NotificationRecordController`：发送记录查询和失败重试，路径前缀 `/notification/records`
- `NotificationServiceManager`：按场景和渠道解析模板、渲染内容、调用渠道服务并保存记录
- `INotificationService`：通知渠道服务接口
- `SmsNotificationService`、`EmailNotificationService`
- `ISmsProvider` / `SimulateSmsProvider`
- `NotificationScene`、`NotificationTemplateConfig`、`NotificationRecord`

## 发送 API

`POST /notification/send`

请求体为 `NotificationSendForm`：

```json
{
  "target": "13800138000",
  "sceneCode": "LOGIN_CAPTCHA",
  "channel": "SMS",
  "args": {
    "code": "123456",
    "minutes": "5"
  }
}
```

字段说明：

| 字段 | 必填 | 说明 |
| --- | --- | --- |
| `target` | 是 | 接收目标，例如手机号或邮箱 |
| `sceneCode` | 是 | 通知场景编码 |
| `channel` | 否 | 通知渠道；为空时使用该场景下第一个启用模板 |
| `args` | 否 | 模板变量，按 `{name}` 替换 |

响应为 `NotificationSendResponse`，包含：

- `recordId`
- `messageId`
- `sceneCode`
- `channel`
- `status`
- `failureReason`
- `sentTime`

## 管理 API

通知场景：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/notification/scenes` | 查询场景列表 |
| `GET` | `/notification/scenes/enabled` | 查询启用场景 |
| `GET` | `/notification/scenes/{sceneCode}` | 获取场景 |
| `POST` | `/notification/scenes` | 创建场景 |
| `PUT` | `/notification/scenes/{sceneCode}` | 更新场景 |
| `DELETE` | `/notification/scenes/{sceneCode}` | 删除场景 |

通知模板：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/notification/templates` | 查询模板，可按 `sceneCode`、`channel`、`enabled` 过滤 |
| `GET` | `/notification/templates/{id}` | 获取模板 |
| `POST` | `/notification/templates` | 创建模板 |
| `PUT` | `/notification/templates/{id}` | 更新模板 |
| `DELETE` | `/notification/templates/{id}` | 删除模板 |

通知记录：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/notification/records` | 分页查询记录，可按 `sceneCode`、`channel`、`status` 过滤 |
| `GET` | `/notification/records/{id}` | 获取记录 |
| `POST` | `/notification/records/{id}/retry` | 重试失败记录 |

## 数据模型

`NotificationScene` 字段：

- `sceneCode`
- `sceneName`
- `description`
- `enabled`

`NotificationTemplateConfig` 字段：

- `sceneCode`
- `channel`
- `templateName`
- `title`
- `content`
- `paramSchema`
- `sort`
- `enabled`

`NotificationRecord` 字段：

- `sceneCode`
- `channel`
- `target`
- `templateId`
- `templateTitle`
- `templateContent`
- `argsJson`
- `status`
- `messageId`
- `failureReason`
- `retryCount`
- `nextRetryTime`
- `sentTime`

## 数据表

表结构由 `src/main/resources/db/os-base-sysadmin-ddl.sql` 维护：

- `base_sys_notification_scene`
- `base_sys_notification_template`
- `base_sys_notification_record`

初始数据由 `src/main/resources/db/os-base-sysadmin-db.sql` 维护：

- 场景：`LOGIN_CAPTCHA`、`ORDER_CREATED`
- 模板：`NOTIFY_TPL_LOGIN_SMS`、`NOTIFY_TPL_LOGIN_EMAIL`、`NOTIFY_TPL_ORDER_SMS`、`NOTIFY_TPL_ORDER_EMAIL`

## 发送流程

1. `NotificationController` 接收 `NotificationSendForm`。
2. `NotificationServiceManager` 校验场景存在且启用。
3. 根据 `channel` 获取指定模板；未传 `channel` 时取第一个启用模板。
4. 使用 `args` 渲染模板标题和内容。
5. 调用对应 `INotificationService` 发送。
6. 写入 `base_sys_notification_record`。
7. 返回发送记录和状态。
