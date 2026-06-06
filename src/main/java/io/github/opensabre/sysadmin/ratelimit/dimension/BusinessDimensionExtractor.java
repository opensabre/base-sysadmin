package io.github.opensabre.sysadmin.ratelimit.dimension;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 业务维度提取器
 * 用于自定义业务维度的限次
 *
 * <p>该提取器不直接从请求中提取值，
 * 而是通过RateLimit注解的key参数（支持SpEL表达式）获取业务Key
 * </p>
 *
 * <p>使用场景：
 * <ul>
 *   <li>手机号限次</li>
 *   <li>邮箱限次</li>
 *   <li>订单号限次</li>
 *   <li>其他业务标识限次</li>
 * </ul>
 * </p>
 *
 * @author Opensabre Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class BusinessDimensionExtractor implements DimensionExtractor {

    /**
     * 提取业务Key
     * 该提取器仅在RateLimit注解中指定了key参数时使用
     * 实际值由RateLimitAspect通过SpEL表达式解析后传入
     *
     * @param request HTTP 请求
     * @return 业务Key，通常为null（由Aspect处理）
     */
    @Override
    public String extract(HttpServletRequest request) {
        // 该提取器由RateLimitAspect处理，不从请求中直接提取
        // 实际使用时，业务Key通过注解的key参数指定
        return request.getHeader("X-Bid");
    }
}
