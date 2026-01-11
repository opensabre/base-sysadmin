package io.github.opensabre.sysadmin.notification.enums;

import lombok.Getter;

/**
 * 模板枚举
 */
@Getter
public enum NotificationTemplate {

    /**
     * 短信验证码
     */
    CAPTCHA("CAPTCHA", "验证码", NotificationType.SMS, "【通知】您的登录验证码为：%s，请在%d分钟内使用。如非本人操作，请忽略本信息。", "验证码类");

    /**
     * Unique code identifying the template
     */
    private final String code;

    /**
     * Name of the template
     */
    private final String name;

    /**
     * 模板内容
     */
    private final String content;

    /**
     * 模板 类型
     */
    private final NotificationType type;

    /**
     * 描述
     */
    private final String description;

    /**
     * Constructor for Template
     *
     * @param code        Unique identifier for the template
     * @param name        Template Name
     * @param type        Template Type
     * @param content     Template Content
     * @param description Template Description
     */
    NotificationTemplate(String code, String name, NotificationType type, String content, String description) {
        this.code = code;
        this.type = type;
        this.content = content;
        this.description = description;
        this.name = name;
    }
}