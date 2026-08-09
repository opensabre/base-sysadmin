package io.github.opensabre.sysadmin.dict.service;

import io.github.opensabre.sysadmin.dict.dao.DictTypeMapper;
import io.github.opensabre.sysadmin.dict.model.po.DictType;
import io.github.opensabre.sysadmin.dict.service.impl.DictTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DictTypeServiceTest {

    @Test
    void createsManualDictionaryEvenWhenClientSuppliesApplicationOwner() {
        DictTypeMapper mapper = mock(DictTypeMapper.class);
        DictTypeService service = serviceWith(mapper);
        DictType dictType = DictType.builder()
                .dictCode("gender")
                .sourceApplication("spoofed-application")
                .build();

        service.saveDict(dictType);

        assertNull(dictType.getSourceApplication());
        verify(mapper).insert(dictType);
    }

    @Test
    void rejectsManualChangesToApplicationReportedDictionary() {
        DictTypeMapper mapper = mock(DictTypeMapper.class);
        DictTypeService service = serviceWith(mapper);
        DictType current = DictType.builder()
                .dictCode("usage_event")
                .sourceApplication("base-sysadmin")
                .build();
        when(mapper.selectById("42")).thenReturn(current);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> service.updateDict("42", DictType.builder().name("changed").build()));

        assertEquals("application-reported dictionary usage_event cannot be manually modified",
                exception.getMessage());
    }

    private DictTypeService serviceWith(DictTypeMapper mapper) {
        DictTypeService service = new DictTypeService();
        ReflectionTestUtils.setField(service, "baseMapper", mapper);
        return service;
    }
}
