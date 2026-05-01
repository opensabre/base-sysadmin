package io.github.opensabre.sysadmin.ratelimit.storage;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
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
