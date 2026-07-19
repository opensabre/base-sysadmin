package io.github.opensabre.sysadmin.gateway.service.impl;

import io.github.opensabre.sysadmin.gateway.model.GatewayRoute;
import io.github.opensabre.sysadmin.gateway.model.GatewayRouteConfig;
import io.github.opensabre.sysadmin.gateway.model.GatewayRouteChange;
import io.github.opensabre.sysadmin.gateway.model.GatewayRouteDefinition;
import io.github.opensabre.sysadmin.gateway.model.GatewayDefaultFilterChange;
import io.github.opensabre.sysadmin.gateway.model.GatewayOauth2Client;
import io.github.opensabre.sysadmin.gateway.model.GatewayOauth2ClientChange;
import io.github.opensabre.sysadmin.gateway.service.IGatewayRouteConfigService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.jasypt.encryption.StringEncryptor;
import jakarta.annotation.Resource;
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
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 从 Nacos 读取 base-gateway.yml，并提取显式网关路由。
 */
@Service
public class GatewayRouteConfigService implements IGatewayRouteConfigService {

    private static final Pattern ROUTE_ID_PATTERN = Pattern.compile("[A-Za-z0-9][A-Za-z0-9._-]{0,99}");
    private static final Set<String> SUPPORTED_PREDICATES = Set.of(
            "Path", "Host", "Method", "Header", "Query", "RemoteAddr", "After", "Before", "Between");
    private static final Set<String> SUPPORTED_FILTERS = Set.of(
            "StripPrefix", "PrefixPath", "RewritePath", "AddRequestHeader", "AddResponseHeader",
            "RemoveRequestHeader", "RemoveResponseHeader", "Retry", "CircuitBreaker");
    private static final Set<String> SUPPORTED_DEFAULT_FILTERS = Set.of(
            "TokenRelay", "AddRequestHeader", "AddResponseHeader", "RemoveRequestHeader", "RemoveResponseHeader",
            "Retry", "CircuitBreaker", "RequestRateLimiter");

    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private final String nacosServerUrl;
    private final String dataId;
    private final String group;

