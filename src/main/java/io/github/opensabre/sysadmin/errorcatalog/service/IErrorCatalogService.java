package io.github.opensabre.sysadmin.errorcatalog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.opensabre.governance.errorcatalog.ErrorCatalogSnapshot;
import io.github.opensabre.sysadmin.errorcatalog.model.ErrorCatalog;

/** Maintains immutable ownership of globally unique error codes. */
public interface IErrorCatalogService {
    void register(ErrorCatalogSnapshot snapshot);
    IPage<ErrorCatalog> page(long pageNum, long pageSize, String keywords, String application, Boolean deprecated);
}
