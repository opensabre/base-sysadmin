package io.github.opensabre.sysadmin.internaltoken.model.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Previous key retirement request.
 */
@Data
public class InternalTokenPreviousKeyRetirementForm {

    @Min(value = 0, message = "配置版本不能小于 0")
    private long expectedConfigVersion;

    @NotBlank(message = "退役原因不能为空")
    @Size(max = 500, message = "退役原因长度不能超过 500")
    private String reason;
}
