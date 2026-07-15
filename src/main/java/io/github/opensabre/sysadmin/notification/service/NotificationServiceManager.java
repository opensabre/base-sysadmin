package io.github.opensabre.sysadmin.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.opensabre.sysadmin.notification.enums.NotificationTemplate;
import io.github.opensabre.sysadmin.notification.enums.NotificationSendStatus;
import io.github.opensabre.sysadmin.notification.enums.NotificationType;
import io.github.opensabre.sysadmin.notification.model.form.NotificationSendForm;
import io.github.opensabre.sysadmin.notification.model.po.NotificationRecord;
import io.github.opensabre.sysadmin.notification.model.po.NotificationScene;
import io.github.opensabre.sysadmin.notification.model.po.NotificationTemplateConfig;
import io.github.opensabre.sysadmin.notification.model.vo.NotificationSendResponse;
import io.github.opensabre.sysadmin.usage.enums.UsageEvent;
import io.github.opensabre.sysadmin.usage.enums.UsageObjectType;
import io.github.opensabre.sysadmin.usage.enums.UsageOutcome;
import io.github.opensabre.sysadmin.usage.service.IUsageCounterService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通知服务管理器
 * 管理不同类型的通知服务实现
 */
@Component
public class NotificationServiceManager {

    private final Map<NotificationType, INotificationService> notificationServices;

    @Resource
    private INotificationSceneService notificationSceneService;

    @Resource
    private INotificationTemplateConfigService notificationTemplateConfigService;

    @Resource
    private INotificationRecordService notificationRecordService;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private IUsageCounterService usageCounterService;

    @Autowired
    public NotificationServiceManager(List<INotificationService> services) {
        this.notificationServices = services.stream().collect(Collectors.toMap(INotificationService::getType, service -> service));
    }

    /**
     * 发送通知
     *
     * @param target   目标地址
     * @param template 通知模板
     * @param args     模板参数
     * @return 发送结果
     */
    public String sendNotification(String target, NotificationTemplate template, Object... args) {
        INotificationService service = notificationServices.get(template.getType());
        Assert.notNull(service, "No notification service found for type: " + template.getType());
        return service.send(target, template, args);
    }

    /**
     * 发送通知
     *
     * @param target   目标地址
     * @param template 通知模板
     * @param args     模板参数 (Map格式)
     * @return 发送结果
     */
    public String sendNotification(String target, NotificationTemplate template, Map<String, String> args) {
        INotificationService service = notificationServices.get(template.getType());
        Assert.notNull(service, "No notification service found for type: " + template.getType());
        return service.send(target, template, args);
    }

    /**
     * 发送通知（不带参数）
     *
     * @param target   目标地址
     * @param template 通知模板
     * @return 发送结果
     */
    public String sendNotification(String target, NotificationTemplate template) {
        INotificationService service = notificationServices.get(template.getType());
        Assert.notNull(service, "No notification service found for type: " + template.getType());
        return service.send(target, template);
    }

    public NotificationSendResponse sendNotification(NotificationSendForm form) {
        Assert.notNull(form, "Notification form must not be null");
        NotificationTemplateConfig template = resolveTemplate(form.getSceneCode(), form.getChannel());
        return sendWithTemplate(form.getTarget(), template, safeArgs(form.getArgs()), 0, null);
    }

    public NotificationSendResponse retry(String recordId) {
        NotificationRecord record = notificationRecordService.getRecord(recordId);
        Assert.notNull(record, "Notification record not found: " + recordId);
        Assert.isTrue(record.getStatus() == NotificationSendStatus.FAILED, "Only failed notification records can be retried");

        NotificationTemplateConfig template = resolveTemplate(record.getSceneCode(), record.getChannel());
        return sendWithTemplate(record.getTarget(), template, parseArgs(record.getArgsJson()),
                record.getRetryCount() == null ? 1 : record.getRetryCount() + 1, record);
    }

    public String renderContent(String content, Map<String, String> args) {
        if (StringUtils.isBlank(content) || args == null || args.isEmpty()) {
            return content;
        }
        String rendered = content;
        for (Map.Entry<String, String> entry : args.entrySet()) {
            rendered = rendered.replace("{" + entry.getKey() + "}", entry.getValue() == null ? "" : entry.getValue());
        }
        return rendered.replaceAll("\\{[A-Za-z0-9_]+}", "");
    }

