package io.github.opensabre.sysadmin.usage.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.opensabre.sysadmin.usage.model.form.UsageBatchSummaryQuery;
import io.github.opensabre.sysadmin.usage.model.form.UsageRankingQuery;
import io.github.opensabre.sysadmin.usage.model.form.UsageSummaryQuery;
import io.github.opensabre.sysadmin.usage.model.form.UsageTrendQuery;
import io.github.opensabre.sysadmin.usage.model.po.UsageCounterMinute;
import io.github.opensabre.sysadmin.usage.model.vo.UsageObjectSummaryVo;
import io.github.opensabre.sysadmin.usage.model.vo.UsageRankingVo;
import io.github.opensabre.sysadmin.usage.model.vo.UsageSummaryVo;
import io.github.opensabre.sysadmin.usage.model.vo.UsageTrendVo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对象使用量持久化接口。
 */
@Mapper
public interface UsageCounterMapper extends BaseMapper<UsageCounterMinute> {

    @Insert("""
            INSERT INTO base_sys_usage_counter_minute
              (id, bucket_start, object_type, object_id, usage_event, attempt_count, success_count, failure_count, created_by, updated_by)
            VALUES
              (#{id}, #{bucketStart}, #{objectType}, #{objectId}, #{usageEvent}, #{attemptDelta}, #{successDelta}, #{failureDelta}, 'system', 'system')
            ON DUPLICATE KEY UPDATE
              attempt_count = attempt_count + VALUES(attempt_count),
              success_count = success_count + VALUES(success_count),
              failure_count = failure_count + VALUES(failure_count),
              updated_by = 'system',
              updated_time = CURRENT_TIMESTAMP
            """)
    int increment(@Param("id") String id,
                  @Param("bucketStart") LocalDateTime bucketStart,
                  @Param("objectType") String objectType,
                  @Param("objectId") String objectId,
                  @Param("usageEvent") String usageEvent,
                  @Param("attemptDelta") long attemptDelta,
                  @Param("successDelta") long successDelta,
                  @Param("failureDelta") long failureDelta);

    @SelectProvider(type = UsageCounterSqlProvider.class, method = "trend")
    List<UsageTrendVo> trend(@Param("query") UsageTrendQuery query);

    @SelectProvider(type = UsageCounterSqlProvider.class, method = "summary")
    UsageSummaryVo summary(@Param("query") UsageSummaryQuery query);

    @SelectProvider(type = UsageCounterSqlProvider.class, method = "summaries")
    List<UsageObjectSummaryVo> summaries(@Param("query") UsageBatchSummaryQuery query);

    @SelectProvider(type = UsageCounterSqlProvider.class, method = "ranking")
    List<UsageRankingVo> ranking(@Param("query") UsageRankingQuery query);
}
