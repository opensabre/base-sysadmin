package io.github.opensabre.sysadmin.captcha.service.impl;

import io.github.opensabre.sysadmin.captcha.enums.BusinessScenario;
import io.github.opensabre.sysadmin.captcha.model.po.ClientInfo;
import io.github.opensabre.sysadmin.captcha.model.po.CaptchaInfo;
import io.github.opensabre.sysadmin.captcha.model.po.CaptchaScene;
import io.github.opensabre.sysadmin.captcha.model.vo.CaptchaVo;
import io.github.opensabre.sysadmin.captcha.service.ICaptchaGenerator;
import io.github.opensabre.sysadmin.captcha.service.ICaptchaService;
import io.github.opensabre.sysadmin.captcha.service.ICaptchaStorageService;
import io.github.opensabre.sysadmin.captcha.service.IRateLimitService;
import io.github.opensabre.sysadmin.usage.enums.UsageEvent;
import io.github.opensabre.sysadmin.usage.enums.UsageObjectType;
import io.github.opensabre.sysadmin.usage.enums.UsageOutcome;
import io.github.opensabre.sysadmin.usage.service.IUsageCounterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

/**
 * Main captcha service that orchestrates captcha generation, validation, and security
 */
@Slf4j
public abstract class CaptchaService implements ICaptchaService {

    /**
     * 验证码生成器
     */
    private final ICaptchaGenerator captchaGenerator;

    @Autowired
    protected ICaptchaStorageService captchaStorage;

    @Autowired
    protected IRateLimitService rateLimitService;

    @Autowired
    protected IUsageCounterService usageCounterService;


    public CaptchaService(ICaptchaGenerator captchaGenerator) {
        this.captchaGenerator = captchaGenerator;
    }

    /**
     * 验证码生成前
     */
    protected abstract void beforeGenerateCaptcha(String businessKey, CaptchaScene scenario, ClientInfo clientInfo);

    @Override
    public CaptchaVo generateCaptcha(String businessKey, BusinessScenario scenario, ClientInfo clientInfo) {
        return generateCaptcha(businessKey, CaptchaScene.from(scenario), clientInfo);
    }

    @Override
    public CaptchaVo generateCaptcha(String businessKey, CaptchaScene scenario, ClientInfo clientInfo) {
        // IP、设备和业务标识都由限次场景表维护，便于运行时调整。
        if (!rateLimitService.isAllowed(IRateLimitService.CAPTCHA_IP_SCENE, clientInfo.clientIp())) {
            throw new RuntimeException("IP rate limit exceeded");
        }
        if (!rateLimitService.isAllowed(IRateLimitService.CAPTCHA_DEVICE_SCENE, clientInfo.deviceId())) {
            throw new RuntimeException("Device rate limit exceeded");
        }
        if (!rateLimitService.isAllowed(IRateLimitService.captchaBusinessSceneCode(scenario.getSceneCode()),
                clientInfo.businessId())) {
            throw new RuntimeException("BusinessId rate limit exceeded");
        }
        recordUsage(scenario, UsageEvent.CAPTCHA_GENERATE, UsageOutcome.ATTEMPT);
        try {
            // 验证码生成前置逻辑
            beforeGenerateCaptcha(businessKey, scenario, clientInfo);
            CaptchaInfo captchaInfo = this.captchaGenerator.generate(businessKey, scenario, clientInfo);
            captchaStorage.save(captchaInfo, scenario);
            CaptchaVo captchaVo = afterGenerateCaptcha(captchaInfo);
            recordUsage(scenario, UsageEvent.CAPTCHA_GENERATE, UsageOutcome.SUCCESS);
            log.info("Captcha generated: businessKey={}, captchaId={}, scenario={}", businessKey, captchaVo.getCaptchaId(), scenario);
            return captchaVo;
        } catch (RuntimeException exception) {
            recordUsage(scenario, UsageEvent.CAPTCHA_GENERATE, UsageOutcome.FAILURE);
            throw exception;
        }
    }

    /**
     * 验证码生成后
     */
    protected abstract CaptchaVo afterGenerateCaptcha(CaptchaInfo captchaInfo);

    @Override
    public boolean validateCaptcha(String captchaId, BusinessScenario scenario, String inputCode) {
        return validateCaptcha(captchaId, CaptchaScene.from(scenario), inputCode);
    }

    @Override
    public boolean validateCaptcha(String captchaId, CaptchaScene scenario, String inputCode) {
        recordUsage(scenario, UsageEvent.CAPTCHA_VERIFY, UsageOutcome.ATTEMPT);
        try {
            boolean valid = validateCaptchaInternal(captchaId, scenario, inputCode);
            recordUsage(scenario, UsageEvent.CAPTCHA_VERIFY, valid ? UsageOutcome.SUCCESS : UsageOutcome.FAILURE);
            return valid;
        } catch (RuntimeException exception) {
            recordUsage(scenario, UsageEvent.CAPTCHA_VERIFY, UsageOutcome.FAILURE);
            throw exception;
        }
    }

    private boolean validateCaptchaInternal(String captchaId, CaptchaScene scenario, String inputCode) {
        if (!customValidateCaptcha(captchaId, scenario, inputCode)) {
            return false;
        }
        CaptchaInfo captchaInfo = captchaStorage.get(captchaId, scenario);
        if (captchaInfo == null) {
            log.warn("Captcha not found: captchaId={}, scenario={}", captchaId, scenario);
            return false;
        }
        if (captchaInfo.isVerified() || LocalDateTime.now().isAfter(captchaInfo.getExpireTime())) {
            log.warn("Captcha already verified or expired: captchaId={}, scenario={}", captchaId, scenario);
            captchaStorage.delete(captchaId, scenario);
            return false;
        }
        if (captchaInfo.getAttempts() >= scenario.getCaptchaAttempts()) {
            log.warn("Max attempts exceeded: sceneId={}, scenario={}", captchaId, scenario);
            captchaStorage.delete(captchaId, scenario);
            return false;
        }
        boolean isValid = captchaInfo.getCode().equalsIgnoreCase(inputCode);
        if (isValid) {
            captchaStorage.delete(captchaId, scenario);
            log.info("Captcha validated successfully: captchaId={}, scenario={}", captchaId, scenario);
            return true;
        }
        captchaStorage.incrementAttempts(captchaId, scenario);
        log.warn("Captcha validation failed: captchaId={}, scenario={}, attempts={}", captchaId, scenario, captchaInfo.getAttempts() + 1);
        return false;
    }

    private void recordUsage(CaptchaScene scenario, UsageEvent event, UsageOutcome outcome) {
        usageCounterService.record(UsageObjectType.CAPTCHA_SCENE, scenario.getSceneCode(), event, outcome);
    }

    /**
     * 自定义校验
     *
     * @param captchaId 验证码 Id
     * @param scenario  验证码场景
     * @param inputCode 用户输入的验证码
     * @return 验证结果 true/ false
     */
    protected abstract boolean customValidateCaptcha(String captchaId, CaptchaScene scenario, String inputCode);
}
