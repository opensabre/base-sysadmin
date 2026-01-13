package io.github.opensabre.sysadmin.notification.service;

import io.github.opensabre.sysadmin.notification.enums.NotificationTemplate;
import io.github.opensabre.sysadmin.notification.enums.NotificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通知服务管理器
 * 管理不同类型的通知服务实现
 */
@Component
public class NotificationServiceManager {

    private final Map<NotificationType, INotificationService> notificationServices;

    @Autowired
    public NotificationServiceManager(List<INotificationService> services) {
        this.notificationServices = services.stream().collect(Collectors.toMap(INotificationService::getType, service -> service));
    }

    /**
     * 发送通知
     *
     * @param target   目标地址
     * @param template 通知模板
     * @param args     模板参数
     * @return 发送结果
     */
    public String sendNotification(String target, NotificationTemplate template, Object... args) {
        INotificationService service = notificationServices.get(template.getType());
        Assert.notNull(service, "No notification service found for type: " + template.getType());
        return service.send(target, template, args);
    }

    /**
     * 发送通知
     *
     * @param target   目标地址
     * @param template 通知模板
     * @param args     模板参数 (Map格式)
     * @return 发送结果
     */
    public String sendNotification(String target, NotificationTemplate template, Map<String, String> args) {
        INotificationService service = notificationServices.get(template.getType());
        Assert.notNull(service, "No notification service found for type: " + template.getType());
        return service.send(target, template, args);
    }

    /**
     * 发送通知（不带参数）
     *
     * @param target   目标地址
     * @param template 通知模板
     * @return 发送结果
     */
    public String sendNotification(String target, NotificationTemplate template) {
        INotificationService service = notificationServices.get(template.getType());
        Assert.notNull(service, "No notification service found for type: " + template.getType());
        return service.send(target, template);
    }

    /**
     * 获取通知服务
     *
     * @param type 通知类型
     * @return 通知服务实例
     */
    public INotificationService get(NotificationType type) {
        return notificationServices.get(type);
    }

    /**
     * 检查是否支持指定类型的通知
     *
     * @param type 通知类型
     * @return 是否支持
     */
    public boolean supportsType(NotificationType type) {
        return notificationServices.containsKey(type);
    }
}