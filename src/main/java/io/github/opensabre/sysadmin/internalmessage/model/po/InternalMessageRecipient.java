package io.github.opensabre.sysadmin.internalmessage.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.opensabre.persistence.entity.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/** 已发布站内信的收件人快照和阅读状态。 */
@Data
@TableName("base_sys_internal_message_recipient")
@EqualsAndHashCode(callSuper = true)
public class InternalMessageRecipient extends BasePo {
    private String messageId;
    private String username;
    private LocalDateTime readTime;
}
