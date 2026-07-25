package io.github.opensabre.sysadmin.internaltoken.rest;

import io.github.opensabre.governance.audit.annotations.Audit;
import io.github.opensabre.governance.audit.annotations.OperationType;
import io.github.opensabre.sysadmin.internaltoken.model.form.InternalTokenKeyRotationForm;
import io.github.opensabre.sysadmin.internaltoken.model.form.InternalTokenPreviousKeyRetirementForm;
import io.github.opensabre.sysadmin.internaltoken.model.vo.InternalTokenKeyManagementStatus;
import io.github.opensabre.sysadmin.internaltoken.service.NacosInternalTokenKeyManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Internal token shared key control plane.
 */
@Tag(name = "内部 Token 密钥管理")
@RestController
@RequestMapping("/security/internal-token/keys")
public class InternalTokenKeyController {

    private final NacosInternalTokenKeyManager keyManager;

    public InternalTokenKeyController(NacosInternalTokenKeyManager keyManager) {
        this.keyManager = keyManager;
    }

    @Operation(summary = "查询共享密钥状态")
    @GetMapping
    public InternalTokenKeyManagementStatus status() {
        return keyManager.currentStatus();
    }

    @Operation(summary = "生成并轮换共享密钥")
    @Audit(
            operationType = OperationType.UPDATE,
            description = "轮换内部 Token 共享密钥",
            module = "INTERNAL_TOKEN_KEY",
            response = true,
            key = "#form.newKeyId")
    @PostMapping("/rotate")
    public InternalTokenKeyManagementStatus rotate(
            @Valid @RequestBody InternalTokenKeyRotationForm form) {
        return keyManager.rotate(
                form.getExpectedConfigVersion(),
                form.getNewKeyId());
    }

    @Operation(summary = "退役 previous 密钥")
    @Audit(
            operationType = OperationType.UPDATE,
            description = "退役内部 Token previous 密钥",
            module = "INTERNAL_TOKEN_KEY",
            response = true,
            key = "#form.expectedConfigVersion")
    @PostMapping("/retire-previous")
    public InternalTokenKeyManagementStatus retirePrevious(
            @Valid @RequestBody InternalTokenPreviousKeyRetirementForm form) {
        return keyManager.retirePrevious(form.getExpectedConfigVersion());
    }
}
