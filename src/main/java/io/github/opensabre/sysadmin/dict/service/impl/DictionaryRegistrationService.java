package io.github.opensabre.sysadmin.dict.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.github.opensabre.sysadmin.dict.model.DictionaryRegistrationRequest;
import io.github.opensabre.sysadmin.dict.model.po.DictItem;
import io.github.opensabre.sysadmin.dict.model.po.DictType;
import io.github.opensabre.sysadmin.dict.service.IDictItemService;
import io.github.opensabre.sysadmin.dict.service.IDictTypeService;
import io.github.opensabre.sysadmin.dict.service.IDictionaryRegistrationService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 按应用维护字典定义归属，并以停用代替删除来保留历史值回显。
 */
@Service
public class DictionaryRegistrationService implements IDictionaryRegistrationService {

    private static final int STATUS_DISABLED = 0;
    private static final int STATUS_ENABLED = 1;
    private static final int DEFAULT_SORT = 1;
    private static final String DEFAULT_TAG_TYPE = "N";

    @Resource
    private IDictTypeService dictTypeService;

    @Resource
    private IDictItemService dictItemService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(DictionaryRegistrationRequest snapshot) {
        validateSnapshot(snapshot);
        Map<String, DictionaryRegistrationRequest.Definition> definitions = uniqueDefinitions(snapshot);

        for (DictionaryRegistrationRequest.Definition definition : definitions.values()) {
            registerDefinition(snapshot.application(), definition);
        }
        disableMissingDefinitions(snapshot.application(), definitions.keySet());
    }

    private void validateSnapshot(DictionaryRegistrationRequest snapshot) {
        if (snapshot == null || StringUtils.isBlank(snapshot.application()) || snapshot.dictionaries() == null) {
            throw new IllegalArgumentException("application and dictionaries are required");
        }
    }

    private Map<String, DictionaryRegistrationRequest.Definition> uniqueDefinitions(
            DictionaryRegistrationRequest snapshot) {
        Map<String, DictionaryRegistrationRequest.Definition> definitions = new LinkedHashMap<>();
        for (DictionaryRegistrationRequest.Definition definition : snapshot.dictionaries()) {
            validateDefinition(definition);
            DictionaryRegistrationRequest.Definition previous =
                    definitions.putIfAbsent(definition.dictCode(), definition);
            if (previous != null) {
                throw new IllegalArgumentException("duplicate dictionary definition " + definition.dictCode());
            }
        }
        return definitions;
    }

    private void validateDefinition(DictionaryRegistrationRequest.Definition definition) {
        if (definition == null || StringUtils.isAnyBlank(definition.dictCode(), definition.dictName())
                || definition.items() == null) {
            throw new IllegalArgumentException("dictCode, dictName and items are required");
        }
        Set<String> values = new HashSet<>();
        for (DictionaryRegistrationRequest.Item item : definition.items()) {
            if (item == null || StringUtils.isAnyBlank(item.value(), item.label())) {
                throw new IllegalArgumentException("dictionary item value and label are required");
            }
            if (!values.add(item.value())) {
                throw new IllegalArgumentException("duplicate dictionary item "
                        + definition.dictCode() + ":" + item.value());
            }
        }
    }

    private void registerDefinition(String application, DictionaryRegistrationRequest.Definition definition) {
        DictType current = dictTypeService.getOne(new LambdaQueryWrapper<DictType>()
                .eq(DictType::getDictCode, definition.dictCode())
                .last("limit 1"));
        if (current != null && !application.equals(current.getSourceApplication())) {
            String owner = StringUtils.defaultIfBlank(current.getSourceApplication(), "sysadmin");
            throw new IllegalArgumentException("dictionary " + definition.dictCode()
                    + " is already owned by " + owner);
        }

        DictType dictType = current == null ? new DictType() : current;
        dictType.setName(definition.dictName());
        dictType.setDictCode(definition.dictCode());
        dictType.setSourceApplication(application);
        dictType.setStatus(STATUS_ENABLED);
        if (current == null) {
            dictTypeService.save(dictType);
        } else {
            dictTypeService.updateById(dictType);
        }
        replaceItems(definition);
    }

    private void replaceItems(DictionaryRegistrationRequest.Definition definition) {
        Map<String, DictItem> existing = dictItemService.list(new LambdaQueryWrapper<DictItem>()
                        .eq(DictItem::getDictCode, definition.dictCode()))
                .stream()
                .collect(java.util.stream.Collectors.toMap(DictItem::getValue, item -> item));
        Set<String> registeredValues = new HashSet<>();

        for (DictionaryRegistrationRequest.Item declared : definition.items()) {
            DictItem item = existing.getOrDefault(declared.value(), new DictItem());
            item.setDictCode(definition.dictCode());
            item.setValue(declared.value());
            item.setLabel(declared.label());
            item.setSort(Objects.requireNonNullElse(declared.sort(), DEFAULT_SORT));
            item.setTagType(StringUtils.defaultIfBlank(declared.tagType(), DEFAULT_TAG_TYPE));
            item.setStatus(STATUS_ENABLED);
            if (item.getId() == null) {
                dictItemService.save(item);
            } else {
                dictItemService.updateById(item);
            }
            registeredValues.add(declared.value());
        }

        // 快照中消失的条目仅停用，供历史值 labelOf 回显。
        for (DictItem item : existing.values()) {
            if (!registeredValues.contains(item.getValue())
                    && !Objects.equals(item.getStatus(), STATUS_DISABLED)) {
                item.setStatus(STATUS_DISABLED);
                dictItemService.updateById(item);
            }
        }
    }

    private void disableMissingDefinitions(String application, Set<String> registeredCodes) {
        List<DictType> ownedTypes = dictTypeService.list(new LambdaQueryWrapper<DictType>()
                .eq(DictType::getSourceApplication, application));
        for (DictType type : ownedTypes) {
            if (!registeredCodes.contains(type.getDictCode())
                    && !Objects.equals(type.getStatus(), STATUS_DISABLED)) {
                dictTypeService.update(new LambdaUpdateWrapper<DictType>()
                        .eq(DictType::getId, type.getId())
                        .set(DictType::getStatus, STATUS_DISABLED));
                dictItemService.update(new LambdaUpdateWrapper<DictItem>()
                        .eq(DictItem::getDictCode, type.getDictCode())
                        .set(DictItem::getStatus, STATUS_DISABLED));
            }
        }
    }
}
