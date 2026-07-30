# base-sysadmin

系统管理平台，提供审计、验证码、通知、限次和字典等基础管理能力。

## 文档

业务模块文档统一维护在 [docs/](docs/README.md)：

- [审计模块](docs/audit.md)
- [验证码模块](docs/captcha.md)
- [通知模块](docs/notification.md)
- [限次模块](docs/ratelimit.md)
- [字典模块](docs/dict.md)
- [内部 Token 密钥管理](docs/modules/internal-token-key-management.md)

## 本地构建

```bash
mvn clean package
```

## 本地运行

```bash
mvn spring-boot:run
```

默认端口为 `8020`，可通过 `SERVER_PORT` 覆盖。

## 分支策略

`main` 是默认开发与镜像发布主干。功能分支通过 Pull Request 合入 `main`，
发布流水线仅响应 `main` 和版本标签。
