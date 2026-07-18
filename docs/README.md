# base-sysadmin 文档索引

本目录集中维护应用文档。根目录只保留仓库入口和工具约定文件，业务模块文档统一放在 `docs/` 下。

## 模块文档

| 文档 | 覆盖范围 |
| --- | --- |
| [audit.md](audit.md) | 审计日志接口、查询条件和数据表 |
| [captcha.md](captcha.md) | 验证码发送/校验、动态场景、Redis 存储和通知集成 |
| [notification.md](notification.md) | 通知发送、场景/模板/记录管理和发送流程 |
| [ratelimit.md](ratelimit.md) | 集中式限次检查、动态场景、算法和维度 |
| [dict.md](dict.md) | 字典类型、字典项和兼容路由 |
| [架构与边界](architecture.md) | 应用职责、模块边界和事实源 |
| [开发与运行](development.md) | 启动、数据初始化与验证 |
| [模块地图](modules/README.md) | 全部模块的介绍、功能、使用、流程和规划入口 |
| [规划](roadmap.md) | 跨模块演进与待确认项 |

## 维护规则

- 新增业务模块文档放入 `docs/`。
- 根目录不要新增零散业务 Markdown。
- 接口文档以 Controller 的 `@RequestMapping` 和方法映射为准。
- 表结构以 `src/main/resources/db/os-base-sysadmin-ddl.sql` 为准。
- 初始数据以 `src/main/resources/db/os-base-sysadmin-db.sql` 为准。
- 新模块应在 `modules/` 下建立独立文档；历史模块文档可逐步从平铺文件迁入，但迁移时必须保留链接兼容性。
