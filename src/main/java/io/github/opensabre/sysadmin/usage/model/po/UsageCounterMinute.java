package io.github.opensabre.sysadmin.usage.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.opensabre.persistence.entity.po.BasePo;
import io.github.opensabre.sysadmin.usage.enums.UsageEvent;
import io.github.opensabre.sysadmin.usage.enums.UsageObjectType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 分钟粒度的对象使用量聚合记录。
 */
@Data
@TableName("base_sys_usage_counter_minute")
@EqualsAndHashCode(callSuper = true)
public class UsageCounterMinute extends BasePo {

    private LocalDateTime bucketStart;
    private UsageObjectType objectType;
    private String objectId;
    private UsageEvent usageEvent;
    private Long attemptCount;
    private Long successCount;
    private Long failureCount;
}
