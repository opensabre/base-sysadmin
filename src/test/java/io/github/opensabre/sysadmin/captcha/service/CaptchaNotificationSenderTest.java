package io.github.opensabre.sysadmin.captcha.service;

import io.github.opensabre.sysadmin.captcha.enums.CaptchaType;
import io.github.opensabre.sysadmin.captcha.model.po.CaptchaScene;
import io.github.opensabre.sysadmin.notification.enums.NotificationSendStatus;
import io.github.opensabre.sysadmin.notification.enums.NotificationType;
import io.github.opensabre.sysadmin.notification.model.form.NotificationSendForm;
import io.github.opensabre.sysadmin.notification.model.po.NotificationTemplateConfig;
import io.github.opensabre.sysadmin.notification.model.vo.NotificationSendResponse;
import io.github.opensabre.sysadmin.notification.service.INotificationTemplateConfigService;
import io.github.opensabre.sysadmin.notification.service.NotificationServiceManager;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CaptchaNotificationSenderTest {

    @Test
    void sendsSmsCaptchaWithBoundNotificationTemplate() {
        INotificationTemplateConfigService templateService = mock(INotificationTemplateConfigService.class);
        NotificationServiceManager notificationServiceManager = mock(NotificationServiceManager.class);
        CaptchaNotificationSender sender = new CaptchaNotificationSender(templateService, notificationServiceManager);
        when(templateService.getFormData("NOTIFY_TPL_LOGIN_SMS")).thenReturn(template(NotificationType.SMS));
        when(notificationServiceManager.sendNotification(org.mockito.ArgumentMatchers.any(NotificationSendForm.class)))
                .thenReturn(response(NotificationSendStatus.SUCCESS, "sms-1", null));
        ArgumentCaptor<NotificationSendForm> formCaptor = ArgumentCaptor.forClass(NotificationSendForm.class);

        String messageId = sender.sendCaptcha(scene(CaptchaType.SMS, "NOTIFY_TPL_LOGIN_SMS"), "13800138000", "123456");

        assertEquals("sms-1", messageId);
        verify(notificationServiceManager).sendNotification(formCaptor.capture());
        assertEquals("13800138000", formCaptor.getValue().getTarget());
        assertEquals("LOGIN_CAPTCHA", formCaptor.getValue().getSceneCode());
        assertEquals(NotificationType.SMS, formCaptor.getValue().getChannel());
        assertEquals("123456", formCaptor.getValue().getArgs().get("code"));
        assertEquals("5", formCaptor.getValue().getArgs().get("minutes"));
    }

    @Test
    void sendsEmailCaptchaWithBoundNotificationTemplate() {
        INotificationTemplateConfigService templateService = mock(INotificationTemplateConfigService.class);
        NotificationServiceManager notificationServiceManager = mock(NotificationServiceManager.class);
        CaptchaNotificationSender sender = new CaptchaNotificationSender(templateService, notificationServiceManager);
        when(templateService.getFormData("NOTIFY_TPL_LOGIN_EMAIL")).thenReturn(template(NotificationType.EMAIL));
        when(notificationServiceManager.sendNotification(org.mockito.ArgumentMatchers.any(NotificationSendForm.class)))
                .thenReturn(response(NotificationSendStatus.SUCCESS, "email-1", null));

        String messageId = sender.sendCaptcha(scene(CaptchaType.EMAIL, "NOTIFY_TPL_LOGIN_EMAIL"), "user@example.com", "654321");

        assertEquals("email-1", messageId);
    }

    @Test
    void rejectsMissingNotificationTemplateBinding() {
        CaptchaNotificationSender sender = new CaptchaNotificationSender(
                mock(INotificationTemplateConfigService.class),
                mock(NotificationServiceManager.class));

        assertThrows(IllegalArgumentException.class,
                () -> sender.sendCaptcha(scene(CaptchaType.SMS, null), "13800138000", "123456"));
    }

    @Test
    void rejectsChannelMismatch() {
        INotificationTemplateConfigService templateService = mock(INotificationTemplateConfigService.class);
        CaptchaNotificationSender sender = new CaptchaNotificationSender(
                templateService,
                mock(NotificationServiceManager.class));
        when(templateService.getFormData("NOTIFY_TPL_LOGIN_EMAIL")).thenReturn(template(NotificationType.EMAIL));

        assertThrows(IllegalArgumentException.class,
                () -> sender.sendCaptcha(scene(CaptchaType.SMS, "NOTIFY_TPL_LOGIN_EMAIL"), "13800138000", "123456"));
    }

    @Test
    void rejectsFailedNotificationSend() {
        INotificationTemplateConfigService templateService = mock(INotificationTemplateConfigService.class);
        NotificationServiceManager notificationServiceManager = mock(NotificationServiceManager.class);
        CaptchaNotificationSender sender = new CaptchaNotificationSender(templateService, notificationServiceManager);
        when(templateService.getFormData("NOTIFY_TPL_LOGIN_SMS")).thenReturn(template(NotificationType.SMS));
        when(notificationServiceManager.sendNotification(org.mockito.ArgumentMatchers.any(NotificationSendForm.class)))
                .thenReturn(response(NotificationSendStatus.FAILED, null, "provider down"));

        assertThrows(IllegalArgumentException.class,
                () -> sender.sendCaptcha(scene(CaptchaType.SMS, "NOTIFY_TPL_LOGIN_SMS"), "13800138000", "123456"));
    }

    private static CaptchaScene scene(CaptchaType captchaType, String notificationTemplateId) {
        return CaptchaScene.builder()
                .sceneCode("LOGIN_" + captchaType.name())
                .sceneName("登录验证码")
                .captchaType(captchaType)
                .notificationTemplateId(notificationTemplateId)
                .captchaLength(6)
                .captchaExpireTime(300)
                .captchaAttempts(3)
                .minInterval(60)
                .maxLimitCount(100)
                .enabled(true)
                .build();
    }

    private static NotificationTemplateConfig template(NotificationType channel) {
        NotificationTemplateConfig template = NotificationTemplateConfig.builder()
                .sceneCode("LOGIN_CAPTCHA")
                .channel(channel)
                .templateName("登录验证码")
                .content("验证码{code}")
                .enabled(true)
                .build();
        template.setId("template-" + channel.name());
        return template;
    }

    private static NotificationSendResponse response(NotificationSendStatus status, String messageId, String failureReason) {
        return NotificationSendResponse.builder()
                .recordId("record-1")
                .messageId(messageId)
                .sceneCode("LOGIN_CAPTCHA")
                .channel(NotificationType.SMS)
                .status(status)
                .failureReason(failureReason)
                .sentTime(LocalDateTime.now())
                .build();
    }
}
