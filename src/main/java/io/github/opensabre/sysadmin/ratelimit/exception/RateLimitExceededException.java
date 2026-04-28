package io.github.opensabre.sysadmin.ratelimit.exception;

import lombok.Getter;

/**
 * 限次超限异常
 * 当请求超过限次限制时抛出此异常
 *
 * @author Opensabre Team
 * @since 1.0.0
 */
@Getter
public class RateLimitExceededException extends RuntimeException {

    /**
     * 错误消息
     */
    private final String message;

    /**
     * 剩余次数
     */
    private final int remaining;

    /**
     * 重置时间（时间戳）
     */
    private final long resetTime;

    /**
     * 构造函数
     *
     * @param message    错误消息
     * @param remaining   剩余次数
     * @param resetTime   重置时间
     */
    public RateLimitExceededException(String message, int remaining, long resetTime) {
        super(message);
        this.message = message;
        this.remaining = remaining;
        this.resetTime = resetTime;
    }

    /**
     * 构造函数（仅消息）
     *
     * @param message 错误消息
     */
    public RateLimitExceededException(String message) {
        this(message, 0, 0L);
    }
}
