package io.github.opensabre.sysadmin.common.utils;

import cn.hutool.core.util.HashUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            // Get the first IP if there are multiple in the X-Forwarded-For header
            int index = xForwardedFor.indexOf(",");
            if (index != -1) {
                return xForwardedFor.substring(0, index).trim();
            } else {
                return xForwardedFor.trim();
            }
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp.trim();
        }

        return request.getRemoteAddr();
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