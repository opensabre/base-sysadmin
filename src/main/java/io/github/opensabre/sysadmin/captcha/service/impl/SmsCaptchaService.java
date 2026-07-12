package io.github.opensabre.sysadmin.captcha.service.impl;

import io.github.opensabre.sysadmin.captcha.model.po.CaptchaInfo;
import io.github.opensabre.sysadmin.captcha.model.po.ClientInfo;
import io.github.opensabre.sysadmin.captcha.model.po.CaptchaScene;
import io.github.opensabre.sysadmin.captcha.model.vo.CaptchaVo;
import io.github.opensabre.sysadmin.captcha.service.CaptchaNotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 短信验证码服务
 */
@Slf4j
@Service
public class SmsCaptchaService extends CaptchaService {

    private final CaptchaNotificationSender captchaNotificationSender;

    @Autowired
    public SmsCaptchaService(SmsCaptchaGenerator captchaGenerator, CaptchaNotificationSender captchaNotificationSender) {
        super(captchaGenerator);
        this.captchaNotificationSender = captchaNotificationSender;
    }

    @Override
    protected void beforeGenerateCaptcha(String businessKey, CaptchaScene scenario, ClientInfo clientInfo) {
        // 验证短信验证码发送间隔
        if (!rateLimitService.isTargetIntervalAllowed(clientInfo.businessId(), captchaConfig.getSecurity().getMinInterval())) {
            throw new RuntimeException("Target interval not met");
        }
    }

    @Override
    protected CaptchaVo afterGenerateCaptcha(CaptchaInfo captchaInfo) {
        String messageId = captchaNotificationSender.sendCaptcha(
                captchaInfo.getCaptchaScene(),
                captchaInfo.getBusinessKey(),
                captchaInfo.getCode());
        log.info("Sms sent successfully, messageId: {}", messageId);
        return CaptchaVo.builder()
                .captchaId(captchaInfo.getCaptchaId())
                .expireTime(captchaInfo.getCaptchaScene().getCaptchaExpireTime())
                .build();
    }

    @Override
    protected boolean customValidateCaptcha(String captchaId, CaptchaScene scenario, String inputCode) {
        // nothing to do
        return true;
    }
}
