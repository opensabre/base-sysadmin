package io.github.opensabre.sysadmin.notification.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.opensabre.persistence.entity.po.BasePo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("base_sys_notification_scene")
@EqualsAndHashCode(callSuper = true)
public class NotificationScene extends BasePo {

    private String sceneCode;

    private String sceneName;

    private String description;

    private Boolean enabled;
}
