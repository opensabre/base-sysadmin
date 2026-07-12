package io.github.opensabre.sysadmin.notification.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.opensabre.persistence.entity.po.BasePo;
import io.github.opensabre.sysadmin.notification.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("base_sys_notification_template")
@EqualsAndHashCode(callSuper = true)
public class NotificationTemplateConfig extends BasePo {

    private String sceneCode;

    private NotificationType channel;

    private String templateName;

    private String title;

    private String content;

    private String paramSchema;

    private Integer sort;

    private Boolean enabled;
}
