package io.github.opensabre.sysadmin.notification.service;

import io.github.opensabre.sysadmin.notification.enums.NotificationType;
import io.github.opensabre.sysadmin.notification.model.po.NotificationTemplateConfig;

import java.util.List;

public interface INotificationTemplateConfigService {

    NotificationTemplateConfig getFormData(String id);

    List<NotificationTemplateConfig> listTemplates(String sceneCode, NotificationType channel, Boolean enabled);

    NotificationTemplateConfig getEnabledTemplate(String sceneCode, NotificationType channel);

    NotificationTemplateConfig getFirstEnabledTemplate(String sceneCode);

    boolean saveTemplate(NotificationTemplateConfig template);

    boolean updateTemplate(String id, NotificationTemplateConfig template);

    boolean deleteTemplate(String id);
}
