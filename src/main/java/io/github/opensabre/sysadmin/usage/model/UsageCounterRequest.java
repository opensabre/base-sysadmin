package io.github.opensabre.sysadmin.usage.model;

import io.github.opensabre.sysadmin.usage.enums.UsageEvent;
import io.github.opensabre.sysadmin.usage.enums.UsageObjectType;
import io.github.opensabre.sysadmin.usage.enums.UsageOutcome;
import lombok.Builder;
import lombok.Value;

/**
 * 写入一次对象使用计数的请求。
 */
@Value
@Builder
public class UsageCounterRequest {
    UsageObjectType objectType;
    String objectId;
    UsageEvent usageEvent;
    UsageOutcome outcome;
}
