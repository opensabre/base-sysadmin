package io.github.opensabre.sysadmin.internalmessage.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.opensabre.sysadmin.internalmessage.enums.InternalMessageStatus;
import io.github.opensabre.sysadmin.internalmessage.model.form.InternalMessageForm;
import io.github.opensabre.sysadmin.internalmessage.model.po.InternalMessage;
import io.github.opensabre.sysadmin.internalmessage.model.vo.InternalMessageStatistics;

/** 站内信管理和用户收件箱服务。 */
public interface IInternalMessageService {
    IPage<InternalMessage> page(long pageNum, long pageSize, String title, InternalMessageStatus status);
    InternalMessage get(String id);
    boolean create(InternalMessageForm form);
    boolean update(String id, InternalMessageForm form);
    boolean delete(String id);
    boolean publish(String id);
    boolean revoke(String id);
    IPage<InternalMessage> inbox(long pageNum, long pageSize, String username, Integer isRead);
    InternalMessage read(String messageId, String username);
    boolean readAll(String username);
    long unreadCount(String username);
    InternalMessageStatistics statistics(String id);
}
