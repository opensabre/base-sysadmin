package io.github.opensabre.sysadmin.errorcatalog.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import io.github.opensabre.governance.errorcatalog.ErrorCatalogEntry;
import io.github.opensabre.governance.errorcatalog.ErrorCatalogSnapshot;
import io.github.opensabre.sysadmin.errorcatalog.model.ErrorCatalog;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ErrorCatalogServiceTest {
    @Test
    void rejectsCodeOwnedByAnotherApplication() {
        ErrorCatalog existing = new ErrorCatalog();
        existing.setCode("020000");
        existing.setSourceApplication("base-organization");
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
}
