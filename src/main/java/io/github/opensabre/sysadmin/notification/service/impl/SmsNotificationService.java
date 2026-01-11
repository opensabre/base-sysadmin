package io.github.opensabre.sysadmin.notification.service.impl;

import io.github.opensabre.sysadmin.notification.enums.NotificationTemplate;
import io.github.opensabre.sysadmin.notification.enums.NotificationType;
import io.github.opensabre.sysadmin.notification.provider.ISmsProvider;
import io.github.opensabre.sysadmin.notification.service.INotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 短信通知服务实现
 * 采用模拟实现，实际项目中应替换为真实的短信服务商API
 */
@Slf4j
@Service
public class SmsNotificationService implements INotificationService {

    @Autowired
    private ISmsProvider smsProvider;

    @Override
    public String send(String target, NotificationTemplate template, Map<String, String> args) {
        log.info("Sending SMS to: {} template: {}, map arg: {},", target, template, args);
        // 模拟短信发送过程
        String content = String.format(template.getContent(), args);
        return smsProvider.sendSms(target, content);
    }

    @Override
    public String send(String target, NotificationTemplate template, Object... args) {
        log.info("Sending SMS to: {} template: {}, args: {},", target, template, args);
        // 模拟短信发送过程
        String content = String.format(template.getContent(), args);
        return smsProvider.sendSms(target, content);
    }

    @Override
    public NotificationType getType() {
        return NotificationType.SMS;
    }
}