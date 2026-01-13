package io.github.opensabre.sysadmin.notification.service;

import io.github.opensabre.sysadmin.notification.enums.NotificationTemplate;
import io.github.opensabre.sysadmin.notification.enums.NotificationType;

import java.util.Map;

/**
 * 通用通知服务接口
 * 支持多种通知方式如短信、邮件、推送等
 */
public interface INotificationService {
    /**
     * 发送通知
     *
     * @param target   目标地址（手机号、邮箱等）
     * @param template 模板
     * @param args     Map参数
     * @return 发送结果
     */
    String send(String target, NotificationTemplate template, Map<String, String> args);

    /**
     * 发送通知
     *
     * @param target   目标地址（手机号、邮箱等）
     * @param template 模板
     * @param args     参数
     * @return 发送结果
     */
    String send(String target, NotificationTemplate template, Object... args);

    /**
     * 发送通知
     *
     * @param target   目标地址（手机号、邮箱等）
     * @param template 模板
     * @return 发送结果
     */
    String send(String target, NotificationTemplate template);

    /**
     * 获取通知类型
     *
     * @return 通知类型
     */
    NotificationType getType();
}