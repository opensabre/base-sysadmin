package io.github.opensabre.sysadmin.model.form;

import io.github.opensabre.boot.annotations.OperationType;
import io.github.opensabre.persistence.entity.form.BaseQueryForm;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import io.github.opensabre.sysadmin.model.param.AuditLogQueryParam;
/**
 * 审计日志查询表单
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuditLogQueryForm extends BaseQueryForm<AuditLogQueryParam> {

    /**
     * 操作类型
     */
    private OperationType operationType;

    /**
     * 操作开始时间
     */
    private LocalDateTime operationStartTime;

    /**
     * 操作结束时间
     */
    private LocalDateTime operationEndTime;

    /**
     * 操作人用户名
     */
    private String operatorUsername;

    /**
     * 操作模块
     */
    private String module;

    /**
     * 操作IP地址
     */
    private String clientIp;

    /**
     * 操作目标ID
     */
    private String targetKey;
}