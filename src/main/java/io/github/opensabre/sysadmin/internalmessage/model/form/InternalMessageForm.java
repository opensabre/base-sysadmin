package io.github.opensabre.sysadmin.internalmessage.model.form;

import io.github.opensabre.sysadmin.internalmessage.enums.InternalMessageKind;
import io.github.opensabre.sysadmin.internalmessage.enums.InternalMessageTargetScope;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/** 创建或编辑站内信的表单。 */
@Data
public class InternalMessageForm {
    @NotBlank(message = "标题不能为空")
    private String title;
    @NotBlank(message = "内容不能为空")
    private String content;
    @NotNull(message = "消息类型不能为空")
    private InternalMessageKind kind;
    private String level;
    @NotNull(message = "目标范围不能为空")
    private InternalMessageTargetScope targetScope;
    private List<String> targetUsernames;
    private String targetUrl;
    private LocalDateTime expireTime;
}
