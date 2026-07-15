package io.github.opensabre.sysadmin.usage.event;

import io.github.opensabre.sysadmin.usage.enums.UsageEvent;
import io.github.opensabre.sysadmin.usage.enums.UsageObjectType;
import io.github.opensabre.sysadmin.usage.enums.UsageOutcome;

import java.time.LocalDateTime;

/**
 * 已被业务线程受理、等待异步落库的对象使用计次事件。
 */
public record UsageCounterEvent(LocalDateTime bucketStart,
                                UsageObjectType objectType,
                                String objectId,
                                UsageEvent usageEvent,
                                UsageOutcome outcome) {
}
