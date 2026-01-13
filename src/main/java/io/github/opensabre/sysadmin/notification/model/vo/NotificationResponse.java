package io.github.opensabre.sysadmin.notification.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 发送通知响应对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private String messageId;  // 消息唯一标识
    private LocalDateTime sentTime;       // 发送时间戳
}