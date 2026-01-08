package io.github.opensabre.sysadmin.captcha.service;

import io.github.opensabre.sysadmin.captcha.enums.BusinessScenario;
import io.github.opensabre.sysadmin.captcha.model.po.ClientInfo;
import io.github.opensabre.sysadmin.captcha.model.vo.CaptchaVo;

public interface ICaptchaService {
    /**
     * Generate a captcha of specified type
     *
     * @param businessKey      Business Key
     * @param businessScenario Business Scenario
     * @param clientInfo       Additional Info (businessId, ip, device info, etc.)
     * @return CaptchaVo containing captcha information
     */
    CaptchaVo generateCaptcha(String businessKey, BusinessScenario businessScenario, ClientInfo clientInfo);

    /**
     * Validate a captcha
     *
     * @param captchaId Captcha identifier
     * @param scenario  Send identifier
     * @param inputCode User input code
     * @return true if valid, false otherwise
     */
    boolean validateCaptcha(String captchaId, BusinessScenario scenario, String inputCode);
}
