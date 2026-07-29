# 全局错误码目录

## 介绍

错误码目录接收各应用在启动完成后上报的声明快照，集中维护错误码、文案、模块、应用归属、可见性和废弃状态。目录只用于治理与检索，不参与业务运行时异常转换。

## 配置

```yaml
opensabre:
  error-catalog:
    registration-token: ${ERROR_CATALOG_REGISTRATION_TOKEN:}
```

生产环境必须配置非空凭据，并与所有注册应用的 `opensabre.governance.error-catalog.registration-token` 保持一致。

## API

| 方法 | 路径 | 用途 |
| --- | --- | --- |
| `POST` | `/error-catalog/snapshots` | 注册应用完整快照；Header 为 `X-Opensabre-Error-Catalog-Token` |
| `GET` | `/error-catalog` | 分页查询；支持 `pageNum`、`pageSize`、`keywords`、`application`、`deprecated` |

注册端使用常量时间比较校验凭据。空凭据、错误凭据、同一错误码的非法归属或定义冲突均应拒绝。

## 归属规则

- 应用错误码由声明应用拥有，其他应用不能覆盖。
- 公共错误码固定归 `opensabre-framework` 所有，只有 Sysadmin 可以创建新的公共定义；
  其他应用可以幂等上报已有公共定义，但内容必须保持一致。
- Framework 0.7 未携带 `owner`、`scope` 时，已有公共定义沿用服务端可信归属；
  新错误码仍默认归上报应用所有。
- 快照是全量声明；已移除条目的生命周期以服务实现和数据库迁移为准。
- 旧错误码优先标记废弃，不要复用给新的业务含义。

## 数据与排障

表结构位于 `base_sys_error_catalog`，迁移脚本为 `V20260720_03__create_error_catalog_table.sql` 和 `V20260726_01__add_error_catalog_ownership.sql`。

应用条目未出现时依次检查：应用启动日志、注册 Token、Sysadmin 路由、归属冲突和数据库迁移。注册失败不会阻塞业务应用启动，因此必须监控告警日志。
