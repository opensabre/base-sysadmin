package io.github.opensabre.sysadmin.errorcatalog.model;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/** Version-neutral registration payload accepted from framework 0.6 and later clients. */
public record ErrorCatalogRegistrationRequest(
        String application,
        String version,
        List<Entry> entries
) {
    /** One error-code declaration in a registration snapshot. */
    public record Entry(
            String code,
            String message,
            String module,
            Integer httpStatus,
            boolean publicVisible,
            boolean deprecated,
            String description,
            String owner,
            ErrorCatalogScope scope
    ) {
        /** Resolves ownership omitted by legacy framework clients. */
        public Entry resolveOwner(String application) {
            return resolveOwner(application, ErrorCatalogScope.APPLICATION);
        }

        /**
         * Resolves omitted ownership using a known existing definition.
         */
        public Entry resolveOwner(String defaultOwner, ErrorCatalogScope defaultScope) {
            String resolvedOwner = StringUtils.defaultIfBlank(owner, defaultOwner);
            ErrorCatalogScope resolvedScope = scope == null ? defaultScope : scope;
            return new Entry(code, message, module, httpStatus, publicVisible, deprecated,
                    description, resolvedOwner, resolvedScope);
        }
    }
}
