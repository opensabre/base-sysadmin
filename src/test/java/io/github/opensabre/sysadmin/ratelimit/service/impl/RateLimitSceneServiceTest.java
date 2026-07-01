package io.github.opensabre.sysadmin.ratelimit.service.impl;

import io.github.opensabre.sysadmin.ratelimit.enums.RateLimitAlgorithmType;
import io.github.opensabre.sysadmin.ratelimit.model.RateLimitScene;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RateLimitSceneServiceTest {

    @Test
    void buildsSceneSnapshotFromDynamicDefinition() {
        RateLimitScene scene = RateLimitScene.builder()
                .sceneCode("LOGIN_DYNAMIC")
                .sceneName("动态登录限次")
                .algorithm(RateLimitAlgorithmType.SLIDING_WINDOW)
                .dimensions("IP,DEVICE")
                .maxCount(5)
                .period(60)
                .enabled(true)
                .build();

        assertEquals("LOGIN_DYNAMIC", scene.getSceneCode());
        assertEquals(RateLimitAlgorithmType.SLIDING_WINDOW, scene.getAlgorithm());
        assertEquals(2, scene.getDimensionList().size());
    }
}
