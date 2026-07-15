package io.github.opensabre.sysadmin.gateway.model;

import jakarta.validation.constraints.NotBlank;

/** 删除网关路由时携带的 Nacos 基线版本。 */
public class GatewayRouteDeleteForm {

    @NotBlank(message = "配置版本不能为空，请刷新后重试")
    private String baseVersion;

    public String getBaseVersion() {
        return baseVersion;
    }

    public void setBaseVersion(String baseVersion) {
        this.baseVersion = baseVersion;
    }
}
