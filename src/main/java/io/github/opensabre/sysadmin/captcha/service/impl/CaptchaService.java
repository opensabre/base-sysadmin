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
        // 验证码生成前置逻辑
        beforeGenerateCaptcha(businessKey, scenario, clientInfo);
        // Generate the captcha
        CaptchaInfo captchaInfo = this.captchaGenerator.generate(businessKey, scenario, clientInfo);
        // 保存到缓存中
        captchaStorage.save(captchaInfo, scenario);
        // 验证码处理后续逻辑，返回Vo
        CaptchaVo captchaVo = afterGenerateCaptcha(captchaInfo);
        log.info("Captcha generated: businessKey={}, captchaId={}, scenario={}", businessKey, captchaVo.getCaptchaId(), scenario);
        return captchaVo;
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
        // 自定义校验
        if (!customValidateCaptcha(captchaId, scenario, inputCode)) {
            return false;
        }
        // Retrieve captcha from storage
        CaptchaInfo captchaInfo = captchaStorage.get(captchaId, scenario);
        // 无此 captchaId
        if (captchaInfo == null) {
            log.warn("Captcha not found: captchaId={}, scenario={}", captchaId, scenario);
            return false;
        }
        // Check if already verified
        if (captchaInfo.isVerified() || LocalDateTime.now().isAfter(captchaInfo.getExpireTime())) {
            log.warn("Captcha already verified or expired: captchaId={}, scenario={}", captchaId, scenario);
            captchaStorage.delete(captchaId, scenario); // Clean up expired captcha
            return false;
        }
        // 超过验证次数
        if (captchaInfo.getAttempts() >= scenario.getCaptchaAttempts()) {
            log.warn("Max attempts exceeded: sceneId={}, scenario={}", captchaId, scenario);
            captchaStorage.delete(captchaId, scenario); // Clean up after max attempts
            return false;
        }

        // 比对验证码
        boolean isValid = captchaInfo.getCode().equalsIgnoreCase(inputCode);
        // 验证码正确，失效验证码，返回true
        if (isValid) {
            // Mark as verified
            captchaStorage.delete(captchaId, scenario);
            log.info("Captcha validated successfully: captchaId={}, scenario={}", captchaId, scenario);
            return true;
        }
        // 验证不通过，尝试次数+1
        captchaStorage.incrementAttempts(captchaId, scenario);
        log.warn("Captcha validation failed: captchaId={}, scenario={}, attempts={}", captchaId, scenario, captchaInfo.getAttempts() + 1);
        return false;
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
