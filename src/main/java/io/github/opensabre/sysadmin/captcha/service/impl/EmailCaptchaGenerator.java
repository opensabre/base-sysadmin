package io.github.opensabre.sysadmin.captcha.service.impl;

import cn.hutool.core.util.RandomUtil;
import io.github.opensabre.sysadmin.captcha.enums.BusinessScenario;
import io.github.opensabre.sysadmin.captcha.model.po.ClientInfo;
import io.github.opensabre.sysadmin.captcha.model.po.CaptchaInfo;
import io.github.opensabre.sysadmin.captcha.service.ICaptchaGenerator;
import io.github.opensabre.sysadmin.captcha.enums.CaptchaType;
import io.github.opensabre.sysadmin.notification.enums.NotificationTemplate;
import io.github.opensabre.sysadmin.notification.service.INotificationService;
import io.github.opensabre.sysadmin.notification.service.NotificationServiceManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Email captcha generator implementation
 */
@Slf4j
@Component
public class EmailCaptchaGenerator implements ICaptchaGenerator {

    @Autowired
    private NotificationServiceManager notificationServiceManager;

    @Override
    public CaptchaInfo generate(String businessKey, BusinessScenario scenario, ClientInfo clientInfo) {
        // Generate a random code for Email
        String code = RandomUtil.randomString(scenario.getCaptchaLength());
        log.info("Generated Email captcha for businessKey: {}, scenario: {}", businessKey, scenario.getCode());

        // Create CaptchaInfo with the generated code
        CaptchaInfo captchaInfo = CaptchaInfo.builder()
                .businessKey(businessKey)
                .captchaType(scenario.getType())
                .businessScenario(scenario)
                .code(code)
                .clientInfo(clientInfo)
                .expireTime(LocalDateTime.now().plusSeconds(scenario.getCaptchaExpireTime()))
                .build();

        // Send Email with the generated code
        sendEmailNotification(businessKey, code, scenario);

        return captchaInfo;
    }

    /**
     * Send Email notification with the captcha code
     *
     * @param targetEmail Target email address
     * @param captchaCode Captcha code to send
     * @param scenario    Business scenario
     */
    private void sendEmailNotification(String targetEmail, String captchaCode, BusinessScenario scenario) {
        // Get Email notification service
        INotificationService emailService = notificationServiceManager.getService("EMAIL");
        
        if (emailService == null) {
            log.error("Email notification service not available");
            return;
        }

        // Send Email
        String result = notificationServiceManager.sendNotification("EMAIL", targetEmail, 
            NotificationTemplate.CAPTCHA, captchaCode, scenario.getCaptchaExpireTime()/60);

        if (result != null) {
            log.info("Email captcha sent successfully to: {}, messageId: {}", targetEmail, result);
        } else {
            log.error("Failed to send Email captcha to: {}", targetEmail);
        }
    }

    @Override
    public String getType() {
        return CaptchaType.EMAIL.getCode();
    }
}