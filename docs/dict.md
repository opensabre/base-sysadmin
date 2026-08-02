# 字典模块

## 当前实现

字典模块提供字典类型和字典项管理，兼容多个路由前缀，便于前端或旧接口迁移。

实现位于 `io.github.opensabre.sysadmin.dict` 包。

主要组件：

- `DictController`：字典 REST 入口
- `IDictTypeService` / `DictTypeService`
- `IDictItemService` / `DictItemService`
- `IDictionaryRegistrationService` / `DictionaryRegistrationService`
- `DictType`：字典类型，表名 `base_sys_dict_type`
- `DictItem`：字典项，表名 `base_sys_dict_item`
- `PageData`、`OptionItem`、`DictItemOption`：分页和选项响应对象

## 路由前缀

`DictController` 同时支持：

- `/dicts`
- `/api/v1/dicts`
- `/v1/dicts`

以下 API 均以其中任一路由前缀为基础。

## API

字典类型：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/dicts` | 字典分页列表，支持 `pageNum`、`pageSize`、`keywords`、`status` |
| `GET` | `/dicts/options` | 查询启用字典选项 |
| `GET` | `/dicts/{id}/form` | 获取字典表单数据 |
| `POST` | `/dicts` | 新增字典 |
| `PUT` | `/dicts/{id}` | 修改字典 |
| `DELETE` | `/dicts/{ids}` | 删除字典，`ids` 支持逗号分隔 |
| `POST` | `/dicts/snapshots` | 接收 Framework 0.7 应用字典完整快照 |

字典项：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/dicts/{dictCode}/items` | 字典项分页列表 |
| `GET` | `/dicts/{dictCode}/items/options` | 查询启用字典项选项 |
| `GET` | `/dicts/{dictCode}/items/all` | 查询全部字典项，包含停用项 |
| `POST` | `/dicts/{dictCode}/items` | 新增字典项 |
| `GET` | `/dicts/{dictCode}/items/{id}/form` | 获取字典项表单数据 |
| `PUT` | `/dicts/{dictCode}/items/{id}` | 修改字典项 |
| `DELETE` | `/dicts/{dictCode}/items/{ids}` | 删除字典项，`ids` 支持逗号分隔 |

## 数据模型

`DictType` 字段：

- `name`
- `dictCode`
- `sourceApplication`：快照注册应用；空值表示管理员维护的存量字典
- `status`
- `remark`

`DictItem` 字段：

- `dictCode`
- `label`
- `value`
- `status`
- `sort`
- `tagType`

## Framework 0.7 治理协议

业务应用通过 `POST /dicts/snapshots` 上报完整快照，并在
`X-Opensabre-Dictionary-Token` 请求头携带注册凭据。Sysadmin 配置：

```yaml
opensabre:
  governance:
    dictionary:
      registration-token: ${DICTIONARY_REGISTRATION_TOKEN:${GOVERNANCE_REGISTRATION_TOKEN:${ERROR_CATALOG_REGISTRATION_TOKEN:}}}
```

字典与错误码注册默认共享 `GOVERNANCE_REGISTRATION_TOKEN`。如需能力级隔离，可用
`DICTIONARY_REGISTRATION_TOKEN` 单独覆盖；`ERROR_CATALOG_REGISTRATION_TOKEN` 保留为旧部署兼容回退。
服务端使用常量时间比较，且不会在日志、响应或审计中输出凭据。未配置凭据时注册接口默认拒绝请求。

`dictCode` 的首次定义归注册应用所有。管理员维护的存量字典不会被应用自动接管，
其他应用也不能覆盖已有归属。同一快照出现重复 `dictCode` 或重复字典项值会整批拒绝。
同一应用后续快照会覆盖名称和条目；快照中消失的字典或条目只会转为停用，不会删除，
因此 `items/all` 和 Framework 的 `labelOf` 仍可回显历史值，而 `items`、`contains`
及现有 options 接口只使用启用项。注册在事务内完成，失败不会留下部分更新；
Framework 侧异步注册失败不会阻止业务应用启动。

## 数据表

表结构由 `src/main/resources/db/os-base-sysadmin-ddl.sql` 维护：

- `base_sys_dict_type`
- `base_sys_dict_item`

初始数据由 `src/main/resources/db/os-base-sysadmin-db.sql` 维护，当前包含：

- `gender`
- `notice_level`
- `notice_type`

## 内置应用字典

`base-sysadmin` 启动时声明并注册管理端统计页面使用的 `usage_object_type`、
`usage_event` 和 `usage_granularity`。`gender`、`notice_level`、`notice_type` 仍由管理员维护，
不会被应用快照自动接管。
