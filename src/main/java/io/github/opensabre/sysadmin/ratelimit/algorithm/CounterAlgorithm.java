package io.github.opensabre.sysadmin.ratelimit.algorithm;

import io.github.opensabre.sysadmin.ratelimit.model.RateLimitResult;
import io.github.opensabre.sysadmin.ratelimit.storage.RateLimitStorage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 固定窗口计数器算法实现
 * <p>在固定时间窗口内统计请求次数，简单高效</p>
 * <p>实现原理：使用Redis的INCR和EXPIRE命令</p>
 *
 * @author Opensabre Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class CounterAlgorithm implements RateLimitAlgorithm {

    @Resource
    private RateLimitStorage storage;

    /**
     * 检查限次
     * 使用固定窗口计数器算法检查是否超过限次
     *
     * @param key      限次 Key
     * @param maxCount 最大次数
     * @param period   时间窗口（秒）
     * @return 限次检查结果
     */
    @Override
    public RateLimitResult checkLimit(String key, int maxCount, int period) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Rate limit key must not be blank");
        }
        try {
            // 原子递增并在首次创建时设置窗口过期时间，避免并发请求同时通过旧计数判断。
            Long newCount = storage.incrementAndExpire(key, 1L, period);
            long currentCount = ObjectUtils.defaultIfNull(newCount, 0L);
            if (currentCount > maxCount) {
                log.warn("Rate limit exceeded: key={}, current={}, max={}", key, currentCount, maxCount);
                return RateLimitResult.denied(key, currentCount, maxCount, String.format("当前已超过限次，请%d秒后再试", period));
            }

            // 计算剩余次数和重置时间
            int remaining = Math.max(0, maxCount - (int) currentCount);
            long resetTime = System.currentTimeMillis() + period * 1000L;
            log.debug("Rate limit check passed: key={}, count={}, max={}, remaining={}", key, currentCount, maxCount, remaining);
            return RateLimitResult.allowed(key, currentCount, maxCount, remaining, resetTime);

        } catch (Exception e) {
            log.error("Failed to check rate limit for key: {}", key, e);
            // 发生异常时默认允许通过（fail-open）
            return RateLimitResult.allowed(key, 0L, maxCount, maxCount, System.currentTimeMillis() + period * 1000L);
        }
    }

    /**
     * 重置限次
     *
     * @param key 限次 Key
     */
    @Override
    public void resetLimit(String key) {
        storage.delete(key);
        log.info("Rate limit reset for key: {}", key);
    }

    /**
     * 获取剩余次数
     *
     * @param key      限次 Key
     * @param maxCount 最大次数
     * @param period   时间窗口（秒）
     * @return 剩余次数
     */
    @Override
    public int getRemaining(String key, int maxCount, int period) {
        Long currentCount = ObjectUtils.defaultIfNull(storage.getCount(key), 0L);
        return Math.max(0, maxCount - currentCount.intValue());
    }
}
