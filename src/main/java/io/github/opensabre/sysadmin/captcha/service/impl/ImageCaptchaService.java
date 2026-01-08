package io.github.opensabre.sysadmin.captcha.service.impl;

import io.github.opensabre.sysadmin.captcha.enums.BusinessScenario;
import io.github.opensabre.sysadmin.captcha.model.po.CaptchaInfo;
import io.github.opensabre.sysadmin.captcha.model.po.ClientInfo;
import io.github.opensabre.sysadmin.captcha.model.vo.CaptchaVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 图形验证码服务
 */
@Service
public class ImageCaptchaService extends CaptchaService {

    @Autowired
    public ImageCaptchaService(ImageCaptchaGenerator captchaGenerator) {
        super(captchaGenerator);
    }

    @Override
    protected void beforeGenerateCaptcha(String businessKey, BusinessScenario scenario, ClientInfo clientInfo) {
        // nothing to do
    }

    @Override
    protected CaptchaVo afterGenerateCaptcha(CaptchaInfo captchaInfo) {
        // CaptchaVo
        return CaptchaVo.builder()
                .captchaId(captchaInfo.getCaptchaId())
                .imageData(captchaInfo.getData()) // 图片数据
                .expireTime(captchaInfo.getBusinessScenario().getCaptchaExpireTime())
                .build();
    }

    @Override
    protected boolean customValidateCaptcha(String captchaId, BusinessScenario scenario, String inputCode) {
        // nothing to do
        return true;
    }
}
