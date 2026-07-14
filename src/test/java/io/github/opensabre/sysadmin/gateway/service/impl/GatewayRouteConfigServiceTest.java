package io.github.opensabre.sysadmin.gateway.service.impl;

import io.github.opensabre.sysadmin.gateway.model.GatewayRoute;
import org.junit.jupiter.api.Test;

import java.util.List;

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
}
