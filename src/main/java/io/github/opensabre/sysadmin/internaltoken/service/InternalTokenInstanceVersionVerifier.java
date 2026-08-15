package io.github.opensabre.sysadmin.internaltoken.service;

/** Confirms that every required application instance has loaded a key configuration version. */
public interface InternalTokenInstanceVersionVerifier {

    void requireAllInstances(long configVersion, String activeKeyId);
}
