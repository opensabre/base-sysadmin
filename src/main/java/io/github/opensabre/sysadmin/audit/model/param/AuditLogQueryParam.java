package io.github.opensabre.sysadmin.audit.model.param;

import io.github.opensabre.boot.annotations.OperationType;
import io.github.opensabre.persistence.entity.param.BaseParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 审计日志查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuditLogQueryParam extends BaseParam {

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