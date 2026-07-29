package io.github.opensabre.sysadmin.dict.model;

import java.util.List;

/**
 * Framework 0.7 上报的应用字典完整快照。
 */
public record DictionaryRegistrationRequest(
        String application,
        List<Definition> dictionaries
) {

    /**
     * 一个由应用声明的字典定义。
     */
    public record Definition(
            String dictCode,
            String dictName,
            List<Item> items
    ) {
    }

    /**
     * 快照中的字典项；注册项默认启用。
     */
    public record Item(
            String value,
            String label,
            Integer sort,
            String tagType
    ) {
    }
}
