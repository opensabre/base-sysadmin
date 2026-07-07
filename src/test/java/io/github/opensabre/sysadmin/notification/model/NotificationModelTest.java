package io.github.opensabre.sysadmin.notification.model;

import io.github.opensabre.sysadmin.notification.enums.NotificationSendStatus;
import io.github.opensabre.sysadmin.notification.enums.NotificationType;
import io.github.opensabre.sysadmin.notification.model.form.NotificationSendForm;
import io.github.opensabre.sysadmin.notification.model.po.NotificationRecord;
import io.github.opensabre.sysadmin.notification.model.po.NotificationScene;
import io.github.opensabre.sysadmin.notification.model.po.NotificationTemplateConfig;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationModelTest {

    @Test
    void buildsSceneAndChannelTemplate() {
        NotificationScene scene = NotificationScene.builder()
                .sceneCode("LOGIN_CAPTCHA")
                .sceneName("登录验证码")
                .enabled(true)
                .build();
        NotificationTemplateConfig template = NotificationTemplateConfig.builder()
                .sceneCode("LOGIN_CAPTCHA")
                .channel(NotificationType.SMS)
                .templateName("登录验证码短信")
                .content("验证码为：{code}")
                .sort(1)
                .enabled(true)
                .build();

        assertEquals("LOGIN_CAPTCHA", scene.getSceneCode());
        assertEquals(NotificationType.SMS, template.getChannel());
        assertTrue(template.getContent().contains("{code}"));
    }

    @Test
    void buildsSendFormAndRecordSnapshot() {
        NotificationSendForm form = new NotificationSendForm("13800138000", "LOGIN_CAPTCHA",
                NotificationType.SMS, Map.of("code", "123456"));
        NotificationRecord record = NotificationRecord.builder()
                .sceneCode(form.getSceneCode())
                .channel(form.getChannel())
                .target(form.getTarget())
                .templateContent("验证码为：123456")
                .status(NotificationSendStatus.SUCCESS)
                .retryCount(0)
                .build();

        assertEquals("LOGIN_CAPTCHA", form.getSceneCode());
        assertEquals("13800138000", record.getTarget());
        assertEquals(NotificationSendStatus.SUCCESS, record.getStatus());
    }
}
