package io.github.opensabre.sysadmin.captcha.model.po;

/**
 * @param businessId Business ID
 * @param clientIp   IP address of the client requesting the captcha
 *                   Used for security monitoring and rate limiting
 * @param deviceId   Device identifier for the client
 *                   Helps track requests from the same device for security purposes
 */
public record ClientInfo(String businessId, String clientIp, String deviceId) {
}
