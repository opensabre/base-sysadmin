package io.github.opensabre.sysadmin.errorcatalog.rest;

import io.github.opensabre.common.core.entity.vo.Result;
import io.github.opensabre.governance.errorcatalog.ErrorCatalogSnapshot;
import io.github.opensabre.sysadmin.dict.model.vo.PageData;
import io.github.opensabre.sysadmin.errorcatalog.model.ErrorCatalog;
import io.github.opensabre.sysadmin.errorcatalog.service.IErrorCatalogService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/** Internal registration and administrator query endpoints for the error-code directory. */
@RestController
@RequestMapping("/error-catalog")
public class ErrorCatalogController {
    @Resource private IErrorCatalogService errorCatalogService;
    @Value("${opensabre.error-catalog.registration-token:}") private String registrationToken;
    @PostMapping("/snapshots")
    public Result<Boolean> register(@RequestBody ErrorCatalogSnapshot snapshot,
                                    @RequestHeader("X-Opensabre-Error-Catalog-Token") String token) {
        if (registrationToken.isBlank() || !java.security.MessageDigest.isEqual(
                registrationToken.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                token.getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
            throw new SecurityException("invalid error catalog registration token");
        }
        errorCatalogService.register(snapshot);
        return Result.success(true);
    }
    @GetMapping
    public PageData<ErrorCatalog> page(@RequestParam(defaultValue = "1") long pageNum,
                                       @RequestParam(defaultValue = "10") long pageSize,
                                       @RequestParam(required = false) String keywords,
                                       @RequestParam(required = false) String application,
                                       @RequestParam(required = false) Boolean deprecated) {
        return PageData.from(errorCatalogService.page(pageNum, pageSize, keywords, application, deprecated));
    }
}
