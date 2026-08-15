package io.github.opensabre.sysadmin.errorcatalog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import io.github.opensabre.sysadmin.errorcatalog.dao.ErrorCatalogMapper;
import io.github.opensabre.sysadmin.errorcatalog.model.ErrorCatalog;
import io.github.opensabre.sysadmin.errorcatalog.model.ErrorCatalogRegistrationRequest;
import io.github.opensabre.sysadmin.errorcatalog.model.ErrorCatalogScope;
import io.github.opensabre.sysadmin.errorcatalog.service.IErrorCatalogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/** Persists application snapshots and rejects ownership conflicts deterministically. */
@Service
public class ErrorCatalogService extends ServiceImpl<ErrorCatalogMapper, ErrorCatalog> implements IErrorCatalogService {

    private static final String FRAMEWORK_OWNER = "opensabre-framework";
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(ErrorCatalogRegistrationRequest snapshot) {
        for (ErrorCatalogRegistrationRequest.Entry entry : snapshot.entries()) {
            ErrorCatalog current = findByCode(entry.code());
            ErrorCatalogRegistrationRequest.Entry resolvedEntry = resolveOwnership(
                    snapshot.application(), entry, current);
            validateRegistration(snapshot.application(), resolvedEntry, current);
            ErrorCatalog catalog = toCatalog(snapshot, resolvedEntry, current);
            if (current != null) {
                updateById(catalog);
                continue;
            }
            try {
                save(catalog);
            } catch (DuplicateKeyException duplicate) {
                // Another instance registered the same code after our lookup. Re-read the winner
                // and apply the same ownership/definition rules instead of leaking a DB race.
                ErrorCatalog concurrent = findByCode(entry.code());
                if (concurrent == null) {
                    throw duplicate;
                }
                ErrorCatalogRegistrationRequest.Entry concurrentEntry =
                        resolveOwnership(snapshot.application(), entry, concurrent);
                validateRegistration(snapshot.application(), concurrentEntry, concurrent);
                updateById(toCatalog(snapshot, concurrentEntry, concurrent));
            }
        }
    }

    private void validateRegistration(String application,
                                      ErrorCatalogRegistrationRequest.Entry entry,
                                      ErrorCatalog current) {
        if (entry.scope() != ErrorCatalogScope.COMMON) {
            if (!application.equals(entry.owner())) {
                throw new IllegalArgumentException("application error code " + entry.code()
                        + " can only be reported by " + entry.owner());
            }
        } else if (!FRAMEWORK_OWNER.equals(entry.owner())) {
            throw new IllegalArgumentException("common error code " + entry.code()
                    + " must be owned by " + FRAMEWORK_OWNER);
        }
        if (current == null) {
            return;
        }
        String currentOwner = StringUtils.defaultIfBlank(
                current.getOwner(), current.getSourceApplication());
        ErrorCatalogScope currentScope = current.getScope() == null
                ? ErrorCatalogScope.APPLICATION : current.getScope();
        if (!entry.owner().equals(currentOwner) || entry.scope() != currentScope) {
            throw new IllegalArgumentException("error code " + entry.code()
                    + " is already owned by " + currentOwner);
        }
        if (!sameDefinition(current, entry)) {
            throw new IllegalArgumentException("error code " + entry.code()
                    + " conflicts with the definition owned by " + currentOwner);
        }
    }

    private ErrorCatalog findByCode(String code) {
        return getOne(new LambdaQueryWrapper<ErrorCatalog>()
                .eq(ErrorCatalog::getCode, code)
                .last("limit 1"));
    }

    private ErrorCatalog toCatalog(ErrorCatalogRegistrationRequest snapshot,
                                   ErrorCatalogRegistrationRequest.Entry entry,
                                   ErrorCatalog current) {
        ErrorCatalog catalog = current == null ? new ErrorCatalog() : current;
        catalog.setCode(entry.code());
        catalog.setDefaultMessage(entry.message());
        catalog.setSourceApplication(snapshot.application());
        catalog.setOwner(entry.owner());
        catalog.setScope(entry.scope());
        catalog.setModule(entry.module());
        catalog.setSourceVersion(snapshot.version());
        catalog.setHttpStatus(entry.httpStatus());
        catalog.setPublicVisible(entry.publicVisible());
        catalog.setDeprecated(entry.deprecated());
        catalog.setDescription(entry.description());
        return catalog;
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
