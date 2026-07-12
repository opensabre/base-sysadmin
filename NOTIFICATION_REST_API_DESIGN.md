# 通知模块REST API设计文档 (Notification Module REST API Design Document)

## 1. 概述 (Overview)

本文档描述了通知模块的REST API设计，用于通过HTTP接口发送各种类型的通知，包括短信、邮件、微信等。API设计遵循RESTful原则，提供统一的通知发送入口。通知类型由模板代码自动确定，无需显式传递。

## 2. API 设计目标 (API Design Goals)

- **统一入口**: 提供单一端点支持多种通知类型
- **灵活参数**: 支持位置参数和键值对参数两种模式
- **扩展性**: 易于扩展新的通知类型和模板
- **安全性**: 包含必要的认证和权限控制
- **易用性**: 简单直观的API调用方式

## 3. API 接口定义 (API Endpoint Definition)

### 3.1 发送通知接口 (Send Notification API)

#### 基本信息
- **请求路径**: `POST /notification/send`
- **请求方式**: POST
- **内容类型**: `application/json`
- **认证方式**: JWT Token 或 API Key

#### 请求参数 (Request Parameters)

##### 请求体 (Request Body)
```json
{
  "target": "接收者地址(手机号、邮箱等)",
  "templateCode": "模板代码",
  "args": [],
  "mapArgs": {}
}
```

##### 字段说明 (Field Description)
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| target | String | 是 | 接收者地址，如手机号、邮箱地址 |
| templateCode | String | 是 | 模板代码，对应NotificationTemplate枚举中的code，通知类型由此自动确定 |
| args | Array | 否 | 位置参数数组，用于String.format格式化，与mapArgs二选一 |
| mapArgs | Object | 否 | 键值对参数对象，用于模板参数替换，与args二选一 |

#### 响应格式 (Response Format)

API响应将直接返回响应对象，符合简化后的设计。

##### 成功响应 (Success Response)
```json
{
  "messageId": "消息唯一标识",
  "sentTime": "发送时间戳"
}
```

##### 错误响应 (Error Response)
```json
{
  "timestamp": "错误发生的时间戳",
  "status": "HTTP状态码",
  "error": "错误类型",
  "message": "错误信息",
  "path": "请求路径"
}
```

## 4. API 详细设计 (Detailed API Design)

### 4.1 Controller 层设计 (Controller Layer Design)

#### NotificationController 类
```java
@RestController
@RequestMapping("/notification")
@Slf4j
public class NotificationController {

    @Resource
    private NotificationServiceManager notificationServiceManager;

    /**
     * 发送通知
     * 
     * @param form 发送通知请求参数
     * @return 发送结果
     */
    @PostMapping("/send")
    public NotificationResponse sendNotification(@Valid @RequestBody NotificationForm form) {
        // 实现逻辑
    }
}
```

### 4.2 Request/Response DTO 设计 (Request/Response DTO Design)

#### NotificationForm 类
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationForm {
    
    @NotBlank(message = "目标地址不能为空")
    private String target;  // 接收者地址
    
    @NotBlank(message = "模板代码不能为空")
    private String templateCode;  // NotificationTemplate枚举code
    
    private Object[] args;  // 位置参数，与mapArgs二选一
    
    private Map<String, String> mapArgs;  // 键值对参数，与args二选一
}
```

> **注意**: `args` 和 `mapArgs` 参数是互斥的，只能选择其中一种方式传递模板参数。
> - 当使用位置参数时（如短信验证码模板），使用 `args` 数组
> - 当使用键值对参数时（如邮件模板），使用 `mapArgs` 对象
> - 两个参数同时存在时，系统会优先使用 `args` 参数

#### NotificationResponse 类
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    
    private String messageId;  // 消息唯一标识
    private Long sentTime;       // 发送时间戳
}
```

### 4.3 Service 层集成 (Service Layer Integration)

