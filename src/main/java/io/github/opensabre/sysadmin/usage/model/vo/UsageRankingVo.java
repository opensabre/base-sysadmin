package io.github.opensabre.sysadmin.usage.model.vo;

import io.github.opensabre.sysadmin.usage.enums.UsageEvent;
import io.github.opensabre.sysadmin.usage.enums.UsageObjectType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 使用量排行榜数据。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UsageRankingVo extends UsageSummaryVo {
    private UsageObjectType objectType;
    private String objectId;
    private UsageEvent usageEvent;
}
