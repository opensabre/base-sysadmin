package io.github.opensabre.sysadmin.errorcatalog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.opensabre.governance.errorcatalog.ErrorCatalogEntry;
import io.github.opensabre.governance.errorcatalog.ErrorCatalogSnapshot;
import io.github.opensabre.sysadmin.errorcatalog.dao.ErrorCatalogMapper;
import io.github.opensabre.sysadmin.errorcatalog.model.ErrorCatalog;
import io.github.opensabre.sysadmin.errorcatalog.service.IErrorCatalogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Persists application snapshots and rejects ownership conflicts deterministically. */
@Service
public class ErrorCatalogService extends ServiceImpl<ErrorCatalogMapper, ErrorCatalog> implements IErrorCatalogService {
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(ErrorCatalogSnapshot snapshot) {
        for (ErrorCatalogEntry entry : snapshot.entries()) {
            ErrorCatalog current = getOne(new LambdaQueryWrapper<ErrorCatalog>().eq(ErrorCatalog::getCode, entry.code()).last("limit 1"));
            if (current != null && !snapshot.application().equals(current.getSourceApplication())) {
                throw new IllegalArgumentException("error code " + entry.code() + " is already owned by " + current.getSourceApplication());
            }
            ErrorCatalog catalog = current == null ? new ErrorCatalog() : current;
            catalog.setCode(entry.code());
            catalog.setDefaultMessage(entry.message());
            catalog.setSourceApplication(snapshot.application());
            catalog.setModule(entry.module());
            catalog.setSourceVersion(snapshot.version());
            catalog.setHttpStatus(entry.httpStatus());
            catalog.setPublicVisible(entry.publicVisible());
            catalog.setDeprecated(entry.deprecated());
            catalog.setDescription(entry.description());
            if (current == null) save(catalog); else updateById(catalog);
        }
    }
    @Override
    public IPage<ErrorCatalog> page(long pageNum, long pageSize, String keywords, String application, Boolean deprecated) {
        return super.page(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<ErrorCatalog>()
                .and(StringUtils.isNotBlank(keywords), wrapper -> wrapper.like(ErrorCatalog::getCode, keywords)
                        .or().like(ErrorCatalog::getDefaultMessage, keywords).or().like(ErrorCatalog::getModule, keywords))
                .eq(StringUtils.isNotBlank(application), ErrorCatalog::getSourceApplication, application)
                .eq(deprecated != null, ErrorCatalog::isDeprecated, deprecated).orderByAsc(ErrorCatalog::getCode));
    }
}
