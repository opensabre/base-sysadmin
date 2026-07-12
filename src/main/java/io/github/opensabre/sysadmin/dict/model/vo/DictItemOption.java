package io.github.opensabre.sysadmin.dict.model.vo;

import io.github.opensabre.sysadmin.dict.model.po.DictItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 前端字典缓存选项。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictItemOption {

    private String value;

    private String label;

    private String tagType;

    public static DictItemOption from(DictItem item) {
        if (item == null) {
            return null;
        }
        return DictItemOption.builder()
                .value(item.getValue())
                .label(item.getLabel())
                .tagType(item.getTagType())
                .build();
    }
}
