package io.github.opensabre.sysadmin.captcha.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaptchaVo {
    private String captchaId;
    private String imageData; // For image captcha
    private Integer expireTime; // Expiration time in seconds
}