package io.github.opensabre.sysadmin.ratelimit.aspect;

import io.github.opensabre.sysadmin.common.utils.HttpUtils;
import io.github.opensabre.sysadmin.ratelimit.annotations.RateLimit;
import io.github.opensabre.sysadmin.ratelimit.exception.RateLimitExceededException;
import io.github.opensabre.sysadmin.ratelimit.model.RateLimitConfig;
import io.github.opensabre.sysadmin.ratelimit.model.RateLimitResult;
import io.github.opensabre.sysadmin.ratelimit.service.IRateLimitService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Arrays;

/**
 * 限次切面
 * 通过AOP拦截@RateLimit注解的方法，自动进行限次检查
 *
 * <p>功能：
 * <ul>
 *   <li>解析@RateLimit注解配置</li>
 *   <li>支持SpEL表达式提取限次Key</li>
 *   <li>执行限次检查</li>
 *   <li>设置响应头（剩余次数、重置时间）</li>
 *   <li>超限时抛出异常</li>
 * </ul>
 * </p>
 *
 * @author Opensabre Team
 * @since 1.0.0
 */
@Slf4j
@Aspect
public class RateLimitAspect {

    @Resource
    private IRateLimitService rateLimitService;

    /**
     * el表达式 parser
     */
    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 环绕通知
     * 拦截带有@RateLimit注解的方法
     *
     * @param joinPoint 连接点
     * @param rateLimit 限次注解
     * @return 方法执行结果
     * @throws Throwable 方法执行异常
     */
    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        // 检查是否启用限次
        if (!rateLimit.enabled()) {
            return joinPoint.proceed();
        }
        // 执行限次检查
        RateLimitResult result = rateLimitService.checkLimit(buildConfig(joinPoint, rateLimit));
        // 处理检查结果
        if (!result.isAllowed()) {
            handleRateLimitExceeded(result, rateLimit.message());
            throw new RateLimitExceededException(rateLimit.message(), result.getRemaining(), result.getResetTime());
        }
        // 设置响应头
        setResponseHeaders(result, rateLimit.showRemaining());
        // 执行目标方法
        return joinPoint.proceed();
    }

    /**
     * 构建限次配置
     * 从注解和上下文中构建完整的限次配置
     *
     * @param joinPoint 连接点
     * @param rateLimit 限次注解
     * @return 限次配置
     */
    private RateLimitConfig buildConfig(ProceedingJoinPoint joinPoint, RateLimit rateLimit) {
        // 解析SpEL表达式获取业务Key
        String businessKey = parseKey(joinPoint, rateLimit.key());
        return RateLimitConfig.builder()
                .key(businessKey)
                .keyPrefix(rateLimit.keyPrefix())
                .algorithm(rateLimit.algorithm())
                .dimensions(Arrays.asList(rateLimit.dimensions()))
                .maxCount(rateLimit.maxCount())
                .period(rateLimit.period())
                .enabled(rateLimit.enabled())
                .message(rateLimit.message())
                .showRemaining(rateLimit.showRemaining())
                .customDimensionExtractor(rateLimit.customDimensionExtractor())
                .build();
    }

    /**
     * 解析SpEL表达式
     * 从方法参数中提取限次Key
     *
     * @param joinPoint     连接点
     * @param keyExpression SpEL表达式
     * @return 解析后的Key值
     */
    private String parseKey(ProceedingJoinPoint joinPoint, String keyExpression) {
        if (StringUtils.isBlank(keyExpression)) {
            return Strings.EMPTY;
        }

        StandardEvaluationContext context = new StandardEvaluationContext();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        // 设置方法参数到SpEL上下文
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        try {
            Expression expression = parser.parseExpression(keyExpression);
            Object value = expression.getValue(context);

            if (value != null) {
                return value.toString();
            }
            return Strings.EMPTY;

        } catch (Exception e) {
            log.warn("Failed to parse rate limit key expression: {}", keyExpression, e);
            return Strings.EMPTY;
        }
    }

    /**
     * 处理限次超限
     * 记录限次超限日志
     *
     * @param result  检查结果
     * @param message 错误消息
     */
    private void handleRateLimitExceeded(RateLimitResult result, String message) {
        log.warn("Rate limit exceeded: key={}, current={}, max={}, message={}",
                result.getKey(), result.getCurrentCount(), result.getMaxCount(), message);
        // 可以在这里集成监控系统，发送限次告警
        // eventPublisher.publishEvent(new RateLimitExceededEvent(result));
    }

    /**
     * 设置响应头
     * 在HTTP响应中添加限次相关的响应头
     *
     * @param result        检查结果
     * @param showRemaining 是否显示剩余次数
     */
    private void setResponseHeaders(RateLimitResult result, boolean showRemaining) {
        if (!showRemaining) {
            return;
        }
        HttpServletResponse response = HttpUtils.getCurrentResponse();
        if (response != null) {
            response.setHeader("X-RateLimit-Limit", String.valueOf(result.getMaxCount()));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(result.getRemaining()));
            response.setHeader("X-RateLimit-Reset", String.valueOf(result.getResetTime()));
        }
    }
}
