package io.github.opensabre.sysadmin.gateway.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Nacos 中定义的 Spring Cloud Gateway 显式路由。
 */
public class GatewayRoute {

    private String id;
    private String uri;
    private int order;
    private List<GatewayRouteDefinition> predicates = new ArrayList<>();
    private List<GatewayRouteDefinition> filters = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public List<GatewayRouteDefinition> getPredicates() {
        return predicates;
    }

    public void setPredicates(List<GatewayRouteDefinition> predicates) {
        this.predicates = predicates;
    }

    public List<GatewayRouteDefinition> getFilters() {
        return filters;
    }

    public void setFilters(List<GatewayRouteDefinition> filters) {
        this.filters = filters;
    }
}
