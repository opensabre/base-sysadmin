package io.github.opensabre.sysadmin.internaltoken.repository;

import io.github.opensabre.sysadmin.internaltoken.config.InternalTokenKeyManagementProperties;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies Spring constructor resolution for the Nacos configuration repository.
 */
class NacosInternalTokenSharedConfigRepositoryTest {

    @Test
    void shouldCreateRepositoryFromSpringContext() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext()) {
            context.registerBean(
                    InternalTokenKeyManagementProperties.class,
                    InternalTokenKeyManagementProperties::new);
            context.register(NacosInternalTokenSharedConfigRepository.class);
            context.refresh();

            assertThat(context.getBean(NacosInternalTokenSharedConfigRepository.class))
                    .isNotNull();
            assertThat(context.getBean(InternalTokenKeyManagementProperties.class).getDataId())
                    .isEqualTo("opensabre-common.yml");
        }
    }
}
