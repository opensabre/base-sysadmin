package io.github.opensabre.sysadmin.ratelimit.dimension;

import io.github.opensabre.webmvc.util.HttpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * IP维度提取器
 * 从HTTP请求中提取客户端IP地址
 *
 * <p>支持以下IP提取优先级：
 * <ol>
 *   <li>X-Forwarded-For: 代理服务器的转发IP</li>
 *   <li>X-Real-IP: Nginx等反向代理的真实IP</li>
 *   <li>RemoteAddr: 直接连接的客户端IP</li>
 * </ol>
 * </p>
 *
 * @author Opensabre Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class IpDimensionExtractor implements DimensionExtractor {

    /**
     * 提取客户端IP地址
     *
     * @param request HTTP 请求
     * @return IP地址，如果无法提取则返回null
     */
    @Override
    public String extract(HttpServletRequest request) {
        // 复用 HttpUtils 中的方法
        return HttpUtils.getClientIpAddress(request);
    }
}
