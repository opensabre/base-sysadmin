package io.github.opensabre.sysadmin.gateway.service;

import io.github.opensabre.sysadmin.gateway.model.GatewayRouteConfig;

/**
 * 读取配置中心中的网关路由。
 */
public interface IGatewayRouteConfigService {

    /**
     * 获取当前 Nacos 配置版本及其显式路由。
     *
     * @return 路由配置快照
     */
    GatewayRouteConfig getCurrentConfig();
}
