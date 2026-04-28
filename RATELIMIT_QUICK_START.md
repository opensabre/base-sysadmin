# 限次模块快速使用指南

## 一、快速开始

### 1. 启用限次模块
在主应用类上添加 `@EnableRateLimit` 注解：

```java
package io.github.opensabre.sysadmin;

import io.github.opensabre.sysadmin.ratelimit.annotations.EnableRateLimit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRateLimit  // ⭐ 添加此行
public class SysadminApplication {
    public static void main(String[] args) {
        SpringApplication.run(SysadminApplication.class, args);
    }
}
```

### 2. 配置 application.yml

```yaml
ratelimit:
  enabled: true
  key-prefix: "ratelimit:"
  default:
    algorithm: "COUNTER"
    max-count: 5
    period: 60
    dimensions:
      - "IP"
```

### 3. 使用注解

```java
import io.github.opensabre.sysadmin.ratelimit.annotations.RateLimit;
import io.github.opensabre.sysadmin.ratelimit.enums.RateLimitDimension;
import io.github.opensabre.sysadmin.ratelimit.enums.RateLimitAlgorithmType;

@RestController
@RequestMapping("/api")
public class MyController {

    @PostMapping("/login")
    @RateLimit(
        algorithm = RateLimitAlgorithmType.COUNTER,
        dimensions = {RateLimitDimension.IP},
        maxCount = 5,
        period = 60,
        message = "访问过于频繁，请1分钟后再试"
    )
    public Result<String> login(@RequestBody LoginForm form) {
        // 业务逻辑
        return Result.success("登录成功");
    }
}
```

---

## 二、常用场景

### 场景1：登录接口限次（IP维度）

```java
@PostMapping("/login")
@RateLimit(
    dimensions = {RateLimitDimension.IP},
    maxCount = 5,
    period = 60,
    message = "登录过于频繁，请1分钟后再试"
)
public Result<String> login(@RequestBody LoginForm form) {
    return Result.success("登录成功");
}
```

**说明**：每个IP地址1分钟内最多登录5次

---

### 场景2：短信发送限次（手机号维度）

```java
@PostMapping("/sms/send")
@RateLimit(
    dimensions = {RateLimitDimension.BUSINESS},
    maxCount = 3,
    period = 3600,
    key = "#request.phone",  // 从请求参数提取手机号
    message = "短信发送过于频繁，请1小时后再试"
)
public Result<String> sendSms(@RequestBody SmsRequest request) {
    return Result.success("短信发送成功");
}
```

**说明**：每个手机号1小时内最多发送3次短信

---

### 场景3：API调用限次（多维度）

```java
@PostMapping("/api/data")
@RateLimit(
    dimensions = {RateLimitDimension.IP, RateLimitDimension.DEVICE},
    maxCount = 10,
    period = 60,
    message = "调用过于频繁，请1分钟后再试"
)
public Result<Data> getData(@RequestBody DataRequest request) {
    return Result.success(data);
}
```

**说明**：同一个IP和设备组合1分钟内最多调用10次

---

### 场景4：重要操作限次（业务Key）

```java
@PostMapping("/money/transfer")
@RateLimit(
    dimensions = {RateLimitDimension.BUSINESS},
    maxCount = 3,
    period = 3600,
    key = "#request.userId",  // 使用用户ID作为限次Key
    message = "转账过于频繁，请1小时后再试"
)
public Result<String> transfer(@RequestBody TransferRequest request) {
    // 转账逻辑
    return Result.success("转账成功");
}
```

**说明**：每个用户1小时内最多转账3次

---

## 三、编程方式使用

### 1. 简单限次检查

```java
@Service
public class MyService {

    @Resource
    private IRateLimitService rateLimitService;

    public void doSomething() {
        // 使用默认配置检查限次
        RateLimitResult result = rateLimitService.checkLimit();

        if (!result.isAllowed()) {
            throw new RateLimitExceededException(result.getErrorMessage());
        }

        // 执行业务逻辑
    }
}
```

### 2. 自定义限次配置

```java
@Service
public class MyService {

    @Resource
    private IRateLimitService rateLimitService;

    public void doSomethingWithCustomConfig(String userId) {
        // 自定义配置
        RateLimitConfig config = RateLimitConfig.builder()
                .key(userId)
                .algorithm(RateLimitAlgorithmType.COUNTER)
                .dimensions(List.of(RateLimitDimension.BUSINESS))
                .maxCount(10)
                .period(600)
                .message("操作过于频繁，请10分钟后再试")
                .build();

        RateLimitResult result = rateLimitService.checkLimit(config);

        if (!result.isAllowed()) {
            throw new RateLimitExceededException(result.getErrorMessage());
        }

        // 执行业务逻辑
    }
}
```

### 3. 获取剩余次数

```java
@Service
public class MyService {

    @Resource
    private IRateLimitService rateLimitService;

    public void checkRemaining(String key) {
        int remaining = rateLimitService.getRemaining(key, 5, 60);
        System.out.println("剩余次数：" + remaining);
    }
}
```

### 4. 重置限次

```java
@Service
public class MyService {

    @Resource
    private IRateLimitService rateLimitService;

    public void resetUserLimit(String userId) {
        rateLimitService.resetLimit(userId);
    }
}
```

---

## 四、异常处理

