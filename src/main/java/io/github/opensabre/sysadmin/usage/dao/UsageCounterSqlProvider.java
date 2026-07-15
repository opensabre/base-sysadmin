package io.github.opensabre.sysadmin.usage.dao;

import io.github.opensabre.sysadmin.usage.enums.UsageGranularity;
import io.github.opensabre.sysadmin.usage.model.form.UsageTrendQuery;

import java.util.Map;

/**
 * 使用量聚合查询 SQL 提供器。
 */
public final class UsageCounterSqlProvider {

    private UsageCounterSqlProvider() {
    }

    public static String trend(Map<String, Object> parameters) {
        UsageTrendQuery query = (UsageTrendQuery) parameters.get("query");
        String bucketExpression = bucketExpression(query.getGranularity());
        StringBuilder sql = new StringBuilder("SELECT ")
                .append(bucketExpression).append(" AS bucketStart, ")
                .append("SUM(attempt_count) AS attemptCount, ")
                .append("SUM(success_count) AS successCount, ")
                .append("SUM(failure_count) AS failureCount ")
                .append("FROM base_sys_usage_counter_minute ")
                .append("WHERE bucket_start >= #{query.from} AND bucket_start < #{query.to} ");
        if (query.getObjectType() != null) {
            sql.append("AND object_type = #{query.objectType} ");
        }
        if (query.getObjectId() != null && !query.getObjectId().isBlank()) {
            sql.append("AND object_id = #{query.objectId} ");
        }
        if (query.getUsageEvent() != null) {
            sql.append("AND usage_event = #{query.usageEvent} ");
        }
        return sql.append("GROUP BY ").append(bucketExpression).append(" ORDER BY bucketStart ASC").toString();
    }

    private static String bucketExpression(UsageGranularity granularity) {
        if (granularity == null) {
            return "DATE_FORMAT(bucket_start, '%Y-%m-%d %H:%i:00')";
        }
        return switch (granularity) {
            case MINUTE -> "DATE_FORMAT(bucket_start, '%Y-%m-%d %H:%i:00')";
            case HOUR -> "DATE_FORMAT(bucket_start, '%Y-%m-%d %H:00:00')";
            case DAY -> "DATE_FORMAT(bucket_start, '%Y-%m-%d 00:00:00')";
            case WEEK -> "DATE_FORMAT(DATE_SUB(DATE(bucket_start), INTERVAL WEEKDAY(bucket_start) DAY), '%Y-%m-%d 00:00:00')";
        };
    }
}
