package io.github.opensabre.sysadmin.ratelimit.dimension;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 维度提取器接口
 * 定义从HTTP请求中提取特定维度值的抽象
 *
 * <p>实现此接口可以扩展自定义的维度提取逻辑：
 * <ul>
 *   <li>IP维度：提取客户端IP地址</li>
 *   <li>设备维度：提取设备指纹</li>
 *   <li>用户维度：从安全上下文提取用户ID</li>
 *   <li>租户维度：提取租户ID</li>
 *   <li>自定义维度：实现自定义提取逻辑</li>
 * </ul>
 * </p>
 *
 * @author Opensabre Team
 * @since 1.0.0
 */
public interface DimensionExtractor {

    /**
     * 提取维度值
     * 从HTTP请求中提取特定维度的标识值
     *
     * @param request HTTP 请求
     * @return 维度值，如果无法提取则返回null
     */
    String extract(HttpServletRequest request);
}
