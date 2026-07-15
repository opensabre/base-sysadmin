package io.github.opensabre.sysadmin.usage.event;

import io.github.opensabre.sysadmin.usage.dao.UsageCounterMapper;
import io.github.opensabre.sysadmin.usage.enums.UsageOutcome;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;

import java.util.UUID;

/**
 * 异步消费对象使用计次事件并原子更新分钟聚合表。
 */
@Slf4j
@Component
public class UsageCounterEventHandler {

    private final UsageCounterMapper usageCounterMapper;

    public UsageCounterEventHandler(UsageCounterMapper usageCounterMapper) {
        this.usageCounterMapper = usageCounterMapper;
    }

    @Async("usageCounterTaskExecutor")
    @EventListener
    public void handle(UsageCounterEvent event) {
        long attemptDelta = event.outcome() == UsageOutcome.ATTEMPT ? 1 : 0;
        long successDelta = event.outcome() == UsageOutcome.SUCCESS ? 1 : 0;
        long failureDelta = event.outcome() == UsageOutcome.FAILURE ? 1 : 0;
        try {
            usageCounterMapper.increment(newId(), event.bucketStart(), event.objectType().name(), event.objectId(),
                    event.usageEvent().name(), attemptDelta, successDelta, failureDelta);
        } catch (Exception exception) {
            // 异步统计失败只影响观测数据，不能反向影响已完成的主营业务。
            log.error("Failed to persist usage counter event: objectType={}, objectId={}, event={}, outcome={}",
                    event.objectType(), event.objectId(), event.usageEvent(), event.outcome(), exception);
        }
    }

    private String newId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
