package io.github.opensabre.sysadmin.notification.rest;

import io.github.opensabre.sysadmin.notification.enums.NotificationTemplate;
import io.github.opensabre.sysadmin.notification.model.form.NotificationForm;
import io.github.opensabre.sysadmin.notification.model.vo.NotificationResponse;
import io.github.opensabre.sysadmin.notification.service.NotificationServiceManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * 通知控制器
 */
@Slf4j
@Tag(name = "通知")
@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Resource
    private NotificationServiceManager notificationServiceManager;

    /**
     * 发送通知
     *
     * @param form 发送通知请求参数
     * @return 发送结果
     */
    @Operation(summary = "发送通知", description = "根据指定类型发送通知")
    @PostMapping("/send")
    public NotificationResponse sendNotification(@Valid @RequestBody NotificationForm form) {
        log.info("Received send notification request: target={}, templateCode={}", form.getTarget(), form.getTemplateCode());
        // 查找模板
        NotificationTemplate template = NotificationTemplate.valueOf(form.getTemplateCode());

        String messageId;
        // 根据参数类型选择发送方式
        if (form.getArgs() != null && form.getArgs().length > 0) {
            // 使用位置参数发送
            messageId = notificationServiceManager.sendNotification(form.getTarget(), template, form.getArgs());
        } else if (form.getMapArgs() != null && !form.getMapArgs().isEmpty()) {
            // 使用键值对参数发送
            messageId = notificationServiceManager.sendNotification(form.getTarget(), template, form.getMapArgs());
        } else {
            // 如果都没有参数，则发送不带参数的模板
            messageId = notificationServiceManager.sendNotification(form.getTarget(), template);
        }

        // 构造响应

        log.info("Notification sent successfully: messageId={}", messageId);
        return NotificationResponse.builder()
                .messageId(messageId)
                .sentTime(LocalDateTime.now())
                .build();

    }
}