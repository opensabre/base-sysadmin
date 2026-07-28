package io.github.opensabre.sysadmin.usage.event;

import io.github.opensabre.eda.api.EdaEvent;
import io.github.opensabre.eda.api.EdaEventHandler;
import io.github.opensabre.governance.usage.UsageEventTypes;
import io.github.opensabre.governance.usage.UsageOutcome;
import io.github.opensabre.governance.usage.UsageCounterRecorder;
import io.github.opensabre.governance.usage.UsageRecord;
import io.github.opensabre.sysadmin.usage.dao.UsageCounterMapper;
import io.github.opensabre.sysadmin.usage.service.IUsageSceneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

/**
 * 接收治理通用计次事件；HTTP 与 EDA 复用同一异步聚合实现。
 */
@Slf4j
@Component
public class UsageCounterEdaEventHandler implements EdaEventHandler<UsageRecord>, UsageCounterRecorder {

    private static final ZoneId STATISTICS_ZONE = ZoneId.of("Asia/Shanghai");
    private final UsageCounterMapper usageCounterMapper;
    private final IUsageSceneService usageSceneService;

    public UsageCounterEdaEventHandler(UsageCounterMapper usageCounterMapper, IUsageSceneService usageSceneService) {
        this.usageCounterMapper = usageCounterMapper;
        this.usageSceneService = usageSceneService;
    }

    @Override
    public String eventType() {
        return UsageEventTypes.EDA_EVENT_TYPE;
    }

    @Override
    public void handle(EdaEvent<UsageRecord> event) {
        record(event.payload());
    }

    @Async("usageCounterTaskExecutor")
    public void record(UsageRecord record) {
        try {
            if (!usageSceneService.isEnabled(record.objectType(), record.objectId(), record.eventType())) {
                log.warn("Ignore unregistered or disabled usage scene: objectType={}, objectId={}, eventType={}", record.objectType(), record.objectId(), record.eventType());
                return;
            }
            LocalDateTime bucket = LocalDateTime.ofInstant(record.occurredAt(), STATISTICS_ZONE).withSecond(0).withNano(0);
            long attempt = record.outcome() == UsageOutcome.ATTEMPT ? 1 : 0;
            long success = record.outcome() == UsageOutcome.SUCCESS ? 1 : 0;
            long failure = record.outcome() == UsageOutcome.FAILURE ? 1 : 0;
            usageCounterMapper.increment(UUID.randomUUID().toString().replace("-", ""), bucket, record.objectType(),
                    record.objectId(), record.eventType(), attempt, success, failure);
        } catch (Exception exception) {
            log.error("Failed to persist governance usage record: objectType={}, objectId={}, eventType={}",
                    record.objectType(), record.objectId(), record.eventType(), exception);
        }
    }
}
