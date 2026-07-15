package io.github.opensabre.sysadmin.usage.enums;

/**
 * 对业务对象执行的使用事件。
 */
public enum UsageEvent {
    CAPTCHA_GENERATE,
    CAPTCHA_VERIFY,
    RATE_LIMIT_CHECK,
    NOTIFICATION_SEND
}
