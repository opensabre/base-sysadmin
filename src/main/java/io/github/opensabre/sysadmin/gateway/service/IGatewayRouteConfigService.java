package io.github.opensabre.sysadmin.gateway.service;

import io.github.opensabre.sysadmin.gateway.model.GatewayRouteConfig;
import io.github.opensabre.sysadmin.gateway.model.GatewayRouteChange;

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

    /** 将一条新增或修改后的路由发布到 Nacos。 */
    GatewayRouteConfig create(GatewayRouteChange change);

    /** 将指定路由替换为新定义并发布到 Nacos。 */
    GatewayRouteConfig update(String routeId, GatewayRouteChange change);

    /** 使用调用方读取到的版本删除一条路由并发布到 Nacos。 */
    GatewayRouteConfig delete(String routeId, String baseVersion);
}
