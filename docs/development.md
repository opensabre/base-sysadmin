# 开发与运行

1. 数据库变更写入 `resources/db/migration/mysql/`，由发布阶段的独立 Flyway 进程执行；应用 Pod 启动时不执行迁移。
2. 配置注册中心、数据库、缓存和通知渠道等依赖，再通过 Maven 启动。
3. 变更后按模块验证 API、数据持久化、权限和异常路径。

## 审计时间毫秒精度迁移

`V20260818_01__use_millisecond_precision_for_audit_timestamps.sql` 将所有公共
`created_time`、`updated_time` 及审计日志的 `operation_time` 升级为 `DATETIME(3)`；
`updated_time` 使用 `ON UPDATE CURRENT_TIMESTAMP(3)` 自动维护。统计桶起点、过期时间、
发布时间等业务时间字段维持原有精度与语义。

迁移前已被秒级列截断的毫秒无法恢复。发布前备份目标库并记录迁移版本；回滚为 `DATETIME`
会截断迁移后写入的毫秒。

涉及验证码、通知、限流或站内信时，禁止在日志、文档和测试数据中保存真实验证码、手机号、邮箱、密钥或消息内容。
