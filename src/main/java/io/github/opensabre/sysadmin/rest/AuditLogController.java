package io.github.opensabre.sysadmin.rest;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.opensabre.sysadmin.model.param.AuditLogQueryParam;
import io.github.opensabre.sysadmin.model.form.AuditLogQueryForm;
import io.github.opensabre.sysadmin.model.form.AuditLogForm;
import io.github.opensabre.sysadmin.model.po.AuditLog;
import io.github.opensabre.sysadmin.service.IAuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 审计日志控制器
 */
@Schema(name = "审计日志")
@ApiResponses(
        @ApiResponse(responseCode = "200", description = "处理成功", content = @Content(schema = @Schema(implementation = Object.class)))
)
@Slf4j
@RestController
@RequestMapping("/audit/log")
public class AuditLogController {

    @Resource
    private IAuditLogService auditLogService;

    @Operation(summary = "保存审计日志", description = "保存审计日志")
    @PostMapping
    public boolean save(@Parameter(name = "auditLogForm", description = "保存审订日志表单", required = true) @Valid @RequestBody AuditLogForm auditLogForm) {
        log.debug("auditLogForm:{}", auditLogForm);
        return auditLogService.add(auditLogForm.toPo(AuditLog.class));
    }

    @Operation(summary = "获取审计日志", description = "根据ID获取指定审计日志信息", security = @SecurityRequirement(name = "Authorization"))
    @GetMapping(value = "/{id}")
    public AuditLog get(@Parameter(name = "id", description = "审计日志ID", required = true) @PathVariable String id) {
        log.info("get audit log with id:{}", id);
        return auditLogService.get(id);
    }

    @Operation(summary = "搜索审计日志", description = "根据条件查询审计日志信息")
    @PostMapping(value = "/conditions")
    public IPage<AuditLog> search(@Parameter(description = "审计日志查询参数", required = true) @Valid @RequestBody AuditLogQueryForm auditLogQueryForm) {
        log.debug("search audit logs with query form:{}", auditLogQueryForm);
        return auditLogService.query(auditLogQueryForm.getPage(), auditLogQueryForm.toParam(AuditLogQueryParam.class));
    }

    @Operation(summary = "清理过期审计日志", description = "清理指定天数之前的审计日志")
    @DeleteMapping(value = "/clean/{days}")
    public int cleanExpiredLogs(@Parameter(name = "days", description = "保留天数", required = true) @PathVariable int days) {
        log.info("clean expired audit logs, keep days:{}", days);
        return auditLogService.cleanExpiredLogs(days);
    }
}