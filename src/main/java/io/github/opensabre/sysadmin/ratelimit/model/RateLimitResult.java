package io.github.opensabre.sysadmin.ratelimit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 限次检查结果
 * 封装限次检查的返回结果，包含是否通过、剩余次数等信息
 *
 * @author Opensabre Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitResult {

    /**
     * 是否通过限次检查
     * true表示允许访问，false表示触发限次
     */
    private boolean allowed;

    /**
     * 剩余次数
     * 当前时间窗口内还剩余的请求次数
     */
    private int remaining;

    /**
     * 重置时间（时间戳）
     * 时间窗口的重置时间（毫秒）
     */
    private long resetTime;

    /**
     * 错误消息
     * 当限次触发时的错误消息
     */
    private String errorMessage;

    /**
     * 限次 Key
     * 当前限次检查的Key
     */
    private String key;

    /**
     * 当前计数
     * 当前时间窗口内的请求计数
     */
    private long currentCount;

    /**
     * 最大次数
     * 当前时间窗口内的最大允许次数
     */
    private long maxCount;

    /**
     * 创建允许的结果
     *
     * @param key          限次Key
     * @param currentCount 当前计数
     * @param maxCount     最大次数
     * @param remaining    剩余次数
     * @param resetTime    重置时间
     * @return 限次检查结果
     */
    public static RateLimitResult allowed(String key, long currentCount, long maxCount, int remaining, long resetTime) {
        return RateLimitResult.builder()
                .allowed(true)
                .key(key)
                .currentCount(currentCount)
                .maxCount(maxCount)
                .remaining(remaining)
                .resetTime(resetTime)
                .build();
    }

    /**
     * 创建拒绝的结果
     *
     * @param key          限次Key
     * @param currentCount 当前计数
     * @param maxCount     最大次数
     * @param errorMessage  错误消息
     * @return 限次检查结果
     */
    public static RateLimitResult denied(String key, long currentCount, long maxCount, String errorMessage) {
        return RateLimitResult.builder()
                .allowed(false)
                .key(key)
                .currentCount(currentCount)
                .maxCount(maxCount)
                .remaining(0)
                .errorMessage(errorMessage)
                .build();
    }

    /**
     * 创建拒绝的结果
     *
     * @param key          限次Key
     * @param currentCount 当前计数
     * @param maxCount     最大次数
     * @param errorMessage  错误消息
     * @param resetTime     重置时间
     * @return 限次检查结果
     */
    public static RateLimitResult denied(String key, long currentCount, long maxCount, String errorMessage, long resetTime) {
        return RateLimitResult.builder()
                .allowed(false)
                .key(key)
                .currentCount(currentCount)
                .maxCount(maxCount)
                .remaining(0)
                .errorMessage(errorMessage)
                .resetTime(resetTime)
                .build();
    }
}
