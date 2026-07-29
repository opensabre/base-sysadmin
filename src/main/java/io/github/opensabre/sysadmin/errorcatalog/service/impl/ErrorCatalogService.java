package io.github.opensabre.sysadmin.errorcatalog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.opensabre.sysadmin.errorcatalog.dao.ErrorCatalogMapper;
import io.github.opensabre.sysadmin.errorcatalog.model.ErrorCatalog;
import io.github.opensabre.sysadmin.errorcatalog.model.ErrorCatalogRegistrationRequest;
import io.github.opensabre.sysadmin.errorcatalog.model.ErrorCatalogScope;
import io.github.opensabre.sysadmin.errorcatalog.service.IErrorCatalogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/** Persists application snapshots and rejects ownership conflicts deterministically. */
@Service
public class ErrorCatalogService extends ServiceImpl<ErrorCatalogMapper, ErrorCatalog> implements IErrorCatalogService {

    private static final String FRAMEWORK_OWNER = "opensabre-framework";
    private static final String SYSADMIN_APPLICATION = "base-sysadmin";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(ErrorCatalogRegistrationRequest snapshot) {
        for (ErrorCatalogRegistrationRequest.Entry entry : snapshot.entries()) {
            ErrorCatalog current = getOne(new LambdaQueryWrapper<ErrorCatalog>()
                    .eq(ErrorCatalog::getCode, entry.code())
                    .last("limit 1"));
            ErrorCatalogRegistrationRequest.Entry resolvedEntry = resolveOwnership(
                    snapshot.application(), entry, current);
            validateCommonOwnership(snapshot.application(), resolvedEntry, current);
            if (resolvedEntry.scope() == ErrorCatalogScope.APPLICATION
                    && !snapshot.application().equals(resolvedEntry.owner())) {
                throw new IllegalArgumentException("application error code " + resolvedEntry.code()
                        + " can only be reported by " + resolvedEntry.owner());
            }
            if (current != null) {
                String currentOwner = StringUtils.defaultIfBlank(current.getOwner(), current.getSourceApplication());
                ErrorCatalogScope currentScope = current.getScope() == null
                        ? ErrorCatalogScope.APPLICATION : current.getScope();
                if (!resolvedEntry.owner().equals(currentOwner) || resolvedEntry.scope() != currentScope) {
                    throw new IllegalArgumentException("error code " + entry.code() + " is already owned by " + currentOwner);
                }
                if (!sameDefinition(current, resolvedEntry)) {
                    throw new IllegalArgumentException("error code " + entry.code()
                            + " conflicts with the definition owned by " + currentOwner);
                }
            }
            ErrorCatalog catalog = current == null ? new ErrorCatalog() : current;
            catalog.setCode(resolvedEntry.code());
            catalog.setDefaultMessage(resolvedEntry.message());
            catalog.setSourceApplication(snapshot.application());
            catalog.setOwner(resolvedEntry.owner());
            catalog.setScope(resolvedEntry.scope());
            catalog.setModule(resolvedEntry.module());
            catalog.setSourceVersion(snapshot.version());
            catalog.setHttpStatus(resolvedEntry.httpStatus());
            catalog.setPublicVisible(resolvedEntry.publicVisible());
            catalog.setDeprecated(resolvedEntry.deprecated());
            catalog.setDescription(resolvedEntry.description());
            if (current == null) save(catalog); else updateById(catalog);
        }
    }

    private void validateCommonOwnership(
            String application,
            ErrorCatalogRegistrationRequest.Entry entry,
            ErrorCatalog current) {
        if (entry.scope() != ErrorCatalogScope.COMMON) {
            return;
        }
        if (!FRAMEWORK_OWNER.equals(entry.owner())) {
            throw new IllegalArgumentException("common error code " + entry.code()
                    + " must be owned by " + FRAMEWORK_OWNER);
        }
        if (current == null && !SYSADMIN_APPLICATION.equals(application)) {
            throw new IllegalArgumentException("new common error code " + entry.code()
                    + " can only be registered by " + SYSADMIN_APPLICATION);
        }
    }

    private ErrorCatalogRegistrationRequest.Entry resolveOwnership(
            String application,
            ErrorCatalogRegistrationRequest.Entry entry,
            ErrorCatalog current) {
        if (current == null || StringUtils.isNotBlank(entry.owner()) || entry.scope() != null) {
            return entry.resolveOwner(application);
        }
        ErrorCatalogScope currentScope = current.getScope() == null
                ? ErrorCatalogScope.APPLICATION : current.getScope();
        if (currentScope != ErrorCatalogScope.COMMON) {
            return entry.resolveOwner(application);
        }
        // Framework 0.7 未携带归属字段；已有公共定义沿用服务端可信归属。
        String currentOwner = StringUtils.defaultIfBlank(
                current.getOwner(), current.getSourceApplication());
        return entry.resolveOwner(currentOwner, ErrorCatalogScope.COMMON);
    }

    private boolean sameDefinition(ErrorCatalog current, ErrorCatalogRegistrationRequest.Entry entry) {
        return Objects.equals(current.getDefaultMessage(), entry.message())
                && Objects.equals(current.getModule(), entry.module())
                && Objects.equals(current.getHttpStatus(), entry.httpStatus())
                && current.isPublicVisible() == entry.publicVisible()
                && current.isDeprecated() == entry.deprecated()
                && Objects.equals(current.getDescription(), entry.description());
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
