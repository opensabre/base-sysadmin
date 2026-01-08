package io.github.opensabre.sysadmin.captcha.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.opensabre.sysadmin.captcha.enums.BusinessScenario;
import io.github.opensabre.sysadmin.captcha.service.ICaptchaStorageService;
import io.github.opensabre.sysadmin.captcha.model.po.CaptchaInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Redis-based captcha storage implementation
 */
@Slf4j
@Component
public class RedisICaptchaStorage implements ICaptchaStorageService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String CAPTCHA_PREFIX = "captcha:";

    @Override
    public void save(CaptchaInfo captchaInfo, BusinessScenario scenario) {
        String key = buildKey(captchaInfo.getCaptchaId(), scenario);
        long ttl = Duration.between(LocalDateTime.now(), captchaInfo.getExpireTime()
        ).getSeconds();

        try {
            String jsonStr = objectMapper.writeValueAsString(captchaInfo);
            if (ttl > 0) {
                stringRedisTemplate.opsForValue().set(key, jsonStr, ttl, TimeUnit.SECONDS);
                log.debug("Saved captcha: {} with TTL: {} seconds", key, ttl);
            } else {
                log.warn("Captcha already expired when saving: {}", key);
            }
        } catch (JsonProcessingException e) {
            log.error("Error serializing captcha entity", e);
            throw new RuntimeException("Error serializing captcha entity", e);
        }
    }

    @Override
    public CaptchaInfo get(String captchaId, BusinessScenario scenario) {
        String key = buildKey(captchaId, scenario);
        String jsonStr = stringRedisTemplate.opsForValue().get(key);

        if (jsonStr != null && !jsonStr.isEmpty()) {
            try {
                return objectMapper.readValue(jsonStr, CaptchaInfo.class);
            } catch (JsonProcessingException e) {
                log.error("Error deserializing captcha entity", e);
                return null;
            }
        }
        return null;
    }

    @Override
    public void delete(String captchaId, BusinessScenario scenario) {
        String key = buildKey(captchaId, scenario);
        stringRedisTemplate.delete(key);
        log.debug("Deleted captcha: {}", key);
    }

    @Override
    public void updateVerified(String captchaId, BusinessScenario scenario, boolean verified) {
        CaptchaInfo captchaInfo = get(captchaId, scenario);
        if (captchaInfo != null) {
            captchaInfo.setVerified(verified);
            if (verified) {
                captchaInfo.setVerificationTime(java.time.LocalDateTime.now().toString());
            }
            save(captchaInfo, scenario);
        }
    }

    @Override
    public void incrementAttempts(String captchaId, BusinessScenario scenario) {
        String key = buildKey(captchaId, scenario);
        // Get the current captcha entity
        String jsonStr = stringRedisTemplate.opsForValue().get(key);
        if (jsonStr != null) {
            try {
                CaptchaInfo captchaInfo = objectMapper.readValue(jsonStr, CaptchaInfo.class);
                captchaInfo.setAttempts(captchaInfo.getAttempts() + 1);
                // Update the TTL to maintain the same expiration time
                long ttl = Duration.between(LocalDateTime.now(), captchaInfo.getExpireTime()).getSeconds();

                if (ttl > 0) {
                    String updatedJsonStr = objectMapper.writeValueAsString(captchaInfo);
                    stringRedisTemplate.opsForValue().set(key, updatedJsonStr, ttl, TimeUnit.SECONDS);
                }
            } catch (JsonProcessingException e) {
                log.error("Error processing captcha entity", e);
            }
        }
    }

    /**
     *
     * @param captchaId        Captcha ID
     * @param scenario business scenario
     * @return key captcha:image:LOGIN_IMAGE:xxx
     */
    private String buildKey(String captchaId, BusinessScenario scenario) {
        return CAPTCHA_PREFIX + scenario.getType().getCode() + ":" + scenario.getCode() + ":" + captchaId;
    }
}