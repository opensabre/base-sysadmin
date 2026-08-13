# 内部 Token 密钥管理

## 介绍

`base-sysadmin` 提供内部 Token 共享 HMAC 密钥的控制面。所有应用从同一份
Nacos 公共配置 `opensabre-common.yml` 读取 active/previous 双密钥；管理端只展示
密钥元数据，不通过 API 或日志返回密钥内容。

控制面只依赖这份稳定的 YAML 协议，不依赖尚未发布的
`opensabre-starter-security:0.7.0` Java 类型，因此可以独立发布。

该模块仅用于 Servlet 应用链路。0.7.0 暂不实现 WebFlux/WebClient 内部 Token。

## 功能

- 查询 active/previous 密钥 ID、配置版本和轮换时间。
- 在服务端生成 256 位随机密钥，并通过 Nacos CAS 发布。
- 将原 active 密钥切换为 previous，保留至少 125 秒的验证窗口。
- 到达保护期后退役 previous 密钥。
- 复用 Governance `@Audit` 和通用审计日志记录轮换、退役及失败信息。
- 默认关闭写操作，防止未配置完成时误轮换生产密钥。

## 配置

```yaml
opensabre:
  sysadmin:
    internal-token:
      write-enabled: false
      nacos-server-url: http://localhost:8848
      data-id: opensabre-common.yml
      group: DEFAULT_GROUP
      namespace: ""
      rotation-grace-period: 5m
```

可使用以下环境变量覆盖：

| 环境变量 | 默认值 | 说明 |
| --- | --- | --- |
| `INTERNAL_TOKEN_KEY_WRITE_ENABLED` | `false` | 是否允许轮换和退役 |
| `REGISTER_HOST` / `REGISTER_PORT` | `localhost` / `8848` | Nacos 地址 |
| `REGISTER_NAMESPACE` | 空 | Nacos namespace |
| `OPENSABRE_COMMON_CONFIG_DATA_ID` | `opensabre-common.yml` | 公共配置 Data ID |
| `OPENSABRE_COMMON_CONFIG_GROUP` | `DEFAULT_GROUP` | 公共配置 Group |
| `INTERNAL_TOKEN_KEY_ROTATION_GRACE_PERIOD` | `5m` | previous 密钥保护期，不得少于 125 秒 |

开启写操作前，必须确认各 Servlet 应用已经加载同一 namespace/group/data-id，
且内部 Token 校验和时钟同步正常。当前公共配置不支持热刷新，轮换后必须滚动重启
所有相关应用。

## API

基础路径：`/security/internal-token/keys`

| 方法 | 路径 | 用途 |
| --- | --- | --- |
| `GET` | `/security/internal-token/keys` | 查询安全的密钥状态元数据 |
| `POST` | `/security/internal-token/keys/rotate` | 生成并轮换 active 密钥 |
| `POST` | `/security/internal-token/keys/retire-previous` | 退役 previous 密钥 |

密钥操作审计复用 `/audit/log/conditions`，按
`module=INTERNAL_TOKEN_KEY` 查询。

轮换请求：

```json
{
  "expectedConfigVersion": 1,
  "newKeyId": "internal-20260725-01",
  "reason": "例行密钥轮换"
}
```

退役请求：

```json
{
  "expectedConfigVersion": 2,
  "reason": "previous 密钥保护期已结束"
}
```

`expectedConfigVersion` 用于乐观锁。版本不一致时应刷新状态，确认没有其他操作人
已经修改配置，再重新提交。密钥 ID 只能包含字母、数字、点、下划线和连字符，
长度不超过 64。

## 安全边界

- 新密钥只在服务端使用 `SecureRandom` 生成，不接受前端输入。
- 状态接口仅返回 key ID、是否已配置、配置版本和时间，不返回密钥。
- 密钥管理 API 只接受已验证的外部 JWT，并要求其中包含 `ADMIN` 角色。
- 未验证的 Header、请求参数和普通 `UserContextHolder` 内容不能成为操作人。
- Nacos 发布使用配置内容 MD5 做 CAS；并发修改会失败，不会覆盖他人变更。
- `write-enabled=false` 时，状态和通用审计仍可查询，轮换和退役均拒绝执行。

## 轮换流程

1. 查询当前状态并记录 `configVersion`。
2. 提交轮换请求。
3. 服务端生成新密钥，将旧 active 设置为 previous，并写入退役时间。
4. 使用 Nacos CAS 发布共享配置。
5. `@Audit` 将操作人、原因、目标 key id、响应或异常写入通用审计日志。
6. 按接收方优先、调用方随后滚动重启所有相关应用。
7. 确认每个实例均加载新的 `configVersion`，并等待超过保护期。
8. 使用最新 `configVersion` 提交 previous 退役请求。

## 事实源

- Controller：`internaltoken/rest/InternalTokenKeyController.java`
- Nacos 写入：`internaltoken/repository/NacosInternalTokenSharedConfigRepository.java`
- 轮换规则：`internaltoken/service/NacosInternalTokenKeyManager.java`
- 通用审计：Governance `@Audit` 与 `base_sys_audit_log`

## 当前交付边界

- 管理台页面已实现，只展示安全元数据和审计记录。
- examples 已接入首应用 JWT 校验、首次签发和 Servlet 逐跳重签配置。
- 生产写开关仍保持关闭；完成包含滚动重启的环境级双密钥轮换演练后再由运维开启。
- WebFlux/WebClient 链路不在 0.7.0 当前迭代范围内。