    @Resource
    private StringEncryptor stringEncryptor;

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
        config.setDefaultFilters(parseDefaultFilters(content));
        config.setOauth2Clients(maskSecrets(parseOauth2Clients(content)));
        return config;
    }

    @Override
    public GatewayRouteConfig create(GatewayRouteChange change) {
        return changeRoutes(change.getBaseVersion(), routes -> {
            GatewayRoute route = change.getRoute();
            validateRoute(route);
            if (routes.stream().anyMatch(item -> item.getId().equals(route.getId()))) {
                throw new IllegalArgumentException("路由 ID 已存在：" + route.getId());
            }
            routes.add(route);
        });
    }

    @Override
    public GatewayRouteConfig update(String routeId, GatewayRouteChange change) {
        return changeRoutes(change.getBaseVersion(), routes -> {
            GatewayRoute route = change.getRoute();
            validateRoute(route);
            int index = findRouteIndex(routes, routeId);
            if (index < 0) {
                throw new IllegalArgumentException("路由不存在：" + routeId);
            }
            if (!routeId.equals(route.getId()) && routes.stream().anyMatch(item -> item.getId().equals(route.getId()))) {
                throw new IllegalArgumentException("路由 ID 已存在：" + route.getId());
            }
            routes.set(index, route);
        });
    }

    @Override
    public GatewayRouteConfig delete(String routeId, String baseVersion) {
        return changeRoutes(baseVersion, routes -> {
            int index = findRouteIndex(routes, routeId);
            if (index < 0) {
                throw new IllegalArgumentException("路由不存在：" + routeId);
            }
            routes.remove(index);
        });
    }

    @Override
    public GatewayRouteConfig updateDefaultFilters(GatewayDefaultFilterChange change) {
        String content = readConfigContent();
        String currentVersion = md5(content);
        if (!currentVersion.equals(change.getBaseVersion())) {
            throw new IllegalStateException("网关配置已被其他人修改，请刷新后重试");
        }
        validateDefaultFilters(change.getDefaultFilters());
        String updatedContent = replaceDefaultFilters(content, change.getDefaultFilters());
        publishConfig(updatedContent, currentVersion);
        GatewayRouteConfig result = new GatewayRouteConfig();
        result.setVersion(md5(updatedContent));
        result.setRoutes(parseRoutes(updatedContent));
        result.setDefaultFilters(change.getDefaultFilters());
        return result;
    }

    @Override
    public GatewayRouteConfig updateOauth2Clients(GatewayOauth2ClientChange change) {
        String content = readConfigContent();
        String currentVersion = md5(content);
        if (!currentVersion.equals(change.getBaseVersion())) {
            throw new IllegalStateException("网关配置已被其他人修改，请刷新后重试");
        }
        validateOauth2Clients(change.getClients());
        List<GatewayOauth2Client> merged = mergeSecrets(change.getClients(), parseOauth2Clients(content));
        String updatedContent = replaceOauth2Clients(content, merged);
        publishConfig(updatedContent, currentVersion);
        GatewayRouteConfig result = new GatewayRouteConfig();
        result.setVersion(md5(updatedContent));
        result.setRoutes(parseRoutes(updatedContent));
        result.setDefaultFilters(parseDefaultFilters(updatedContent));
        result.setOauth2Clients(maskSecrets(merged));
        return result;
    }

    /**
     * 每次发布都从 Nacos 重新读取全文，并让 Nacos 以 casMd5 执行最终比较，避免覆盖其他管理员的变更。
     */
    private GatewayRouteConfig changeRoutes(String baseVersion, RouteMutator mutator) {
        String content = readConfigContent();
        String currentVersion = md5(content);
        if (!currentVersion.equals(baseVersion)) {
            throw new IllegalStateException("网关配置已被其他人修改，请刷新后重试");
        }
        List<GatewayRoute> routes = new ArrayList<>(parseRoutes(content));
        mutator.mutate(routes);
        String updatedContent = replaceRoutes(content, routes);
        publishConfig(updatedContent, currentVersion);
        GatewayRouteConfig result = new GatewayRouteConfig();
        result.setVersion(md5(updatedContent));
        result.setRoutes(routes);
        return result;
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

    /** 使用 Nacos 配置发布接口的 casMd5 参数提交全文，false 表示版本已冲突。 */
    private void publishConfig(String content, String baseVersion) {
        String body = "dataId=" + URLEncoder.encode(dataId, StandardCharsets.UTF_8)
                + "&group=" + URLEncoder.encode(group, StandardCharsets.UTF_8)
                + "&type=yaml"
                + "&casMd5=" + URLEncoder.encode(baseVersion, StandardCharsets.UTF_8)
                + "&content=" + URLEncoder.encode(content, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder(URI.create(nacosServerUrl + "/nacos/v1/cs/configs"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200) {
                throw new IllegalStateException("发布网关配置失败，Nacos 返回 HTTP " + response.statusCode());
            }
            if (!Boolean.parseBoolean(response.body())) {
                throw new IllegalStateException("网关配置已被其他人修改，请刷新后重试");
            }
        } catch (IOException exception) {
            throw new IllegalStateException("无法连接配置中心发布网关路由", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("发布网关配置被中断", exception);
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

    @SuppressWarnings("unchecked")
    static List<GatewayRouteDefinition> parseDefaultFilters(String content) {
        Object loaded = new Yaml().load(content);
        Object gateway = child(child(loaded instanceof Map<?, ?> root ? root.get("spring") : null, "cloud"), "gateway");
        return parseDefinitions(child(gateway, "default-filters"), false);
    }

    @SuppressWarnings("unchecked")
    static List<GatewayOauth2Client> parseOauth2Clients(String content) {
        Object loaded = new Yaml().load(content);
        Object registrationValue = child(child(child(child(loaded instanceof Map<?, ?> root ? root.get("spring") : null,
                "security"), "oauth2"), "client"), "registration");
        Object providerValue = child(child(child(child(loaded instanceof Map<?, ?> root ? root.get("spring") : null,
                "security"), "oauth2"), "client"), "provider");
        Object disabledValue = child(child(child(loaded instanceof Map<?, ?> root ? root.get("opensabre") : null,
                "gateway"), "oauth2"), "disabled-registration-ids");
        Set<String> disabled = disabledValue instanceof List<?> values
                ? values.stream().map(String::valueOf).collect(java.util.stream.Collectors.toSet()) : Set.of();
        if (!(registrationValue instanceof Map<?, ?> registrations)) return List.of();
        List<GatewayOauth2Client> result = new ArrayList<>();
        registrations.forEach((id, value) -> {
            if (!(value instanceof Map<?, ?> registration)) return;
            GatewayOauth2Client item = new GatewayOauth2Client();
            item.setRegistrationId(String.valueOf(id));
            item.setProvider(stringValue(registration.get("provider")));
            Object provider = providerValue instanceof Map<?, ?> providers ? providers.get(item.getProvider()) : null;
            item.setIssuerUri(stringValue(child(provider, "issuer-uri")));
            item.setClientId(stringValue(registration.get("client-id")));
            item.setClientSecret(stringValue(registration.get("client-secret")));
            item.setRedirectUri(stringValue(registration.get("redirect-uri")));
            Object scopes = registration.get("scope");
            if (scopes instanceof List<?> values) item.setScopes(values.stream().map(String::valueOf).toList());
            Object enabled = registration.get("enabled");
            item.setEnabled(!disabled.contains(item.getRegistrationId()) && (enabled == null || !"false".equals(String.valueOf(enabled))));
            result.add(item);
        });
        return result;
    }

    @SuppressWarnings("unchecked")
    static String replaceOauth2Clients(String content, List<GatewayOauth2Client> clients) {
        Object loaded = new Yaml().load(content);
        if (!(loaded instanceof Map<?, ?> root)) throw new IllegalStateException("网关配置不是有效的 YAML 对象");
        Map<Object, Object> spring = (Map<Object, Object>) root.get("spring");
        Map<Object, Object> security = (Map<Object, Object>) spring.computeIfAbsent("security", key -> new LinkedHashMap<>());
        Map<Object, Object> oauth2 = (Map<Object, Object>) security.computeIfAbsent("oauth2", key -> new LinkedHashMap<>());
        Map<Object, Object> client = (Map<Object, Object>) oauth2.computeIfAbsent("client", key -> new LinkedHashMap<>());
        Map<Object, Object> registrations = new LinkedHashMap<>();
        Map<Object, Object> providers = new LinkedHashMap<>();
        for (GatewayOauth2Client item : clients) {
            Map<Object, Object> value = new LinkedHashMap<>();
            value.put("provider", item.getProvider()); value.put("client-id", item.getClientId());
            value.put("client-secret", item.getClientSecret()); value.put("client-authentication-method", "client_secret_basic");
            value.put("authorization-grant-type", "authorization_code"); value.put("redirect-uri", item.getRedirectUri());
            value.put("scope", item.getScopes()); registrations.put(item.getRegistrationId(), value);
            Map<Object, Object> provider = new LinkedHashMap<>();
            provider.put("issuer-uri", item.getIssuerUri());
            provider.put("user-info-uri", item.getIssuerUri() + "/userinfo");
            provider.put("user-name-attribute", "name");
            providers.put(item.getProvider(), provider);
        }
        client.put("registration", registrations);
        client.put("provider", providers);
        Map<Object, Object> rootMap = (Map<Object, Object>) root;
        Map<Object, Object> opensabre = (Map<Object, Object>) rootMap.computeIfAbsent("opensabre", key -> new LinkedHashMap<>());
        Map<Object, Object> gateway = (Map<Object, Object>) opensabre.computeIfAbsent("gateway", key -> new LinkedHashMap<>());
        Map<Object, Object> gatewayOauth2 = (Map<Object, Object>) gateway.computeIfAbsent("oauth2", key -> new LinkedHashMap<>());
        gatewayOauth2.put("disabled-registration-ids", clients.stream().filter(item -> !item.isEnabled())
                .map(GatewayOauth2Client::getRegistrationId).toList());
        return new Yaml().dump(root);
    }

    /**
     * 仅替换 spring.cloud.gateway.routes 节点；路由外的键和值保持原有语义及顺序。
     */
    @SuppressWarnings("unchecked")
    static String replaceRoutes(String content, List<GatewayRoute> routes) {
        Object loaded = new Yaml().load(content);
        if (!(loaded instanceof Map<?, ?> root)) {
            throw new IllegalStateException("网关配置不是有效的 YAML 对象");
        }
        Object spring = root.get("spring");
        Object cloud = child(spring, "cloud");
        Object gateway = child(cloud, "gateway");
        if (!(gateway instanceof Map<?, ?> gatewayMap)) {
            throw new IllegalStateException("网关配置缺少 spring.cloud.gateway 节点");
        }
        ((Map<Object, Object>) gatewayMap).put("routes", toRouteMaps(routes));
        return new Yaml().dump(root);
    }

    /** 仅替换 spring.cloud.gateway.default-filters，不影响 routes 及其他网关配置。 */
    @SuppressWarnings("unchecked")
    static String replaceDefaultFilters(String content, List<GatewayRouteDefinition> filters) {
        Object loaded = new Yaml().load(content);
        if (!(loaded instanceof Map<?, ?> root)) throw new IllegalStateException("网关配置不是有效的 YAML 对象");
        Object gateway = child(child(root.get("spring"), "cloud"), "gateway");
        if (!(gateway instanceof Map<?, ?> gatewayMap)) throw new IllegalStateException("网关配置缺少 spring.cloud.gateway 节点");
        ((Map<Object, Object>) gatewayMap).put("default-filters", toDefinitionMaps(filters));
        return new Yaml().dump(root);
    }

    private static List<Map<String, Object>> toRouteMaps(List<GatewayRoute> routes) {
        List<Map<String, Object>> values = new ArrayList<>();
        for (GatewayRoute route : routes) {
            Map<String, Object> value = new LinkedHashMap<>();
            value.put("id", route.getId());
            value.put("uri", route.getUri());
            value.put("order", route.getOrder());
            value.put("predicates", toDefinitionMaps(route.getPredicates()));
            value.put("filters", toDefinitionMaps(route.getFilters()));
            values.add(value);
        }
        return values;
    }

    private static List<Map<String, Object>> toDefinitionMaps(List<GatewayRouteDefinition> definitions) {
        List<Map<String, Object>> values = new ArrayList<>();
        for (GatewayRouteDefinition definition : definitions) {
            Map<String, Object> value = new LinkedHashMap<>();
            value.put("name", definition.getName());
            value.put("args", new LinkedHashMap<>(definition.getArgs()));
            values.add(value);
        }
        return values;
    }

    static void validateRoute(GatewayRoute route) {
        if (route == null || route.getId() == null || !ROUTE_ID_PATTERN.matcher(route.getId()).matches()) {
            throw new IllegalArgumentException("路由 ID 只能包含字母、数字、点、下划线和连字符，且不能以符号开头");
        }
        String uri = route.getUri();
        if (uri == null || !(uri.startsWith("lb://") || uri.startsWith("http://")
                || uri.startsWith("https://") || uri.startsWith("forward:"))) {
            throw new IllegalArgumentException("目标 URI 仅支持 lb://、http://、https:// 或 forward: 前缀");
        }
        validateDefinitions(route.getPredicates(), SUPPORTED_PREDICATES, "断言", true);
        validateDefinitions(route.getFilters(), SUPPORTED_FILTERS, "过滤器", false);
    }

    static void validateDefaultFilters(List<GatewayRouteDefinition> filters) {
        validateDefinitions(filters, SUPPORTED_DEFAULT_FILTERS, "全局过滤器", false);
        for (GatewayRouteDefinition filter : filters) {
            if ("RequestRateLimiter".equals(filter.getName())) {
                Map<String, String> args = filter.getArgs();
                int replenish = parsePositive(args.get("redis-rate-limiter.replenishRate"), "限流补充速率");
                int burst = parsePositive(args.get("redis-rate-limiter.burstCapacity"), "限流突发容量");
                if (burst < replenish) throw new IllegalArgumentException("限流突发容量不能小于补充速率");
                if (!"#{@defaultRedisRateLimiter}".equals(args.get("rate-limiter"))) throw new IllegalArgumentException("限流器必须使用 defaultRedisRateLimiter");
                String keyResolver = args.get("key-resolver");
                if (!Set.of("#{@remoteAddressKeyResolver}", "#{@apiKeyResolver}").contains(keyResolver)) throw new IllegalArgumentException("限流 Key Resolver 仅支持 IP 或请求路径");
            }
        }
    }

    static void validateOauth2Clients(List<GatewayOauth2Client> clients) {
        Set<String> ids = new java.util.HashSet<>();
        Map<String, String> issuers = new LinkedHashMap<>();
        for (GatewayOauth2Client client : clients) {
            if (client == null || client.getRegistrationId() == null
                    || !ROUTE_ID_PATTERN.matcher(client.getRegistrationId()).matches()) {
                throw new IllegalArgumentException("OAuth2 注册名格式不正确");
            }
            if (!ids.add(client.getRegistrationId())) throw new IllegalArgumentException("OAuth2 注册名不能重复");
            if (isBlank(client.getProvider()) || isBlank(client.getIssuerUri()) || isBlank(client.getClientId()) || isBlank(client.getRedirectUri())) {
                throw new IllegalArgumentException("OAuth2 Provider、Issuer URI、客户端 ID 和回调地址不能为空");
            }
            if (!(client.getIssuerUri().startsWith("http://") || client.getIssuerUri().startsWith("https://"))) throw new IllegalArgumentException("Issuer URI 必须是 HTTP(S) 地址");
            String previousIssuer = issuers.putIfAbsent(client.getProvider(), client.getIssuerUri());
            if (previousIssuer != null && !previousIssuer.equals(client.getIssuerUri())) {
                throw new IllegalArgumentException("同一 Provider 只能配置一个 Issuer URI");
            }
            if (!(client.getRedirectUri().startsWith("http://localhost:")
                    || client.getRedirectUri().startsWith("https://"))) {
                throw new IllegalArgumentException("回调地址仅支持 HTTPS 或 localhost");
            }
            if (client.getScopes() == null || client.getScopes().isEmpty()) {
                throw new IllegalArgumentException("OAuth2 作用域不能为空");
            }
        }
    }

    private List<GatewayOauth2Client> mergeSecrets(List<GatewayOauth2Client> clients,
            List<GatewayOauth2Client> current) {
        Map<String, String> currentSecrets = new LinkedHashMap<>();
        current.forEach(item -> currentSecrets.put(item.getRegistrationId(), item.getClientSecret()));
        List<GatewayOauth2Client> result = new ArrayList<>();
        for (GatewayOauth2Client client : clients) {
            if (isBlank(client.getClientSecret())) client.setClientSecret(currentSecrets.get(client.getRegistrationId()));
            if (client.isEnabled() && isBlank(client.getClientSecret())) {
                throw new IllegalArgumentException("启用的 OAuth2 认证方式必须设置客户端密钥");
            }
            if (!isBlank(client.getClientSecret()) && !client.getClientSecret().startsWith("ENC(")
                    && !client.getClientSecret().startsWith("${")) {
                client.setClientSecret("ENC(" + stringEncryptor.encrypt(client.getClientSecret()) + ")");
            }
            result.add(client);
        }
        return result;
    }

    private static List<GatewayOauth2Client> maskSecrets(List<GatewayOauth2Client> clients) {
        List<GatewayOauth2Client> result = new ArrayList<>();
        for (GatewayOauth2Client source : clients) {
            GatewayOauth2Client target = new GatewayOauth2Client();
            target.setRegistrationId(source.getRegistrationId()); target.setProvider(source.getProvider());
            target.setIssuerUri(source.getIssuerUri());
            target.setClientId(source.getClientId()); target.setRedirectUri(source.getRedirectUri());
            target.setScopes(new ArrayList<>(source.getScopes())); target.setEnabled(source.isEnabled());
            target.setClientSecret(isBlank(source.getClientSecret()) ? "" : "******");
            result.add(target);
        }
        return result;
    }

    private static int parsePositive(String value, String field) {
        try { int parsed = Integer.parseInt(value); if (parsed > 0 && parsed <= 100000) return parsed; } catch (NumberFormatException ignored) { }
        throw new IllegalArgumentException(field + "必须是 1 到 100000 的整数");
    }

    private static void validateDefinitions(List<GatewayRouteDefinition> definitions, Set<String> allowed, String type,
                                            boolean required) {
        if (required && (definitions == null || definitions.isEmpty())) {
            throw new IllegalArgumentException("路由至少需要一个断言");
        }
        if (definitions == null) {
            return;
        }
        for (GatewayRouteDefinition definition : definitions) {
            if (definition == null || definition.getName() == null || !allowed.contains(definition.getName())) {
                throw new IllegalArgumentException("不支持的" + type + "：" + (definition == null ? "" : definition.getName()));
            }
            if (definition.getArgs() == null || definition.getArgs().isEmpty()
                    || definition.getArgs().entrySet().stream().anyMatch(item -> isBlank(item.getKey()) || isBlank(item.getValue()))) {
                throw new IllegalArgumentException(type + " " + definition.getName() + " 的参数不能为空");
            }
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static int findRouteIndex(List<GatewayRoute> routes, String routeId) {
        for (int index = 0; index < routes.size(); index++) {
            if (routes.get(index).getId().equals(routeId)) {
                return index;
            }
        }
        return -1;
    }

    @FunctionalInterface
    private interface RouteMutator {
        void mutate(List<GatewayRoute> routes);
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
