package io.github.opensabre.sysadmin.dict.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.opensabre.sysadmin.dict.dao.DictTypeMapper;
import io.github.opensabre.sysadmin.dict.model.po.DictType;
import io.github.opensabre.sysadmin.dict.model.vo.OptionItem;
import io.github.opensabre.sysadmin.dict.service.IDictItemService;
import io.github.opensabre.sysadmin.dict.service.IDictTypeService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class DictTypeService extends ServiceImpl<DictTypeMapper, DictType> implements IDictTypeService {

    private static final int STATUS_ENABLED = 1;

    @Resource
    private IDictItemService dictItemService;

    @Override
    public IPage<DictType> pageDicts(long pageNum, long pageSize, String keywords, Integer status) {
        return this.page(new Page<>(pageNum, pageSize), baseQuery(keywords, status).orderByDesc(DictType::getUpdatedTime));
    }

    @Override
    public List<OptionItem> listOptions() {
        return this.list(baseQuery(null, STATUS_ENABLED).orderByAsc(DictType::getDictCode))
                .stream()
                .map(OptionItem::from)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public DictType getFormData(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return this.getById(id);
    }

    @Override
    public boolean saveDict(DictType dictType) {
        if (dictType == null) {
            return false;
        }
        if (dictType.getStatus() == null) {
            dictType.setStatus(STATUS_ENABLED);
        }
        return this.save(dictType);
    }

    @Override
    public boolean updateDict(String id, DictType dictType) {
        if (StringUtils.isBlank(id) || dictType == null) {
            return false;
        }
        if (dictType.getStatus() == null) {
            dictType.setStatus(STATUS_ENABLED);
        }
        dictType.setId(id);
        return this.update(dictType, new LambdaUpdateWrapper<DictType>().eq(DictType::getId, id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(String ids) {
        List<String> idList = splitIds(ids);
        if (idList.isEmpty()) {
            return false;
        }
        List<String> dictCodes = this.list(new LambdaQueryWrapper<DictType>()
                        .select(DictType::getDictCode)
                        .in(DictType::getId, idList))
                .stream()
                .map(DictType::getDictCode)
                .filter(StringUtils::isNotBlank)
                .toList();
        boolean removedTypes = this.removeByIds(idList);
        boolean removedItems = dictItemService.deleteByDictCodes(dictCodes);
        return removedTypes && removedItems;
    }

    private LambdaQueryWrapper<DictType> baseQuery(String keywords, Integer status) {
        LambdaQueryWrapper<DictType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(status != null, DictType::getStatus, status);
        queryWrapper.and(StringUtils.isNotBlank(keywords), wrapper -> wrapper
                .like(DictType::getName, keywords)
                .or()
                .like(DictType::getDictCode, keywords));
        return queryWrapper;
    }

    private List<String> splitIds(String ids) {
        if (StringUtils.isBlank(ids)) {
            return List.of();
        }
        return Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .toList();
    }
}