    private NotificationTemplateConfig resolveTemplate(String sceneCode, NotificationType channel) {
        NotificationScene scene = notificationSceneService.getByCode(sceneCode);
        Assert.notNull(scene, "Notification scene not found: " + sceneCode);
        Assert.isTrue(Boolean.TRUE.equals(scene.getEnabled()), "Notification scene disabled: " + sceneCode);

        NotificationTemplateConfig template = channel == null
                ? notificationTemplateConfigService.getFirstEnabledTemplate(sceneCode)
                : notificationTemplateConfigService.getEnabledTemplate(sceneCode, channel);
        Assert.notNull(template, "Notification template not found for scene: " + sceneCode);
        Assert.isTrue(Boolean.TRUE.equals(template.getEnabled()), "Notification template disabled: " + sceneCode);
        Assert.notNull(notificationServices.get(template.getChannel()), "No notification service found for type: " + template.getChannel());
        return template;
    }

    private NotificationSendResponse sendWithTemplate(String target, NotificationTemplateConfig template, Map<String, String> args,
                                                      int retryCount, NotificationRecord existingRecord) {
        String renderedTitle = renderContent(template.getTitle(), args);
        String renderedContent = renderContent(template.getContent(), args);
        NotificationRecord record = existingRecord == null ? NotificationRecord.builder().build() : existingRecord;
        record.setSceneCode(template.getSceneCode());
        record.setChannel(template.getChannel());
        record.setTarget(target);
        record.setTemplateId(template.getId());
        record.setTemplateTitle(renderedTitle);
        record.setTemplateContent(renderedContent);
        record.setArgsJson(toJson(args));
        record.setRetryCount(retryCount);
        recordUsage(template, UsageOutcome.ATTEMPT);

        try {
            INotificationService service = notificationServices.get(template.getChannel());
            String messageId = service.sendContent(target, renderedTitle, renderedContent);
            record.setMessageId(messageId);
            record.setStatus(NotificationSendStatus.SUCCESS);
            record.setFailureReason(null);
            record.setSentTime(LocalDateTime.now());
            recordUsage(template, UsageOutcome.SUCCESS);
        } catch (Exception e) {
            record.setStatus(NotificationSendStatus.FAILED);
            record.setFailureReason(e.getMessage());
            record.setSentTime(LocalDateTime.now());
            recordUsage(template, UsageOutcome.FAILURE);
        }

        if (existingRecord == null) {
            notificationRecordService.saveRecord(record);
        } else {
            notificationRecordService.updateRecord(record);
        }
        return toResponse(record);
    }

    private void recordUsage(NotificationTemplateConfig template, UsageOutcome outcome) {
        usageCounterService.record(UsageObjectType.NOTIFICATION_SCENE, template.getSceneCode(),
                UsageEvent.NOTIFICATION_SEND, outcome);
        usageCounterService.record(UsageObjectType.NOTIFICATION_TEMPLATE, template.getId(),
                UsageEvent.NOTIFICATION_SEND, outcome);
    }

    private NotificationSendResponse toResponse(NotificationRecord record) {
        return NotificationSendResponse.builder()
                .recordId(record.getId())
                .messageId(record.getMessageId())
                .sceneCode(record.getSceneCode())
                .channel(record.getChannel())
                .status(record.getStatus())
                .failureReason(record.getFailureReason())
                .sentTime(record.getSentTime())
                .build();
    }

    private String toJson(Map<String, String> args) {
        try {
            return objectMapper.writeValueAsString(args == null ? Collections.emptyMap() : args);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize notification args", e);
        }
    }

    private Map<String, String> parseArgs(String argsJson) {
        if (StringUtils.isBlank(argsJson)) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(argsJson, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to parse notification args", e);
        }
    }

    private Map<String, String> safeArgs(Map<String, String> args) {
        return args == null ? Collections.emptyMap() : args;
    }

    /**
     * 获取通知服务
     *
     * @param type 通知类型
     * @return 通知服务实例
     */
    public INotificationService get(NotificationType type) {
        return notificationServices.get(type);
    }

    /**
     * 检查是否支持指定类型的通知
     *
     * @param type 通知类型
     * @return 是否支持
     */
    public boolean supportsType(NotificationType type) {
        return notificationServices.containsKey(type);
    }
}
