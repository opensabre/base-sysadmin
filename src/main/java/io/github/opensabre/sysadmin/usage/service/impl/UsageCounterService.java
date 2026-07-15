package io.github.opensabre.sysadmin.usage.service.impl;

import io.github.opensabre.sysadmin.usage.dao.UsageCounterMapper;
import io.github.opensabre.sysadmin.usage.event.UsageCounterEvent;
import io.github.opensabre.sysadmin.usage.model.UsageCounterRequest;
import io.github.opensabre.sysadmin.usage.model.form.UsageTrendQuery;
import io.github.opensabre.sysadmin.usage.model.vo.UsageTrendVo;
import io.github.opensabre.sysadmin.usage.service.IUsageCounterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 基于分钟聚合表的对象使用计次实现。
 */
@Slf4j
@Service
public class UsageCounterService implements IUsageCounterService {

    private static final int SUCCESS_RATE_SCALE = 4;
    private static final ZoneId STATISTICS_ZONE = ZoneId.of("Asia/Shanghai");

    private final UsageCounterMapper usageCounterMapper;
    private final ApplicationEventPublisher eventPublisher;

    public UsageCounterService(UsageCounterMapper usageCounterMapper, ApplicationEventPublisher eventPublisher) {
        this.usageCounterMapper = usageCounterMapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void record(UsageCounterRequest request) {
        if (!isValid(request)) {
            log.warn("Ignore invalid usage counter request: {}", request);
            return;
        }
        try {
            eventPublisher.publishEvent(new UsageCounterEvent(
                    LocalDateTime.now(STATISTICS_ZONE).truncatedTo(ChronoUnit.MINUTES),
                    request.getObjectType(), request.getObjectId(), request.getUsageEvent(), request.getOutcome()));
        } catch (Exception exception) {
            // 线程池拒绝或事件分发故障也不能改变验证码、通知、限次等主营业务的结果。
            log.error("Failed to publish usage counter event: objectType={}, objectId={}, event={}, outcome={}",
                    request.getObjectType(), request.getObjectId(), request.getUsageEvent(), request.getOutcome(), exception);
        }
    }

    @Override
    public List<UsageTrendVo> trend(UsageTrendQuery query) {
        validateQuery(query);
        List<UsageTrendVo> trend = usageCounterMapper.trend(query);
        trend.forEach(this::fillSuccessRate);
        return trend;
    }

    private boolean isValid(UsageCounterRequest request) {
        return request != null && request.getObjectType() != null && StringUtils.isNotBlank(request.getObjectId())
                && request.getUsageEvent() != null && request.getOutcome() != null;
    }

    private void validateQuery(UsageTrendQuery query) {
        if (query == null || query.getFrom() == null || query.getTo() == null || !query.getFrom().isBefore(query.getTo())) {
            throw new IllegalArgumentException("from must be before to");
        }
        if (query.getGranularity() == null) {
            throw new IllegalArgumentException("granularity must not be null");
        }
        long hours = ChronoUnit.HOURS.between(query.getFrom(), query.getTo());
        long maxHours = switch (query.getGranularity()) {
            case MINUTE -> 24;
            case HOUR -> 31L * 24;
            case DAY -> 366L * 24;
            case WEEK -> 3L * 366 * 24;
        };
        if (hours > maxHours) {
            throw new IllegalArgumentException("query range exceeds the maximum for " + query.getGranularity());
        }
    }

    private void fillSuccessRate(UsageTrendVo trend) {
        if (trend.getAttemptCount() == null || trend.getAttemptCount() == 0) {
            trend.setSuccessRate(null);
            return;
        }
        long successCount = trend.getSuccessCount() == null ? 0 : trend.getSuccessCount();
        trend.setSuccessRate(BigDecimal.valueOf(successCount)
                .divide(BigDecimal.valueOf(trend.getAttemptCount()), SUCCESS_RATE_SCALE, RoundingMode.HALF_UP));
    }

}
