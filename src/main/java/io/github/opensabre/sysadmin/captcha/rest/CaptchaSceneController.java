package io.github.opensabre.sysadmin.captcha.rest;

import io.github.opensabre.governance.audit.annotations.Audit;
import io.github.opensabre.governance.audit.annotations.OperationType;
import io.github.opensabre.sysadmin.captcha.model.po.CaptchaScene;
import io.github.opensabre.sysadmin.captcha.service.ICaptchaSceneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "验证码场景")
@RestController
@RequestMapping("/captcha/scenes")
public class CaptchaSceneController {

    @Resource
    private ICaptchaSceneService captchaSceneService;

    @GetMapping("/{sceneCode}")
    @Operation(summary = "获取验证码场景")
    public CaptchaScene get(@PathVariable String sceneCode) {
        return captchaSceneService.getByCode(sceneCode);
    }

    @GetMapping
    @Operation(summary = "查询验证码场景列表")
    public List<CaptchaScene> list() {
        return captchaSceneService.list();
    }

    @GetMapping("/enabled")
    @Operation(summary = "查询启用的验证码场景")
    public List<CaptchaScene> listEnabled() {
        return captchaSceneService.listEnabled();
    }

    @PostMapping
    @Operation(summary = "创建验证码场景")
    @Audit(operationType = OperationType.CREATE, description = "创建验证码场景", module = "CAPTCHA_SCENE", response = true, key = "#scene.sceneCode")
    public boolean create(@Valid @RequestBody CaptchaScene scene) {
        return captchaSceneService.saveScene(scene);
    }

    @PutMapping("/{sceneCode}")
    @Operation(summary = "更新验证码场景")
    @Audit(operationType = OperationType.UPDATE, description = "更新验证码场景", module = "CAPTCHA_SCENE", response = true, key = "#sceneCode")
    public boolean update(@PathVariable String sceneCode, @Valid @RequestBody CaptchaScene scene) {
        scene.setSceneCode(sceneCode);
        return captchaSceneService.updateScene(scene);
    }

    @DeleteMapping("/{sceneCode}")
    @Operation(summary = "删除验证码场景")
    @Audit(operationType = OperationType.DELETE, description = "删除验证码场景", module = "CAPTCHA_SCENE", response = true, key = "#sceneCode")
    public boolean delete(@PathVariable String sceneCode) {
        return captchaSceneService.deleteScene(sceneCode);
    }
}
