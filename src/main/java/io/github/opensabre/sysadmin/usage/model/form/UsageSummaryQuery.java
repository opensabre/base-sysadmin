package io.github.opensabre.sysadmin.usage.model.form;

import io.github.opensabre.sysadmin.usage.enums.UsageEvent;
import io.github.opensabre.sysadmin.usage.enums.UsageObjectType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 使用量汇总查询条件。
 */
@Data
public class UsageSummaryQuery {
    private LocalDateTime from;
    private LocalDateTime to;
    private UsageObjectType objectType;
    private String objectId;
    private UsageEvent usageEvent;
}
