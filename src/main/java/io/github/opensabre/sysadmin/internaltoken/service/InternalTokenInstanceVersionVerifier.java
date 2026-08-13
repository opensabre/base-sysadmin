package io.github.opensabre.sysadmin.internaltoken.service;

public interface InternalTokenInstanceVersionVerifier {
    void requireAllInstances(long configVersion, String activeKeyId);
}
