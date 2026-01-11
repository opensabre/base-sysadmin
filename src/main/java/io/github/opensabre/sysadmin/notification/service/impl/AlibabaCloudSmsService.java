package io.github.opensabre.sysadmin.notification.service.impl;

import io.github.opensabre.sysadmin.captcha.config.CaptchaConfig;
import io.github.opensabre.sysadmin.notification.service.INotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 阿里云短信服务实现
 * 当配置 captcha.sms.provider=alibaba 时启用
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "captcha.sms.provider", havingValue = "alibaba")
public class AlibabaCloudSmsService implements INotificationService {

    @Autowired
    private CaptchaConfig captchaConfig;

    @Override
    public NotificationResult send(String target, String content, String template) {
        log.info("Sending SMS via Alibaba Cloud to: {}, content: {}, template: {}", target, content, template);

        try {
            // 这里应该集成阿里云短信服务SDK
            // 实际项目中需要引入阿里云SDK依赖
            boolean success = sendViaAlibabaCloud(target, content, template);
            
            if (success) {
                // 生成消息ID（在真实实现中，这通常来自阿里云响应）
                String messageId = "ALIBABA_SMS_" + System.currentTimeMillis() + "_" + target;
                log.info("SMS sent successfully via Alibaba Cloud to: {}, messageId: {}", target, messageId);
                return NotificationResult.success(messageId);
            } else {
                log.error("Failed to send SMS via Alibaba Cloud to: {}, content: {}", target, content);
                return NotificationResult.failure("Failed to send SMS via Alibaba Cloud");
            }
        } catch (Exception e) {
            log.error("Exception occurred while sending SMS via Alibaba Cloud to: {}", target, e);
            return NotificationResult.failure("Exception occurred: " + e.getMessage());
        }
    }

    @Override
    public String getType() {
        return "SMS";
    }

    /**
     * 通过阿里云发送短信
     * 
     * @param phoneNumber 目标手机号
     * @param content 短信内容
     * @param template 短信模板
     * @return 发送是否成功
     */
    private boolean sendViaAlibabaCloud(String phoneNumber, String content, String template) {
        // 实际的阿里云短信发送逻辑
        // 这里仅作为示例，实际实现需要：
        // 1. 引入阿里云SDK依赖
        // 2. 配置访问密钥
        // 3. 设置短信模板
        // 4. 调用API发送短信
        
        log.debug("Sending SMS to {} via Alibaba Cloud with content: {} and template: {}", 
                  phoneNumber, content, template);
        
        // 模拟发送成功
        return true;
    }
}