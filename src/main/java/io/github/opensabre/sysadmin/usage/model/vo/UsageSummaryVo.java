package io.github.opensabre.sysadmin.usage.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 使用量汇总数据。
 */
@Data
public class UsageSummaryVo {
    private Long attemptCount;
    private Long successCount;
    private Long failureCount;
    private BigDecimal successRate;
}
