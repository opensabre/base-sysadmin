package io.github.opensabre.sysadmin.usage.enums;

import io.github.opensabre.governance.dictionary.DictionaryEnum;
import io.github.opensabre.governance.dictionary.OpenSabreDictionary;

/**
 * 对业务对象执行的使用事件。
 */
@OpenSabreDictionary(code = "usage_event", name = "使用事件")
public enum UsageEvent implements DictionaryEnum {
    CAPTCHA_GENERATE("生成验证码"),
    CAPTCHA_VERIFY("校验验证码"),
    RATE_LIMIT_CHECK("限次检查"),
    NOTIFICATION_SEND("发送通知");

    private final String label;

    UsageEvent(String label) {
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
