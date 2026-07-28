package io.github.opensabre.sysadmin.usage.dao;

import io.github.opensabre.sysadmin.usage.enums.UsageGranularity;
import io.github.opensabre.sysadmin.usage.model.form.UsageBatchSummaryQuery;
import io.github.opensabre.sysadmin.usage.model.form.UsageRankingQuery;
import io.github.opensabre.sysadmin.usage.model.form.UsageSummaryQuery;
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

    public static String summary(Map<String, Object> parameters) {
        UsageSummaryQuery query = (UsageSummaryQuery) parameters.get("query");
        StringBuilder sql = new StringBuilder(selectSummary())
                .append("FROM base_sys_usage_counter_minute ")
                .append("WHERE bucket_start >= #{query.from} AND bucket_start < #{query.to} ");
        appendObjectConditions(sql, query.getObjectType(), query.getObjectId(), query.getUsageEvent());
        return sql.toString();
    }

    public static String summaries(Map<String, Object> parameters) {
        UsageBatchSummaryQuery query = (UsageBatchSummaryQuery) parameters.get("query");
        StringBuilder sql = new StringBuilder("SELECT object_id AS objectId, ")
                .append(summaryColumns())
                .append("FROM base_sys_usage_counter_minute ")
                .append("WHERE bucket_start >= #{query.from} AND bucket_start < #{query.to} ")
                .append("AND object_type = #{query.objectType} AND object_id IN (");
        for (int index = 0; index < query.getObjectIds().size(); index++) {
            if (index > 0) {
                sql.append(", ");
            }
            sql.append("#{query.objectIds[").append(index).append("]}");
        }
        sql.append(") ");
        if (query.getUsageEvent() != null) {
            sql.append("AND usage_event = #{query.usageEvent} ");
        }
        return sql.append("GROUP BY object_id").toString();
    }

    public static String ranking(Map<String, Object> parameters) {
        UsageRankingQuery query = (UsageRankingQuery) parameters.get("query");
        StringBuilder sql = new StringBuilder("SELECT object_type AS objectType, object_id AS objectId, usage_event AS usageEvent, ")
                .append(summaryColumns())
                .append("FROM base_sys_usage_counter_minute ")
                .append("WHERE bucket_start >= #{query.from} AND bucket_start < #{query.to} ");
        appendObjectConditions(sql, query.getObjectType(), null, query.getUsageEvent());
        return sql.append("GROUP BY object_type, object_id, usage_event ")
                .append("ORDER BY attemptCount DESC, objectType ASC, objectId ASC LIMIT #{query.limit}")
                .toString();
    }

    private static String selectSummary() {
        return "SELECT " + summaryColumns();
    }

    private static String summaryColumns() {
        return "COALESCE(SUM(attempt_count), 0) AS attemptCount, "
                + "COALESCE(SUM(success_count), 0) AS successCount, "
                + "COALESCE(SUM(failure_count), 0) AS failureCount ";
    }

    private static void appendObjectConditions(StringBuilder sql, Object objectType, String objectId, Object usageEvent) {
        if (objectType != null) {
            sql.append("AND object_type = #{query.objectType} ");
        }
        if (objectId != null && !objectId.isBlank()) {
            sql.append("AND object_id = #{query.objectId} ");
        }
        if (usageEvent != null) {
            sql.append("AND usage_event = #{query.usageEvent} ");
        }
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
