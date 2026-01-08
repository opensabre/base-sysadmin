package io.github.opensabre.sysadmin.captcha.service;

import io.github.opensabre.sysadmin.captcha.enums.BusinessScenario;
import io.github.opensabre.sysadmin.captcha.model.po.ClientInfo;
import io.github.opensabre.sysadmin.captcha.model.po.CaptchaInfo;

/**
 * Base interface for captcha implementations
 */
public interface ICaptchaGenerator {

    /**
     * Generate a captcha
     *
     * @param businessKey Business key
     * @param scenario   Business Scenario
     * @param clientInfo Additional clientInfo
     * @return CaptchaInfo containing captcha information
     */
    CaptchaInfo generate(String businessKey, BusinessScenario scenario, ClientInfo clientInfo);

    /**
     * Get the type of captcha this generator handles
     *
     * @return Captcha type
     */
    String getType();
}