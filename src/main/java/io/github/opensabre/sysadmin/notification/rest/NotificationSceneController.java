package io.github.opensabre.sysadmin.notification.rest;

import io.github.opensabre.governance.audit.annotations.Audit;
import io.github.opensabre.governance.audit.annotations.OperationType;
import io.github.opensabre.sysadmin.notification.model.po.NotificationScene;
import io.github.opensabre.sysadmin.notification.service.INotificationSceneService;
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

@Tag(name = "通知场景")
@RestController
@RequestMapping("/notification/scenes")
public class NotificationSceneController {

    @Resource
    private INotificationSceneService notificationSceneService;

    @GetMapping("/{sceneCode}")
    @Operation(summary = "获取通知场景")
    public NotificationScene get(@PathVariable String sceneCode) {
        return notificationSceneService.getByCode(sceneCode);
    }

    @GetMapping
    @Operation(summary = "查询通知场景列表")
    public List<NotificationScene> list() {
        return notificationSceneService.list();
    }

    @GetMapping("/enabled")
    @Operation(summary = "查询启用通知场景")
    public List<NotificationScene> listEnabled() {
        return notificationSceneService.listEnabled();
    }

    @PostMapping
    @Operation(summary = "创建通知场景")
    @Audit(operationType = OperationType.CREATE, description = "创建通知场景", module = "NOTIFICATION_SCENE", response = true, key = "#scene.sceneCode")
    public boolean create(@Valid @RequestBody NotificationScene scene) {
        return notificationSceneService.saveScene(scene);
    }

    @PutMapping("/{sceneCode}")
    @Operation(summary = "更新通知场景")
    @Audit(operationType = OperationType.UPDATE, description = "更新通知场景", module = "NOTIFICATION_SCENE", response = true, key = "#sceneCode")
    public boolean update(@PathVariable String sceneCode, @Valid @RequestBody NotificationScene scene) {
        scene.setSceneCode(sceneCode);
        return notificationSceneService.updateScene(scene);
    }

    @DeleteMapping("/{sceneCode}")
    @Operation(summary = "删除通知场景")
    @Audit(operationType = OperationType.DELETE, description = "删除通知场景", module = "NOTIFICATION_SCENE", response = true, key = "#sceneCode")
    public boolean delete(@PathVariable String sceneCode) {
        return notificationSceneService.deleteScene(sceneCode);
    }
}
