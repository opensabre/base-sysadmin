package io.github.opensabre.sysadmin.errorcatalog.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import io.github.opensabre.governance.errorcatalog.ErrorCatalogEntry;
import io.github.opensabre.governance.errorcatalog.ErrorCatalogSnapshot;
import io.github.opensabre.governance.errorcatalog.ErrorCatalogScope;
import io.github.opensabre.sysadmin.errorcatalog.model.ErrorCatalog;
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
        ErrorCatalogSnapshot snapshot = new ErrorCatalogSnapshot("base-authorization", "0.6.0",
                List.of(new ErrorCatalogEntry("020000", "请求参数校验不通过", "auth", null, true, false, null)));
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
        ErrorCatalogEntry common = new ErrorCatalogEntry("-1", "系统异常", "framework",
                null, true, false, null, "opensabre-framework", ErrorCatalogScope.COMMON);

        assertDoesNotThrow(() -> service.register(new ErrorCatalogSnapshot(
                "base-authorization", "0.7.0", List.of(common))));
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
        ErrorCatalogEntry changed = new ErrorCatalogEntry("-1", "未知异常", "framework",
                null, true, false, null, "opensabre-framework", ErrorCatalogScope.COMMON);

        assertThrows(IllegalArgumentException.class, () -> service.register(new ErrorCatalogSnapshot(
                "base-authorization", "0.7.0", List.of(changed))));
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
