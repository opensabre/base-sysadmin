package io.github.opensabre.sysadmin.captcha.service.impl;

import cn.hutool.core.util.RandomUtil;
import io.github.opensabre.sysadmin.captcha.enums.BusinessScenario;
import io.github.opensabre.sysadmin.captcha.enums.CaptchaType;
import io.github.opensabre.sysadmin.captcha.model.po.CaptchaInfo;
import io.github.opensabre.sysadmin.captcha.model.po.ClientInfo;
import io.github.opensabre.sysadmin.captcha.service.ICaptchaGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * SMS captcha generator implementation
 */
@Slf4j
@Component
public class SmsCaptchaGenerator implements ICaptchaGenerator {

    @Override
    public CaptchaInfo generate(String businessKey, BusinessScenario scenario, ClientInfo clientInfo) {
        // Generate a random numeric code for SMS
        String code = RandomUtil.randomNumbers(scenario.getCaptchaLength());
        // Get target phone number from params
        log.info("Generated SMS captcha for businessKey: {}, scenario: {}", businessKey, scenario.getCode());

        // Create CaptchaInfo with the generated code
        return CaptchaInfo.builder()
                .businessKey(businessKey)
                .captchaType(scenario.getType())
                .businessScenario(scenario)
                .code(code)
                .clientInfo(clientInfo)
                .expireTime(LocalDateTime.now().plusSeconds(scenario.getCaptchaExpireTime()))
                .build();
    }
    @Override
    public String getType() {
        return CaptchaType.SMS.getCode();
    }
}