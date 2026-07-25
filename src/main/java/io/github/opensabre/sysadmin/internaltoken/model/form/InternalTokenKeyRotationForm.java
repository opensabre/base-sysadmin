package io.github.opensabre.sysadmin.internaltoken.model.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Internal token key rotation request. It intentionally contains no secret.
 */
@Data
public class InternalTokenKeyRotationForm {

    @Min(value = 0, message = "配置版本不能小于 0")
    private long expectedConfigVersion;

    @NotBlank(message = "新密钥 ID 不能为空")
    @Size(max = 64, message = "新密钥 ID 长度不能超过 64")
    private String newKeyId;

    @NotBlank(message = "轮换原因不能为空")
    @Size(max = 500, message = "轮换原因长度不能超过 500")
    private String reason;
}
