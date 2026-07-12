package io.github.opensabre.sysadmin.ratelimit.algorithm;

import io.github.opensabre.sysadmin.ratelimit.model.RateLimitResult;
import io.github.opensabre.sysadmin.ratelimit.storage.RateLimitStorage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 滑动窗口计数器算法实现。
 */
@Slf4j
@Component
public class SlidingWindowAlgorithm implements RateLimitAlgorithm {

    private final RateLimitStorage storage;

    public SlidingWindowAlgorithm(RateLimitStorage storage) {
        this.storage = storage;
    }

    @Override
    public RateLimitResult checkLimit(String key, int maxCount, int period) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Rate limit key must not be blank");
        }
        try {
            return storage.checkSlidingWindow(key, maxCount, period);
        } catch (Exception e) {
            log.error("Failed to check sliding window rate limit for key: {}", key, e);
            return RateLimitResult.allowed(key, 0L, maxCount, maxCount, System.currentTimeMillis() + period * 1000L);
        }
    }

    @Override
    public void resetLimit(String key) {
        storage.delete(key);
        log.info("Sliding window rate limit reset for key: {}", key);
    }

    @Override
    public int getRemaining(String key, int maxCount, int period) {
        Long currentCount = storage.getCount(key);
        if (currentCount == null) {
            return maxCount;
        }
        return Math.max(0, maxCount - currentCount.intValue());
    }
}
