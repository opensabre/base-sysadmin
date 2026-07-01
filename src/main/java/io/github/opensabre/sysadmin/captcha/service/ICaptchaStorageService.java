package io.github.opensabre.sysadmin.captcha.service;

import io.github.opensabre.sysadmin.captcha.enums.BusinessScenario;
import io.github.opensabre.sysadmin.captcha.model.po.CaptchaInfo;
import io.github.opensabre.sysadmin.captcha.model.po.CaptchaScene;

/**
 * Interface for captcha storage operations
 */
public interface ICaptchaStorageService {

    /**
     * Save captcha entity
     *
     * @param captchaInfo      Captcha entity to save
     * @param businessScenario Business Scenario
     */
    void save(CaptchaInfo captchaInfo, CaptchaScene businessScenario);

    /**
     * Get captcha entity by sceneId and sendId
     *
     * @param captchaKey       Captcha Key
     * @param businessScenario Business Scenario
     * @return Captcha entity or null if not found
     */
    CaptchaInfo get(String captchaKey, CaptchaScene businessScenario);

    /**
     * Delete captcha entity
     *
     * @param captchaKey       Captcha Key
     * @param businessScenario Business Scenario
     */
    void delete(String captchaKey, CaptchaScene businessScenario);

    /**
     * Update verification status
     *
     * @param captchaKey       Captcha Key
     * @param businessScenario Business Scenario
     * @param verified         Verification status
     */
    void updateVerified(String captchaKey, CaptchaScene businessScenario, boolean verified);

    /**
     * Increment attempts count
     *
     * @param captchaKey       Captcha Key
     * @param businessScenario Business Scenario
     */
    void incrementAttempts(String captchaKey, CaptchaScene businessScenario);
}
