package io.github.opensabre.sysadmin.ratelimit.model.vo;

import io.github.opensabre.sysadmin.ratelimit.model.RateLimitResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitCheckVo {

    private boolean allowed;

    private int remaining;

    private long resetTime;

    private String errorMessage;

    private String key;

    private long currentCount;

    private long maxCount;

    public static RateLimitCheckVo from(RateLimitResult result) {
        return RateLimitCheckVo.builder()
                .allowed(result.isAllowed())
                .remaining(result.getRemaining())
                .resetTime(result.getResetTime())
                .errorMessage(result.getErrorMessage())
                .key(result.getKey())
                .currentCount(result.getCurrentCount())
                .maxCount(result.getMaxCount())
                .build();
    }
}
