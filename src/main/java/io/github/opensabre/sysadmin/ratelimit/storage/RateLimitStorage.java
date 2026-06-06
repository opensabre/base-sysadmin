package io.github.opensabre.sysadmin.ratelimit.storage;

/**
 * 限次存储接口
 * 定义限次数据的存储抽象
 *
 * <p>支持不同的存储后端：
 * <ul>
 *   <li>Redis：默认实现，支持分布式场景</li>
 *   <li>内存：本地内存存储（开发环境）</li>
 *   <li>其他：可扩展实现其他存储方案</li>
 * </ul>
 * </p>
 *
 * @author Opensabre Team
 * @since 1.0.0
 */
public interface RateLimitStorage {

    /**
     * 获取计数
     *
     * @param key 存储键
     * @return 计数值，如果不存在则返回null
     */
    Long getCount(String key);

    /**
     * 增加计数
     *
     * @param key  存储键
     * @param delta 增量
     * @return 增加后的值
     */
    Long increment(String key, long delta);

    /**
     * 原子增加计数，并在首次创建时设置过期时间。
     *
     * @param key    存储键
     * @param delta  增量
     * @param expire 过期时间（秒）
     * @return 增加后的值
     */
    Long incrementAndExpire(String key, long delta, long expire);

    /**
     * 设置过期时间
     *
     * @param key    存储键
     * @param expire 过期时间（秒）
     */
    void expire(String key, long expire);

    /**
     * 设置值和过期时间
     *
     * @param key    存储键
     * @param value  值
     * @param expire 过期时间（秒）
     */
    void set(String key, Object value, long expire);

    /**
     * 删除键
     *
     * @param key 存储键
     */
    void delete(String key);

    /**
     * 批量删除
     *
     * @param keys 存储键数组
     */
    void delete(String[] keys);

    /**
     * 检查键是否存在
     *
     * @param key 存储键
     * @return 是否存在
     */
    Boolean exists(String key);
}
