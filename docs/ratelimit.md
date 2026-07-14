# 限次模块

## 当前实现

限次模块当前提供集中式限次检查服务和动态场景管理。代码中没有本地 `@RateLimit` 注解或 `@EnableRateLimit` 开关；远程调用方可通过 `/ratelimit/check` 提交限次配置或场景编码。

实现位于 `io.github.opensabre.sysadmin.ratelimit` 包。

主要组件：

- `RateLimitController`：限次检查入口，路径前缀 `/ratelimit`
- `RateLimitSceneController`：限次场景管理，路径前缀 `/ratelimit/scenes`
- `IRateLimitService` / `RateLimitServiceImpl`：限次服务
- `CounterAlgorithm`：固定窗口计数算法
- `SlidingWindowAlgorithm`：滑动窗口算法
- `RedisRateLimitStorage`：Redis 存储
- `RateLimitScene`：动态限次场景配置，表名 `base_sys_ratelimit_scene`

## API

限次检查：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST` | `/ratelimit/check` | 执行限次检查 |

`RateLimitCheckForm` 字段：

- `sceneCode`
- `key`
- `keyPrefix`
- `algorithm`
- `dimensions`
- `dimensionValues`
- `maxCount`
- `period`
- `enabled`

请求示例：

```json
{
  "keyPrefix": "login",
  "algorithm": "COUNTER",
  "dimensions": ["IP"],
  "dimensionValues": {
    "IP": "127.0.0.1"
  },
  "maxCount": 5,
  "period": 60,
  "enabled": true
}
```

动态场景调用示例：

```json
{
  "sceneCode": "LOGIN",
  "dimensionValues": {
    "IP": "127.0.0.1"
  },
  "enabled": true
}
```

限次场景管理：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/ratelimit/scenes` | 查询限次场景列表 |
| `GET` | `/ratelimit/scenes/enabled` | 查询启用场景 |
| `GET` | `/ratelimit/scenes/{sceneCode}` | 获取限次场景 |
| `POST` | `/ratelimit/scenes` | 创建限次场景 |
| `PUT` | `/ratelimit/scenes/{sceneCode}` | 更新限次场景 |
| `DELETE` | `/ratelimit/scenes/{sceneCode}` | 删除限次场景 |

## 场景配置

`RateLimitScene` 字段：

- `sceneCode`
- `sceneName`
- `algorithm`
- `dimensions`：逗号分隔，例如 `IP,DEVICE`
- `keyPrefix`
- `maxCount`
- `period`
- `enabled`
- `description`

`RateLimitScene.toConfig()` 会将表配置转换为 `RateLimitConfig`。

## 算法和维度

算法枚举：

- `COUNTER`
- `SLIDING_WINDOW`

维度枚举：

- `IP`
- `DEVICE`
- `BUSINESS`
- `USER`
- `TENANT`
- `CUSTOM`

`/ratelimit/check` 的远程调用通常应直接传 `key`，或同时传 `dimensions` 与 `dimensionValues`。服务会把维度值拼接成限次 key。

## 配置

`src/main/resources/ratelimit-example.yml` 提供配置样例。运行配置由 `RateLimitProperties` 读取，主要包括：

- `enabled`
- `key-prefix`
- 默认算法、次数、周期、维度

## 数据表

表结构由 `src/main/resources/db/os-base-sysadmin-ddl.sql` 维护：

- `base_sys_ratelimit_scene`

## 编程式使用

内部服务可注入 `IRateLimitService`：

```java
RateLimitConfig config = RateLimitConfig.builder()
        .key("login:127.0.0.1")
        .algorithm(RateLimitAlgorithmType.COUNTER)
        .maxCount(5)
        .period(60)
        .enabled(true)
        .build();

RateLimitResult result = rateLimitService.checkLimit(config);
```

辅助方法：

- `checkLimit()`
- `checkLimit(List<RateLimitDimension> dimensions)`
- `checkLimit(String key, int maxCount, int period, RateLimitAlgorithmType algorithm)`
- `checkLimit(RateLimitConfig config)`
- `resetLimit(String key)`
- `getRemaining(String key, int maxCount, int period)`
- `generateKey(List<RateLimitDimension> dimensions, String keyPrefix)`
