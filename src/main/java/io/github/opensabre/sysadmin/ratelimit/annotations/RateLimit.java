package io.github.opensabre.sysadmin.ratelimit.annotations;

import io.github.opensabre.sysadmin.ratelimit.enums.RateLimitAlgorithmType;
import io.github.opensabre.sysadmin.ratelimit.enums.RateLimitDimension;

import java.lang.annotation.*;

/**
 * 限次注解
 * 用于标记需要进行限次检查的方法
 *
 * <p>示例用法：</p>
 * <pre>
 * {@code
 * @RateLimit(
 *     algorithm = RateLimitAlgorithmType.COUNTER,
 *     dimensions = {RateLimitDimension.IP, RateLimitDimension.DEVICE},
 *     maxCount = 5,
 *     period = 60,
 *     key = "#userForm.username"
 * )
 * public Result<String> login(@RequestBody UserForm userForm) {
 *     // 业务逻辑
 * }
 * }
 * </pre>
 *
 * @author Opensabre Team
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 限次算法类型
     * 默认使用固定窗口计数器算法
     *
     * @return 算法类型
     */
    RateLimitAlgorithmType algorithm() default RateLimitAlgorithmType.COUNTER;

    /**
     * 限次维度（支持多个）
     * 默认只使用IP维度
     *
     * @return 限次维度数组
     */
    RateLimitDimension[] dimensions() default {RateLimitDimension.IP};

    /**
     * 最大允许次数
     * 默认5次
     *
     * @return 最大次数
     */
    int maxCount() default 5;

    /**
     * 时间窗口（秒）
     * 默认60秒
     *
     * @return 时间窗口秒数
     */
    int period() default 60;

    /**
     * 限次 Key（支持 SpEL 表达式）
     * 用于自定义业务维度的Key
     * 例如: "#userForm.username" 或 "#request.getParameter('phone')"
     *
     * @return SpEL表达式
     */
    String key() default "";

    /**
     * 限次 Key 前缀
     * 用于区分不同的限次场景
     *
     * @return Key前缀
     */
    String keyPrefix() default "";

    /**
     * 是否启用限次
     * 默认启用
     *
     * @return 是否启用
     */
    boolean enabled() default true;

    /**
     * 超限时的错误消息
     * 默认消息为"访问过于频繁，请稍后再试"
     *
     * @return 错误消息
     */
    String message() default "访问过于频繁，请稍后再试";

    /**
     * 是否返回剩余次数（响应头）
     * 默认返回
     *
     * @return 是否显示剩余次数
     */
    boolean showRemaining() default true;

    /**
     * 自定义维度提取器（Bean 名称）
     * 当维度为CUSTOM时，指定自定义提取器的Bean名称
     *
     * @return 自定义提取器Bean名称
     */
    String customDimensionExtractor() default "";
}
