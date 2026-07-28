package io.github.opensabre.sysadmin.usage.model.form;

import io.github.opensabre.sysadmin.usage.enums.UsageEvent;
import io.github.opensabre.sysadmin.usage.enums.UsageGranularity;
import io.github.opensabre.sysadmin.usage.enums.UsageObjectType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 使用量趋势查询条件。
 */
@Data
public class UsageTrendQuery {
    private LocalDateTime from;
    private LocalDateTime to;
    private UsageObjectType objectType;
    private String objectId;
    private UsageEvent usageEvent;
    private UsageGranularity granularity;
}
