package io.github.opensabre.sysadmin.gateway.service.impl;

import io.github.opensabre.sysadmin.gateway.model.GatewayRoute;
import io.github.opensabre.sysadmin.gateway.model.GatewayRouteDefinition;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GatewayRouteConfigServiceTest {

    @Test
    void shouldParseShortFormGatewayDefinitions() {
        String yaml = """
                spring:
                  cloud:
                    gateway:
                      routes:
                        - id: base-organization
                          uri: lb://base-organization
                          predicates:
                            - Path=/api/org/**
                          filters:
                            - StripPrefix=2
                """;

        List<GatewayRoute> routes = GatewayRouteConfigService.parseRoutes(yaml);

        assertThat(routes).singleElement().satisfies(route -> {
            assertThat(route.getId()).isEqualTo("base-organization");
            assertThat(route.getPredicates()).singleElement().satisfies(predicate ->
                    assertThat(predicate.getArgs()).containsEntry("pattern", "/api/org/**"));
            assertThat(route.getFilters()).singleElement().satisfies(filter ->
                    assertThat(filter.getArgs()).containsEntry("value", "2"));
        });
    }

    @Test
    void shouldReplaceOnlyGatewayRouteNode() {
        String yaml = """
                management:
                  endpoints:
                    web:
                      exposure:
                        include: health
                spring:
                  cloud:
                    gateway:
                      discovery:
                        locator:
                          enabled: true
                      routes: []
                """;
        GatewayRoute route = route("base-organization", "lb://base-organization", "Path", "pattern", "/api/org/**");

        String updated = GatewayRouteConfigService.replaceRoutes(yaml, List.of(route));

        assertThat(updated).contains("management:", "discovery:", "enabled: true");
        assertThat(GatewayRouteConfigService.parseRoutes(updated)).singleElement()
                .extracting(GatewayRoute::getId).isEqualTo("base-organization");
    }

    @Test
    void shouldRejectUnsupportedFilter() {
        GatewayRoute route = route("base-org", "lb://base-organization", "Path", "pattern", "/api/org/**");
        GatewayRouteDefinition filter = new GatewayRouteDefinition();
        filter.setName("SetStatus");
        filter.setArgs(Map.of("status", "200"));
        route.setFilters(List.of(filter));

        org.assertj.core.api.Assertions.assertThatIllegalArgumentException()
                .isThrownBy(() -> GatewayRouteConfigService.validateRoute(route))
                .withMessageContaining("不支持的过滤器");
    }

    private GatewayRoute route(String id, String uri, String predicateName, String argName, String argValue) {
        GatewayRoute route = new GatewayRoute();
        route.setId(id);
        route.setUri(uri);
        GatewayRouteDefinition predicate = new GatewayRouteDefinition();
        predicate.setName(predicateName);
        predicate.setArgs(Map.of(argName, argValue));
        route.setPredicates(List.of(predicate));
        return route;
    }
}
