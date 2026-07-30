package io.github.opensabre.sysadmin.errorcatalog.rest;

import io.github.opensabre.sysadmin.errorcatalog.model.ErrorCatalogRegistrationRequest;
import io.github.opensabre.sysadmin.errorcatalog.service.IErrorCatalogService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ErrorCatalogControllerTest {

    private ErrorCatalogController controller;
    private RecordingErrorCatalogService service;
    private ErrorCatalogRegistrationRequest snapshot;

    @BeforeEach
    void setUp() {
        controller = new ErrorCatalogController();
        service = new RecordingErrorCatalogService();
        snapshot = new ErrorCatalogRegistrationRequest("base-demo", "0.7.0", List.of());
        ReflectionTestUtils.setField(controller, "errorCatalogService", service);
        ReflectionTestUtils.setField(controller, "registrationToken", "registration-secret");
    }

    @Test
    void acceptsValidRegistrationToken() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.register(snapshot, "registration-secret", response);

        org.junit.jupiter.api.Assertions.assertEquals(snapshot, service.registered);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    void rejectsInvalidRegistrationTokenWithoutCallingService() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertTrue(controller.register(snapshot, "wrong-secret", response).isFail());

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        org.junit.jupiter.api.Assertions.assertNull(service.registered);
    }

    @Test
    void rejectsRegistrationWhenServerTokenIsNotConfigured() {
        ReflectionTestUtils.setField(controller, "registrationToken", "");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertTrue(controller.register(snapshot, "", response).isFail());

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        org.junit.jupiter.api.Assertions.assertNull(service.registered);
    }

    private static class RecordingErrorCatalogService implements IErrorCatalogService {
        private ErrorCatalogRegistrationRequest registered;

        @Override
        public void register(ErrorCatalogRegistrationRequest snapshot) {
            registered = snapshot;
        }

        @Override
        public com.baomidou.mybatisplus.core.metadata.IPage<
                io.github.opensabre.sysadmin.errorcatalog.model.ErrorCatalog> page(
                long pageNum, long pageSize, String keywords, String application,
                Boolean deprecated) {
            throw new UnsupportedOperationException("not used by registration tests");
        }
    }
}
