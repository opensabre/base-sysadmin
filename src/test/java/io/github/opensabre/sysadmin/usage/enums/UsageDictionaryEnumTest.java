package io.github.opensabre.sysadmin.usage.enums;

import io.github.opensabre.governance.dictionary.ClasspathDictionaryEnumProvider;
import io.github.opensabre.governance.dictionary.DictionaryDefinition;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 验证 Framework 能自动发现 Sysadmin 声明的使用量字典。 */
class UsageDictionaryEnumTest {

    @Test
    void discoversUsageDictionariesFromApplicationPackage() {
        Map<String, DictionaryDefinition> definitions = new ClasspathDictionaryEnumProvider(
                new DefaultResourceLoader(),
                java.util.List.of("io.github.opensabre.sysadmin.usage.enums"))
                .dictionaries().stream()
                .collect(Collectors.toMap(DictionaryDefinition::dictCode, Function.identity()));

        assertEquals(3, definitions.size());
        assertEquals("验证码场景", definitions.get("usage_object_type").items().get(0).label());
        assertEquals("CAPTCHA_GENERATE", definitions.get("usage_event").items().get(0).value());
        assertEquals("HOUR", definitions.get("usage_granularity").items().get(1).value());
    }
}
