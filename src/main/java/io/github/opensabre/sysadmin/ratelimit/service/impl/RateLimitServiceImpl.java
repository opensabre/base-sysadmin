package io.github.opensabre.sysadmin.ratelimit.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import io.github.opensabre.sysadmin.common.utils.HttpUtils;
import io.github.opensabre.sysadmin.ratelimit.algorithm.CounterAlgorithm;
import io.github.opensabre.sysadmin.ratelimit.algorithm.RateLimitAlgorithm;
import io.github.opensabre.sysadmin.ratelimit.config.RateLimitProperties;
import io.github.opensabre.sysadmin.ratelimit.dimension.DimensionExtractor;
import io.github.opensabre.sysadmin.ratelimit.enums.RateLimitAlgorithmType;
import io.github.opensabre.sysadmin.ratelimit.enums.RateLimitDimension;
import io.github.opensabre.sysadmin.ratelimit.model.RateLimitConfig;
import io.github.opensabre.sysadmin.ratelimit.model.RateLimitResult;
import io.github.opensabre.sysadmin.ratelimit.service.IRateLimitService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 限次服务实现
 * 提供编程式限次检查功能
 *
 * @author Opensabre Team
 * @since 1.0.0
 */
@Slf4j
@Service
public class RateLimitServiceImpl implements IRateLimitService {

    @Resource
    private CounterAlgorithm counterAlgorithm;

    @Resource
    private RateLimitProperties properties;

    @Resource
    private ApplicationContext applicationContext;

    /**
     * 维度提取器缓存
     */
    private final Map<RateLimitDimension, DimensionExtractor> extractorCache = new ConcurrentHashMap<>();

    /**
     * 初始化
     * 加载维度提取器和算法实现
     */
    public RateLimitServiceImpl() {
        // 后续通过@PostConstruct初始化
    }

    /**
     * 检查限次（使用默认配置）
     */
    @Override
    public RateLimitResult checkLimit() {
        RateLimitConfig config = RateLimitConfig.builder()
                .algorithm(RateLimitAlgorithmType.COUNTER)
                .dimensions(List.of(RateLimitDimension.IP))
                .maxCount(properties.getDefaultConfig().getMaxCount())
                .period(properties.getDefaultConfig().getPeriod())
                .enabled(true)
                .build();
        return checkLimit(config);
    }

    /**
     * 检查限次（指定维度）
     */
    @Override
    public RateLimitResult checkLimit(List<RateLimitDimension> dimensions) {
        RateLimitConfig config = RateLimitConfig.builder()
                .algorithm(RateLimitAlgorithmType.COUNTER)
                .dimensions(dimensions)
                .maxCount(properties.getDefaultConfig().getMaxCount())
                .period(properties.getDefaultConfig().getPeriod())
                .enabled(true)
                .build();
        return checkLimit(config);
    }

    /**
     * 检查限次（指定所有参数）
     */
    @Override
    public RateLimitResult checkLimit(String key, int maxCount, int period, RateLimitAlgorithmType algorithm) {
        RateLimitConfig config = RateLimitConfig.builder()
                .key(key)
                .algorithm(algorithm)
                .maxCount(maxCount)
                .period(period)
                .enabled(true)
                .build();
        return checkLimit(config);
    }

    /**
     * 检查限次（使用完整配置）
     */
    @Override
    public RateLimitResult checkLimit(RateLimitConfig config) {
        if (config == null || !config.isEnabled()) {
            // 未启用限次，默认允许
            return RateLimitResult.allowed("", 0L, 0L, 0, 0L);
        }
        // 生成限次Key。配置错误不能被fail-open吞掉，否则限流会静默失效。
        String key = generateKey(config);
        try {
            // 获取限次算法（目前只支持固定窗口）
            RateLimitAlgorithm algorithm = getAlgorithm(config.getAlgorithm());
            // 执行限次检查
            return algorithm.checkLimit(key, config.getMaxCount(), config.getPeriod());
        } catch (Exception e) {
            log.error("Failed to check rate limit", e);
            // 发生异常时默认允许通过（fail-open）
            return RateLimitResult.allowed(config.getKey(), 0L, config.getMaxCount(),
                    config.getMaxCount(), System.currentTimeMillis() + config.getPeriod() * 1000L);
        }
    }

