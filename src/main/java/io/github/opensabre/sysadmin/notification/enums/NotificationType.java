package io.github.opensabre.sysadmin.notification.enums;

import lombok.Getter;
import io.github.opensabre.governance.dictionary.DictionaryEnum;
import io.github.opensabre.governance.dictionary.OpenSabreDictionary;

/**
 * Captcha type enumeration
 */
@Getter
@OpenSabreDictionary(code = "notification_type", name = "通知类型")
public enum NotificationType implements DictionaryEnum {
    SMS("sms", "短信"),
    EMAIL("email", "邮件"),
    WECHAT("wechat", "微信");

    private final String code;
    private final String description;

    NotificationType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    public String value() { return code; }
    public String label() { return description; }
}
