package io.github.opensabre.sysadmin.dict.rest;

import io.github.opensabre.sysadmin.dict.model.DictionaryRegistrationRequest;
import io.github.opensabre.sysadmin.dict.model.po.DictItem;
import io.github.opensabre.sysadmin.dict.service.IDictItemService;
import io.github.opensabre.sysadmin.dict.service.IDictTypeService;
import io.github.opensabre.sysadmin.dict.service.IDictionaryRegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DictControllerTest {

    private DictController controller;
    private IDictionaryRegistrationService registrationService;
    private IDictItemService itemService;

    @BeforeEach
    void setUp() {
        controller = new DictController();
        registrationService = mock(IDictionaryRegistrationService.class);
        itemService = mock(IDictItemService.class);
        ReflectionTestUtils.setField(controller, "dictTypeService", mock(IDictTypeService.class));
        ReflectionTestUtils.setField(controller, "dictItemService", itemService);
        ReflectionTestUtils.setField(controller, "dictionaryRegistrationService", registrationService);
        ReflectionTestUtils.setField(controller, "dictionaryRegistrationToken", "registration-secret");
    }

    @Test
    void acceptsValidRegistrationToken() {
        DictionaryRegistrationRequest snapshot =
                new DictionaryRegistrationRequest("base-demo", List.of());

        controller.registerSnapshot(snapshot, "registration-secret");

        verify(registrationService).register(snapshot);
    }

    @Test
    void rejectsInvalidRegistrationTokenWithoutCallingService() {
        DictionaryRegistrationRequest snapshot =
                new DictionaryRegistrationRequest("base-demo", List.of());

        assertThrows(ResponseStatusException.class,
                () -> controller.registerSnapshot(snapshot, "wrong-secret"));

        verify(registrationService, never()).register(snapshot);
    }

    @Test
    void allItemsIncludesDisabledItemsReturnedByService() {
        DictItem disabled = DictItem.builder().value("legacy").status(0).build();
        when(itemService.listAll("gender")).thenReturn(List.of(disabled));

        List<DictItem> result = controller.allItems("gender");

        assertEquals(List.of(disabled), result);
    }
}
