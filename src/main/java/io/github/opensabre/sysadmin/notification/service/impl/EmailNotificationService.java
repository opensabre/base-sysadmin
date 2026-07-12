package io.github.opensabre.sysadmin.notification.service.impl;

import io.github.opensabre.sysadmin.notification.enums.NotificationTemplate;
import io.github.opensabre.sysadmin.notification.enums.NotificationType;
import io.github.opensabre.sysadmin.notification.service.INotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 邮件通知服务实现
 */
@Slf4j
@Service
public class EmailNotificationService implements INotificationService {

    @Override
    public String send(String target, NotificationTemplate template, Map<String, String> args) {
        log.info("Sending Email to: {} with template: {} and map args: {}", target, template, args);
        
        String content = buildEmailContent(template, args);
        return sendContent(target, template.getName(), content);
    }

    @Override
    public String send(String target, NotificationTemplate template, Object... args) {
        log.info("Sending Email to: {} with template: {} and args: {}", target, template, args);
        
        String content = String.format(template.getContent(), args);
        return sendContent(target, template.getName(), content);
    }

    @Override
    public String send(String target, NotificationTemplate template) {
        log.info("Sending Email to: {} with template: {} without args", target, template);
        
        return sendContent(target, template.getName(), template.getContent());
    }

    @Override
    public String sendContent(String target, String title, String content) {
        log.info("Sending Email to {} with title: {} content: {}", target, title, content);
        return "EMAIL_" + System.currentTimeMillis() + "_" + target.hashCode();
    }

    @Override
    public NotificationType getType() {
        return NotificationType.EMAIL;
    }

    /**
     * 构建邮件内容
     *
     * @param template 模板
     * @param args     参数
     * @return 邮件内容
     */
    private String buildEmailContent(NotificationTemplate template, Map<String, String> args) {
        String content = template.getContent();
        
        // 替换模板中的参数
        for (Map.Entry<String, String> entry : args.entrySet()) {
            content = content.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        
        return content;
    }

}
