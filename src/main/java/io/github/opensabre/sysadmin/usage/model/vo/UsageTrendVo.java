package io.github.opensabre.sysadmin.usage.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 一个统计时间桶内的对象使用量。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UsageTrendVo extends UsageSummaryVo {
    private String bucketStart;
}
