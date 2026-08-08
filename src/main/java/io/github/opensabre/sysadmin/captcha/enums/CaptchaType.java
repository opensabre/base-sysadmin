package io.github.opensabre.sysadmin.captcha.enums;

import lombok.Getter;
import io.github.opensabre.governance.dictionary.DictionaryEnum;
import io.github.opensabre.governance.dictionary.OpenSabreDictionary;

/**
 * Captcha type enumeration
 */
@Getter
@OpenSabreDictionary(code = "captcha_type", name = "验证码类型")
public enum CaptchaType implements DictionaryEnum {
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
    public String value() { return code; }
    public String label() { return description; }
}
