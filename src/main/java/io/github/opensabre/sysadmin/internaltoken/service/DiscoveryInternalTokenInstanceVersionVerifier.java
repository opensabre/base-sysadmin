package io.github.opensabre.sysadmin.internaltoken.service;

import io.github.opensabre.sysadmin.internaltoken.config.InternalTokenKeyManagementProperties;
import io.github.opensabre.security.actuator.ActuatorMonitoringTokenIssuer;
import io.github.opensabre.security.token.InternalTokenConstants;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/** Polls the safe actuator endpoint of every discovered instance before previous-key retirement. */
@Component
public class DiscoveryInternalTokenInstanceVersionVerifier
        implements InternalTokenInstanceVersionVerifier {

    private final DiscoveryClient discoveryClient;
    private final RestClient restClient;
    private final InternalTokenKeyManagementProperties properties;
    private final ActuatorMonitoringTokenIssuer tokenIssuer;

    public DiscoveryInternalTokenInstanceVersionVerifier(
            DiscoveryClient discoveryClient,
            InternalTokenKeyManagementProperties properties,
            ActuatorMonitoringTokenIssuer tokenIssuer) {
        this.discoveryClient = discoveryClient;
        this.restClient = RestClient.create();
        this.properties = properties;
        this.tokenIssuer = tokenIssuer;
    }

    @Override
    public void requireAllInstances(long configVersion, String activeKeyId) {
        List<String> failures = new ArrayList<>();
        for (String application : properties.getRequiredApplications()) {
            List<ServiceInstance> instances = discoveryClient.getInstances(application);
            if (instances.isEmpty()) {
                failures.add(application + ": no healthy instance");
                continue;
            }
            for (ServiceInstance instance : instances) {
                try {
                    InstanceRefreshStatus status = restClient.get()
                            .uri(statusUri(instance))
                            .header(InternalTokenConstants.HEADER, tokenIssuer.issue(application))
                            .retrieve()
                            .body(InstanceRefreshStatus.class);
                    if (status == null || !status.successful()
                            || status.configVersion() != configVersion
                            || !activeKeyId.equals(status.activeKeyId())) {
                        failures.add(application + "@" + instance.getHost() + ": stale version");
                    }
                } catch (Exception exception) {
                    failures.add(application + "@" + instance.getHost() + ": unavailable");
                }
            }
        }
        if (!failures.isEmpty()) {
            throw new IllegalStateException(
                    "仍有应用实例未确认加载当前内部 Token 密钥版本: " + String.join(", ", failures));
        }
    }

    static URI statusUri(ServiceInstance instance) {
        var metadata = instance.getMetadata();
        String scheme = metadata.getOrDefault("management.scheme", instance.isSecure() ? "https" : "http");
        String host = metadata.getOrDefault("management.host", instance.getHost());
        String port = metadata.getOrDefault("management.port", Integer.toString(instance.getPort()));
        String path = metadata.getOrDefault("management.path", "/actuator");
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("Actuator management path must start with /");
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return URI.create(scheme + "://" + host + ":" + port + path + "/internalTokenKeyStatus");
    }

    record InstanceRefreshStatus(
            long configVersion,
            String activeKeyId,
            Instant refreshedAt,
            boolean successful,
            String message) {
    }
}
