package io.github.opensabre.sysadmin.internaltoken.model.vo;

import java.time.Instant;

/**
 * Safe key-management status exposed to the management console.
 */
public record InternalTokenKeyManagementStatus(
        boolean enabled,
        boolean writeEnabled,
        long configVersion,
        String activeKeyId,
        boolean activeKeyConfigured,
        String previousKeyId,
        boolean previousKeyConfigured,
        Instant activeKeyActivatedAt,
        Instant previousKeyRetireAfter) {

    public InternalTokenKeyManagementStatus withWriteEnabled(boolean writeEnabled) {
        return new InternalTokenKeyManagementStatus(
                enabled,
                writeEnabled,
                configVersion,
                activeKeyId,
                activeKeyConfigured,
                previousKeyId,
                previousKeyConfigured,
                activeKeyActivatedAt,
                previousKeyRetireAfter);
    }
}
