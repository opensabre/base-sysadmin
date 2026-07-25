package io.github.opensabre.sysadmin.internaltoken.service;

import io.github.opensabre.sysadmin.internaltoken.model.vo.InternalTokenKeyManagementStatus;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Narrow YAML editor for the opensabre.security.internal-token subtree.
 */
final class InternalTokenSecurityConfigDocument {

    private final Map<String, Object> root;

    private InternalTokenSecurityConfigDocument(Map<String, Object> root) {
        this.root = root;
    }

    static InternalTokenSecurityConfigDocument parse(String content) {
        if (content == null || content.isBlank()) {
            return new InternalTokenSecurityConfigDocument(new LinkedHashMap<>());
        }
        Object loaded = new Yaml(new SafeConstructor(new LoaderOptions())).load(content);
        if (!(loaded instanceof Map<?, ?> map)) {
            throw new IllegalStateException("内部 Token 共享配置必须是 YAML 对象");
        }
        return new InternalTokenSecurityConfigDocument(stringKeyMap(map));
    }

    InternalTokenKeyManagementStatus status() {
        Map<String, Object> token = token(false);
        if (token == null) {
            return new InternalTokenKeyManagementStatus(
                    false, false, 0, null, false, null, false, null, null);
        }
        String activeKeyId = text(token.get("active-key-id"));
        String previousKeyId = text(token.get("previous-key-id"));
        return new InternalTokenKeyManagementStatus(
                booleanValue(token.get("enabled"), true),
                false,
                longValue(token.get("key-config-version")),
                activeKeyId,
                hasText(token.get("active-key")),
                previousKeyId,
                hasText(token.get("previous-key")),
                instant(token.get("active-key-activated-at")),
                instant(token.get("previous-key-retire-after")));
    }

    void rotate(String newKeyId, String newKey, Instant now, Duration gracePeriod) {
        InternalTokenKeyManagementStatus before = status();
        Map<String, Object> token = token(true);
        Object oldActiveKey = token.get("active-key");

        token.putIfAbsent("enabled", true);
        token.put("key-config-version", before.configVersion() + 1);
        if (before.activeKeyConfigured() && hasText(before.activeKeyId())) {
            token.put("previous-key-id", before.activeKeyId());
            token.put("previous-key", oldActiveKey);
            token.put("previous-key-retire-after", now.plus(gracePeriod).toString());
        } else {
            token.remove("previous-key-id");
            token.remove("previous-key");
            token.remove("previous-key-retire-after");
        }
        token.put("active-key-id", newKeyId);
        token.put("active-key", newKey);
        token.put("active-key-activated-at", now.toString());
    }

    void retirePrevious(Instant now) {
        Map<String, Object> token = token(false);
        if (token == null) {
            throw new IllegalStateException("内部 Token 共享配置尚未初始化");
        }
        InternalTokenKeyManagementStatus before = status();
        if (!before.previousKeyConfigured() || !hasText(before.previousKeyId())) {
            throw new IllegalStateException("当前没有可退役的 previous 密钥");
        }
        if (before.previousKeyRetireAfter() == null) {
            throw new IllegalStateException("previous 密钥缺少最早退役时间");
        }
        if (now.isBefore(before.previousKeyRetireAfter())) {
            throw new IllegalStateException(
                    "previous 密钥尚在轮换保护期，最早退役时间为 "
                            + before.previousKeyRetireAfter());
        }
        token.put("key-config-version", before.configVersion() + 1);
        token.remove("previous-key-id");
        token.remove("previous-key");
        token.remove("previous-key-retire-after");
    }

    String dump() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        options.setIndent(2);
        return new Yaml(options).dump(root);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> token(boolean create) {
        Map<String, Object> opensabre = child(root, "opensabre", create);
        if (opensabre == null) {
            return null;
        }
        Map<String, Object> security = child(opensabre, "security", create);
        return security == null ? null : child(security, "internal-token", create);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> child(
            Map<String, Object> parent, String name, boolean create) {
        Object value = parent.get(name);
        if (value == null && create) {
            Map<String, Object> child = new LinkedHashMap<>();
            parent.put(name, child);
            return child;
        }
        if (value == null) {
            return null;
        }
        if (!(value instanceof Map<?, ?> map)) {
            throw new IllegalStateException(name + " 配置必须是 YAML 对象");
        }
        if (value instanceof LinkedHashMap<?, ?>) {
            return (Map<String, Object>) value;
        }
        Map<String, Object> child = stringKeyMap(map);
        parent.put(name, child);
        return child;
    }

    private static Map<String, Object> stringKeyMap(Map<?, ?> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        source.forEach((key, value) -> result.put(String.valueOf(key), value));
        return result;
    }

    private static long longValue(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("key-config-version 必须是整数", exception);
        }
    }

    private static boolean booleanValue(Object value, boolean defaultValue) {
        return value == null ? defaultValue : Boolean.parseBoolean(String.valueOf(value));
    }

    private static Instant instant(Object value) {
        if (value == null || String.valueOf(value).isBlank()) {
            return null;
        }
        if (value instanceof Date date) {
            return date.toInstant();
        }
        try {
            return Instant.parse(String.valueOf(value));
        } catch (RuntimeException exception) {
            throw new IllegalStateException("密钥时间字段必须使用 ISO-8601 UTC 格式", exception);
        }
    }

    private static String text(Object value) {
        return hasText(value) ? String.valueOf(value).trim() : null;
    }

    private static boolean hasText(Object value) {
        return value != null && !String.valueOf(value).isBlank();
    }
}
