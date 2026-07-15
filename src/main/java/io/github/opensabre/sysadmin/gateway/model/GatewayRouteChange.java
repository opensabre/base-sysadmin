package io.github.opensabre.sysadmin.gateway.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 基于一个 Nacos 配置版本提交的路由变更。
 *
 * <p>baseVersion 是防止两个管理员互相覆盖配置的乐观锁前置条件。</p>
 */
public class GatewayRouteChange {

    @NotNull(message = "路由不能为空")
    @Valid
    private GatewayRoute route;

    @NotBlank(message = "配置版本不能为空，请刷新后重试")
    private String baseVersion;

    public GatewayRoute getRoute() {
        return route;
    }

    public void setRoute(GatewayRoute route) {
        this.route = route;
    }

    public String getBaseVersion() {
        return baseVersion;
    }

    public void setBaseVersion(String baseVersion) {
        this.baseVersion = baseVersion;
    }
}
