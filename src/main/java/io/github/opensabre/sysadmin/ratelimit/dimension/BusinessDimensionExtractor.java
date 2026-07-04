package io.github.opensabre.sysadmin.ratelimit.dimension;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 业务维度提取器
 * 用于自定义业务维度的限次
 *
 * <p>业务系统可通过治理 starter 解析业务表达式后传入 key；
 * sysadmin 侧也支持从请求头中读取业务标识。
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
     * 默认从请求头 X-Bid 提取业务Key
     *
     * @param request HTTP 请求
     * @return 业务Key
     */
    @Override
    public String extract(HttpServletRequest request) {
        return request.getHeader("X-Bid");
    }
}
