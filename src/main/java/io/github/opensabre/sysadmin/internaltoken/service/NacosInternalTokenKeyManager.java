package io.github.opensabre.sysadmin.internaltoken.service;

import io.github.opensabre.sysadmin.internaltoken.config.InternalTokenKeyManagementProperties;
import io.github.opensabre.sysadmin.internaltoken.model.vo.InternalTokenKeyManagementStatus;
import io.github.opensabre.sysadmin.internaltoken.repository.InternalTokenSharedConfigRepository;
import io.github.opensabre.sysadmin.internaltoken.repository.InternalTokenSharedConfigSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Generates shared HMAC keys and publishes them to Nacos with optimistic locking.
 */
@Service
public class NacosInternalTokenKeyManager {

    private static final Pattern KEY_ID_PATTERN =
            Pattern.compile("[A-Za-z0-9][A-Za-z0-9._-]{0,63}");
    private static final Duration MINIMUM_GRACE_PERIOD = Duration.ofSeconds(125);

    private final InternalTokenSharedConfigRepository repository;
    private final InternalTokenKeyManagementProperties properties;
    private final SecureRandom secureRandom;
    private final Clock clock;
    private final InternalTokenInstanceVersionVerifier instanceVersionVerifier;

    @Autowired
    public NacosInternalTokenKeyManager(
            InternalTokenSharedConfigRepository repository,
            InternalTokenKeyManagementProperties properties,
            InternalTokenInstanceVersionVerifier instanceVersionVerifier) {
        this(repository, properties, new SecureRandom(), Clock.systemUTC(), instanceVersionVerifier);
    }

    NacosInternalTokenKeyManager(
            InternalTokenSharedConfigRepository repository,
            InternalTokenKeyManagementProperties properties,
            SecureRandom secureRandom,
            Clock clock,
            InternalTokenInstanceVersionVerifier instanceVersionVerifier) {
        this.repository = repository;
        this.properties = properties;
        this.secureRandom = secureRandom;
        this.clock = clock;
        this.instanceVersionVerifier = instanceVersionVerifier;
    }

    public InternalTokenKeyManagementStatus currentStatus() {
        return exposed(document(repository.read()).status());
    }

    public InternalTokenKeyManagementStatus rotate(
            long expectedConfigVersion, String newKeyId) {
        requireWriteEnabled();
        validateKeyId(newKeyId);
        Duration gracePeriod = properties.getRotationGracePeriod();
        if (gracePeriod == null || gracePeriod.compareTo(MINIMUM_GRACE_PERIOD) < 0) {
            throw new IllegalStateException("密钥轮换保护期不得少于 125 秒");
        }

        InternalTokenSharedConfigSnapshot snapshot = repository.read();
        InternalTokenSecurityConfigDocument document = document(snapshot);
        InternalTokenKeyManagementStatus before = document.status();
        requireExpectedVersion(expectedConfigVersion, before.configVersion());

        if (newKeyId.equals(before.activeKeyId())
                || newKeyId.equals(before.previousKeyId())) {
            throw new IllegalArgumentException("新密钥 ID 必须与 active/previous 密钥不同");
        }

        byte[] key = new byte[32];
        secureRandom.nextBytes(key);
        document.rotate(
                newKeyId,
                Base64.getEncoder().encodeToString(key),
                Instant.now(clock),
                gracePeriod);
        String updatedContent = document.dump();
        repository.publish(updatedContent, snapshot);
        return exposed(InternalTokenSecurityConfigDocument.parse(updatedContent).status());
    }

    public InternalTokenKeyManagementStatus retirePrevious(long expectedConfigVersion) {
        requireWriteEnabled();
        InternalTokenSharedConfigSnapshot snapshot = repository.read();
        InternalTokenSecurityConfigDocument document = document(snapshot);
        InternalTokenKeyManagementStatus before = document.status();
        requireExpectedVersion(expectedConfigVersion, before.configVersion());

        instanceVersionVerifier.requireAllInstances(before.configVersion(), before.activeKeyId());

        document.retirePrevious(Instant.now(clock));
        String updatedContent = document.dump();
        repository.publish(updatedContent, snapshot);
        return exposed(InternalTokenSecurityConfigDocument.parse(updatedContent).status());
    }

    private InternalTokenSecurityConfigDocument document(
            InternalTokenSharedConfigSnapshot snapshot) {
        return InternalTokenSecurityConfigDocument.parse(snapshot.content());
    }

    private InternalTokenKeyManagementStatus exposed(
            InternalTokenKeyManagementStatus status) {
        return status.withWriteEnabled(properties.isWriteEnabled());
    }

    private void requireWriteEnabled() {
        if (!properties.isWriteEnabled()) {
            throw new IllegalStateException("内部 Token 密钥配置写入尚未启用");
        }
    }

    private static void requireExpectedVersion(long expected, long actual) {
        if (expected != actual) {
            throw new IllegalStateException("密钥配置版本已变化，请刷新后重试");
        }
    }

    private static void validateKeyId(String keyId) {
        if (!KEY_ID_PATTERN.matcher(keyId).matches()) {
            throw new IllegalArgumentException(
                    "密钥 ID 只能包含字母、数字、点、下划线和连字符，长度不得超过 64");
        }
    }
}
