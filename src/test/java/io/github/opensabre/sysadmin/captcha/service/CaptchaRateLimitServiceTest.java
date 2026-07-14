package io.github.opensabre.sysadmin.captcha.service;

import io.github.opensabre.sysadmin.ratelimit.enums.RateLimitAlgorithmType;
import io.github.opensabre.sysadmin.ratelimit.model.RateLimitConfig;
import io.github.opensabre.sysadmin.ratelimit.model.RateLimitResult;
import io.github.opensabre.sysadmin.ratelimit.model.RateLimitScene;
import io.github.opensabre.sysadmin.ratelimit.service.IRateLimitSceneService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CaptchaRateLimitServiceTest {

    @Test
    void usesDatabaseRateLimitSceneParameters() {
        IRateLimitService service = new IRateLimitService();
        IRateLimitSceneService sceneService = mock(IRateLimitSceneService.class);
        io.github.opensabre.sysadmin.ratelimit.service.IRateLimitService rateLimitService = mock(io.github.opensabre.sysadmin.ratelimit.service.IRateLimitService.class);
        RateLimitScene scene = RateLimitScene.builder()
                .sceneCode(IRateLimitService.CAPTCHA_IP_SCENE)
                .algorithm(RateLimitAlgorithmType.SLIDING_WINDOW)
                .keyPrefix("captcha:ip")
                .maxCount(8)
                .period(120)
                .enabled(true)
                .build();
        when(sceneService.getByCode(IRateLimitService.CAPTCHA_IP_SCENE)).thenReturn(scene);
        when(rateLimitService.checkLimit(any())).thenReturn(RateLimitResult.allowed("key", 1, 8, 7, 0));
        ReflectionTestUtils.setField(service, "rateLimitSceneService", sceneService);
        ReflectionTestUtils.setField(service, "rateLimitService", rateLimitService);

        assertTrue(service.isAllowed(IRateLimitService.CAPTCHA_IP_SCENE, "127.0.0.1"));
        verify(rateLimitService).checkLimit(argThat((RateLimitConfig config) ->
                "captcha:ip".equals(config.getKeyPrefix())
                        && "127.0.0.1".equals(config.getKey())
                        && config.getMaxCount() == 8
                        && config.getPeriod() == 120
                        && config.getAlgorithm() == RateLimitAlgorithmType.SLIDING_WINDOW));
    }
}
