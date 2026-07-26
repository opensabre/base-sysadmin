package io.github.opensabre.sysadmin.errorcatalog.model;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.opensabre.persistence.entity.po.BasePo;
import io.github.opensabre.governance.errorcatalog.ErrorCatalogScope;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** Centrally managed declaration of a framework or application error code. */
@Data
@TableName("base_sys_error_catalog")
@EqualsAndHashCode(callSuper = true)
public class ErrorCatalog extends BasePo {
    private String code;
    private String defaultMessage;
    private String sourceApplication;
    private String owner;
    private ErrorCatalogScope scope;
    private String module;
    private String sourceVersion;
    private Integer httpStatus;
    private boolean publicVisible;
    private boolean deprecated;
    private String description;
}
