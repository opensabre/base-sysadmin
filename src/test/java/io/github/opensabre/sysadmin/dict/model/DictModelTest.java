package io.github.opensabre.sysadmin.dict.model;

import io.github.opensabre.sysadmin.dict.model.po.DictItem;
import io.github.opensabre.sysadmin.dict.model.po.DictType;
import io.github.opensabre.sysadmin.dict.model.vo.DictItemOption;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DictModelTest {

    @Test
    void buildsDictTypeForAdminList() {
        DictType dictType = DictType.builder()
                .name("性别")
                .dictCode("gender")
                .status(1)
                .remark("用户性别")
                .build();

        assertEquals("性别", dictType.getName());
        assertEquals("gender", dictType.getDictCode());
        assertEquals(1, dictType.getStatus());
    }

    @Test
    void convertsEnabledDictItemToCacheOption() {
        DictItem item = DictItem.builder()
                .dictCode("gender")
                .label("男")
                .value("M")
                .sort(1)
                .status(1)
                .tagType("P")
                .build();

        DictItemOption option = DictItemOption.from(item);

        assertEquals("男", option.getLabel());
        assertEquals("M", option.getValue());
        assertEquals("P", option.getTagType());
    }
}
