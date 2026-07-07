package io.github.opensabre.sysadmin.notification.rest;

import io.github.opensabre.sysadmin.notification.model.form.NotificationSendForm;
import io.github.opensabre.sysadmin.notification.model.vo.NotificationSendResponse;
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
    public NotificationSendResponse sendNotification(@Valid @RequestBody NotificationSendForm form) {
        log.info("Received send notification request: target={}, sceneCode={}, channel={}",
                form.getTarget(), form.getSceneCode(), form.getChannel());
        return notificationServiceManager.sendNotification(form);
    }
}
