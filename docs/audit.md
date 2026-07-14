# 审计模块

## 当前实现

审计模块提供审计日志的写入、查询、按 ID 获取和过期清理能力。实现位于 `io.github.opensabre.sysadmin.audit` 包。

主要组件：

- `AuditLogController`：REST 入口，路径前缀 `/audit/log`
- `IAuditLogService` / `AuditLogService`：审计日志服务
- `AuditLogMapper`：MyBatis-Plus Mapper
- `AuditLog`：持久化对象，表名 `base_sys_audit_log`
- `AuditLogForm`：新增审计日志表单
- `AuditLogQueryForm` / `AuditLogQueryParam`：查询条件

## API

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST` | `/audit/log` | 保存审计日志 |
| `GET` | `/audit/log/{id}` | 根据 ID 获取审计日志 |
| `POST` | `/audit/log/conditions` | 条件分页查询审计日志 |
| `DELETE` | `/audit/log/clean/{days}` | 清理指定保留天数之前的日志 |

## 数据表

表结构由 `src/main/resources/db/os-base-sysadmin-ddl.sql` 维护。

`base_sys_audit_log` 主要字段：

- `id`
- `operation_type`
- `operation_time`
- `operator_username`
- `module`
- `description`
- `client_ip`
- `target_key`
- `user_agent`
- `request_method`
- `request_url`
- `request`
- `response`
- `error_message`
- `execution_time`
- `created_by`
- `created_time`
- `updated_by`
- `updated_time`

## 查询条件

`AuditLogQueryForm` 支持：

- `operationType`
- `operationStartTime`
- `operationEndTime`
- `operatorUsername`
- `module`
- `clientIp`
- `targetKey`

## 注意事项

- 当前表结构没有单独的 `version` 或 `deleted` 字段；公共字段以 `BasePo` 和 DDL 为准。
- 该模块依赖统一异常处理和 MyBatis-Plus 分页能力。
