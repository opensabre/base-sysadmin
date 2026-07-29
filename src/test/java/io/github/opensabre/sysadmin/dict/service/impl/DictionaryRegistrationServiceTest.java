package io.github.opensabre.sysadmin.dict.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import io.github.opensabre.sysadmin.dict.model.DictionaryRegistrationRequest;
import io.github.opensabre.sysadmin.dict.model.po.DictType;
import io.github.opensabre.sysadmin.dict.service.IDictItemService;
import io.github.opensabre.sysadmin.dict.service.IDictTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DictionaryRegistrationServiceTest {

    private DictionaryRegistrationService service;
    private IDictTypeService dictTypeService;
    private IDictItemService dictItemService;

    @BeforeEach
    void setUp() {
        service = new DictionaryRegistrationService();
        dictTypeService = mock(IDictTypeService.class);
        dictItemService = mock(IDictItemService.class);
        ReflectionTestUtils.setField(service, "dictTypeService", dictTypeService);
        ReflectionTestUtils.setField(service, "dictItemService", dictItemService);
        when(dictTypeService.list(any(Wrapper.class))).thenReturn(List.of());
        when(dictItemService.list(any(Wrapper.class))).thenReturn(List.of());
    }

    @Test
    void rejectsDuplicateDictionaryDefinitionsBeforePersistence() {
        DictionaryRegistrationRequest.Definition definition = definition("gender");
        DictionaryRegistrationRequest snapshot = new DictionaryRegistrationRequest(
                "base-demo", List.of(definition, definition));

        assertThrows(IllegalArgumentException.class, () -> service.register(snapshot));

        verify(dictTypeService, never()).save(any());
    }

    @Test
    void rejectsDictionaryOwnedByAnotherApplication() {
        DictType existing = DictType.builder()
                .dictCode("gender")
                .sourceApplication("base-organization")
                .build();
        when(dictTypeService.getOne(any())).thenReturn(existing);

        assertThrows(IllegalArgumentException.class,
                () -> service.register(snapshot("base-authorization", "gender")));

        verify(dictItemService, never()).save(any());
    }

    @Test
    void registersNewDictionaryAndItems() {
        service.register(snapshot("base-demo", "gender"));

        verify(dictTypeService).save(any(DictType.class));
        verify(dictItemService).save(any());
    }

    @Test
    void propagatesSysadminPersistenceFailureForTransactionalRollback() {
        when(dictTypeService.save(any())).thenThrow(new DataAccessResourceFailureException("database unavailable"));

        assertThrows(DataAccessResourceFailureException.class,
                () -> service.register(snapshot("base-demo", "gender")));
    }

    private DictionaryRegistrationRequest snapshot(String application, String code) {
        return new DictionaryRegistrationRequest(application, List.of(definition(code)));
    }

    private DictionaryRegistrationRequest.Definition definition(String code) {
        return new DictionaryRegistrationRequest.Definition(
                code,
                "性别",
                List.of(new DictionaryRegistrationRequest.Item("M", "男", 1, "N")));
    }
}
