package io.github.opensabre.sysadmin.internaltoken.service;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.client.DefaultServiceInstance;

import static org.assertj.core.api.Assertions.assertThat;

class DiscoveryInternalTokenInstanceVersionVerifierTest {

    @Test
    void usesDiscoveredManagementAddressInsteadOfBusinessPort() {
        var instance = new DefaultServiceInstance("id", "base-gateway", "business-host", 8000, false);
        instance.getMetadata().put("management.host", "management-host");
        instance.getMetadata().put("management.port", "18080");

        assertThat(DiscoveryInternalTokenInstanceVersionVerifier.statusUri(instance))
                .hasToString("http://management-host:18080/actuator/internalTokenKeyStatus");
    }
}