    /**
     * 重置限次
     */
    @Override
    public void resetLimit(String key) {
        RateLimitAlgorithm algorithm = getAlgorithm(RateLimitAlgorithmType.COUNTER);
        algorithm.resetLimit(key);
    }

    /**
     * 获取剩余次数
     */
    @Override
    public int getRemaining(String key, int maxCount, int period) {
        RateLimitAlgorithm algorithm = getAlgorithm(RateLimitAlgorithmType.COUNTER);
        return algorithm.getRemaining(key, maxCount, period);
    }

    /**
     * 生成限次Key
     */
    @Override
    public String generateKey(List<RateLimitDimension> dimensions, String keyPrefix) {
        return generateKey(dimensions, keyPrefix, StringUtils.EMPTY);
    }

    private String generateKey(List<RateLimitDimension> dimensions, String keyPrefix, String customDimensionExtractor) {
        if (CollectionUtil.isEmpty(dimensions)) {
            throw new IllegalArgumentException("Rate limit dimensions must not be empty when key is not configured");
        }
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(properties.getKeyPrefix());

        if (StringUtils.isNotBlank(keyPrefix)) {
            keyBuilder.append(keyPrefix).append(":");
        }
        // 获取当前请求
        HttpServletRequest request = HttpUtils.getCurrentRequest();
        if (request == null) {
            throw new IllegalStateException("Rate limit dimension key requires current HTTP request");
        }
        boolean hasDimensionValue = false;
        for (RateLimitDimension dimension : dimensions) {
            String value = getExtractor(dimension, customDimensionExtractor).extract(request);
            if (StringUtils.isNotBlank(value)) {
                keyBuilder.append(dimension.getCode()).append(":").append(value).append(":");
                hasDimensionValue = true;
            }
        }
        if (!hasDimensionValue) {
            throw new IllegalStateException("Rate limit dimension value must not be empty");
        }
        keyBuilder.deleteCharAt(keyBuilder.length() - 1);
        return keyBuilder.toString();
    }

    /**
     * 生成限次Key（使用配置对象）
     */
    private String generateKey(RateLimitConfig config) {
        // 如果配置中指定了key，直接使用
        if (StringUtils.isNotBlank(config.getKey())) {
            return properties.getKeyPrefix() + (config.getKeyPrefix() != null ? config.getKeyPrefix() + ":" : StringUtils.EMPTY) + config.getKey();
        }
        // 否则根据维度生成key
        return generateKey(config.getDimensions(), config.getKeyPrefix(), config.getCustomDimensionExtractor());
    }

    /**
     * 获取维度提取器
     */
    private DimensionExtractor getExtractor(RateLimitDimension dimension, String customDimensionExtractor) {
        if (dimension == RateLimitDimension.CUSTOM && StringUtils.isNotBlank(customDimensionExtractor)) {
            return applicationContext.getBean(customDimensionExtractor, DimensionExtractor.class);
        }
        return extractorCache.computeIfAbsent(dimension, d -> {
            try {
                return applicationContext.getBean(getExtractorBeanName(d), DimensionExtractor.class);
            } catch (NoSuchBeanDefinitionException e) {
                throw new IllegalStateException("Dimension extractor not found for: " + d, e);
            }
        });
    }

    /**
     * 获取维度提取器Bean名称
     */
    private String getExtractorBeanName(RateLimitDimension dimension) {
        return switch (dimension) {
            case IP -> "ipDimensionExtractor";
            case DEVICE -> "deviceDimensionExtractor";
            case BUSINESS -> "businessDimensionExtractor";
            case USER -> "userDimensionExtractor";
            case TENANT -> "tenantDimensionExtractor";
            case CUSTOM -> "customDimensionExtractor";
        };
    }

    /**
     * 获取限次算法
     */
    private RateLimitAlgorithm getAlgorithm(RateLimitAlgorithmType algorithmType) {
        // Phase 1 只支持固定窗口算法
        if (algorithmType == RateLimitAlgorithmType.COUNTER) {
            return counterAlgorithm;
        }
        log.warn("Algorithm not implemented yet: {}, falling back to COUNTER", algorithmType);
        return counterAlgorithm;
    }
}
