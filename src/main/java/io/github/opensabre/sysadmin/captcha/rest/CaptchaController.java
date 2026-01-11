package io.github.opensabre.sysadmin.captcha.rest;

import io.github.opensabre.sysadmin.captcha.enums.BusinessScenario;
import io.github.opensabre.sysadmin.captcha.model.po.ClientInfo;
import io.github.opensabre.sysadmin.captcha.model.vo.CaptchaVo;
import io.github.opensabre.sysadmin.captcha.service.ICaptchaService;
import io.github.opensabre.sysadmin.captcha.utils.ClientUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Schema(name = "验证码")
@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @Autowired
    private ICaptchaService smsCaptchaService;
    @Autowired
    private ICaptchaService imageCaptchaService;

    @Operation(summary = "发送短信验证码", description = "发送一个随机的短信验证码")
    @PostMapping("/send/sms")
    public CaptchaVo captchaSms(@Parameter(name = "scenario", description = "业务场景", required = true) @RequestParam BusinessScenario scenario,
                          @Parameter(name = "phoneNo", description = "手机号", required = true) @RequestParam String phoneNo,
                          @Parameter(name = "imageCaptchaId", description = "图形验证码 ID", required = true) @RequestParam String imageCaptchaId,
                          @Parameter(name = "imageCaptchaCode", description = "用户输入的图形验证码", required = true) @RequestParam String imageCaptchaCode,
                          HttpServletRequest request) {
        // Get client IP and device info
        ClientInfo clientInfo = ClientUtils.getClientInfo(request, phoneNo, scenario);
        // Generate Captcha
        CaptchaVo captchaVo = smsCaptchaService.generateCaptcha(phoneNo, scenario, clientInfo);
        log.info("Captcha SMS sent: businessKey={}, scenario={}, info={}", phoneNo, scenario, captchaVo);
        return captchaVo;
    }

    @Operation(summary = "生成图形验证码", description = "生成一个随机的图形验证码")
    @PostMapping("/send/image")
    public CaptchaVo captchaImage(@Parameter(name = "scenario", description = "业务场景", required = true) @RequestParam BusinessScenario scenario,
                          @Parameter(name = "requestKey", description = "唯一标识（sessionId）", required = true) @RequestParam String requestKey,
                          HttpServletRequest request) {
        // Get client IP and device info
        ClientInfo clientInfo = ClientUtils.getClientInfo(request, requestKey, scenario);
        // Generate Captcha
        CaptchaVo captchaVo = imageCaptchaService.generateCaptcha(requestKey, scenario, clientInfo);
        log.info("Captcha image sent: businessKey={}, scenario={}, info={}", requestKey, scenario, captchaVo);
        return captchaVo;
    }

    @Operation(summary = "验证验证码", description = "验证该验证码是否有效")
    @PostMapping("/verify")
    public boolean verify(@Parameter(name = "scenario", description = "业务场景", required = true) @RequestParam BusinessScenario scenario,
                          @Parameter(name = "captchaId", description = "验证码 id", required = true) @RequestParam @NotBlank(message = "验证码 ID 不能为空") String captchaId,
                          @Parameter(name = "inputCode", description = "验证码", required = true)  @RequestParam @NotBlank(message = "验证码不能为空") String inputCode) {
        boolean result = smsCaptchaService.validateCaptcha(captchaId, scenario, inputCode);
        log.info("Captcha verification result: captchaId={}, inputCode={}, result={}", captchaId, inputCode, result);
        return result;
    }
}