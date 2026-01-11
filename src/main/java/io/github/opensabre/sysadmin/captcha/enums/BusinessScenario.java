package io.github.opensabre.sysadmin.captcha.enums;

import lombok.Getter;

/**
 * Business scenario enumeration for different captcha usage scenarios
 * Defines specific business contexts where captchas are used with their configurations
 */
@Getter
public enum BusinessScenario {

    /**
     * Login with image captcha scenario
     * Used for graphical verification during user login process
     */
    LOGIN_IMAGE("LOGIN_IMAGE", CaptchaType.IMAGE, null, "登录时图形验证码", 4, 300, 1, -1, 100),

    /**
     * Login with SMS captcha scenario
     * Used for SMS verification during user login process
     */
    LOGIN_SMS("LOGIN_SMS", CaptchaType.SMS, "CAPTCHA", "登录时短信验证码", 6, 60, 2, -1, 100),

    /**
     * Registration with image captcha scenario
     * Used for graphical verification during user registration process
     */
    REGISTER_IMAGE("REGISTER_IMAGE", CaptchaType.IMAGE, null, "注册时图形验证码", 4, 60, 3, 60, 50),

    /**
     * Login with Email captcha scenario
     * Used for Email verification during user login process
     */
    LOGIN_EMAIL("LOGIN_EMAIL", CaptchaType.EMAIL, "CAPTCHA", "登录时邮箱验证码", 6, 300, 3, 60, 100);
    /**
     * Unique code identifying the business scenario
     */
    private final String code;

    /**
     * Type of captcha to be used for this scenario
     */
    private final CaptchaType type;

    /**
     * 消息模板code
     */
    private final String templateCode;

    /**
     * Human-readable description of the scenario
     */
    private final String description;

    /**
     * Length of the captcha code to be generated
     */
    private final int captchaLength;

    /**
     * Expiration time of the captcha in seconds
     */
    private final int captchaExpireTime;

    /**
     * Maximum number of verification attempts allowed
     */
    private final int captchaAttempts;

    /**
     * 最小间隔时间
     */
    private final int minInterval;

    /**
     * 同一用户同一场景生成限制次数
     * count per hour
     */
    private final int maxLimitCount;

    /**
     * Constructor for BusinessScenario enum
     *
     * @param code              Unique identifier for the scenario
     * @param type              Type of captcha to use
     * @param description       Human-readable description
     * @param captchaLength     Length of captcha code
     * @param captchaExpireTime Expiration time in seconds
     * @param captchaAttempts   Maximum verification attempts
     */
    BusinessScenario(String code, CaptchaType type, String templateCode, String description,
                     int captchaLength, int captchaExpireTime,
                     int captchaAttempts, int minInterval, int maxLimitCount) {
        this.code = code;
        this.type = type;
        this.templateCode = templateCode;
        this.description = description;
        this.captchaLength = captchaLength;
        this.captchaExpireTime = captchaExpireTime;
        this.captchaAttempts = captchaAttempts;
        this.minInterval = minInterval;
        this.maxLimitCount = maxLimitCount;
    }
}