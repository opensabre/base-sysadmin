package io.github.opensabre.sysadmin.captcha.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Security service for captcha rate limiting and other security controls
 */
@Slf4j
@Component
public class IRateLimitService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String IP_RATE_LIMIT_PREFIX = "captcha:clientip:rate:";
    private static final String DEVICE_RATE_LIMIT_PREFIX = "captcha:deviceid:rate:";
    private static final String BUSINESS_RATE_LIMIT_PREFIX = "captcha:businessid:rate:";

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
        String key = IP_RATE_LIMIT_PREFIX + ip;
        String countStr = stringRedisTemplate.opsForValue().get(key);
        int currentCount = countStr != null ? Integer.parseInt(countStr) : 0;

        if (currentCount >= maxAttempts) {
            log.warn("IP rate limit exceeded: {} (max: {}, current: {})", ip, maxAttempts, currentCount);
            return false;
        }
        // Increment the count
        stringRedisTemplate.opsForValue().increment(key);
        stringRedisTemplate.expire(key, timeWindow, TimeUnit.SECONDS);

        return true;
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
        String key = DEVICE_RATE_LIMIT_PREFIX + deviceId;
        String countStr = stringRedisTemplate.opsForValue().get(key);
        int currentCount = countStr != null ? Integer.parseInt(countStr) : 0;

        if (currentCount >= maxAttempts) {
            log.warn("Device rate limit exceeded: {} (max: {}, current: {})", deviceId, maxAttempts, currentCount);
            return false;
        }
        // Increment the count
        stringRedisTemplate.opsForValue().increment(key);
        stringRedisTemplate.expire(key, timeWindow, TimeUnit.SECONDS);

        return true;
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
        String key = BUSINESS_RATE_LIMIT_PREFIX + businessId;
        String countStr = stringRedisTemplate.opsForValue().get(key);
        int currentCount = countStr != null ? Integer.parseInt(countStr) : 0;

        if (currentCount >= maxAttempts) {
            log.warn("BusinessId rate limit exceeded: {} (max: {}, current: {})", businessId, maxAttempts, currentCount);
            return false;
        }
        // Increment the count
        stringRedisTemplate.opsForValue().increment(key);
        stringRedisTemplate.expire(key, timeWindow, TimeUnit.SECONDS);

        return true;
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

        String key = BUSINESS_RATE_LIMIT_PREFIX + "interval:" + businessId;
        String lastSendTimeStr = stringRedisTemplate.opsForValue().get(key);

        if (lastSendTimeStr != null) {
            long lastSendTime = Long.parseLong(lastSendTimeStr);
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastSendTime < minInterval * 1000L) {
                log.warn("Target interval not met: {} (min: {}s, elapsed: {}s)", businessId, minInterval, (currentTime - lastSendTime) / 1000L);
                return false;
            }
        }
        // Update the last send time
        stringRedisTemplate.opsForValue().set(key, String.valueOf(System.currentTimeMillis()), minInterval, TimeUnit.SECONDS);
        return true;
    }
}