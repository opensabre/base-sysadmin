package io.github.opensabre.sysadmin.notification.rest;

import io.github.opensabre.sysadmin.dict.model.vo.PageData;
import io.github.opensabre.sysadmin.notification.enums.NotificationSendStatus;
import io.github.opensabre.sysadmin.notification.enums.NotificationType;
import io.github.opensabre.sysadmin.notification.model.po.NotificationRecord;
import io.github.opensabre.sysadmin.notification.model.vo.NotificationSendResponse;
import io.github.opensabre.sysadmin.notification.service.INotificationRecordService;
import io.github.opensabre.sysadmin.notification.service.NotificationServiceManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "通知记录")
@RestController
@RequestMapping("/notification/records")
public class NotificationRecordController {

    @Resource
    private INotificationRecordService notificationRecordService;

    @Resource
    private NotificationServiceManager notificationServiceManager;

    @GetMapping
    @Operation(summary = "通知发送记录分页")
    public PageData<NotificationRecord> page(@RequestParam(defaultValue = "1") long pageNum,
                                             @RequestParam(defaultValue = "10") long pageSize,
                                             @RequestParam(required = false) String sceneCode,
                                             @RequestParam(required = false) NotificationType channel,
                                             @RequestParam(required = false) NotificationSendStatus status) {
        return PageData.from(notificationRecordService.pageRecords(pageNum, pageSize, sceneCode, channel, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取通知发送记录")
    public NotificationRecord get(@PathVariable String id) {
        return notificationRecordService.getRecord(id);
    }

    @PostMapping("/{id}/retry")
    @Operation(summary = "重试失败通知")
    public NotificationSendResponse retry(@PathVariable String id) {
        return notificationServiceManager.retry(id);
    }
}
