package io.github.opensabre.sysadmin.captcha.service.impl;

import io.github.opensabre.sysadmin.captcha.enums.CaptchaType;
import io.github.opensabre.sysadmin.captcha.model.po.CaptchaScene;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CaptchaSceneServiceTest {

    @Test
    void buildsSceneSnapshotFromDynamicDefinition() {
        CaptchaScene scene = CaptchaScene.builder()
                .sceneCode("LOGIN_DYNAMIC")
                .sceneName("动态登录验证码")
                .captchaType(CaptchaType.SMS)
                .templateCode("CAPTCHA")
                .captchaLength(6)
                .captchaExpireTime(300)
                .captchaAttempts(3)
                .minInterval(60)
                .maxLimitCount(100)
                .enabled(true)
                .build();

        assertEquals("LOGIN_DYNAMIC", scene.getSceneCode());
        assertEquals(CaptchaType.SMS, scene.getCaptchaType());
        assertEquals(6, scene.getCaptchaLength());
    }
}
