package io.github.opensabre.sysadmin.captcha.service.impl;

import io.github.opensabre.sysadmin.captcha.enums.BusinessScenario;
import io.github.opensabre.sysadmin.captcha.model.po.CaptchaInfo;
import io.github.opensabre.sysadmin.captcha.model.po.ClientInfo;
import io.github.opensabre.sysadmin.captcha.model.vo.CaptchaVo;
import io.github.opensabre.sysadmin.notification.enums.NotificationTemplate;
import io.github.opensabre.sysadmin.notification.service.NotificationServiceManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 邮箱验证码服务
 */
@Slf4j
@Service
public class EmailCaptchaService extends CaptchaService {

    @Autowired
    private NotificationServiceManager notificationServiceManager;

    @Autowired
    public EmailCaptchaService(EmailCaptchaGenerator captchaGenerator) {
        super(captchaGenerator);
    }

    @Override
    protected void beforeGenerateCaptcha(String businessKey, BusinessScenario scenario, ClientInfo clientInfo) {
        // 验证邮件验证码发送间隔
        if (!rateLimitService.isTargetIntervalAllowed(clientInfo.businessId(), captchaConfig.getSecurity().getMinInterval())) {
            throw new RuntimeException("Target interval not met");
        }
    }

    @Override
    protected CaptchaVo afterGenerateCaptcha(CaptchaInfo captchaInfo) {
        // Send Email
        try {
            String messageId = notificationServiceManager.sendNotification(
                "EMAIL",
                captchaInfo.getBusinessKey(), 
                NotificationTemplate.CAPTCHA, 
                captchaInfo.getCode(), 
                captchaInfo.getBusinessScenario().getCaptchaExpireTime() / 60
            );
            log.info("Email sent successfully, messageId: {}", messageId);
        } catch (Exception e) {
            log.error("Failed to send email captcha", e);
        }
        
        // CaptchaVo
        return CaptchaVo.builder()
                .captchaId(captchaInfo.getCaptchaId())
                .expireTime(captchaInfo.getBusinessScenario().getCaptchaExpireTime())
                .build();
    }

    @Override
    protected boolean customValidateCaptcha(String captchaId, BusinessScenario scenario, String inputCode) {
        // nothing to do
        return false;
    }
}