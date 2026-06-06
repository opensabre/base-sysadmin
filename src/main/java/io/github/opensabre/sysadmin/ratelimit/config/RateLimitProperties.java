package io.github.opensabre.sysadmin.ratelimit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 限次配置属性
 * 通过application.yml或application.properties配置限次参数
 *
 * <p>配置示例：</p>
 * <pre>
 * ratelimit:
 *   enabled: true
 *   key-prefix: "ratelimit:"
 *   default:
 *     algorithm: COUNTER
 *     max-count: 5
 *     period: 60
 *     dimensions: [IP, DEVICE]
 * </pre>
 *
 * @author Opensabre Team
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "ratelimit")
public class RateLimitProperties {

    /**
     * 是否启用限次功能
     * 默认启用
     */
    private boolean enabled = true;

    /**
     * Redis Key 前缀
     * 用于区分不同应用的限次数据
     * 默认前缀：ratelimit:
     */
    private String keyPrefix = "ratelimit:";

    /**
     * 默认限次配置
     * 当注解中未指定参数时使用的默认值
     */
    private Default defaultConfig = new Default();

    /**
     * 全局限次配置
     * 针对不同维度的全局限次配置
     */
    private Global global = new Global();

    /**
     * 默认限次配置
     */
    @Data
    public static class Default {
        /**
         * 默认算法类型
         * 默认使用固定窗口计数器
         */
        private String algorithm = "COUNTER";

        /**
         * 默认最大次数
         * 默认5次
         */
        private int maxCount = 5;

        /**
         * 默认时间窗口（秒）
         * 默认60秒
         */
        private int period = 60;

        /**
         * 默认维度列表
         * 默认只使用IP维度
         */
        private String[] dimensions = {"IP"};
    }

    /**
     * 全局限次配置
     */
    @Data
    public static class Global {
        /**
         * 全局 IP 限次配置
         */
        private Ip ip = new Ip();

        /**
         * 全局设备限次配置
         */
        private Device device = new Device();

        /**
         * 全局用户限次配置
         */
        private User user = new User();

        @Data
        public static class Ip {
            /**
             * IP限次最大次数
             * 默认10次/小时
             */
            private int maxCount = 10;

            /**
             * IP限次时间窗口（秒）
             * 默认1小时
             */
            private int period = 3600;
        }

        @Data
        public static class Device {
            /**
             * 设备限次最大次数
             * 默认10次/小时
             */
            private int maxCount = 10;

            /**
             * 设备限次时间窗口（秒）
             * 默认1小时
             */
            private int period = 3600;
        }

        @Data
        public static class User {
            /**
             * 用户限次最大次数
             * 默认20次/小时
             */
            private int maxCount = 20;

            /**
             * 用户限次时间窗口（秒）
             * 默认1小时
             */
            private int period = 3600;
        }
    }
}
