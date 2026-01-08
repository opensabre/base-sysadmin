package io.github.opensabre.sysadmin.captcha.model.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.opensabre.sysadmin.captcha.enums.BusinessScenario;
import io.github.opensabre.sysadmin.captcha.enums.CaptchaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Captcha information entity for storing and managing captcha data
 * This entity represents a captcha instance with all its metadata and state information
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaptchaInfo {
    /**
     * Unique identifier for this captcha instance
     */
    @Builder.Default
    private String captchaId = UUID.randomUUID().toString();

    /**
     * Business key identifying
     * Could be phone number, email address, or other identifier
     */
    private String businessKey;

    /**
     * Business scenario this captcha belongs to
     * Defines the context where this captcha is used (login, register, etc.)
     */
    private BusinessScenario businessScenario;

    /**
     * The actual captcha code/string that needs to be verified
     * For image captcha this might be alphanumeric, for SMS it's typically numeric
     */
    private String code;

    /**
     * Type of captcha (IMAGE, SMS, EMAIL, etc.)
     * Determines how the captcha is generated and delivered
     */
    private CaptchaType captchaType;

    /**
     * Client information associated with this captcha
     */
    private ClientInfo clientInfo;

    /**
     * Timestamp when the captcha was created
     * Used to calculate expiration and measure captcha lifetime
     */
    @Builder.Default
    private LocalDateTime createTime = LocalDateTime.now();

    /**
     * Timestamp when the captcha expires
     * Captcha become invalid after this time
     */
    private LocalDateTime expireTime;

    /**
     * Number of verification attempts made for this captcha
     * Used to enforce maximum attempt limits for security
     */
    private int attempts;

    /**
     * Flag indicating whether this captcha has been successfully verified
     * Prevents reuse of already-verified captcha
     */
    private boolean verified;

    /**
     * Timestamp when the captcha was successfully verified
     * Null if not yet verified or verification failed
     */
    private String verificationTime;

    /**
     * ex Data
     */
    @JsonIgnore
    private String data;
}