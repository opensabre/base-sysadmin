package io.github.opensabre.sysadmin.internalmessage.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.opensabre.persistence.entity.po.BasePo;
import io.github.opensabre.sysadmin.internalmessage.enums.InternalMessageKind;
import io.github.opensabre.sysadmin.internalmessage.enums.InternalMessageStatus;
import io.github.opensabre.sysadmin.internalmessage.enums.InternalMessageTargetScope;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/** 站内信主体，发布后内容与目标范围不可再编辑。 */
@Data
@TableName("base_sys_internal_message")
@EqualsAndHashCode(callSuper = true)
public class InternalMessage extends BasePo {
    private InternalMessageKind kind;
    private String title;
    private String content;
    private String level;
    private InternalMessageTargetScope targetScope;
    /** 草稿目标用户名快照，发布时转换为收件人记录。 */
    private String targetUsernames;
    private String targetUrl;
    private InternalMessageStatus status;
    private LocalDateTime publishTime;
    private LocalDateTime expireTime;

    /** 当前收件人阅读状态，仅用于收件箱响应，不持久化到消息主体表。 */
    @TableField(exist = false)
    private Boolean read;
}
