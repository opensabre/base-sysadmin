package io.github.opensabre.sysadmin.notification.model.vo;

import io.github.opensabre.sysadmin.notification.enums.NotificationSendStatus;
import io.github.opensabre.sysadmin.notification.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSendResponse {

    private String recordId;

    private String messageId;

    private String sceneCode;

    private NotificationType channel;

    private NotificationSendStatus status;

    private String failureReason;

    private LocalDateTime sentTime;
}
