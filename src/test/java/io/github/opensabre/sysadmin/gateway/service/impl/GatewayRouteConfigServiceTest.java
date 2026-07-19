package io.github.opensabre.sysadmin.gateway.service.impl;

import io.github.opensabre.sysadmin.gateway.model.GatewayRoute;
import io.github.opensabre.sysadmin.gateway.model.GatewayRouteDefinition;
import io.github.opensabre.sysadmin.gateway.model.GatewayOauth2Client;
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

    @Test
    void shouldReplaceOnlyGatewayDefaultFiltersNode() {
        String yaml = """
                spring:
                  cloud:
                    gateway:
                      routes:
                        - id: base-organization
                          uri: lb://base-organization
                      default-filters:
                        - AddResponseHeader=X-Old, old
                """;
        GatewayRouteDefinition filter = definition("AddResponseHeader", Map.of("name", "X-Trace", "value", "enabled"));

        String updated = GatewayRouteConfigService.replaceDefaultFilters(yaml, List.of(filter));

        assertThat(GatewayRouteConfigService.parseRoutes(updated)).singleElement()
                .extracting(GatewayRoute::getId).isEqualTo("base-organization");
        assertThat(GatewayRouteConfigService.parseDefaultFilters(updated)).singleElement()
                .satisfies(item -> assertThat(item.getArgs()).containsEntry("name", "X-Trace"));
    }

    @Test
    void shouldRejectInvalidDefaultRateLimit() {
        GatewayRouteDefinition rateLimit = definition("RequestRateLimiter", Map.of(
                "redis-rate-limiter.replenishRate", "10",
                "redis-rate-limiter.burstCapacity", "5",
                "rate-limiter", "#{@defaultRedisRateLimiter}",
                "key-resolver", "#{@remoteAddressKeyResolver}"));

        org.assertj.core.api.Assertions.assertThatIllegalArgumentException()
                .isThrownBy(() -> GatewayRouteConfigService.validateDefaultFilters(List.of(rateLimit)))
                .withMessageContaining("突发容量不能小于补充速率");
    }

    @Test
    void shouldReplaceOauth2RegistrationsWithoutChangingRoutes() {
        String yaml = """
                spring:
                  cloud:
                    gateway:
                      routes: []
                  security:
                    oauth2:
                      client:
                        provider:
                          custom-issuer:
                            issuer-uri: http://authorization:8000
                        registration: {}
                """;
        GatewayOauth2Client client = new GatewayOauth2Client();
        client.setRegistrationId("base-gateway-local");
        client.setProvider("custom-issuer");
        client.setIssuerUri("http://authorization:8000");
        client.setClientId("base-gateway-local");
        client.setClientSecret("ENC(ciphertext)");
        client.setRedirectUri("http://localhost:3000/login/oauth2/code/base-gateway-client");
        client.setScopes(List.of("openid", "profile"));

        String updated = GatewayRouteConfigService.replaceOauth2Clients(yaml, List.of(client));

        assertThat(GatewayRouteConfigService.parseRoutes(updated)).isEmpty();
        assertThat(GatewayRouteConfigService.parseOauth2Clients(updated)).singleElement()
                .satisfies(item -> {
                    assertThat(item.getRegistrationId()).isEqualTo("base-gateway-local");
                    assertThat(item.getIssuerUri()).isEqualTo("http://authorization:8000");
                    assertThat(item.getClientSecret()).isEqualTo("ENC(ciphertext)");
                });
    }

    private GatewayRouteDefinition definition(String name, Map<String, String> args) {
        GatewayRouteDefinition definition = new GatewayRouteDefinition();
        definition.setName(name);
        definition.setArgs(args);
        return definition;
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
