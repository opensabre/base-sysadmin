# 字典模块

## 当前实现

字典模块提供字典类型和字典项管理，兼容多个路由前缀，便于前端或旧接口迁移。

实现位于 `io.github.opensabre.sysadmin.dict` 包。

主要组件：

- `DictController`：字典 REST 入口
- `IDictTypeService` / `DictTypeService`
- `IDictItemService` / `DictItemService`
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

字典项：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/dicts/{dictCode}/items` | 字典项分页列表 |
| `GET` | `/dicts/{dictCode}/items/options` | 查询启用字典项选项 |
| `POST` | `/dicts/{dictCode}/items` | 新增字典项 |
| `GET` | `/dicts/{dictCode}/items/{id}/form` | 获取字典项表单数据 |
| `PUT` | `/dicts/{dictCode}/items/{id}` | 修改字典项 |
| `DELETE` | `/dicts/{dictCode}/items/{ids}` | 删除字典项，`ids` 支持逗号分隔 |

## 数据模型

`DictType` 字段：

- `name`
- `dictCode`
- `status`
- `remark`

`DictItem` 字段：

- `dictCode`
- `label`
- `value`
- `status`
- `sort`
- `tagType`

## 数据表

表结构由 `src/main/resources/db/os-base-sysadmin-ddl.sql` 维护：

- `base_sys_dict_type`
- `base_sys_dict_item`

初始数据由 `src/main/resources/db/os-base-sysadmin-db.sql` 维护，当前包含：

- `gender`
- `notice_level`
- `notice_type`
