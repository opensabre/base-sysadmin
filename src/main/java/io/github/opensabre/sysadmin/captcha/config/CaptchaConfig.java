package io.github.opensabre.sysadmin.captcha.config;

import io.github.opensabre.sysadmin.notification.config.NotificationConfig;
import org.springframework.context.annotation.Import;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Captcha configuration properties
 */
@Data
@Component
@ConfigurationProperties(prefix = "captcha")
@Import(NotificationConfig.class)
public class CaptchaConfig {
    
    private Security security = new Security();

    @Data
    public static class Security {
        private RateLimit ip = new RateLimit(5, 3600); // 5 attempts per hour per IP
        private RateLimit device = new RateLimit(5, 3600); // 5 attempts per hour per device
        private int minInterval = 60; // Minimum interval between sends in seconds (1 minute)
        private int maxAttempts = 3; // Max verification attempts per captcha
    }
    
    @Data
    public static class RateLimit {
        private int maxAttempts;
        private int timeWindow; // in seconds
        
        public RateLimit() {}
        
        public RateLimit(int maxAttempts, int timeWindow) {
            this.maxAttempts = maxAttempts;
            this.timeWindow = timeWindow;
        }
    }
}