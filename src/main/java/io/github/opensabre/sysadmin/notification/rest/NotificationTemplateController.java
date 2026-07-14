package io.github.opensabre.sysadmin.notification.rest;

import io.github.opensabre.governance.audit.annotations.Audit;
import io.github.opensabre.governance.audit.annotations.OperationType;
import io.github.opensabre.sysadmin.notification.enums.NotificationType;
import io.github.opensabre.sysadmin.notification.model.po.NotificationTemplateConfig;
import io.github.opensabre.sysadmin.notification.service.INotificationTemplateConfigService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "通知模板")
@RestController
@RequestMapping("/notification/templates")
public class NotificationTemplateController {

    @Resource
    private INotificationTemplateConfigService notificationTemplateConfigService;

    @GetMapping
    @Operation(summary = "查询通知模板列表")
    public List<NotificationTemplateConfig> list(@RequestParam(required = false) String sceneCode,
                                                 @RequestParam(required = false) NotificationType channel,
                                                 @RequestParam(required = false) Boolean enabled) {
        return notificationTemplateConfigService.listTemplates(sceneCode, channel, enabled);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取通知模板")
    public NotificationTemplateConfig get(@PathVariable String id) {
        return notificationTemplateConfigService.getFormData(id);
    }

    @PostMapping
    @Operation(summary = "创建通知模板")
    @Audit(operationType = OperationType.CREATE, description = "创建通知模板", module = "NOTIFICATION_TEMPLATE", response = true, key = "#template.sceneCode")
    public boolean create(@Valid @RequestBody NotificationTemplateConfig template) {
        return notificationTemplateConfigService.saveTemplate(template);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新通知模板")
    @Audit(operationType = OperationType.UPDATE, description = "更新通知模板", module = "NOTIFICATION_TEMPLATE", response = true, key = "#id")
    public boolean update(@PathVariable String id, @Valid @RequestBody NotificationTemplateConfig template) {
        return notificationTemplateConfigService.updateTemplate(id, template);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除通知模板")
    @Audit(operationType = OperationType.DELETE, description = "删除通知模板", module = "NOTIFICATION_TEMPLATE", response = true, key = "#id")
    public boolean delete(@PathVariable String id) {
        return notificationTemplateConfigService.deleteTemplate(id);
    }
}
