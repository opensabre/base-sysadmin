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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Map;

@Service
public class CaptchaNotificationSender {

    private final INotificationTemplateConfigService notificationTemplateConfigService;

    private final NotificationServiceManager notificationServiceManager;

    public CaptchaNotificationSender(INotificationTemplateConfigService notificationTemplateConfigService,
                                     NotificationServiceManager notificationServiceManager) {
        this.notificationTemplateConfigService = notificationTemplateConfigService;
        this.notificationServiceManager = notificationServiceManager;
    }

    public String sendCaptcha(CaptchaScene scene, String target, String code) {
        Assert.notNull(scene, "Captcha scene must not be null");
        Assert.isTrue(StringUtils.isNotBlank(scene.getNotificationTemplateId()),
                "Captcha scene notification template must not be blank: " + scene.getSceneCode());

        NotificationTemplateConfig template = notificationTemplateConfigService.getFormData(scene.getNotificationTemplateId());
        Assert.notNull(template, "Notification template not found: " + scene.getNotificationTemplateId());
        Assert.isTrue(Boolean.TRUE.equals(template.getEnabled()),
                "Notification template disabled: " + scene.getNotificationTemplateId());
        Assert.isTrue(matchesChannel(scene.getCaptchaType(), template.getChannel()),
                "Notification template channel does not match captcha type: " + scene.getSceneCode());

        NotificationSendResponse response = notificationServiceManager.sendNotification(new NotificationSendForm(
                target,
                template.getSceneCode(),
                template.getChannel(),
                Map.of(
                        "code", code,
                        "minutes", String.valueOf(scene.getCaptchaExpireTime() / 60)
                )
        ));
        Assert.isTrue(response.getStatus() == NotificationSendStatus.SUCCESS,
                "Captcha notification send failed: " + response.getFailureReason());
        return response.getMessageId();
    }

    private boolean matchesChannel(CaptchaType captchaType, NotificationType channel) {
        return (captchaType == CaptchaType.SMS && channel == NotificationType.SMS)
                || (captchaType == CaptchaType.EMAIL && channel == NotificationType.EMAIL);
    }
}
