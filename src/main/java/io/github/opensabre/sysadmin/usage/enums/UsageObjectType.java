package io.github.opensabre.sysadmin.usage.enums;

import io.github.opensabre.governance.dictionary.DictionaryEnum;
import io.github.opensabre.governance.dictionary.OpenSabreDictionary;

/**
 * 被计数的业务对象类型。
 */
@OpenSabreDictionary(code = "usage_object_type", name = "使用对象类型")
public enum UsageObjectType implements DictionaryEnum {
    CAPTCHA_SCENE("验证码场景"),
    RATE_LIMIT_SCENE("限次场景"),
    NOTIFICATION_SCENE("通知场景"),
    NOTIFICATION_TEMPLATE("通知模板");

    private final String label;

    UsageObjectType(String label) {
        this.label = label;
    }

    @Override
    public String value() {
        return name();
    }

    @Override
    public String label() {
        return label;
    }
}
