package io.github.opensabre.sysadmin.gateway.rest;

import io.github.opensabre.sysadmin.gateway.model.GatewayRouteConfig;
import io.github.opensabre.sysadmin.gateway.model.GatewayRouteChange;
import io.github.opensabre.sysadmin.gateway.model.GatewayRouteDeleteForm;
import io.github.opensabre.sysadmin.gateway.service.IGatewayRouteConfigService;
import io.github.opensabre.governance.audit.annotations.Audit;
import io.github.opensabre.governance.audit.annotations.OperationType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

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

    /** 新增路由并立即发布至 Nacos。 */
    @PostMapping
    @Operation(summary = "新增并发布网关路由")
    @Audit(operationType = OperationType.CREATE, description = "新增并发布网关路由", module = "GATEWAY_ROUTE", response = true, key = "#change.route.id")
    public GatewayRouteConfig create(@Valid @RequestBody GatewayRouteChange change) {
        return gatewayRouteConfigService.create(change);
    }

    /** 修改路由并立即发布至 Nacos。 */
    @PutMapping("/{routeId}")
    @Operation(summary = "修改并发布网关路由")
    @Audit(operationType = OperationType.UPDATE, description = "修改并发布网关路由", module = "GATEWAY_ROUTE", response = true, key = "#routeId")
    public GatewayRouteConfig update(@PathVariable String routeId, @Valid @RequestBody GatewayRouteChange change) {
        return gatewayRouteConfigService.update(routeId, change);
    }

    /** 删除路由并立即发布至 Nacos。 */
    @DeleteMapping("/{routeId}")
    @Operation(summary = "删除并发布网关路由")
    @Audit(operationType = OperationType.DELETE, description = "删除并发布网关路由", module = "GATEWAY_ROUTE", response = true, key = "#routeId")
    public GatewayRouteConfig delete(@PathVariable String routeId, @Valid @RequestBody GatewayRouteDeleteForm form) {
        return gatewayRouteConfigService.delete(routeId, form.getBaseVersion());
    }
}
