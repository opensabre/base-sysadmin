package io.github.opensabre.sysadmin.usage.enums;

import io.github.opensabre.governance.dictionary.DictionaryEnum;
import io.github.opensabre.governance.dictionary.OpenSabreDictionary;

/**
 * 使用量趋势的聚合粒度。
 */
@OpenSabreDictionary(code = "usage_granularity", name = "统计粒度")
public enum UsageGranularity implements DictionaryEnum {
    MINUTE("分钟"),
    HOUR("小时"),
    DAY("天"),
    WEEK("周");

    private final String label;

    UsageGranularity(String label) {
        this.label = label;
    }

    @Override
    public String value() {
        return name();
    }

    @Override
    public String label() {
        return label;
    }
}
