package io.github.opensabre.sysadmin.notification.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.opensabre.persistence.entity.po.BasePo;
import io.github.opensabre.sysadmin.notification.enums.NotificationSendStatus;
import io.github.opensabre.sysadmin.notification.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("base_sys_notification_record")
@EqualsAndHashCode(callSuper = true)
public class NotificationRecord extends BasePo {

    private String sceneCode;

    private NotificationType channel;

    private String target;

    private String templateId;

    private String templateTitle;

    private String templateContent;

    private String argsJson;

    private NotificationSendStatus status;

    private String messageId;

    private String failureReason;

    private Integer retryCount;

    private LocalDateTime nextRetryTime;

    private LocalDateTime sentTime;
}
