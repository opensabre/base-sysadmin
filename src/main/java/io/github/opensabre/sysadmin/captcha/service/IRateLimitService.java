package io.github.opensabre.sysadmin.captcha.service;

import io.github.opensabre.sysadmin.ratelimit.enums.RateLimitAlgorithmType;
import io.github.opensabre.sysadmin.ratelimit.model.RateLimitConfig;
import io.github.opensabre.sysadmin.ratelimit.model.RateLimitResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Security service for captcha rate limiting and other security controls
 */
@Slf4j
@Component
public class IRateLimitService {

    @Resource
    private io.github.opensabre.sysadmin.ratelimit.service.IRateLimitService rateLimitService;

    private static final String IP_RATE_LIMIT_PREFIX = "captcha:clientip:rate";
    private static final String DEVICE_RATE_LIMIT_PREFIX = "captcha:deviceid:rate";
    private static final String BUSINESS_RATE_LIMIT_PREFIX = "captcha:businessid:rate";
    private static final String BUSINESS_INTERVAL_PREFIX = "captcha:businessid:rate:interval";

    /**
     * Check if IP is allowed to send captcha
     *
     * @param ip          IP address
     * @param maxAttempts Maximum attempts allowed
     * @param timeWindow  Time window in seconds
     * @return true if allowed, false otherwise
     */
    public boolean isIpAllowed(String ip, int maxAttempts, int timeWindow) {
        if (StringUtils.isBlank(ip)) {
            return false;
        }
        RateLimitResult result = checkLimit(IP_RATE_LIMIT_PREFIX, ip, maxAttempts, timeWindow);
        logIfExceeded("IP", ip, result);
        return result.isAllowed();
    }

    /**
     * Check if device is allowed to send captcha
     *
     * @param deviceId    Device information
     * @param maxAttempts Maximum attempts allowed
     * @param timeWindow  Time window in seconds
     * @return true if allowed, false otherwise
     */
    public boolean isDeviceAllowed(String deviceId, int maxAttempts, int timeWindow) {
        if (StringUtils.isBlank(deviceId)) {
            return false;
        }
        RateLimitResult result = checkLimit(DEVICE_RATE_LIMIT_PREFIX, deviceId, maxAttempts, timeWindow);
        logIfExceeded("Device", deviceId, result);
        return result.isAllowed();
    }

    /**
     * Check if businessId (phone/email  && scenario) is allowed to receive captcha
     *
     * @param businessId  businessId (phone number, email, etc.) && scenario
     * @param maxAttempts Maximum attempts allowed
     * @param timeWindow  Time window in seconds
     * @return true if allowed, false otherwise
     */
    public boolean isBusinessAllowed(String businessId, int maxAttempts, int timeWindow) {
        if (StringUtils.isBlank(businessId)) {
            return false;
        }
        RateLimitResult result = checkLimit(BUSINESS_RATE_LIMIT_PREFIX, businessId, maxAttempts, timeWindow);
        logIfExceeded("BusinessId", businessId, result);
        return result.isAllowed();
    }

    /**
     * Check if target has recently received a captcha (to prevent spam)
     *
     * @param businessId  Target (phone number, email, etc.)
     * @param minInterval Minimum interval in seconds
     * @return true if allowed, false if too soon
     */
    public boolean isTargetIntervalAllowed(String businessId, int minInterval) {
        if (StringUtils.isBlank(businessId)) {
            return false;
        }
        RateLimitResult result = checkLimit(BUSINESS_INTERVAL_PREFIX, businessId, 1, minInterval);
        if (!result.isAllowed()) {
            log.warn("Target interval not met: {} (min: {}s, current: {})", businessId, minInterval, result.getCurrentCount());
        }
        return result.isAllowed();
    }

    private RateLimitResult checkLimit(String keyPrefix, String key, int maxAttempts, int timeWindow) {
        RateLimitConfig config = RateLimitConfig.builder()
                .keyPrefix(keyPrefix)
                .key(key)
                .algorithm(RateLimitAlgorithmType.COUNTER)
                .maxCount(maxAttempts)
                .period(timeWindow)
                .enabled(true)
                .build();
        return rateLimitService.checkLimit(config);
    }

    private void logIfExceeded(String dimensionName, String dimensionValue, RateLimitResult result) {
        if (!result.isAllowed()) {
            log.warn("{} rate limit exceeded: {} (max: {}, current: {})",
                    dimensionName, dimensionValue, result.getMaxCount(), result.getCurrentCount());
        }
    }
}
