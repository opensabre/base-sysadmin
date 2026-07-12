package io.github.opensabre.sysadmin.notification.model.form;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 通知请求表单
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationForm {

    @NotBlank(message = "目标地址不能为空")
    private String target;  // 接收者地址

    @NotBlank(message = "模板代码不能为空")
    private String templateCode;  // NotificationTemplate枚举code

    private Object[] args;  // 位置参数，与mapArgs二选一

    private Map<String, String> mapArgs;  // 键值对参数，与args二选一
}