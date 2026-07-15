package io.github.opensabre.sysadmin.internalmessage.rest;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.opensabre.governance.audit.annotations.Audit;
import io.github.opensabre.governance.audit.annotations.OperationType;
import io.github.opensabre.sysadmin.dict.model.vo.PageData;
import io.github.opensabre.sysadmin.internalmessage.enums.InternalMessageStatus;
import io.github.opensabre.sysadmin.internalmessage.model.form.InternalMessageForm;
import io.github.opensabre.sysadmin.internalmessage.model.po.InternalMessage;
import io.github.opensabre.sysadmin.internalmessage.model.vo.InternalMessageStatistics;
import io.github.opensabre.sysadmin.internalmessage.service.IInternalMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/** 管理端站内信和用户收件箱接口。 */
@Tag(name = "站内信")
@RestController
@RequestMapping("/internal-messages")
public class InternalMessageController {
    private final IInternalMessageService internalMessageService;

    public InternalMessageController(IInternalMessageService internalMessageService) {
        this.internalMessageService = internalMessageService;
    }

    @GetMapping
    @Operation(summary = "站内信管理分页")
    public PageData<InternalMessage> page(@RequestParam(defaultValue = "1") long pageNum,
                                          @RequestParam(defaultValue = "10") long pageSize,
                                          @RequestParam(required = false) String title,
                                          @RequestParam(required = false) InternalMessageStatus status) {
        return PageData.from(internalMessageService.page(pageNum, pageSize, title, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取站内信")
    public InternalMessage get(@PathVariable String id) {
        return internalMessageService.get(id);
    }

    @PostMapping
    @Audit(operationType = OperationType.CREATE, description = "创建站内信草稿", module = "INTERNAL_MESSAGE", response = true)
    @Operation(summary = "创建站内信草稿")
    public boolean create(@Valid @RequestBody InternalMessageForm form) {
        return internalMessageService.create(form);
    }

    @PutMapping("/{id}")
    @Audit(operationType = OperationType.UPDATE, description = "编辑站内信草稿", module = "INTERNAL_MESSAGE", response = true, key = "#id")
    @Operation(summary = "编辑站内信草稿")
    public boolean update(@PathVariable String id, @Valid @RequestBody InternalMessageForm form) {
        return internalMessageService.update(id, form);
    }

    @DeleteMapping("/{id}")
    @Audit(operationType = OperationType.DELETE, description = "删除站内信草稿", module = "INTERNAL_MESSAGE", response = true, key = "#id")
    @Operation(summary = "删除站内信草稿")
    public boolean delete(@PathVariable String id) {
        return internalMessageService.delete(id);
    }

    @PostMapping("/{id}/publish")
    @Audit(operationType = OperationType.UPDATE, description = "发布站内信", module = "INTERNAL_MESSAGE", response = true, key = "#id")
    @Operation(summary = "发布站内信")
    public boolean publish(@PathVariable String id) {
        return internalMessageService.publish(id);
    }

    @PostMapping("/{id}/revoke")
    @Audit(operationType = OperationType.UPDATE, description = "撤回站内信", module = "INTERNAL_MESSAGE", response = true, key = "#id")
    @Operation(summary = "撤回站内信")
    public boolean revoke(@PathVariable String id) {
        return internalMessageService.revoke(id);
    }

    @GetMapping("/{id}/statistics")
    @Operation(summary = "查询站内信阅读统计")
    public InternalMessageStatistics statistics(@PathVariable String id) {
        return internalMessageService.statistics(id);
    }

    @GetMapping("/inbox")
    @Operation(summary = "我的站内信分页")
    public PageData<InternalMessage> inbox(@RequestParam(defaultValue = "1") long pageNum,
                                           @RequestParam(defaultValue = "10") long pageSize,
                                           @RequestParam(required = false) Integer isRead,
                                           @RequestHeader("X-Username") String username) {
        return PageData.from(internalMessageService.inbox(pageNum, pageSize, username, isRead));
    }

    @GetMapping("/inbox/unread-count")
    @Operation(summary = "我的未读站内信数")
    public long unreadCount(@RequestHeader("X-Username") String username) {
        return internalMessageService.unreadCount(username);
    }

    @GetMapping("/inbox/{messageId}")
    @Operation(summary = "阅读我的站内信")
    public InternalMessage read(@PathVariable String messageId, @RequestHeader("X-Username") String username) {
        return internalMessageService.read(messageId, username);
    }

    @PostMapping("/inbox/read-all")
    @Operation(summary = "我的站内信全部已读")
    public boolean readAll(@RequestHeader("X-Username") String username) {
        return internalMessageService.readAll(username);
    }
}
