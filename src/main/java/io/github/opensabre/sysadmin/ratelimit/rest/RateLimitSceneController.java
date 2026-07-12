package io.github.opensabre.sysadmin.ratelimit.rest;

import io.github.opensabre.sysadmin.ratelimit.model.RateLimitScene;
import io.github.opensabre.sysadmin.ratelimit.service.IRateLimitSceneService;
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

@Tag(name = "限次场景")
@RestController
@RequestMapping("/ratelimit/scenes")
public class RateLimitSceneController {

    @Resource
    private IRateLimitSceneService rateLimitSceneService;

    @GetMapping("/{sceneCode}")
    @Operation(summary = "获取限次场景")
    public RateLimitScene get(@PathVariable String sceneCode) {
        return rateLimitSceneService.getByCode(sceneCode);
    }

    @GetMapping
    @Operation(summary = "查询限次场景列表")
    public List<RateLimitScene> list() {
        return rateLimitSceneService.list();
    }

    @GetMapping("/enabled")
    @Operation(summary = "查询启用的限次场景")
    public List<RateLimitScene> listEnabled() {
        return rateLimitSceneService.listEnabled();
    }

    @PostMapping
    @Operation(summary = "创建限次场景")
    public boolean create(@Valid @RequestBody RateLimitScene scene) {
        return rateLimitSceneService.saveScene(scene);
    }

    @PutMapping("/{sceneCode}")
    @Operation(summary = "更新限次场景")
    public boolean update(@PathVariable String sceneCode, @Valid @RequestBody RateLimitScene scene) {
        scene.setSceneCode(sceneCode);
        return rateLimitSceneService.updateScene(scene);
    }

    @DeleteMapping("/{sceneCode}")
    @Operation(summary = "删除限次场景")
    public boolean delete(@PathVariable String sceneCode) {
        return rateLimitSceneService.deleteScene(sceneCode);
    }
}
