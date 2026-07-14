package io.github.opensabre.sysadmin.gateway.rest;

import io.github.opensabre.sysadmin.gateway.model.GatewayRouteConfig;
import io.github.opensabre.sysadmin.gateway.service.IGatewayRouteConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 网关路由配置查询接口。
 */
@Tag(name = "网关路由")
@RestController
@RequestMapping("/gateway/routes")
public class GatewayRouteController {

    @Resource
    private IGatewayRouteConfigService gatewayRouteConfigService;

    /**
     * 查询配置中心中当前生效的显式路由。
     *
     * @return 配置版本和路由列表
     */
    @GetMapping
    @Operation(summary = "查询网关路由配置")
    public GatewayRouteConfig getCurrentConfig() {
        return gatewayRouteConfigService.getCurrentConfig();
    }
}
