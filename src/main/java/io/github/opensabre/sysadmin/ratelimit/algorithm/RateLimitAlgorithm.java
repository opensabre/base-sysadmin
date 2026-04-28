package io.github.opensabre.sysadmin.ratelimit.algorithm;

import io.github.opensabre.sysadmin.ratelimit.model.RateLimitResult;

/**
 * 限次算法接口
 * 定义限次检查的核心算法抽象
 *
 * <p>实现此接口可以扩展不同的限次算法：
 * <ul>
 *   <li>固定窗口计数器（Counter）</li>
 *   <li>滑动窗口计数器（SlidingWindow）</li>
 *   <li>令牌桶（TokenBucket）</li>
 *   <li>漏桶（LeakyBucket）</li>
 * </ul>
 * </p>
 *
 * @author Opensabre Team
 * @since 1.0.0
 */
public interface RateLimitAlgorithm {

    /**
     * 检查限次
     * 核心方法，判断是否允许当前请求通过
     *
     * @param key       限次 Key
     * @param maxCount  最大次数
     * @param period    时间窗口（秒）
     * @return 限次检查结果
     */
    RateLimitResult checkLimit(String key, int maxCount, int period);

    /**
     * 重置限次
     * 手动重置指定Key的限次计数
     *
     * @param key 限次 Key
     */
    void resetLimit(String key);

    /**
     * 获取剩余次数
     * 获取当前时间窗口内的剩余请求次数
     *
     * @param key      限次 Key
     * @param maxCount 最大次数
     * @param period   时间窗口（秒）
     * @return 剩余次数
     */
    int getRemaining(String key, int maxCount, int period);
}
