package io.github.opensabre.sysadmin.usage.service;

import io.github.opensabre.sysadmin.usage.enums.UsageEvent;
import io.github.opensabre.sysadmin.usage.enums.UsageObjectType;
import io.github.opensabre.sysadmin.usage.enums.UsageOutcome;
import io.github.opensabre.sysadmin.usage.model.UsageCounterRequest;
import io.github.opensabre.sysadmin.usage.model.form.UsageTrendQuery;
import io.github.opensabre.sysadmin.usage.model.form.UsageBatchSummaryQuery;
import io.github.opensabre.sysadmin.usage.model.form.UsageRankingQuery;
import io.github.opensabre.sysadmin.usage.model.form.UsageSummaryQuery;
import io.github.opensabre.sysadmin.usage.model.vo.UsageObjectSummaryVo;
import io.github.opensabre.sysadmin.usage.model.vo.UsageRankingVo;
import io.github.opensabre.sysadmin.usage.model.vo.UsageSummaryVo;
import io.github.opensabre.sysadmin.usage.model.vo.UsageTrendVo;

import java.util.List;

/**
 * 通用对象使用计次服务。
 */
public interface IUsageCounterService {

    void record(UsageCounterRequest request);

    default void record(UsageObjectType objectType, String objectId, UsageEvent usageEvent, UsageOutcome outcome) {
        record(UsageCounterRequest.builder()
                .objectType(objectType)
                .objectId(objectId)
                .usageEvent(usageEvent)
                .outcome(outcome)
                .build());
    }

    List<UsageTrendVo> trend(UsageTrendQuery query);

    UsageSummaryVo summary(UsageSummaryQuery query);

    List<UsageObjectSummaryVo> summaries(UsageBatchSummaryQuery query);

    List<UsageRankingVo> ranking(UsageRankingQuery query);
}
