package io.github.opensabre.sysadmin.ratelimit.enums;

import lombok.Getter;

/**
 * 限次算法类型枚举
 * 定义不同的限次检查算法
 *
 * @author Opensabre Team
 * @since 1.0.0
 */
@Getter
public enum RateLimitAlgorithmType {

    /**
     * 固定窗口计数器算法
     * <p>在固定时间窗口内统计请求次数，简单高效</p>
     * <p>优点：实现简单，内存占用小</p>
     * <p>缺点：边界问题，突发流量可能超出限制</p>
     */
    COUNTER("COUNTER", "固定窗口计数器"),

    /**
     * 滑动窗口计数器算法
     * <p>将时间窗口分成多个小窗口，统计滑动窗口内的请求次数</p>
     * <p>优点：更精确，边界更平滑</p>
     * <p>缺点：内存占用较大</p>
     */
    SLIDING_WINDOW("SLIDING_WINDOW", "滑动窗口计数器"),

    /**
     * 令牌桶算法
     * <p>桶中存放令牌，请求消耗令牌，以恒定速率补充令牌</p>
     * <p>优点：平滑流量，支持突发</p>
     * <p>缺点：需要维护桶状态</p>
     */
    TOKEN_BUCKET("TOKEN_BUCKET", "令牌桶算法"),

    /**
     * 漏桶算法
     * <p>请求进入桶，桶以恒定速率流出</p>
     * <p>优点：恒定速率输出，保护下游</p>
     * <p>缺点：不适合突发流量</p>
     */
    LEAKY_BUCKET("LEAKY_BUCKET", "漏桶算法");

    /**
     * 算法代码
     */
    private final String code;

    /**
     * 算法描述
     */
    private final String description;

    /**
     * 构造函数
     *
     * @param code        算法代码
     * @param description 算法描述
     */
    RateLimitAlgorithmType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
