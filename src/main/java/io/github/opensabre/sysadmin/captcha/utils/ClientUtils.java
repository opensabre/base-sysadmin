package io.github.opensabre.sysadmin.captcha.utils;

import cn.hutool.core.util.HashUtil;
import io.github.opensabre.sysadmin.captcha.enums.BusinessScenario;
import io.github.opensabre.sysadmin.captcha.model.po.ClientInfo;
import jakarta.servlet.http.HttpServletRequest;

public class ClientUtils {
    private ClientUtils() {
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
     * Gen device id
     *
     * @param businessKey Business Key (phone NO. | Email etc.)
     * @param scenario    Business Scenario
     * @return device id
     */
    public static String genBusinessId(String businessKey, BusinessScenario scenario) {
        return String.valueOf(HashUtil.fnvHash(businessKey + scenario.getCode() + scenario.getType().getCode()));
    }

    /**
     * Get client info
     *
     * @param request     http request
     * @param businessKey Business Key (phone NO. | Email etc.)
     * @param scenario    Business Scenario
     * @return client info
     */
    public static ClientInfo getClientInfo(HttpServletRequest request, String businessKey, BusinessScenario scenario) {
        return new ClientInfo(genBusinessId(businessKey, scenario), getClientIpAddress(request), getDeviceId(request));
    }
}