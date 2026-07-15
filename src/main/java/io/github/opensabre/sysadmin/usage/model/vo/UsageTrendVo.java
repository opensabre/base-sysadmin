package io.github.opensabre.sysadmin.usage.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 一个统计时间桶内的对象使用量。
 */
@Data
public class UsageTrendVo {
    private String bucketStart;
    private Long attemptCount;
    private Long successCount;
    private Long failureCount;
    private BigDecimal successRate;
}
