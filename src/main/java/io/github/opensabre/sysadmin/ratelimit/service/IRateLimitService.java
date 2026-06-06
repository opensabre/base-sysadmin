package io.github.opensabre.sysadmin.ratelimit.service;

import io.github.opensabre.sysadmin.ratelimit.enums.RateLimitAlgorithmType;
import io.github.opensabre.sysadmin.ratelimit.enums.RateLimitDimension;
import io.github.opensabre.sysadmin.ratelimit.model.RateLimitConfig;
import io.github.opensabre.sysadmin.ratelimit.model.RateLimitResult;

import java.util.List;

/**
 * 限次服务接口
 * 提供编程式 API 进行限次检查
 *
 * <p>使用场景：
 * <ul>
 *   <li>注解方式：通过@RateLimit注解自动限次</li>
 *   <li>编程方式：通过此接口手动调用限次检查</li>
 *   <li>自定义场景：在需要特殊逻辑的场景中手动控制</li>
 * </ul>
 * </p>
 *
 * @author Opensabre Team
 * @since 1.0.0
 */
public interface IRateLimitService {

    /**
     * 检查限次（使用默认配置）
     * 使用全局配置进行限次检查
     *
     * @return 限次检查结果
     */
    RateLimitResult checkLimit();

    /**
     * 检查限次（指定维度）
     * 使用指定维度进行限次检查，其他参数使用默认配置
     *
     * @param dimensions 限次维度列表
     * @return 限次检查结果
     */
    RateLimitResult checkLimit(List<RateLimitDimension> dimensions);

    /**
     * 检查限次（指定所有参数）
     * 提供完整的配置进行限次检查
     *
     * @param key        限次 Key
     * @param maxCount   最大次数
     * @param period     时间窗口（秒）
     * @param algorithm  限次算法
     * @return 限次检查结果
     */
    RateLimitResult checkLimit(String key, int maxCount, int period, RateLimitAlgorithmType algorithm);

    /**
     * 检查限次（使用完整配置）
     * 使用RateLimitConfig对象进行限次检查
     *
     * @param config 限次配置
     * @return 限次检查结果
     */
    RateLimitResult checkLimit(RateLimitConfig config);

    /**
     * 重置限次
     * 手动重置指定Key的限次计数
     *
     * @param key 限次 Key
     */
    void resetLimit(String key);

    /**
     * 获取剩余次数
     * 获取指定Key的剩余请求次数
     *
     * @param key      限次 Key
     * @param maxCount 最大次数
     * @param period   时间窗口（秒）
     * @return 剩余次数
     */
    int getRemaining(String key, int maxCount, int period);

    /**
     * 生成限次Key
     * 根据多个维度生成唯一的限次Key
     *
     * @param dimensions 限次维度列表
     * @param keyPrefix Key前缀
     * @return 限次Key
     */
    String generateKey(List<RateLimitDimension> dimensions, String keyPrefix);
}
