package io.github.opensabre.sysadmin.ratelimit.model;

import io.github.opensabre.sysadmin.ratelimit.enums.RateLimitAlgorithmType;
import io.github.opensabre.sysadmin.ratelimit.enums.RateLimitDimension;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 限次配置模型
 * 封装限次检查所需的所有配置参数
 *
 * @author Opensabre Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitConfig {

    /**
     * 限次 Key
     * 用于标识限次对象的唯一标识
     */
    private String key;

    /**
     * Key 前缀
     * 用于区分不同的限次场景
     */
    private String keyPrefix;

    /**
     * 限次算法类型
     */
    private RateLimitAlgorithmType algorithm;

    /**
     * 限次维度列表
     * 支持多个维度组合限次
     */
    private List<RateLimitDimension> dimensions;

    /**
     * 最大次数
     */
    private int maxCount;

    /**
     * 时间窗口（秒）
     */
    private int period;

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 超限消息
     */
    private String message;

    /**
     * 是否显示剩余次数
     */
    private boolean showRemaining;

    /**
     * 自定义维度提取器
     * 当维度为CUSTOM时，指定自定义提取器的Bean名称
     */
    private String customDimensionExtractor;
}
