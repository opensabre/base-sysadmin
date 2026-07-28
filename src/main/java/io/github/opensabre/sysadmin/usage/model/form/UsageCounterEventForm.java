package io.github.opensabre.sysadmin.usage.model.form;

import io.github.opensabre.sysadmin.usage.enums.UsageEvent;
import io.github.opensabre.sysadmin.usage.enums.UsageObjectType;
import io.github.opensabre.sysadmin.usage.enums.UsageOutcome;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 外部应用上报对象使用计次的请求。
 */
@Data
public class UsageCounterEventForm {

    @NotNull(message = "对象类型不能为空")
    private UsageObjectType objectType;

    @NotBlank(message = "对象ID不能为空")
    private String objectId;

    @NotNull(message = "使用事件不能为空")
    private UsageEvent usageEvent;

    @NotNull(message = "计次结果不能为空")
    private UsageOutcome outcome;
}
