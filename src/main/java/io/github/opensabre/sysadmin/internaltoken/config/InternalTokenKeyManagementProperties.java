package io.github.opensabre.sysadmin.internaltoken.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Internal token shared key control-plane settings.
 */
@Data
@Component
@ConfigurationProperties(prefix = "opensabre.sysadmin.internal-token")
public class InternalTokenKeyManagementProperties {

    private boolean writeEnabled;
    private String nacosServerUrl = "http://localhost:8848";
    private String dataId = "opensabre-common.yml";
    private String group = "DEFAULT_GROUP";
    private String namespace = "";
    private Duration rotationGracePeriod = Duration.ofMinutes(5);
    private Set<String> requiredApplications = new LinkedHashSet<>(Set.of(
            "base-authorization", "base-organization", "base-sysadmin", "base-gateway-admin"));
}