### 全局异常处理器

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RateLimitExceededException.class)
    public Result<Void> handleRateLimitExceeded(
            RateLimitExceededException ex,
            HttpServletResponse response
    ) {
        // 设置响应头
        response.setHeader("X-RateLimit-Remaining", String.valueOf(ex.getRemaining()));
        response.setHeader("X-RateLimit-Reset", String.valueOf(ex.getResetTime()));

        // 返回错误结果
        return Result.fail("RATE_LIMIT_EXCEEDED", ex.getMessage());
    }
}
```

### 响应示例

**HTTP 状态码**: 429 Too Many Requests

**响应体**:
```json
{
  "code": "RATE_LIMIT_EXCEEDED",
  "mesg": "访问过于频繁，请1分钟后再试",
  "time": "2026-03-13T10:30:00.000Z"
}
```

**响应头**:
```
X-RateLimit-Limit: 5
X-RateLimit-Remaining: 0
X-RateLimit-Reset: 1678886400
```

---

## 五、参数说明

### @RateLimit 注解参数

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `algorithm` | `RateLimitAlgorithmType` | `COUNTER` | 限次算法类型 |
| `dimensions` | `RateLimitDimension[]` | `{IP}` | 限次维度数组 |
| `maxCount` | `int` | `5` | 最大次数 |
| `period` | `int` | `60` | 时间窗口（秒） |
| `key` | `String` | `""` | 业务Key（支持SpEL） |
| `keyPrefix` | `String` | `""` | Key前缀 |
| `enabled` | `boolean` | `true` | 是否启用 |
| `message` | `String` | "访问过于频繁，请稍后再试" | 超限错误消息 |
| `showRemaining` | `boolean` | `true` | 是否返回剩余次数 |
| `customDimensionExtractor` | `String` | `""` | 自定义提取器Bean名称 |

---

## 六、限次维度说明

### IP 维度
- **代码**: `RateLimitDimension.IP`
- **提取规则**: 优先级 X-Forwarded-For → X-Real-IP → RemoteAddr
- **适用场景**: 防止恶意IP攻击

### 设备维度
- **代码**: `RateLimitDimension.DEVICE`
- **提取规则**: User-Agent + IP 的 FNV 哈希
- **适用场景**: 防止同一设备频繁操作

### 业务维度
- **代码**: `RateLimitDimension.BUSINESS`
- **提取规则**: 通过注解的 `key` 参数指定（支持SpEL）
- **适用场景**: 手机号、邮箱、用户ID等业务标识

---

## 七、SpEL 表达式示例

### 提取请求参数
```java
@RateLimit(key = "#request.phone")
public Result<String> sendSms(@RequestBody SmsRequest request) { }
```

### 提取路径变量
```java
@GetMapping("/api/user/{userId}")
@RateLimit(key = "#userId")
public Result<String> getUser(@PathVariable String userId) { }
```

### 提取请求头
```java
@RateLimit(key = "#request.getHeader('X-User-ID')")
public Result<String> doSomething(HttpServletRequest request) { }
```

### 复杂表达式
```java
@RateLimit(key = "#request.userId + ':' + #request.operationType")
public Result<String> doOperation(@RequestBody OperationRequest request) { }
```

---

## 八、测试接口

项目提供了测试控制器，可通过以下接口测试限次功能：

```bash
# IP 限次测试
curl http://localhost:8020/test/ratelimit/ip

# 设备限次测试
curl http://localhost:8020/test/ratelimit/device

# 多维度限次测试
curl http://localhost:8020/test/ratelimit/multi

# 业务Key限次测试
curl -X POST http://localhost:8020/test/ratelimit/business \
  -H "Content-Type: application/json" \
  -d '{"phone": "13800138000"}'

# 编程方式限次测试
curl http://localhost:8020/test/ratelimit/programmatic?key=test123

# 获取剩余次数
curl http://localhost:8020/test/ratelimit/remaining?key=test123

# 重置限次
curl -X POST http://localhost:8020/test/ratelimit/reset?key=test123
```

---

## 九、常见问题

### Q1: 如何禁用限次？
**A**: 在注解中设置 `enabled = false`，或在配置文件中设置 `ratelimit.enabled = false`

### Q2: 如何调整限次严格程度？
**A**: 调整 `maxCount` 和 `period` 参数，或使用多个维度组合

### Q3: 如何在多个服务间共享限次？
**A**: 使用相同的 Redis 实例和 `keyPrefix`

### Q4: 限次数据何时清理？
**A**: Redis Key 会根据 `period` 参数自动过期清理

### Q5: 支持哪些限次算法？
**A**: Phase 1 支持 `COUNTER`（固定窗口），后续版本支持其他算法

### Q6: 如何实现精确到用户的限次？
**A**: 使用 `BUSINESS` 维度 + SpEL 表达式提取用户ID

---

## 十、最佳实践

### 1. 合理设置限次参数
- 登录接口：5次/分钟
- 短信发送：3次/小时
- 数据查询：100次/分钟
- 写操作：10次/分钟

### 2. 多维度组合使用
```java
@RateLimit(
    dimensions = {RateLimitDimension.IP, RateLimitDimension.DEVICE}
)
```

### 3. 友好的错误消息
```java
@RateLimit(
    message = "您操作过于频繁，请{n}秒后再试"
)
```

### 4. 添加监控告警
```java
// 在限次超限时记录日志
log.warn("Rate limit exceeded: {}", result);
```

### 5. 测试验证
- 使用测试接口验证配置
- 压测验证性能表现
- 监控验证准确性

---

## 十一、获取帮助

- 📖 完整文档：查看 `ratelimit-phase1-implementation-summary.md`
- 🧪 测试用例：查看 `RateLimitControllerTest.java`
- ⚙️ 配置示例：查看 `ratelimit-example.yml`

---

**祝使用愉快！** 🎉
