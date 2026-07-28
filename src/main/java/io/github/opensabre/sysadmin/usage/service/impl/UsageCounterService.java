package io.github.opensabre.sysadmin.usage.service.impl;

import io.github.opensabre.sysadmin.usage.dao.UsageCounterMapper;
import io.github.opensabre.sysadmin.usage.service.IUsageSceneService;
import io.github.opensabre.sysadmin.usage.event.UsageCounterEvent;
import io.github.opensabre.sysadmin.usage.model.UsageCounterRequest;
import io.github.opensabre.sysadmin.usage.model.form.UsageTrendQuery;
import io.github.opensabre.sysadmin.usage.model.form.UsageBatchSummaryQuery;
import io.github.opensabre.sysadmin.usage.model.form.UsageRankingQuery;
import io.github.opensabre.sysadmin.usage.model.form.UsageSummaryQuery;
import io.github.opensabre.sysadmin.usage.model.vo.UsageObjectSummaryVo;
import io.github.opensabre.sysadmin.usage.model.vo.UsageRankingVo;
import io.github.opensabre.sysadmin.usage.model.vo.UsageSummaryVo;
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
    private final IUsageSceneService usageSceneService;

    public UsageCounterService(UsageCounterMapper usageCounterMapper, ApplicationEventPublisher eventPublisher, IUsageSceneService usageSceneService) {
        this.usageCounterMapper = usageCounterMapper;
        this.eventPublisher = eventPublisher;
        this.usageSceneService = usageSceneService;
    }

    @Override
    public void record(UsageCounterRequest request) {
        if (!isValid(request)) {
            log.warn("Ignore invalid usage counter request: {}", request);
            return;
        }
        if (!usageSceneService.isEnabled(request.getObjectType().name(), request.getObjectId(), request.getUsageEvent().name())) {
            log.warn("Ignore unregistered or disabled usage scene: objectType={}, objectId={}, eventType={}", request.getObjectType(), request.getObjectId(), request.getUsageEvent());
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

    @Override
    public UsageSummaryVo summary(UsageSummaryQuery query) {
        validateRange(query == null ? null : query.getFrom(), query == null ? null : query.getTo());
        UsageSummaryVo summary = usageCounterMapper.summary(query);
        if (summary == null) {
            summary = new UsageSummaryVo();
            summary.setAttemptCount(0L);
            summary.setSuccessCount(0L);
            summary.setFailureCount(0L);
        }
        fillSuccessRate(summary);
        return summary;
    }

    @Override
    public List<UsageObjectSummaryVo> summaries(UsageBatchSummaryQuery query) {
        validateRange(query == null ? null : query.getFrom(), query == null ? null : query.getTo());
        if (query.getObjectType() == null || query.getObjectIds() == null || query.getObjectIds().isEmpty()
                || query.getObjectIds().size() > 100 || query.getObjectIds().stream().anyMatch(StringUtils::isBlank)) {
            throw new IllegalArgumentException("objectType and 1 to 100 non-blank objectIds are required");
        }
        List<UsageObjectSummaryVo> summaries = usageCounterMapper.summaries(query);
        summaries.forEach(this::fillSuccessRate);
        return summaries;
    }

    @Override
    public List<UsageRankingVo> ranking(UsageRankingQuery query) {
        validateRange(query == null ? null : query.getFrom(), query == null ? null : query.getTo());
        if (query.getLimit() == null || query.getLimit() < 1 || query.getLimit() > 100) {
            throw new IllegalArgumentException("limit must be between 1 and 100");
        }
        List<UsageRankingVo> rankings = usageCounterMapper.ranking(query);
        rankings.forEach(this::fillSuccessRate);
        return rankings;
    }

    private boolean isValid(UsageCounterRequest request) {
        return request != null && request.getObjectType() != null && StringUtils.isNotBlank(request.getObjectId())
                && request.getUsageEvent() != null && request.getOutcome() != null;
    }

    private void validateQuery(UsageTrendQuery query) {
        validateRange(query == null ? null : query.getFrom(), query == null ? null : query.getTo());
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

    private void validateRange(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null || !from.isBefore(to)) {
            throw new IllegalArgumentException("from must be before to");
        }
        if (ChronoUnit.DAYS.between(from, to) > 3L * 366) {
            throw new IllegalArgumentException("query range exceeds three years");
        }
    }

    private void fillSuccessRate(UsageSummaryVo summary) {
        if (summary.getAttemptCount() == null || summary.getAttemptCount() == 0) {
            summary.setSuccessRate(null);
            return;
        }
        long successCount = summary.getSuccessCount() == null ? 0 : summary.getSuccessCount();
        summary.setSuccessRate(BigDecimal.valueOf(successCount)
                .divide(BigDecimal.valueOf(summary.getAttemptCount()), SUCCESS_RATE_SCALE, RoundingMode.HALF_UP));
    }

}
