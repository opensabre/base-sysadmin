package io.github.opensabre.sysadmin.internalmessage.enums;
import io.github.opensabre.governance.dictionary.DictionaryEnum;
import io.github.opensabre.governance.dictionary.OpenSabreDictionary;

/** 站内信收件人范围。 */
@OpenSabreDictionary(code = "internal_message_target_scope", name = "站内信接收范围")
public enum InternalMessageTargetScope implements DictionaryEnum {
    ALL_ACTIVE_USERS("全部启用用户"), USERS("指定用户");
    private final String label; InternalMessageTargetScope(String label) { this.label = label; }
    public String value() { return name(); } public String label() { return label; }
}