#### 与现有服务集成
- 利用现有的 [NotificationServiceManager](file:///Users/zhoutaoo/WorkSpaces/IdeaProjects/opensabre/base-sysadmin/src/main/java/io/github/opensabre/sysadmin/notification/service/NotificationServiceManager.java#L17-L58) 来分发通知请求
- 通过 [NotificationTemplate](file:///Users/zhoutaoo/WorkSpaces/IdeaProjects/opensabre/base-sysadmin/src/main/java/io/github/opensabre/sysadmin/notification/enums/NotificationTemplate.java#L9-L56) 枚举中的类型信息自动确定通知渠道
- 支持现有 [INotificationService](file:///Users/zhoutaoo/WorkSpaces/IdeaProjects/opensabre/base-sysadmin/src/main/java/io/github/opensabre/sysadmin/notification/service/INotificationService.java#L12-L39) 实现的扩展

## 5. API 使用示例 (API Usage Examples)

### 5.1 发送短信验证码 (Send SMS Verification Code)
```bash
curl -X POST http://localhost:8080/notification/send \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "target": "13800138000",
    "templateCode": "CAPTCHA",
    "args": ["123456", 5]
  }'
```

### 5.2 发送邮件通知 (Send Email Notification)
```bash
curl -X POST http://localhost:8080/notification/send \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "target": "user@example.com",
    "templateCode": "WELCOME_EMAIL",
    "mapArgs": {
      "username": "张三",
      "company": "公司名称"
    }
  }'
```

### 5.3 发送微信通知 (Send WeChat Notification)
```bash
curl -X POST http://localhost:8080/notification/send \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "target": "openid123456",
    "templateCode": "ORDER_STATUS",
    "args": ["订单号123", "已发货"]
  }'
```

## 6. 安全考虑 (Security Considerations)

### 6.1 认证和授权 (Authentication and Authorization)
- 使用JWT Token或API Key进行身份验证
- 对敏感操作进行权限检查
- 限制API调用频率以防止滥用

### 6.2 输入验证 (Input Validation)
- 验证目标地址格式的有效性
- 验证模板代码的存在性
- 防止注入攻击和恶意内容

### 6.3 数据保护 (Data Protection)
- 对敏感信息进行加密存储
- 日志中不记录敏感数据
- 符合隐私保护法规

## 7. 错误处理 (Error Handling)

### 7.1 HTTP状态码 (HTTP Status Codes)
- `200 OK`: 请求成功处理
- `400 Bad Request`: 请求参数错误
- `401 Unauthorized`: 认证失败
- `403 Forbidden`: 权限不足
- `429 Too Many Requests`: 请求频率超限
- `500 Internal Server Error`: 服务器内部错误

### 7.2 错误码定义 (Error Code Definitions)
| 错误码 | 说明 |
|--------|------|
| N0002 | 目标地址格式错误 |
| N0003 | 模板代码不存在 |
| N0005 | 通知发送失败 |
| N0006 | 调用频率超限 |

## 8. 性能优化 (Performance Optimization)

### 8.1 异步处理 (Asynchronous Processing)
- 将通知发送操作异步化，提高API响应速度
- 使用消息队列处理大量通知请求

### 8.2 缓存机制 (Caching Mechanism)
- 缓存常用模板内容
- 缓存频繁使用的配置信息

### 8.3 批量处理 (Batch Processing)
- 支持批量发送通知，提高处理效率
- 优化数据库操作

## 9. 监控和日志 (Monitoring and Logging)

### 9.1 日志记录 (Logging)
- 记录所有通知发送请求
- 记录发送结果和错误信息
- 记录性能指标

### 9.2 监控指标 (Monitoring Metrics)
- API调用次数和成功率
- 各种通知类型的分布
- 平均响应时间
- 错误率统计

## 10. 扩展性设计 (Extensibility Design)

### 10.1 新增通知类型 (Adding New Notification Types)
- 添加新的 [NotificationType](file:///Users/zhoutaoo/WorkSpaces/IdeaProjects/opensabre/base-sysadmin/src/main/java/io/github/opensabre/sysadmin/notification/enums/NotificationType.java#L9-L21) 枚举值
- 实现对应的 [INotificationService](file:///Users/zhoutaoo/WorkSpaces/IdeaProjects/opensabre/base-sysadmin/src/main/java/io/github/opensabre/sysadmin/notification/service/INotificationService.java#L12-L39) 服务
- 无需修改API接口

### 10.2 新增模板 (Adding New Templates)
- 在 [NotificationTemplate](file:///Users/zhoutaoo/WorkSpaces/IdeaProjects/opensabre/base-sysadmin/src/main/java/io/github/opensabre/sysadmin/notification/enums/NotificationTemplate.java#L9-L56) 枚举中添加新模板
- 支持运行时动态加载模板

## 11. 配置选项 (Configuration Options)

### 11.1 应用配置 (Application Configuration)
```yaml
opensabre:
  notification:
    api:
      rate-limit: 100  # 每分钟最大请求数
      timeout: 30s     # 请求超时时间
    default-template-path: classpath:templates/  # 默认模板路径
```

## 12. 测试策略 (Testing Strategy)

### 12.1 单元测试 (Unit Tests)
- 测试Controller层的参数验证
- 测试Service层的业务逻辑
- 测试异常处理逻辑

### 12.2 集成测试 (Integration Tests)
- 测试完整的API调用链路
- 测试各种通知类型的发送
- 测试错误场景处理

---

*本文档版本: 1.0*  
*最后更新: 2026年1月*