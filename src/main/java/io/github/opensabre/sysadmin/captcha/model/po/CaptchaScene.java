package io.github.opensabre.sysadmin.captcha.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.opensabre.sysadmin.captcha.enums.BusinessScenario;
import io.github.opensabre.sysadmin.captcha.enums.CaptchaType;
import io.github.opensabre.persistence.entity.po.BasePo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 验证码场景配置。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("base_sys_captcha_scene")
@EqualsAndHashCode(callSuper = true)
public class CaptchaScene extends BasePo {

    private String sceneCode;

    private String sceneName;

    private CaptchaType captchaType;

    private String templateCode;

    private String description;

    private int captchaLength;

    private int captchaExpireTime;

    private int captchaAttempts;

    private int minInterval;

    private int maxLimitCount;

    private boolean enabled;

    public static CaptchaScene from(BusinessScenario scenario) {
        if (scenario == null) {
            return null;
        }
        return CaptchaScene.builder()
                .sceneCode(scenario.getCode())
                .sceneName(scenario.getDescription())
                .captchaType(scenario.getType())
                .templateCode(scenario.getTemplateCode())
                .description(scenario.getDescription())
                .captchaLength(scenario.getCaptchaLength())
                .captchaExpireTime(scenario.getCaptchaExpireTime())
                .captchaAttempts(scenario.getCaptchaAttempts())
                .minInterval(scenario.getMinInterval())
                .maxLimitCount(scenario.getMaxLimitCount())
                .enabled(true)
                .build();
    }
}
