package io.github.opensabre.sysadmin.usage.model.form;

import io.github.opensabre.sysadmin.usage.enums.UsageEvent;
import io.github.opensabre.sysadmin.usage.enums.UsageObjectType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 多个业务对象的使用量汇总查询条件。
 */
@Data
public class UsageBatchSummaryQuery {
    @NotNull
    private LocalDateTime from;
    @NotNull
    private LocalDateTime to;
    @NotNull
    private UsageObjectType objectType;
    @NotEmpty
    @Size(max = 100)
    private List<String> objectIds;
    private UsageEvent usageEvent;
}
