package io.github.opensabre.sysadmin.gateway.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 网关配置中的断言或过滤器定义。
 */
public class GatewayRouteDefinition {

    private String name;
    private Map<String, String> args = new LinkedHashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public void setArgs(Map<String, String> args) {
        this.args = args;
    }
}
