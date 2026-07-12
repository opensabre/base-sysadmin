package io.github.opensabre.sysadmin.ratelimit.model.form;

import io.github.opensabre.sysadmin.ratelimit.enums.RateLimitAlgorithmType;
import io.github.opensabre.sysadmin.ratelimit.enums.RateLimitDimension;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RateLimitCheckForm {

    private String sceneCode;

    private String key;

    private String keyPrefix;

    private RateLimitAlgorithmType algorithm;

    private List<RateLimitDimension> dimensions;

    private Map<RateLimitDimension, String> dimensionValues;

    private int maxCount;

    private int period;

    private boolean enabled = true;
}
