package io.github.opensabre.sysadmin.usage.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 指定业务对象的使用量汇总数据。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UsageObjectSummaryVo extends UsageSummaryVo {
    private String objectId;
}
