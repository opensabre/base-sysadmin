package io.github.opensabre.sysadmin.captcha.enums;

import lombok.Getter;

/**
 * Captcha type enumeration
 */
@Getter
public enum CaptchaType {
    IMAGE("image", "Image captcha"),
    SMS("sms", "SMS captcha"),
    EMAIL("email", "Email captcha"),
    SLIDER("slider", "Slider captcha"),
    CLICK("click", "Click captcha");

    private final String code;
    private final String description;

    CaptchaType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}