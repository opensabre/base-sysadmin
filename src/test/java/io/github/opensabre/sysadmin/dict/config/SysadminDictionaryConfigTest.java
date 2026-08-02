package io.github.opensabre.sysadmin.dict.config;

import io.github.opensabre.governance.dictionary.DictionaryDefinition;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 验证 Sysadmin 声明的管理端字典编码和值。 */
class SysadminDictionaryConfigTest {

    @Test
    void declaresUsageDictionariesConsumedByAdmin() {
        Map<String, DictionaryDefinition> definitions = new SysadminDictionaryConfig()
                .sysadminDictionaryProvider().dictionaries().stream()
                .collect(Collectors.toMap(DictionaryDefinition::dictCode, Function.identity()));

        assertEquals(3, definitions.size());
        assertEquals("验证码场景", definitions.get("usage_object_type").items().get(0).label());
        assertEquals("CAPTCHA_GENERATE", definitions.get("usage_event").items().get(0).value());
        assertEquals("HOUR", definitions.get("usage_granularity").items().get(1).value());
    }
}
