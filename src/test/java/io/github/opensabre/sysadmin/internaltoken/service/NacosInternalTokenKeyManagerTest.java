package io.github.opensabre.sysadmin.internaltoken.service;

import io.github.opensabre.sysadmin.internaltoken.config.InternalTokenKeyManagementProperties;
import io.github.opensabre.sysadmin.internaltoken.model.vo.InternalTokenKeyManagementStatus;
import io.github.opensabre.sysadmin.internaltoken.repository.InternalTokenSharedConfigRepository;
import io.github.opensabre.sysadmin.internaltoken.repository.InternalTokenSharedConfigSnapshot;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

class NacosInternalTokenKeyManagerTest {

    private static final Instant NOW = Instant.parse("2026-07-25T00:00:00Z");
    private static final String INITIAL_CONFIG = """
            management:
              endpoints:
                enabled-by-default: true
            opensabre:
              security:
                internal-token:
                  enabled: true
                  key-config-version: 1
                  active-key-id: key-1
                  active-key: b2xkLWtleS1tYXRlcmlhbC0wMTIzNDU2Nzg5MDEyMzQ1Njc4OQ==
                  active-key-activated-at: 2026-07-24T00:00:00Z
                  ttl: 60s
                  max-ttl: 120s
            """;

    @Test
    void shouldRotateWithGeneratedKeyAndPreserveUnrelatedConfiguration() {
        InMemoryRepository repository = new InMemoryRepository(INITIAL_CONFIG);
        NacosInternalTokenKeyManager manager = manager(repository, NOW, true);

        InternalTokenKeyManagementStatus result = manager.rotate(1, "key-2");

        assertThat(result.configVersion()).isEqualTo(2);
        assertThat(result.activeKeyId()).isEqualTo("key-2");
        assertThat(result.previousKeyId()).isEqualTo("key-1");
        assertThat(result.activeKeyActivatedAt()).isEqualTo(NOW);
        assertThat(result.previousKeyRetireAfter()).isEqualTo(NOW.plusSeconds(300));
        assertThat(repository.content)
                .contains("management:", "enabled-by-default: true", "ttl: 60s");
        assertThat(repository.publishCount).isEqualTo(1);
    }

    @Test
    void shouldRejectPreviousKeyRetirementDuringGracePeriod() {
        InMemoryRepository repository = new InMemoryRepository(INITIAL_CONFIG);
        NacosInternalTokenKeyManager manager = manager(repository, NOW, true);
        manager.rotate(1, "key-2");

        assertThatIllegalStateException()
                .isThrownBy(() -> manager.retirePrevious(2))
                .withMessageContaining("保护期");
        assertThat(repository.publishCount).isEqualTo(1);
    }

    @Test
    void shouldRetirePreviousKeyAfterGracePeriod() {
        InMemoryRepository repository = new InMemoryRepository(INITIAL_CONFIG);
        manager(repository, NOW, true).rotate(1, "key-2");

        InternalTokenKeyManagementStatus result = manager(
                repository, NOW.plusSeconds(301), true).retirePrevious(2);

        assertThat(result.configVersion()).isEqualTo(3);
        assertThat(result.previousKeyId()).isNull();
        assertThat(result.previousKeyConfigured()).isFalse();
        assertThat(result.previousKeyRetireAfter()).isNull();
        assertThat(repository.content).doesNotContain("previous-key:");
    }

    @Test
    void shouldKeepControlPlaneReadOnlyByDefault() {
        InMemoryRepository repository = new InMemoryRepository(INITIAL_CONFIG);
        NacosInternalTokenKeyManager manager = manager(repository, NOW, false);

        assertThatIllegalStateException()
                .isThrownBy(() -> manager.rotate(1, "key-2"))
                .withMessageContaining("尚未启用");
        assertThat(repository.publishCount).isZero();
    }

    @Test
    void shouldRejectRetirementUntilEveryInstanceLoadsCurrentVersion() {
        InMemoryRepository repository = new InMemoryRepository(INITIAL_CONFIG);
        manager(repository, NOW, true).rotate(1, "key-2");
        InternalTokenKeyManagementProperties properties = new InternalTokenKeyManagementProperties();
        properties.setWriteEnabled(true);
        properties.setRotationGracePeriod(Duration.ofMinutes(5));
        NacosInternalTokenKeyManager manager = new NacosInternalTokenKeyManager(
                repository, properties, new SecureRandom(),
                Clock.fixed(NOW.plusSeconds(301), ZoneOffset.UTC),
                (version, keyId) -> { throw new IllegalStateException("stale instance"); });

        assertThatIllegalStateException().isThrownBy(() -> manager.retirePrevious(2))
                .withMessageContaining("stale instance");
        assertThat(repository.content).contains("previous-key:");
    }

    private static NacosInternalTokenKeyManager manager(
            InMemoryRepository repository, Instant instant, boolean writeEnabled) {
        InternalTokenKeyManagementProperties properties =
                new InternalTokenKeyManagementProperties();
        properties.setWriteEnabled(writeEnabled);
        properties.setRotationGracePeriod(Duration.ofMinutes(5));
        SecureRandom random = new SecureRandom() {
            @Override
            public void nextBytes(byte[] bytes) {
                for (int index = 0; index < bytes.length; index++) {
                    bytes[index] = (byte) index;
                }
            }
        };
        return new NacosInternalTokenKeyManager(
                repository,
                properties,
                random,
                Clock.fixed(instant, ZoneOffset.UTC),
                (version, keyId) -> { });
    }

    private static final class InMemoryRepository
            implements InternalTokenSharedConfigRepository {

        private String content;
        private int revision = 1;
        private int publishCount;

        private InMemoryRepository(String content) {
            this.content = content;
        }

        @Override
        public InternalTokenSharedConfigSnapshot read() {
            return new InternalTokenSharedConfigSnapshot(
                    content, "revision-" + revision, true);
        }

        @Override
        public void publish(
                String content, InternalTokenSharedConfigSnapshot expected) {
            if (!expected.casVersion().equals("revision-" + revision)) {
                throw new IllegalStateException("CAS conflict");
            }
            this.content = content;
            revision++;
            publishCount++;
        }
    }
}
