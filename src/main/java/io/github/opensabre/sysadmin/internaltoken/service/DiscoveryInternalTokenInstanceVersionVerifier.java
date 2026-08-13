package io.github.opensabre.sysadmin.internaltoken.service;

import io.github.opensabre.sysadmin.internaltoken.config.InternalTokenKeyManagementProperties;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class DiscoveryInternalTokenInstanceVersionVerifier implements InternalTokenInstanceVersionVerifier {
    private static final String STATUS_PATH = "/actuator/internalTokenKeyStatus";
    private final DiscoveryClient discoveryClient;
    private final RestClient.Builder restClientBuilder;
    private final InternalTokenKeyManagementProperties properties;

    public DiscoveryInternalTokenInstanceVersionVerifier(DiscoveryClient discoveryClient,
            RestClient.Builder restClientBuilder, InternalTokenKeyManagementProperties properties) {
        this.discoveryClient = discoveryClient;
        this.restClientBuilder = restClientBuilder;
        this.properties = properties;
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
                    InstanceRefreshStatus status = restClientBuilder.build().get()
                            .uri(instance.getUri().resolve(STATUS_PATH)).retrieve().body(InstanceRefreshStatus.class);
                    if (status == null || !status.successful() || status.configVersion() != configVersion
                            || !activeKeyId.equals(status.activeKeyId())) {
                        failures.add(application + "@" + instance.getHost() + ": stale version");
                    }
                } catch (Exception exception) {
                    failures.add(application + "@" + instance.getHost() + ": unavailable");
                }
            }
        }
        if (!failures.isEmpty()) {
            throw new IllegalStateException("仍有应用实例未确认加载当前内部 Token 密钥版本: "
                    + String.join(", ", failures));
        }
    }

    record InstanceRefreshStatus(long configVersion, String activeKeyId, Instant refreshedAt,
                                 boolean successful, String message) { }
}
