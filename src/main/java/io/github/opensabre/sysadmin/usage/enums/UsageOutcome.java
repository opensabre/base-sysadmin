package io.github.opensabre.sysadmin.usage.enums;
import io.github.opensabre.governance.dictionary.DictionaryEnum;
import io.github.opensabre.governance.dictionary.OpenSabreDictionary;

/**
 * 使用事件所处的结果阶段。
 */
@OpenSabreDictionary(code = "usage_outcome", name = "使用结果")
public enum UsageOutcome implements DictionaryEnum {
    ATTEMPT("尝试", "I"), SUCCESS("成功", "S"), FAILURE("失败", "D");
    private final String label; private final String tagType;
    UsageOutcome(String label, String tagType) { this.label = label; this.tagType = tagType; }
    public String value() { return name(); } public String label() { return label; }
    public String tagType() { return tagType; }
}
