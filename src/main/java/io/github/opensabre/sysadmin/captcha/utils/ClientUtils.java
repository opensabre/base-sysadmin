package io.github.opensabre.sysadmin.captcha.utils;

import cn.hutool.core.util.HashUtil;
import io.github.opensabre.sysadmin.captcha.enums.BusinessScenario;
import io.github.opensabre.sysadmin.captcha.model.po.CaptchaScene;
import io.github.opensabre.sysadmin.captcha.model.po.ClientInfo;
import io.github.opensabre.webmvc.util.HttpUtils;
import jakarta.servlet.http.HttpServletRequest;

public class ClientUtils {
    private ClientUtils() {
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

    public static String genBusinessId(String businessKey, CaptchaScene scenario) {
        return String.valueOf(HashUtil.fnvHash(businessKey + scenario.getSceneCode() + scenario.getCaptchaType().getCode()));
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
        return new ClientInfo(genBusinessId(businessKey, scenario), HttpUtils.getClientIpAddress(request), HttpUtils.getDeviceId(request));
    }

    public static ClientInfo getClientInfo(HttpServletRequest request, String businessKey, CaptchaScene scenario) {
        return new ClientInfo(genBusinessId(businessKey, scenario), HttpUtils.getClientIpAddress(request), HttpUtils.getDeviceId(request));
    }
}
