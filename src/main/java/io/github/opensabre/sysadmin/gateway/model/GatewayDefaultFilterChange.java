package io.github.opensabre.sysadmin.gateway.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/** 基于 Nacos 当前版本提交的全局默认过滤器变更。 */
public class GatewayDefaultFilterChange {
    @NotNull(message = "全局过滤器不能为空")
    @Valid
    private List<GatewayRouteDefinition> defaultFilters;
    @NotBlank(message = "配置版本不能为空，请刷新后重试")
    private String baseVersion;
    public List<GatewayRouteDefinition> getDefaultFilters() { return defaultFilters; }
    public void setDefaultFilters(List<GatewayRouteDefinition> defaultFilters) { this.defaultFilters = defaultFilters; }
    public String getBaseVersion() { return baseVersion; }
    public void setBaseVersion(String baseVersion) { this.baseVersion = baseVersion; }
}
