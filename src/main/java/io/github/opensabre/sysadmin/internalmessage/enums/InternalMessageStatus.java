package io.github.opensabre.sysadmin.internalmessage.enums;
import io.github.opensabre.governance.dictionary.DictionaryEnum;
import io.github.opensabre.governance.dictionary.OpenSabreDictionary;

/** 站内信发布状态。 */
@OpenSabreDictionary(code = "internal_message_status", name = "站内信状态")
public enum InternalMessageStatus implements DictionaryEnum {
    DRAFT("草稿", "I"), PUBLISHED("已发布", "S"), REVOKED("已撤回", "W");
    private final String label; private final String tagType;
    InternalMessageStatus(String label, String tagType) { this.label = label; this.tagType = tagType; }
    public String value() { return name(); } public String label() { return label; }
    public String tagType() { return tagType; }
}
