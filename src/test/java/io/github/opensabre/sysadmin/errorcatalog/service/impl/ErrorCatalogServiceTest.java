package io.github.opensabre.sysadmin.errorcatalog.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import io.github.opensabre.sysadmin.errorcatalog.model.ErrorCatalog;
import io.github.opensabre.sysadmin.errorcatalog.model.ErrorCatalogRegistrationRequest;
import io.github.opensabre.sysadmin.errorcatalog.model.ErrorCatalogScope;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ErrorCatalogServiceTest {
    @Test
    void rejectsCodeOwnedByAnotherApplication() {
        ErrorCatalog existing = new ErrorCatalog();
        existing.setCode("020000");
        existing.setSourceApplication("base-organization");
        existing.setOwner("base-organization");
        existing.setScope(ErrorCatalogScope.APPLICATION);
        ErrorCatalogService service = new ErrorCatalogService() {
            @Override
            public ErrorCatalog getOne(Wrapper<ErrorCatalog> queryWrapper) {
                return existing;
            }
        };
        ErrorCatalogRegistrationRequest snapshot = new ErrorCatalogRegistrationRequest("base-authorization", "0.6.0",
                List.of(entry("020000", "请求参数校验不通过", "auth", null, null)));
        assertThrows(IllegalArgumentException.class, () -> service.register(snapshot));
    }

    @Test
    void acceptsIdenticalCommonDefinitionFromAnotherApplication() {
        ErrorCatalog existing = new ErrorCatalog();
        existing.setCode("-1");
        existing.setDefaultMessage("系统异常");
        existing.setSourceApplication("base-sysadmin");
        existing.setOwner("opensabre-framework");
        existing.setScope(ErrorCatalogScope.COMMON);
        existing.setModule("framework");
        existing.setPublicVisible(true);
        ErrorCatalogService service = serviceWith(existing);
        ErrorCatalogRegistrationRequest.Entry common = entry("-1", "系统异常", "framework",
                "opensabre-framework", ErrorCatalogScope.COMMON);

        assertDoesNotThrow(() -> service.register(new ErrorCatalogRegistrationRequest(
                "base-authorization", "0.7.0", List.of(common))));
    }

    @Test
    void acceptsLegacyFrameworkCommonDefinitionWithoutOwnershipFields() {
        ErrorCatalog existing = new ErrorCatalog();
        existing.setCode("-1");
        existing.setDefaultMessage("系统异常");
        existing.setSourceApplication("base-sysadmin");
        existing.setOwner("opensabre-framework");
        existing.setScope(ErrorCatalogScope.COMMON);
        existing.setModule("framework");
        existing.setPublicVisible(true);
        ErrorCatalogService service = serviceWith(existing);
        ErrorCatalogRegistrationRequest.Entry legacyFrameworkEntry =
                entry("-1", "系统异常", "framework", null, null);

        assertDoesNotThrow(() -> service.register(new ErrorCatalogRegistrationRequest(
                "base-authorization", "0.7.0", List.of(legacyFrameworkEntry))));
    }

    @Test
    void rejectsChangedCommonDefinition() {
        ErrorCatalog existing = new ErrorCatalog();
        existing.setCode("-1");
        existing.setDefaultMessage("系统异常");
        existing.setSourceApplication("base-sysadmin");
        existing.setOwner("opensabre-framework");
        existing.setScope(ErrorCatalogScope.COMMON);
        existing.setModule("framework");
        existing.setPublicVisible(true);
        ErrorCatalogService service = serviceWith(existing);
        ErrorCatalogRegistrationRequest.Entry changed = entry("-1", "未知异常", "framework",
                "opensabre-framework", ErrorCatalogScope.COMMON);

        assertThrows(IllegalArgumentException.class, () -> service.register(new ErrorCatalogRegistrationRequest(
                "base-authorization", "0.7.0", List.of(changed))));
    }

    @Test
    void rejectsApplicationOwnedCommonDefinition() {
        ErrorCatalogService service = serviceWith(null);
        ErrorCatalogRegistrationRequest.Entry common = entry(
                "020001", "认证失败", "authorization",
                "base-authorization", ErrorCatalogScope.COMMON);

        assertThrows(IllegalArgumentException.class, () -> service.register(
                new ErrorCatalogRegistrationRequest(
                        "base-authorization", "0.7.0", List.of(common))));
    }

    @Test
    void rejectsNewFrameworkCommonDefinitionFromBusinessApplication() {
        ErrorCatalogService service = serviceWith(null);
        ErrorCatalogRegistrationRequest.Entry common = entry(
                "-99", "框架错误", "framework",
                "opensabre-framework", ErrorCatalogScope.COMMON);

        assertThrows(IllegalArgumentException.class, () -> service.register(
                new ErrorCatalogRegistrationRequest(
                        "base-authorization", "0.7.0", List.of(common))));
    }

    private ErrorCatalogRegistrationRequest.Entry entry(String code, String message, String module,
                                                        String owner, ErrorCatalogScope scope) {
        return new ErrorCatalogRegistrationRequest.Entry(
                code, message, module, null, true, false, null, owner, scope);
    }

    private ErrorCatalogService serviceWith(ErrorCatalog existing) {
        return new ErrorCatalogService() {
            @Override
            public ErrorCatalog getOne(Wrapper<ErrorCatalog> queryWrapper) {
                return existing;
            }

            @Override
            public boolean updateById(ErrorCatalog entity) {
                return true;
            }
        };
    }
}
