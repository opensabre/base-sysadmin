package io.github.opensabre.sysadmin.errorcatalog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.opensabre.sysadmin.errorcatalog.model.ErrorCatalog;
import io.github.opensabre.sysadmin.errorcatalog.model.ErrorCatalogRegistrationRequest;

/** Maintains immutable ownership of globally unique error codes. */
public interface IErrorCatalogService {
    void register(ErrorCatalogRegistrationRequest snapshot);
    IPage<ErrorCatalog> page(long pageNum, long pageSize, String keywords, String application, Boolean deprecated);
}
