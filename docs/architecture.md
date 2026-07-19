# 架构与边界

`base-sysadmin` 提供跨业务系统的管理能力：审计日志、验证码、字典、通知、限流、网关路由管理、站内信和使用统计。每个子域独立维护 Controller、Service、DAO 与模型；共享应用配置、数据库脚本和通用约定。

| 模块 | 代码目录 | 当前文档 |
| --- | --- | --- |
| 审计 | `audit/` | [audit.md](audit.md) |
| 验证码 | `captcha/` | [captcha.md](captcha.md) |
| 字典 | `dict/` | [dict.md](dict.md) |
| 通知 | `notification/` | [notification.md](notification.md) |
| 限流 | `ratelimit/` | [ratelimit.md](ratelimit.md) |
| 网关路由 | `gateway/` | [模块地图](modules/README.md) |
| 站内信 | `internalmessage/` | [模块地图](modules/README.md) |
| 使用统计 | `usage/` | [模块地图](modules/README.md) |

接口以各模块 `rest/` 中 Controller 为准，数据结构及迁移以 `resources/db/` 为准。
