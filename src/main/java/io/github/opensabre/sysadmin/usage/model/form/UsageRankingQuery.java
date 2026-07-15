package io.github.opensabre.sysadmin.usage.model.form;

import io.github.opensabre.sysadmin.usage.enums.UsageEvent;
import io.github.opensabre.sysadmin.usage.enums.UsageObjectType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 使用量排行榜查询条件。
 */
@Data
public class UsageRankingQuery {
    private LocalDateTime from;
    private LocalDateTime to;
    private UsageObjectType objectType;
    private UsageEvent usageEvent;
    private Integer limit = 20;
}
