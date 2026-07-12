package io.github.opensabre.sysadmin.ratelimit.storage;

import jakarta.annotation.Resource;
import io.github.opensabre.sysadmin.ratelimit.model.RateLimitResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Redis限次存储实现
 * 基于Redis实现限次数据的存储和计数
 *
 * <p>特性：
 * <ul>
 *   <li>支持分布式场景</li>
 *   <li>自动过期清理</li>
 *   <li>高性能读写</li>
 *   <li>原子性保证</li>
 * </ul>
 * </p>
 *
 * @author Opensabre Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class RedisRateLimitStorage implements RateLimitStorage {

    private static final DefaultRedisScript<Long> INCREMENT_AND_EXPIRE_SCRIPT = new DefaultRedisScript<>("""
            local current = redis.call('incrby', KEYS[1], ARGV[1])
            if current == tonumber(ARGV[1]) then
                redis.call('expire', KEYS[1], ARGV[2])
            end
            return current
            """, Long.class);

    private static final DefaultRedisScript<List> SLIDING_WINDOW_SCRIPT = new DefaultRedisScript<>("""
            redis.call('zremrangebyscore', KEYS[1], 0, ARGV[2])
            redis.call('zadd', KEYS[1], ARGV[1], ARGV[4])
            local current = redis.call('zcard', KEYS[1])
            redis.call('expire', KEYS[1], ARGV[3])
            local oldest = redis.call('zrange', KEYS[1], 0, 0, 'withscores')
            local reset = tonumber(ARGV[1]) + tonumber(ARGV[3]) * 1000
            if oldest[2] ~= nil then
                reset = tonumber(oldest[2]) + tonumber(ARGV[3]) * 1000
            end
            return {current, reset}
            """, List.class);

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取计数
     *
     * @param key 存储键
     * @return 计数值，如果不存在则返回null
     */
    @Override
    public Long getCount(String key) {
        try {
            String value = stringRedisTemplate.opsForValue().get(key);
            return value != null ? Long.parseLong(value) : null;
        } catch (Exception e) {
            log.error("Failed to get count for key: {}", key, e);
            return null;
        }
    }

    /**
     * 增加计数
     *
     * @param key  存储键
     * @param delta 增量
     * @return 增加后的值
     */
    @Override
    public Long increment(String key, long delta) {
        try {
            return stringRedisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            log.error("Failed to increment key: {}", key, e);
            throw new RuntimeException("Failed to increment rate limit count", e);
        }
    }

    /**
     * 原子增加计数，并在首次创建时设置过期时间
     *
     * @param key    存储键
     * @param delta  增量
     * @param expire 过期时间（秒）
     * @return 增加后的值
     */
    @Override
    public Long incrementAndExpire(String key, long delta, long expire) {
        try {
            return stringRedisTemplate.execute(
                    INCREMENT_AND_EXPIRE_SCRIPT,
                    Collections.singletonList(key),
                    String.valueOf(delta),
                    String.valueOf(expire)
            );
        } catch (Exception e) {
            log.error("Failed to increment and expire key: {}", key, e);
            throw new RuntimeException("Failed to increment rate limit count", e);
        }
    }

    /**
     * 使用 Redis ZSET 原子执行滑动窗口限次检查。
     *
     * @param key      存储键
     * @param maxCount 最大次数
     * @param period   时间窗口（秒）
     * @return 限次检查结果
     */
    @Override
    @SuppressWarnings("unchecked")
    public RateLimitResult checkSlidingWindow(String key, int maxCount, int period) {
        long now = System.currentTimeMillis();
        long windowStart = now - period * 1000L;
        String member = now + ":" + UUID.randomUUID();
        try {
            List<Long> result = (List<Long>) stringRedisTemplate.execute(
                    SLIDING_WINDOW_SCRIPT,
                    Collections.singletonList(key),
                    String.valueOf(now),
                    String.valueOf(windowStart),
                    String.valueOf(period),
                    member
            );
            long currentCount = result == null || result.isEmpty() ? 0L : result.get(0);
            long resetTime = result == null || result.size() < 2 ? now + period * 1000L : result.get(1);
            if (currentCount > maxCount) {
                log.warn("Sliding window rate limit exceeded: key={}, current={}, max={}", key, currentCount, maxCount);
                return RateLimitResult.denied(key, currentCount, maxCount, String.format("当前已超过限次，请%d秒后再试", period), resetTime);
            }
            int remaining = Math.max(0, maxCount - (int) currentCount);
            return RateLimitResult.allowed(key, currentCount, maxCount, remaining, resetTime);
        } catch (Exception e) {
            log.error("Failed to check sliding window rate limit for key: {}", key, e);
            throw new RuntimeException("Failed to check sliding window rate limit", e);
        }
    }

    /**
     * 设置过期时间
     *
     * @param key    存储键
     * @param expire 过期时间（秒）
     */
    @Override
    public void expire(String key, long expire) {
        try {
            stringRedisTemplate.expire(key, expire, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to set expire for key: {}", key, e);
        }
    }

    /**
     * 设置值和过期时间
     *
     * @param key    存储键
     * @param value  值
     * @param expire 过期时间（秒）
     */
    @Override
    public void set(String key, Object value, long expire) {
        try {
            stringRedisTemplate.opsForValue().set(key, String.valueOf(value), expire, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to set value for key: {}", key, e);
        }
    }

    /**
     * 删除键
     *
     * @param key 存储键
     */
    @Override
    public void delete(String key) {
        try {
            stringRedisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Failed to delete key: {}", key, e);
        }
    }

    /**
     * 批量删除
     *
     * @param keys 存储键数组
     */
    @Override
    public void delete(String[] keys) {
        try {
            stringRedisTemplate.delete(Arrays.asList(keys));
        } catch (Exception e) {
            log.error("Failed to delete keys: {}", Arrays.toString(keys), e);
        }
    }

    /**
     * 检查键是否存在
     *
     * @param key 存储键
     * @return 是否存在
     */
    @Override
    public Boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Failed to check existence of key: {}", key, e);
            return false;
        }
    }
}
