package io.github.opensabre.sysadmin.ratelimit.enums;

import lombok.Getter;

/**
 * 限次维度枚举
 * 定义限次检查的不同维度
 *
 * @author Opensabre Team
 * @since 1.0.0
 */
@Getter
public enum RateLimitDimension {

    /**
     * IP 地址维度
     * 基于客户端IP地址进行限次
     */
    IP("IP", "IP地址"),

    /**
     * 设备 ID 维度
     * 基于User-Agent和IP的设备指纹进行限次
     */
    DEVICE("DEVICE", "设备ID"),

    /**
     * 用户 ID 维度
     * 基于安全上下文中的用户ID进行限次
     */
    USER("USER", "用户ID"),

    /**
     * 租户 ID 维度
     * 基于租户ID进行限次（多租户场景）
     */
    TENANT("TENANT", "租户ID"),

    /**
     * 业务 Key 维度
     * 基于自定义业务标识进行限次（如手机号、邮箱等）
     * 需要在注解中通过key参数指定
     */
    BUSINESS("BUSINESS", "业务Key"),

    /**
     * 自定义维度
     * 通过自定义维度提取器实现
     * 需要在注解中通过customDimensionExtractor参数指定提取器Bean名称
     */
    CUSTOM("CUSTOM", "自定义维度");

    /**
     * 维度代码
     */
    private final String code;

    /**
     * 维度描述
     */
    private final String description;

    /**
     * 构造函数
     *
     * @param code        维度代码
     * @param description 维度描述
     */
    RateLimitDimension(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
