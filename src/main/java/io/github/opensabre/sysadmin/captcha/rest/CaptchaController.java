package io.github.opensabre.sysadmin.captcha.rest;

import io.github.opensabre.sysadmin.captcha.enums.BusinessScenario;
import io.github.opensabre.sysadmin.captcha.enums.CaptchaType;
import io.github.opensabre.sysadmin.captcha.model.po.CaptchaScene;
import io.github.opensabre.sysadmin.captcha.model.po.ClientInfo;
import io.github.opensabre.sysadmin.captcha.model.vo.CaptchaVo;
import io.github.opensabre.sysadmin.captcha.service.ICaptchaSceneService;
import io.github.opensabre.sysadmin.captcha.service.ICaptchaService;
import io.github.opensabre.sysadmin.captcha.utils.ClientUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Tag(name = "验证码")
@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @Resource
    private ICaptchaService smsCaptchaService;
    @Resource
    private ICaptchaService imageCaptchaService;
    @Resource
    private ICaptchaService emailCaptchaService;
    @Resource
    private ICaptchaSceneService captchaSceneService;

    @Operation(summary = "发送短信验证码", description = "发送一个随机的短信验证码")
    @PostMapping("/send/sms")
    public CaptchaVo captchaSms(@Parameter(name = "scenario", description = "业务场景", required = true) @RequestParam BusinessScenario scenario,
                          @Parameter(name = "phoneNo", description = "手机号", required = true) @RequestParam String phoneNo,
                          @Parameter(name = "imageCaptchaId", description = "图形验证码 ID", required = true) @RequestParam String imageCaptchaId,
                          @Parameter(name = "imageCaptchaCode", description = "用户输入的图形验证码", required = true) @RequestParam String imageCaptchaCode,
                          HttpServletRequest request) {
        CaptchaScene scene = requireScene(scenario.getCode());
        // Get client IP and device info
        ClientInfo clientInfo = ClientUtils.getClientInfo(request, phoneNo, scene);
        // Generate Captcha
        CaptchaVo captchaVo = smsCaptchaService.generateCaptcha(phoneNo, scene, clientInfo);
        log.info("Captcha SMS sent: businessKey={}, scenario={}, info={}", phoneNo, scenario, captchaVo);
        return captchaVo;
    }

    @Operation(summary = "发送邮件验证码", description = "发送一个随机的邮件验证码")
    @PostMapping("/send/email")
    public CaptchaVo captchaEmail(@Parameter(name = "scenario", description = "业务场景", required = true) @RequestParam BusinessScenario scenario,
                          @Parameter(name = "email", description = "邮箱", required = true) @RequestParam String email,
                          @Parameter(name = "imageCaptchaId", description = "图形验证码 ID", required = true) @RequestParam String imageCaptchaId,
                          @Parameter(name = "imageCaptchaCode", description = "用户输入的图形验证码", required = true) @RequestParam String imageCaptchaCode,
                          HttpServletRequest request) {
        CaptchaScene scene = requireScene(scenario.getCode());
        // Get client IP and device info
        ClientInfo clientInfo = ClientUtils.getClientInfo(request, email, scene);
        // Generate Captcha
        CaptchaVo captchaVo = emailCaptchaService.generateCaptcha(email, scene, clientInfo);
        log.info("Captcha Email sent: businessKey={}, scenario={}, info={}", email, scenario, captchaVo);
        return captchaVo;
    }

    @Operation(summary = "生成图形验证码", description = "生成一个随机的图形验证码")
    @PostMapping("/send/image")
    public CaptchaVo captchaImage(@Parameter(name = "scenario", description = "业务场景", required = true) @RequestParam BusinessScenario scenario,
                          @Parameter(name = "requestKey", description = "唯一标识（sessionId）", required = true) @RequestParam String requestKey,
                          HttpServletRequest request) {
        CaptchaScene scene = requireScene(scenario.getCode());
        // Get client IP and device info
        ClientInfo clientInfo = ClientUtils.getClientInfo(request, requestKey, scene);
        // Generate Captcha
        CaptchaVo captchaVo = imageCaptchaService.generateCaptcha(requestKey, scene, clientInfo);
        log.info("Captcha image sent: businessKey={}, scenario={}, info={}", requestKey, scenario, captchaVo);
        return captchaVo;
    }

    @Operation(summary = "动态生成验证码", description = "根据场景代码动态生成验证码")
    @PostMapping("/send/{sceneCode}")
    public CaptchaVo captchaByScene(@Parameter(name = "sceneCode", description = "场景代码", required = true) @PathVariable String sceneCode,
                                    @Parameter(name = "businessKey", description = "业务标识", required = true) @RequestParam String businessKey,
                                    HttpServletRequest request) {
        CaptchaScene scene = requireScene(sceneCode);
        ClientInfo clientInfo = ClientUtils.getClientInfo(request, businessKey, scene);
        CaptchaVo captchaVo = resolveCaptchaService(scene.getCaptchaType()).generateCaptcha(businessKey, scene, clientInfo);
        log.info("Captcha sent by scene: businessKey={}, sceneCode={}, info={}", businessKey, sceneCode, captchaVo);
        return captchaVo;
    }

    @Operation(summary = "验证验证码", description = "验证该验证码是否有效")
    @PostMapping("/verify")
    public boolean verify(@Parameter(name = "scenario", description = "业务场景", required = true) @RequestParam BusinessScenario scenario,
                          @Parameter(name = "captchaId", description = "验证码 id", required = true) @RequestParam @NotBlank(message = "验证码 ID 不能为空") String captchaId,
                          @Parameter(name = "inputCode", description = "验证码", required = true)  @RequestParam @NotBlank(message = "验证码不能为空") String inputCode) {
        CaptchaScene scene = requireScene(scenario.getCode());
        boolean result = resolveCaptchaService(scene.getCaptchaType()).validateCaptcha(captchaId, scene, inputCode);
        log.info("Captcha verification result: captchaId={}, inputCode={}, result={}", captchaId, inputCode, result);
        return result;
    }

    @Operation(summary = "动态验证验证码", description = "根据场景代码动态验证验证码")
    @PostMapping("/verify/{sceneCode}")
    public boolean verifyByScene(@Parameter(name = "sceneCode", description = "场景代码", required = true) @PathVariable String sceneCode,
                                 @Parameter(name = "captchaId", description = "验证码 id", required = true) @RequestParam @NotBlank(message = "验证码 ID 不能为空") String captchaId,
                                 @Parameter(name = "inputCode", description = "验证码", required = true) @RequestParam @NotBlank(message = "验证码不能为空") String inputCode) {
        CaptchaScene scene = requireScene(sceneCode);
        boolean result = resolveCaptchaService(scene.getCaptchaType()).validateCaptcha(captchaId, scene, inputCode);
        log.info("Captcha verification result: captchaId={}, sceneCode={}, inputCode={}, result={}", captchaId, sceneCode, inputCode, result);
        return result;
    }

    private CaptchaScene requireScene(String sceneCode) {
        CaptchaScene scene = captchaSceneService.getByCode(sceneCode);
        if (scene == null) {
            throw new IllegalArgumentException("Captcha scene not found: " + sceneCode);
        }
        if (!scene.isEnabled()) {
            throw new IllegalStateException("Captcha scene disabled: " + sceneCode);
        }
        return scene;
    }

    private ICaptchaService resolveCaptchaService(CaptchaType captchaType) {
        return switch (captchaType) {
            case SMS -> smsCaptchaService;
            case EMAIL -> emailCaptchaService;
            case IMAGE -> imageCaptchaService;
            default -> throw new IllegalArgumentException("Unsupported captcha type: " + captchaType);
        };
    }
}
