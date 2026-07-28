package io.github.opensabre.sysadmin.usage.model;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.opensabre.persistence.entity.po.BasePo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** 已登记且允许写入统计桶的计次场景。 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("base_sys_usage_scene")
@EqualsAndHashCode(callSuper = true)
public class UsageScene extends BasePo {
    private String objectType;
    private String objectId;
    private String usageEvent;
    private String sceneName;
    private String sourceApp;
    private boolean enabled;
    private String description;
}
