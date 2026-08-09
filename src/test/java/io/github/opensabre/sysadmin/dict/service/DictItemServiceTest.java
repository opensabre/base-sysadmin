package io.github.opensabre.sysadmin.dict.service;

import io.github.opensabre.sysadmin.dict.dao.DictItemMapper;
import io.github.opensabre.sysadmin.dict.dao.DictTypeMapper;
import io.github.opensabre.sysadmin.dict.model.po.DictItem;
import io.github.opensabre.sysadmin.dict.model.vo.DictItemOption;
import io.github.opensabre.sysadmin.dict.service.impl.DictItemService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DictItemServiceTest {

    @Test
    void queriesDistinctCodesOnceAndKeepsMissingGroups() {
        DictItemMapper mapper = mock(DictItemMapper.class);
        DictItemService service = new DictItemService();
        ReflectionTestUtils.setField(service, "baseMapper", mapper);
        when(mapper.selectList(any())).thenReturn(List.of(
                item("notice_level", "1", "普通"),
                item("notice_type", "SYSTEM", "系统通知")
        ));

        Map<String, List<DictItemOption>> result = service.listOptions(
                List.of("notice_type", "notice_level", "notice_type", "missing")
        );

        assertEquals(List.of("notice_type", "notice_level", "missing"), List.copyOf(result.keySet()));
        assertEquals("系统通知", result.get("notice_type").getFirst().getLabel());
        assertEquals("普通", result.get("notice_level").getFirst().getLabel());
        assertEquals(List.of(), result.get("missing"));
        verify(mapper).selectList(any());
    }

    @Test
    void skipsDatabaseForEmptyCodes() {
        DictItemMapper mapper = mock(DictItemMapper.class);
        DictItemService service = new DictItemService();
        ReflectionTestUtils.setField(service, "baseMapper", mapper);

        assertEquals(Map.of(), service.listOptions(List.of("", " ")));
    }

    @Test
    void rejectsManualChangesToApplicationReportedDictionary() {
        DictItemService service = new DictItemService();
        DictTypeMapper typeMapper = mock(DictTypeMapper.class);
        ReflectionTestUtils.setField(service, "dictTypeMapper", typeMapper);
        when(typeMapper.selectSourceApplicationByDictCode("usage_event"))
                .thenReturn("base-sysadmin");

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> service.saveItem("usage_event", item("usage_event", "READ", "读取")));

        assertEquals("application-reported dictionary usage_event cannot be manually modified",
                exception.getMessage());
    }

    private DictItem item(String code, String value, String label) {
        return DictItem.builder()
                .dictCode(code)
                .value(value)
                .label(label)
                .status(1)
                .sort(1)
                .build();
    }
}
