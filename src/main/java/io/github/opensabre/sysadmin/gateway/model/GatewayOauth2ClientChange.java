package io.github.opensabre.sysadmin.gateway.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/** 基于当前配置版本整体发布网关认证方式。 */
public class GatewayOauth2ClientChange {
    @NotBlank(message = "配置版本不能为空，请刷新后重试")
    private String baseVersion;
    @NotNull(message = "认证方式不能为空")
    @Valid
    private List<GatewayOauth2Client> clients = new ArrayList<>();
    public String getBaseVersion() { return baseVersion; }
    public void setBaseVersion(String baseVersion) { this.baseVersion = baseVersion; }
    public List<GatewayOauth2Client> getClients() { return clients; }
    public void setClients(List<GatewayOauth2Client> clients) { this.clients = clients == null ? new ArrayList<>() : clients; }
}
