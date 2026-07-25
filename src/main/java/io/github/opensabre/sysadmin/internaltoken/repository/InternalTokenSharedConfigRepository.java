package io.github.opensabre.sysadmin.internaltoken.repository;

/**
 * Reads and atomically publishes the shared internal token configuration.
 */
public interface InternalTokenSharedConfigRepository {

    InternalTokenSharedConfigSnapshot read();

    void publish(String content, InternalTokenSharedConfigSnapshot expected);
}
