package io.github.opensabre.sysadmin.notification.model.form;

import io.github.opensabre.sysadmin.notification.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSendForm {

    @NotBlank(message = "目标地址不能为空")
    private String target;

    @NotBlank(message = "通知场景不能为空")
    private String sceneCode;

    private NotificationType channel;

    private Map<String, String> args;
}
