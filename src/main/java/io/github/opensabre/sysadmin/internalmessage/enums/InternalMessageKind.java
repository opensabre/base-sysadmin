package io.github.opensabre.sysadmin.internalmessage.enums;
import io.github.opensabre.governance.dictionary.DictionaryEnum;
import io.github.opensabre.governance.dictionary.OpenSabreDictionary;

/** 站内信业务类型。 */
@OpenSabreDictionary(code = "internal_message_kind", name = "站内信类型")
public enum InternalMessageKind implements DictionaryEnum {
    ANNOUNCEMENT("公告"), NOTIFICATION("通知");
    private final String label; InternalMessageKind(String label) { this.label = label; }
    public String value() { return name(); } public String label() { return label; }
}
