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
        
        // 构建邮件内容
        String content = buildEmailContent(template, args);
        
        // 发送邮件
        return sendEmail(target, content);
    }

    @Override
    public String send(String target, NotificationTemplate template, Object... args) {
        log.info("Sending Email to: {} with template: {} and args: {}", target, template, args);
        
        // 构建邮件内容
        String content = String.format(template.getContent(), args);
        
        // 发送邮件
        return sendEmail(target, content);
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

    /**
     * 发送邮件
     *
     * @param target  目标邮箱
     * @param content 邮件内容
     * @return 发送结果（消息ID）
     */
    private String sendEmail(String target, String content) {
        // 这里应该是实际的邮件发送逻辑
        // 比如使用Spring Mail或第三方邮件服务
        log.info("Sending email to {} with content: {}", target, content);
        
        // 模拟发送成功，返回消息ID
        return "EMAIL_" + System.currentTimeMillis() + "_" + target.hashCode();
    }
}