package io.github.opensabre.sysadmin.errorcatalog.model;
import io.github.opensabre.governance.dictionary.DictionaryEnum;
import io.github.opensabre.governance.dictionary.OpenSabreDictionary;

/** Defines whether an error-code declaration is shared or owned by one application. */
@OpenSabreDictionary(code = "error_catalog_scope", name = "错误码作用域")
public enum ErrorCatalogScope implements DictionaryEnum {
    COMMON("公共"), APPLICATION("应用");
    private final String label; ErrorCatalogScope(String label) { this.label = label; }
    public String value() { return name(); } public String label() { return label; }
}
