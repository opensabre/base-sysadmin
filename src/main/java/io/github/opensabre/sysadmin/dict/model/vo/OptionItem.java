package io.github.opensabre.sysadmin.dict.model.vo;

import io.github.opensabre.sysadmin.dict.model.po.DictType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionItem {

    private String value;

    private String label;

    public static OptionItem from(DictType dictType) {
        if (dictType == null) {
            return null;
        }
        return OptionItem.builder()
                .value(dictType.getDictCode())
                .label(dictType.getName())
                .build();
    }
}
