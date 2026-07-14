# base-sysadmin

系统管理平台，提供审计、验证码、通知、限次和字典等基础管理能力。

## 文档

业务模块文档统一维护在 [docs/](docs/README.md)：

- [审计模块](docs/audit.md)
- [验证码模块](docs/captcha.md)
- [通知模块](docs/notification.md)
- [限次模块](docs/ratelimit.md)
- [字典模块](docs/dict.md)

## 本地构建

```bash
mvn clean package
```

## 本地运行

```bash
mvn spring-boot:run
```

默认端口为 `8020`，可通过 `SERVER_PORT` 覆盖。
