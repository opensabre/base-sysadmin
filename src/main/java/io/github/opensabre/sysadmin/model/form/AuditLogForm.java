package io.github.opensabre.sysadmin.model.form;

import io.github.opensabre.boot.annotations.OperationType;
import io.github.opensabre.common.web.entity.form.BaseForm;
import io.github.opensabre.sysadmin.model.po.AuditLog;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@Schema
@EqualsAndHashCode(callSuper = true)
public class AuditLogForm extends BaseForm<AuditLog> {

    @Schema(title = "操作类型")
    @NotNull(message = "操作类型不能为空")
    private OperationType operationType;

    @Schema(title = "操作时间")
    @NotNull(message = "操作时间不能为空")
    private Date operationTime;

    @Schema(title = "操作人用户名")
    @NotBlank(message = "操作人用户名不能为空")
    private String operatorUsername;

    @Schema(title = "操作描述")
    private String description;

    @Schema(title = "操作模块")
    @NotBlank(message = "操作模块不能为空")
    private String module;

    @Schema(title = "操作IP地址")
    private String clientIp;

    @Schema(title = "操作终端的User-Agent")
    private String userAgent;

    @Schema(title = "请求方法，GET/POST/PUT/DELETE")
    private String requestMethod;

    @Schema(title = "请求URL")
    private String requestUrl;

    @Schema(title = "请求参数报文")
    private String request;

    @Schema(title = "请求返回报文")
    private String response;

    @Schema(title = "错误信息")
    private String errorMessage;

    @Schema(title = "执行时间(毫秒)")
    private Long executionTime;

    @Schema(title = "操作目标关键值")
    private String targetKey;
}