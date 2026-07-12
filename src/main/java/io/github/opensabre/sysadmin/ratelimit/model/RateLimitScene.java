package io.github.opensabre.sysadmin.ratelimit.model;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.opensabre.persistence.entity.po.BasePo;
import io.github.opensabre.sysadmin.ratelimit.enums.RateLimitAlgorithmType;
import io.github.opensabre.sysadmin.ratelimit.enums.RateLimitDimension;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 限次场景配置。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("base_sys_ratelimit_scene")
@EqualsAndHashCode(callSuper = true)
public class RateLimitScene extends BasePo {

    private String sceneCode;

    private String sceneName;

    private RateLimitAlgorithmType algorithm;

    private String dimensions;

    private String keyPrefix;

    private int maxCount;

    private int period;

    private boolean enabled;

    private String description;

    public List<RateLimitDimension> getDimensionList() {
        if (dimensions == null || dimensions.isBlank()) {
            return List.of();
        }
        return Arrays.stream(dimensions.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .map(RateLimitDimension::valueOf)
                .collect(Collectors.toList());
    }

    public RateLimitConfig toConfig() {
        return RateLimitConfig.builder()
                .algorithm(algorithm == null ? RateLimitAlgorithmType.COUNTER : algorithm)
                .dimensions(getDimensionList())
                .keyPrefix(keyPrefix)
                .maxCount(maxCount)
                .period(period)
                .enabled(enabled)
                .build();
    }
}
