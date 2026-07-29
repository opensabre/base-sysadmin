package io.github.opensabre.sysadmin.dict.service;

import io.github.opensabre.sysadmin.dict.model.DictionaryRegistrationRequest;

/**
 * 管理应用字典完整快照及其定义归属。
 */
public interface IDictionaryRegistrationService {

    /**
     * 校验并覆盖一个应用的完整字典快照。
     */
    void register(DictionaryRegistrationRequest snapshot);
}
