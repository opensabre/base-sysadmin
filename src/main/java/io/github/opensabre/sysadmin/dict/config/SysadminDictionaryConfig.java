package io.github.opensabre.sysadmin.dict.config;

import io.github.opensabre.governance.dictionary.DictionaryDefinition;
import io.github.opensabre.governance.dictionary.DictionaryProvider;
import io.github.opensabre.sysadmin.usage.enums.UsageEvent;
import io.github.opensabre.sysadmin.usage.enums.UsageGranularity;
import io.github.opensabre.sysadmin.usage.enums.UsageObjectType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** 声明 Sysadmin 自有、供管理端复用的枚举字典。 */
@Configuration
public class SysadminDictionaryConfig {

    /**
     * 注册使用统计查询涉及的稳定枚举，避免前端重复维护选项与标签。
     */
    @Bean
    public DictionaryProvider sysadminDictionaryProvider() {
        return DictionaryProvider.of(
                DictionaryDefinition.of("usage_object_type", "使用对象类型", UsageObjectType.values(),
                        UsageObjectType::name, this::usageObjectTypeLabel),
                DictionaryDefinition.of("usage_event", "使用事件", UsageEvent.values(),
                        UsageEvent::name, this::usageEventLabel),
                DictionaryDefinition.of("usage_granularity", "统计粒度", UsageGranularity.values(),
                        UsageGranularity::name, this::usageGranularityLabel));
    }

    private String usageObjectTypeLabel(UsageObjectType value) {
        return switch (value) {
            case CAPTCHA_SCENE -> "验证码场景";
            case RATE_LIMIT_SCENE -> "限次场景";
            case NOTIFICATION_SCENE -> "通知场景";
            case NOTIFICATION_TEMPLATE -> "通知模板";
        };
    }

    private String usageEventLabel(UsageEvent value) {
        return switch (value) {
            case CAPTCHA_GENERATE -> "生成验证码";
            case CAPTCHA_VERIFY -> "校验验证码";
            case RATE_LIMIT_CHECK -> "限次检查";
            case NOTIFICATION_SEND -> "发送通知";
        };
    }

    private String usageGranularityLabel(UsageGranularity value) {
        return switch (value) {
            case MINUTE -> "分钟";
            case HOUR -> "小时";
            case DAY -> "天";
            case WEEK -> "周";
        };
    }
}
