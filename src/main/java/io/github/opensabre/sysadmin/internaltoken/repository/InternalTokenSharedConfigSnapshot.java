package io.github.opensabre.sysadmin.internaltoken.repository;

/**
 * Nacos shared configuration content and its compare-and-set version.
 */
public record InternalTokenSharedConfigSnapshot(
        String content,
        String casVersion,
        boolean exists) {
}
