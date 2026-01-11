package io.github.opensabre.sysadmin.captcha.service.impl;

import io.github.opensabre.sysadmin.captcha.enums.BusinessScenario;
import io.github.opensabre.sysadmin.captcha.model.po.CaptchaInfo;
import io.github.opensabre.sysadmin.captcha.model.po.ClientInfo;
import io.github.opensabre.sysadmin.captcha.model.vo.CaptchaVo;
import io.github.opensabre.sysadmin.notification.enums.NotificationTemplate;
import io.github.opensabre.sysadmin.notification.service.INotificationService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 短信验证码服务
 */
@Slf4j
@Service
public class SmsCaptchaService extends CaptchaService {

    private final INotificationService notificationService;

    @Autowired
    public SmsCaptchaService(SmsCaptchaGenerator captchaGenerator, INotificationService smsNotificationService) {
        super(captchaGenerator);
        this.notificationService = smsNotificationService;
    }

    @Override
    protected void beforeGenerateCaptcha(String businessKey, BusinessScenario scenario, ClientInfo clientInfo) {
        // 验证短信验证码发送间隔
        if (!rateLimitService.isTargetIntervalAllowed(clientInfo.businessId(), captchaConfig.getSecurity().getMinInterval())) {
            throw new RuntimeException("Target interval not met");
        }
    }

    @Override
    protected CaptchaVo afterGenerateCaptcha(CaptchaInfo captchaInfo) {
        // Send Sms
        NotificationTemplate template = NotificationTemplate.valueOf(captchaInfo.getBusinessScenario().getTemplateCode());
        String messageId = notificationService.send(captchaInfo.getBusinessKey(), template, captchaInfo.getCode(), 5);
        log.info("Sms sent successfully, messageId: {}", messageId);
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
