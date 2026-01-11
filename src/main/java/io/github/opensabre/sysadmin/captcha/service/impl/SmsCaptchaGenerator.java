package io.github.opensabre.sysadmin.captcha.service.impl;

import cn.hutool.core.util.RandomUtil;
import io.github.opensabre.sysadmin.captcha.enums.BusinessScenario;
import io.github.opensabre.sysadmin.captcha.model.po.ClientInfo;
import io.github.opensabre.sysadmin.captcha.model.po.CaptchaInfo;
import io.github.opensabre.sysadmin.captcha.service.ICaptchaGenerator;
import io.github.opensabre.sysadmin.captcha.enums.CaptchaType;
import io.github.opensabre.sysadmin.notification.service.INotificationService;
import io.github.opensabre.sysadmin.notification.service.NotificationServiceManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * SMS captcha generator implementation
 */
@Slf4j
@Component
public class SmsCaptchaGenerator implements ICaptchaGenerator {

    @Autowired
    private NotificationServiceManager notificationServiceManager;

    @Override
    public CaptchaInfo generate(String businessKey, BusinessScenario scenario, ClientInfo clientInfo) {
        // Generate a random numeric code for SMS
        String code = RandomUtil.randomNumbers(scenario.getCaptchaLength());
        // Get target phone number from params
        log.info("Generated SMS captcha for businessKey: {}, scenario: {}", businessKey, scenario.getCode());

        // Create CaptchaInfo with the generated code
        CaptchaInfo captchaInfo = CaptchaInfo.builder()
                .businessKey(businessKey)
                .captchaType(scenario.getType())
                .businessScenario(scenario)
                .code(code)
                .clientInfo(clientInfo)
                .expireTime(LocalDateTime.now().plusSeconds(scenario.getCaptchaExpireTime()))
                .build();

        // Send SMS with the generated code
        sendSmsNotification(businessKey, code, scenario);

        return captchaInfo;
    }

    /**
     * Send SMS notification with the captcha code
     *
     * @param targetPhone Target phone number
     * @param captchaCode Captcha code to send
     * @param scenario    Business scenario
     */
    private void sendSmsNotification(String targetPhone, String captchaCode, BusinessScenario scenario) {
        // Get SMS notification service
        INotificationService smsService = notificationServiceManager.getService("SMS");
        
        if (smsService == null) {
            log.error("SMS notification service not available");
            return;
        }

        // Build SMS content
        io.github.opensabre.sysadmin.notification.service.impl.SmsNotificationService smsNotificationService = 
            (io.github.opensabre.sysadmin.notification.service.impl.SmsNotificationService) smsService;
        String content = smsNotificationService.buildSmsContent(captchaCode, scenario.getCode());

        // Send SMS
        INotificationService.NotificationResult result = 
            notificationServiceManager.sendNotification("SMS", targetPhone, content, scenario.getCode());

        if (result.isSuccess()) {
            log.info("SMS captcha sent successfully to: {}, messageId: {}", targetPhone, result.getMessageId());
        } else {
            log.error("Failed to send SMS captcha to: {}, error: {}", targetPhone, result.getErrorMessage());
        }
    }

    @Override
    public String getType() {
        return CaptchaType.SMS.getCode();
    }
}