package io.github.opensabre.sysadmin.captcha.service;

import io.github.opensabre.sysadmin.ratelimit.model.RateLimitConfig;
import io.github.opensabre.sysadmin.ratelimit.model.RateLimitResult;
import io.github.opensabre.sysadmin.ratelimit.model.RateLimitScene;
import io.github.opensabre.sysadmin.ratelimit.service.IRateLimitSceneService;
import io.github.opensabre.sysadmin.usage.enums.UsageEvent;
import io.github.opensabre.sysadmin.usage.enums.UsageObjectType;
import io.github.opensabre.sysadmin.usage.enums.UsageOutcome;
import io.github.opensabre.sysadmin.usage.service.IUsageCounterService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Security service for captcha rate limiting and other security controls
 */
@Slf4j
@Component
public class IRateLimitService {

    @Resource
    private io.github.opensabre.sysadmin.ratelimit.service.IRateLimitService rateLimitService;
    @Resource
    private IRateLimitSceneService rateLimitSceneService;
    @Resource
    private IUsageCounterService usageCounterService;

    /** 验证码全局 IP 限次场景。 */
    public static final String CAPTCHA_IP_SCENE = "CAPTCHA_IP";
    /** 验证码全局设备限次场景。 */
    public static final String CAPTCHA_DEVICE_SCENE = "CAPTCHA_DEVICE";

    /**
     * 返回验证码业务标识对应的限次场景编码。
     *
     * @param captchaSceneCode 验证码场景编码
     * @return 限次场景编码
     */
    public static String captchaBusinessSceneCode(String captchaSceneCode) {
        return "CAPTCHA_" + captchaSceneCode;
    }

    /**
     * 按数据库维护的限次场景执行检查。
     *
     * @param sceneCode 限次场景编码
     * @param key 当前维度对应的唯一值
     * @return 是否允许继续生成验证码
     */
    public boolean isAllowed(String sceneCode, String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        RateLimitScene scene = rateLimitSceneService.getByCode(sceneCode);
        if (scene == null) {
            throw new IllegalStateException("Captcha rate limit scene not found: " + sceneCode);
        }
        if (!scene.isEnabled()) {
            return true;
        }
        usageCounterService.record(UsageObjectType.RATE_LIMIT_SCENE, sceneCode,
                UsageEvent.RATE_LIMIT_CHECK, UsageOutcome.ATTEMPT);
        try {
            RateLimitResult result = rateLimitService.checkLimit(RateLimitConfig.builder()
                    .keyPrefix(scene.getKeyPrefix())
                    .key(key)
                    .algorithm(scene.getAlgorithm())
                    .maxCount(scene.getMaxCount())
                    .period(scene.getPeriod())
                    .enabled(true)
                    .build());
            logIfExceeded(sceneCode, key, result);
            usageCounterService.record(UsageObjectType.RATE_LIMIT_SCENE, sceneCode,
                    UsageEvent.RATE_LIMIT_CHECK, result.isAllowed() ? UsageOutcome.SUCCESS : UsageOutcome.FAILURE);
            return result.isAllowed();
        } catch (RuntimeException exception) {
            usageCounterService.record(UsageObjectType.RATE_LIMIT_SCENE, sceneCode,
                    UsageEvent.RATE_LIMIT_CHECK, UsageOutcome.FAILURE);
            throw exception;
        }
    }

    private void logIfExceeded(String dimensionName, String dimensionValue, RateLimitResult result) {
        if (!result.isAllowed()) {
            log.warn("{} rate limit exceeded: {} (max: {}, current: {})",
                    dimensionName, dimensionValue, result.getMaxCount(), result.getCurrentCount());
        }
    }
}
