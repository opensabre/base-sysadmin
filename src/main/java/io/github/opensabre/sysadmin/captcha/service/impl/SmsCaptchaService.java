package io.github.opensabre.sysadmin.captcha.service.impl;

import io.github.opensabre.sysadmin.captcha.enums.BusinessScenario;
import io.github.opensabre.sysadmin.captcha.model.po.CaptchaInfo;
import io.github.opensabre.sysadmin.captcha.model.po.ClientInfo;
import io.github.opensabre.sysadmin.captcha.model.vo.CaptchaVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 短信验证码服务
 */
@Service
public class SmsCaptchaService extends CaptchaService {

    @Autowired
    public SmsCaptchaService(SmsCaptchaGenerator captchaGenerator) {
        super(captchaGenerator);
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

        // CaptchaVo
        return CaptchaVo.builder()
                .captchaId(captchaInfo.getCaptchaId())
                .expireTime(captchaInfo.getBusinessScenario().getCaptchaExpireTime())
                .build();
    }

    @Override
    protected boolean customValidateCaptcha(String captchaId, BusinessScenario scenario, String inputCode) {
        return false;
    }
}
