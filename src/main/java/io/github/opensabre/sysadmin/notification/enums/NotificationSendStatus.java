package io.github.opensabre.sysadmin.notification.enums;
import io.github.opensabre.governance.dictionary.DictionaryEnum;
import io.github.opensabre.governance.dictionary.OpenSabreDictionary;

@OpenSabreDictionary(code = "notification_send_status", name = "通知发送状态")
public enum NotificationSendStatus implements DictionaryEnum {
    SUCCESS("成功", "S"), FAILED("失败", "D");
    private final String label; private final String tagType;
    NotificationSendStatus(String label, String tagType) { this.label = label; this.tagType = tagType; }
    public String value() { return name(); } public String label() { return label; }
    public String tagType() { return tagType; }
}
