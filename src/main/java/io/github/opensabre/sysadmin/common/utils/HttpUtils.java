package io.github.opensabre.sysadmin.common.utils;

import cn.hutool.core.util.HashUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class HttpUtils {
    private HttpUtils() {
    }

    /**
     * Get client IP address
     *
     * @param request http request
     * @return client IP address
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        String xForwardedIp = normalizeIp(StringUtils.substringBefore(request.getHeader("X-Forwarded-For"), ","));
        String xRealIp = normalizeIp(request.getHeader("X-Real-IP"));
        return StringUtils.defaultIfBlank(StringUtils.defaultIfBlank(xForwardedIp, xRealIp), request.getRemoteAddr());
    }

    private static String normalizeIp(String ip) {
        String trimmedIp = StringUtils.trimToNull(ip);
        return "unknown".equalsIgnoreCase(trimmedIp) ? null : trimmedIp;
    }

    /**
     * Gen device id
     *
     * @param request http request
     * @return device id
     */
    public static String getDeviceId(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        return String.valueOf(HashUtil.fnvHash(userAgent + xForwardedFor));
    }

    /**
     * 获取当前HTTP请求
     */
    public static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }


    /**
     * 获取当前HTTP请求
     */
    public static HttpServletResponse getCurrentResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getResponse() : null;
    }
}
