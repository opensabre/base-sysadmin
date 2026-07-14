package io.github.opensabre.sysadmin.gateway.service.impl;

import io.github.opensabre.sysadmin.gateway.model.GatewayRoute;
import io.github.opensabre.sysadmin.gateway.model.GatewayRouteConfig;
import io.github.opensabre.sysadmin.gateway.model.GatewayRouteDefinition;
import io.github.opensabre.sysadmin.gateway.service.IGatewayRouteConfigService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 从 Nacos 读取 base-gateway.yml，并提取显式网关路由。
 */
@Service
public class GatewayRouteConfigService implements IGatewayRouteConfigService {

    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private final String nacosServerUrl;
    private final String dataId;
    private final String group;

    public GatewayRouteConfigService(
            @Value("${opensabre.gateway.config.server-url:http://${REGISTER_HOST:localhost}:${REGISTER_PORT:8848}}") String nacosServerUrl,
            @Value("${opensabre.gateway.config.data-id:base-gateway.yml}") String dataId,
            @Value("${opensabre.gateway.config.group:DEFAULT_GROUP}") String group) {
        this.nacosServerUrl = nacosServerUrl.replaceAll("/$", "");
        this.dataId = dataId;
        this.group = group;
    }

    @Override
    public GatewayRouteConfig getCurrentConfig() {
        String content = readConfigContent();
        GatewayRouteConfig config = new GatewayRouteConfig();
        config.setVersion(md5(content));
        config.setRoutes(parseRoutes(content));
        return config;
    }

    private String readConfigContent() {
        String query = "dataId=" + URLEncoder.encode(dataId, StandardCharsets.UTF_8)
                + "&group=" + URLEncoder.encode(group, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder(URI.create(nacosServerUrl + "/nacos/v1/cs/configs?" + query))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200) {
                throw new IllegalStateException("读取网关配置失败，Nacos 返回 HTTP " + response.statusCode());
            }
            return response.body();
        } catch (IOException exception) {
            throw new IllegalStateException("无法连接配置中心读取网关路由", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("读取网关配置被中断", exception);
        }
    }

    @SuppressWarnings("unchecked")
    static List<GatewayRoute> parseRoutes(String content) {
        Object loaded = new Yaml().load(content);
        if (!(loaded instanceof Map<?, ?> root)) {
            return List.of();
        }
        Object spring = root.get("spring");
        Object cloud = child(spring, "cloud");
        Object gateway = child(cloud, "gateway");
        Object routeValue = child(gateway, "routes");
        if (!(routeValue instanceof List<?> routeList)) {
            return List.of();
        }
        List<GatewayRoute> routes = new ArrayList<>();
        for (Object routeValueItem : routeList) {
            if (!(routeValueItem instanceof Map<?, ?> routeMap)) {
                continue;
            }
            GatewayRoute route = new GatewayRoute();
            route.setId(stringValue(routeMap.get("id")));
            route.setUri(stringValue(routeMap.get("uri")));
            route.setOrder(numberValue(routeMap.get("order")));
            route.setPredicates(parseDefinitions(routeMap.get("predicates"), true));
            route.setFilters(parseDefinitions(routeMap.get("filters"), false));
            routes.add(route);
        }
        return routes;
    }

    private static Object child(Object parent, String name) {
        return parent instanceof Map<?, ?> values ? values.get(name) : null;
    }

    private static List<GatewayRouteDefinition> parseDefinitions(Object values, boolean predicate) {
        if (!(values instanceof List<?> definitions)) {
            return List.of();
        }
        List<GatewayRouteDefinition> result = new ArrayList<>();
        for (Object definition : definitions) {
            GatewayRouteDefinition routeDefinition = new GatewayRouteDefinition();
            if (definition instanceof String text) {
                int separator = text.indexOf('=');
                routeDefinition.setName(separator < 0 ? text : text.substring(0, separator));
                if (separator >= 0) {
                    String key = predicate && "Path".equals(routeDefinition.getName()) ? "pattern" : "value";
                    routeDefinition.setArgs(Map.of(key, text.substring(separator + 1)));
                }
            } else if (definition instanceof Map<?, ?> map) {
                routeDefinition.setName(stringValue(map.get("name")));
                routeDefinition.setArgs(stringArgs(map.get("args")));
            } else {
                continue;
            }
            result.add(routeDefinition);
        }
        return result;
    }

    private static Map<String, String> stringArgs(Object value) {
        if (!(value instanceof Map<?, ?> args)) {
            return Map.of();
        }
        Map<String, String> result = new LinkedHashMap<>();
        args.forEach((key, item) -> result.put(String.valueOf(key), stringValue(item)));
        return result;
    }

    private static String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private static int numberValue(Object value) {
        return value instanceof Number number ? number.intValue() : 0;
    }

    private static String md5(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("MD5").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("MD5 算法不可用", exception);
        }
    }
}
