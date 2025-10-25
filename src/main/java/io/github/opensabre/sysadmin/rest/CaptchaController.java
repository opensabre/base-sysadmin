package io.github.opensabre.sysadmin.rest;

import cn.hutool.core.util.HashUtil;
import cn.hutool.core.util.RandomUtil;
import io.github.opensabre.sysadmin.model.vo.CaptchaVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Schema(name = "验证码")
@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @Operation(summary = "发送验证码", description = "发送一个随机的验证码")
    @PostMapping("/send")
    public CaptchaVo send(@Parameter(name = "sceneId", description = "场景id", required = true) String sceneId) {
        String code = RandomUtil.randomNumbers(6);
        String sendId = String.valueOf(HashUtil.tianlHash(code));
        log.info("sceneId:{}, sendId:{}, code:{}", sceneId, sendId, code);
        return new CaptchaVo(sceneId, sendId);
    }

    @Operation(summary = "验证验证码", description = "验证该验证码是否有效")
    @PostMapping("/verify")
    public boolean verify(@Parameter(name = "sceneId", description = "场景id", required = true) @NotBlank(message = "场景号不能为空") String sceneId,
                          @Parameter(name = "sendId", description = "发送流水号", required = true) @NotBlank(message = "发送流水号不能为空") String sendId,
                          @Parameter(name = "code", description = "验证码", required = true) @NotBlank(message = "验证码不能为空") String code) {
        log.info("sceneId:{}, sceneId:{}, code:{}", sceneId, sendId, code);
        return false;
    }
}