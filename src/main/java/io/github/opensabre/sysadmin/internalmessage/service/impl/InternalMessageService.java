package io.github.opensabre.sysadmin.internalmessage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.opensabre.sysadmin.internalmessage.dao.InternalMessageMapper;
import io.github.opensabre.sysadmin.internalmessage.dao.InternalMessageRecipientMapper;
import io.github.opensabre.sysadmin.internalmessage.enums.InternalMessageStatus;
import io.github.opensabre.sysadmin.internalmessage.enums.InternalMessageTargetScope;
import io.github.opensabre.sysadmin.internalmessage.model.form.InternalMessageForm;
import io.github.opensabre.sysadmin.internalmessage.model.po.InternalMessage;
import io.github.opensabre.sysadmin.internalmessage.model.po.InternalMessageRecipient;
import io.github.opensabre.sysadmin.internalmessage.model.vo.InternalMessageStatistics;
import io.github.opensabre.sysadmin.internalmessage.service.IInternalMessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/** 默认站内信服务实现。 */
@Service
public class InternalMessageService extends ServiceImpl<InternalMessageMapper, InternalMessage>
        implements IInternalMessageService {

    private final InternalMessageRecipientMapper recipientMapper;

    public InternalMessageService(InternalMessageRecipientMapper recipientMapper) {
        this.recipientMapper = recipientMapper;
    }

    @Override
    public IPage<InternalMessage> page(long pageNum, long pageSize, String title, InternalMessageStatus status) {
        return this.page(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<InternalMessage>()
                .like(StringUtils.isNotBlank(title), InternalMessage::getTitle, title)
                .eq(status != null, InternalMessage::getStatus, status)
                .orderByDesc(InternalMessage::getCreatedTime));
    }

    @Override
    public InternalMessage get(String id) {
        return this.getById(id);
    }

    @Override
    public boolean create(InternalMessageForm form) {
        InternalMessage message = new InternalMessage();
        applyForm(message, form);
        message.setStatus(InternalMessageStatus.DRAFT);
        return this.save(message);
    }

    @Override
    public boolean update(String id, InternalMessageForm form) {
        InternalMessage message = this.getById(id);
        if (message == null || message.getStatus() != InternalMessageStatus.DRAFT) {
            return false;
        }
        applyForm(message, form);
        return this.updateById(message);
    }

    @Override
    public boolean delete(String id) {
        return this.remove(new LambdaQueryWrapper<InternalMessage>()
                .eq(InternalMessage::getId, id).eq(InternalMessage::getStatus, InternalMessageStatus.DRAFT));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publish(String id) {
        InternalMessage message = this.getById(id);
        if (message == null || message.getStatus() != InternalMessageStatus.DRAFT) {
            return false;
        }
        List<String> usernames = targetUsernames(message);
        if (usernames.isEmpty()) {
            return false;
        }
        for (String username : usernames) {
            InternalMessageRecipient recipient = new InternalMessageRecipient();
            recipient.setMessageId(message.getId());
            recipient.setUsername(username);
            recipientMapper.insert(recipient);
        }
        message.setStatus(InternalMessageStatus.PUBLISHED);
        message.setPublishTime(LocalDateTime.now());
        return this.updateById(message);
    }

    @Override
    public boolean revoke(String id) {
        return this.update(new LambdaUpdateWrapper<InternalMessage>()
                .eq(InternalMessage::getId, id)
                .eq(InternalMessage::getStatus, InternalMessageStatus.PUBLISHED)
                .set(InternalMessage::getStatus, InternalMessageStatus.REVOKED));
    }

    @Override
    public IPage<InternalMessage> inbox(long pageNum, long pageSize, String username, Integer isRead) {
        Page<InternalMessageRecipient> recipientPage = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<InternalMessageRecipient> recipientQuery = new LambdaQueryWrapper<InternalMessageRecipient>()
                .eq(InternalMessageRecipient::getUsername, username)
                .isNull(isRead != null && isRead == 0, InternalMessageRecipient::getReadTime)
                .isNotNull(isRead != null && isRead == 1, InternalMessageRecipient::getReadTime)
                .orderByDesc(InternalMessageRecipient::getCreatedTime);
        recipientMapper.selectPage(recipientPage, recipientQuery);
        List<String> ids = recipientPage.getRecords().stream().map(InternalMessageRecipient::getMessageId).toList();
        if (ids.isEmpty()) {
            return new Page<>(pageNum, pageSize, recipientPage.getTotal());
        }
        List<InternalMessage> records = this.list(new LambdaQueryWrapper<InternalMessage>()
                .in(InternalMessage::getId, ids)
                .eq(InternalMessage::getStatus, InternalMessageStatus.PUBLISHED)
                .and(wrapper -> wrapper.isNull(InternalMessage::getExpireTime)
                        .or().gt(InternalMessage::getExpireTime, LocalDateTime.now()))
                .orderByDesc(InternalMessage::getPublishTime));
        Page<InternalMessage> page = new Page<>(pageNum, pageSize, recipientPage.getTotal());
        page.setRecords(records);
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InternalMessage read(String messageId, String username) {
        InternalMessageRecipient recipient = recipientMapper.selectOne(new LambdaQueryWrapper<InternalMessageRecipient>()
                .eq(InternalMessageRecipient::getMessageId, messageId)
                .eq(InternalMessageRecipient::getUsername, username).last("limit 1"));
        InternalMessage message = this.getById(messageId);
        if (recipient == null || message == null || message.getStatus() != InternalMessageStatus.PUBLISHED) {
            return null;
        }
        if (recipient.getReadTime() == null) {
            recipient.setReadTime(LocalDateTime.now());
            recipientMapper.updateById(recipient);
        }
        return message;
    }

    @Override
    public boolean readAll(String username) {
        return recipientMapper.update(null, new LambdaUpdateWrapper<InternalMessageRecipient>()
                .eq(InternalMessageRecipient::getUsername, username)
                .isNull(InternalMessageRecipient::getReadTime)
                .set(InternalMessageRecipient::getReadTime, LocalDateTime.now())) >= 0;
    }

    @Override
    public long unreadCount(String username) {
        return recipientMapper.selectCount(new LambdaQueryWrapper<InternalMessageRecipient>()
                .eq(InternalMessageRecipient::getUsername, username)
                .isNull(InternalMessageRecipient::getReadTime));
    }

    @Override
    public InternalMessageStatistics statistics(String id) {
        long total = recipientMapper.selectCount(new LambdaQueryWrapper<InternalMessageRecipient>()
                .eq(InternalMessageRecipient::getMessageId, id));
        long read = recipientMapper.selectCount(new LambdaQueryWrapper<InternalMessageRecipient>()
                .eq(InternalMessageRecipient::getMessageId, id).isNotNull(InternalMessageRecipient::getReadTime));
        return new InternalMessageStatistics(total, read);
    }

    private void applyForm(InternalMessage message, InternalMessageForm form) {
        message.setTitle(form.getTitle().trim());
        message.setContent(sanitizeHtml(form.getContent()));
        message.setKind(form.getKind());
        message.setLevel(StringUtils.defaultIfBlank(form.getLevel(), "L"));
        message.setTargetScope(form.getTargetScope());
        message.setTargetUsernames(String.join(",", normalizeUsernames(form.getTargetUsernames())));
        message.setTargetUrl(StringUtils.trimToNull(form.getTargetUrl()));
        message.setExpireTime(form.getExpireTime());
    }

    private List<String> targetUsernames(InternalMessage message) {
        return StringUtils.isBlank(message.getTargetUsernames()) ? Collections.emptyList()
                : Arrays.stream(StringUtils.split(message.getTargetUsernames(), ','))
                .filter(StringUtils::isNotBlank).distinct().toList();
    }

    private List<String> normalizeUsernames(List<String> usernames) {
        if (usernames == null) {
            return Collections.emptyList();
        }
        return usernames.stream().map(StringUtils::trimToNull).filter(StringUtils::isNotBlank)
                .distinct().collect(Collectors.toList());
    }

    private String sanitizeHtml(String content) {
        return content.replaceAll("(?is)<script[^>]*>.*?</script>", "");
    }
}
