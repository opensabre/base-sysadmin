package io.github.opensabre.sysadmin.notification.enums;

import lombok.Getter;

/**
 * Captcha type enumeration
 */
@Getter
public enum NotificationType {
    SMS("sms", "短信"),
    EMAIL("email", "邮件"),
    WECHAT("wechat", "微信");

    private final String code;
    private final String description;

    NotificationType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}