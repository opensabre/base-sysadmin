package io.github.opensabre.sysadmin.internalmessage.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/** 站内信投递与阅读统计。 */
@Data
@AllArgsConstructor
public class InternalMessageStatistics {
    private long recipientCount;
    private long readCount;
}
