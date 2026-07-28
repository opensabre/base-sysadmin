package io.github.opensabre.sysadmin.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.opensabre.sysadmin.notification.enums.NotificationSendStatus;
import io.github.opensabre.sysadmin.notification.enums.NotificationType;
import io.github.opensabre.sysadmin.notification.model.form.NotificationSendForm;
import io.github.opensabre.sysadmin.notification.model.po.NotificationRecord;
import io.github.opensabre.sysadmin.notification.model.po.NotificationScene;
import io.github.opensabre.sysadmin.notification.model.po.NotificationTemplateConfig;
import io.github.opensabre.sysadmin.notification.model.vo.NotificationSendResponse;
import io.github.opensabre.governance.usage.NotificationUsageRecorder;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationServiceManagerTest {

    @Test
    void rendersNamedPlaceholdersAndClearsMissingValues() {
        NotificationServiceManager manager = newManager(mock(INotificationService.class));

        String content = manager.renderContent("验证码{code}，{minutes}分钟，{missing}", Map.of(
                "code", "123456",
                "minutes", "5"
        ));

        assertEquals("验证码123456，5分钟，", content);
    }

    @Test
    void sendsWithFirstEnabledTemplateWhenChannelIsAbsent() {
        INotificationService smsService = mock(INotificationService.class);
        when(smsService.getType()).thenReturn(NotificationType.SMS);
        when(smsService.sendContent("13800138000", null, "验证码123456")).thenReturn("sms-1");
        NotificationServiceManager manager = newManager(smsService);
        INotificationSceneService sceneService = sceneService();
        INotificationTemplateConfigService templateService = mock(INotificationTemplateConfigService.class);
        INotificationRecordService recordService = recordService();
        ReflectionTestUtils.setField(manager, "notificationSceneService", sceneService);
        ReflectionTestUtils.setField(manager, "notificationTemplateConfigService", templateService);
        ReflectionTestUtils.setField(manager, "notificationRecordService", recordService);
        when(templateService.getFirstEnabledTemplate("LOGIN_CAPTCHA")).thenReturn(template(NotificationType.SMS));

        NotificationSendResponse response = manager.sendNotification(new NotificationSendForm(
                "13800138000", "LOGIN_CAPTCHA", null, Map.of("code", "123456")));

        assertEquals(NotificationSendStatus.SUCCESS, response.getStatus());
        assertEquals("sms-1", response.getMessageId());
        verify(templateService).getFirstEnabledTemplate("LOGIN_CAPTCHA");
    }

    @Test
    void recordsFailureWhenChannelServiceThrows() {
        INotificationService smsService = mock(INotificationService.class);
        when(smsService.getType()).thenReturn(NotificationType.SMS);
        when(smsService.sendContent(any(), any(), any())).thenThrow(new IllegalStateException("provider down"));
        NotificationServiceManager manager = wiredManager(smsService, template(NotificationType.SMS), recordService());
        ArgumentCaptor<NotificationRecord> recordCaptor = ArgumentCaptor.forClass(NotificationRecord.class);

        NotificationSendResponse response = manager.sendNotification(new NotificationSendForm(
                "13800138000", "LOGIN_CAPTCHA", NotificationType.SMS, Map.of("code", "123456")));

        assertEquals(NotificationSendStatus.FAILED, response.getStatus());
        assertEquals("provider down", response.getFailureReason());
        verify((INotificationRecordService) ReflectionTestUtils.getField(manager, "notificationRecordService"))
                .saveRecord(recordCaptor.capture());
        assertEquals(NotificationSendStatus.FAILED, recordCaptor.getValue().getStatus());
    }

    @Test
    void retryRejectsNonFailedRecord() {
        INotificationRecordService recordService = mock(INotificationRecordService.class);
        when(recordService.getRecord("1")).thenReturn(NotificationRecord.builder()
                .status(NotificationSendStatus.SUCCESS)
                .build());
        NotificationServiceManager manager = wiredManager(mock(INotificationService.class),
                template(NotificationType.SMS), recordService);

        assertThrows(IllegalArgumentException.class, () -> manager.retry("1"));
    }

    private static NotificationServiceManager wiredManager(INotificationService service,
                                                           NotificationTemplateConfig template,
                                                           INotificationRecordService recordService) {
        when(service.getType()).thenReturn(template.getChannel());
        NotificationServiceManager manager = newManager(service);
        ReflectionTestUtils.setField(manager, "notificationSceneService", sceneService());
        INotificationTemplateConfigService templateService = mock(INotificationTemplateConfigService.class);
        when(templateService.getEnabledTemplate(template.getSceneCode(), template.getChannel())).thenReturn(template);
        when(templateService.getFirstEnabledTemplate(template.getSceneCode())).thenReturn(template);
        ReflectionTestUtils.setField(manager, "notificationTemplateConfigService", templateService);
        ReflectionTestUtils.setField(manager, "notificationRecordService", recordService);
        return manager;
    }

    private static NotificationServiceManager newManager(INotificationService service) {
        NotificationServiceManager manager = new NotificationServiceManager(List.of(service));
        ReflectionTestUtils.setField(manager, "objectMapper", new ObjectMapper());
        ReflectionTestUtils.setField(manager, "notificationUsageRecorder", new NotificationUsageRecorder(record -> { }));
        return manager;
    }

    private static INotificationSceneService sceneService() {
        INotificationSceneService sceneService = mock(INotificationSceneService.class);
        when(sceneService.getByCode("LOGIN_CAPTCHA")).thenReturn(NotificationScene.builder()
                .sceneCode("LOGIN_CAPTCHA")
                .sceneName("登录验证码")
                .enabled(true)
                .build());
        return sceneService;
    }

    private static INotificationRecordService recordService() {
        INotificationRecordService recordService = mock(INotificationRecordService.class);
        doAnswer(invocation -> {
            NotificationRecord record = invocation.getArgument(0);
            record.setId("record-1");
            return true;
        }).when(recordService).saveRecord(any(NotificationRecord.class));
        return recordService;
    }

    private static NotificationTemplateConfig template(NotificationType channel) {
        NotificationTemplateConfig template = NotificationTemplateConfig.builder()
                .sceneCode("LOGIN_CAPTCHA")
                .channel(channel)
                .templateName("登录验证码")
                .content("验证码{code}")
                .sort(1)
                .enabled(true)
                .build();
        template.setId("template-1");
        return template;
    }
}
