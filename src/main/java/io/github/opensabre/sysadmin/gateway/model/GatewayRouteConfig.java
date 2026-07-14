package io.github.opensabre.sysadmin.gateway.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关路由配置中心快照。
 */
public class GatewayRouteConfig {

    private String version;
    private List<GatewayRoute> routes = new ArrayList<>();

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<GatewayRoute> getRoutes() {
        return routes;
    }

    public void setRoutes(List<GatewayRoute> routes) {
        this.routes = routes;
    }
}
