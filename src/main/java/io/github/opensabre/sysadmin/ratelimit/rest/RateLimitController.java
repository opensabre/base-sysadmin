package io.github.opensabre.sysadmin.ratelimit.rest;

import cn.hutool.core.collection.CollectionUtil;
import io.github.opensabre.sysadmin.ratelimit.enums.RateLimitAlgorithmType;
import io.github.opensabre.sysadmin.ratelimit.enums.RateLimitDimension;
import io.github.opensabre.sysadmin.ratelimit.model.RateLimitConfig;
import io.github.opensabre.sysadmin.ratelimit.model.RateLimitResult;
import io.github.opensabre.sysadmin.ratelimit.model.RateLimitScene;
import io.github.opensabre.sysadmin.ratelimit.model.form.RateLimitCheckForm;
import io.github.opensabre.sysadmin.ratelimit.model.vo.RateLimitCheckVo;
import io.github.opensabre.sysadmin.ratelimit.service.IRateLimitSceneService;
import io.github.opensabre.sysadmin.ratelimit.service.IRateLimitService;
import io.github.opensabre.sysadmin.usage.enums.UsageEvent;
import io.github.opensabre.sysadmin.usage.enums.UsageObjectType;
import io.github.opensabre.sysadmin.usage.enums.UsageOutcome;
import io.github.opensabre.sysadmin.usage.service.IUsageCounterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "限次执行")
@RestController
@RequestMapping("/ratelimit")
public class RateLimitController {

    @Resource
    private IRateLimitService rateLimitService;

    @Resource
    private IRateLimitSceneService rateLimitSceneService;

    @Resource
    private IUsageCounterService usageCounterService;

    @PostMapping("/check")
    @Operation(summary = "检查限次", description = "供 opensabre-framework starter 远程调用")
    public RateLimitCheckVo check(@RequestBody RateLimitCheckForm form) {
        RateLimitConfig config = buildConfig(form);
        boolean hasScene = StringUtils.isNotBlank(form.getSceneCode());
        if (hasScene) {
            usageCounterService.record(UsageObjectType.RATE_LIMIT_SCENE, form.getSceneCode(),
                    UsageEvent.RATE_LIMIT_CHECK, UsageOutcome.ATTEMPT);
        }
        try {
            RateLimitResult result = rateLimitService.checkLimit(config);
            if (hasScene) {
                usageCounterService.record(UsageObjectType.RATE_LIMIT_SCENE, form.getSceneCode(), UsageEvent.RATE_LIMIT_CHECK,
                        result.isAllowed() ? UsageOutcome.SUCCESS : UsageOutcome.FAILURE);
            }
            return RateLimitCheckVo.from(result);
        } catch (RuntimeException exception) {
            if (hasScene) {
                usageCounterService.record(UsageObjectType.RATE_LIMIT_SCENE, form.getSceneCode(),
                        UsageEvent.RATE_LIMIT_CHECK, UsageOutcome.FAILURE);
            }
            throw exception;
        }
    }

    private RateLimitConfig buildConfig(RateLimitCheckForm form) {
        RateLimitConfig config = StringUtils.isBlank(form.getSceneCode())
                ? buildAnnotationConfig(form)
                : buildSceneConfig(form);
        config.setKey(resolveKey(form, config.getDimensions()));
        config.setEnabled(form.isEnabled() && config.isEnabled());
        return config;
    }

    private RateLimitConfig buildSceneConfig(RateLimitCheckForm form) {
        RateLimitScene scene = rateLimitSceneService.getByCode(form.getSceneCode());
        if (scene == null) {
            throw new IllegalArgumentException("Rate limit scene not found: " + form.getSceneCode());
        }
        if (!scene.isEnabled()) {
            throw new IllegalStateException("Rate limit scene disabled: " + form.getSceneCode());
        }
        RateLimitConfig config = scene.toConfig();
        if (StringUtils.isNotBlank(form.getKeyPrefix())) {
            config.setKeyPrefix(form.getKeyPrefix());
        }
        return config;
    }

    private RateLimitConfig buildAnnotationConfig(RateLimitCheckForm form) {
        return RateLimitConfig.builder()
                .keyPrefix(form.getKeyPrefix())
                .algorithm(form.getAlgorithm() == null ? RateLimitAlgorithmType.COUNTER : form.getAlgorithm())
                .dimensions(CollectionUtil.isEmpty(form.getDimensions()) ? List.of(RateLimitDimension.IP) : form.getDimensions())
                .maxCount(form.getMaxCount())
                .period(form.getPeriod())
                .enabled(form.isEnabled())
                .build();
    }

    private String resolveKey(RateLimitCheckForm form, List<RateLimitDimension> dimensions) {
        if (StringUtils.isNotBlank(form.getKey())) {
            return form.getKey();
        }
        if (CollectionUtil.isEmpty(dimensions)) {
            throw new IllegalArgumentException("Rate limit dimensions must not be empty when key is not configured");
        }
        Map<RateLimitDimension, String> values = form.getDimensionValues();
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Rate limit dimension values must not be empty when key is not configured");
        }
        StringBuilder key = new StringBuilder();
        for (RateLimitDimension dimension : dimensions) {
            String value = values.get(dimension);
            if (StringUtils.isBlank(value)) {
                continue;
            }
            key.append(dimension.getCode()).append(":").append(value).append(":");
        }
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Rate limit dimension value must not be empty");
        }
        key.deleteCharAt(key.length() - 1);
        return key.toString();
    }
}
