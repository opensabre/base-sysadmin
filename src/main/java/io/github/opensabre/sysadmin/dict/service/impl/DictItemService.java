package io.github.opensabre.sysadmin.dict.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.opensabre.sysadmin.dict.dao.DictItemMapper;
import io.github.opensabre.sysadmin.dict.model.po.DictItem;
import io.github.opensabre.sysadmin.dict.model.vo.DictItemOption;
import io.github.opensabre.sysadmin.dict.service.IDictItemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class DictItemService extends ServiceImpl<DictItemMapper, DictItem> implements IDictItemService {

    private static final int STATUS_ENABLED = 1;
    private static final String TAG_TYPE_DEFAULT = "N";

    @Override
    public IPage<DictItem> pageItems(String dictCode, long pageNum, long pageSize, String keywords) {
        return this.page(new Page<>(pageNum, pageSize), baseQuery(dictCode, keywords)
                .orderByAsc(DictItem::getSort)
                .orderByAsc(DictItem::getId));
    }

    @Override
    public List<DictItemOption> listOptions(String dictCode) {
        return this.list(baseQuery(dictCode, null)
                        .eq(DictItem::getStatus, STATUS_ENABLED)
                        .orderByAsc(DictItem::getSort)
                        .orderByAsc(DictItem::getId))
                .stream()
                .map(DictItemOption::from)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public DictItem getFormData(String dictCode, String id) {
        if (StringUtils.isAnyBlank(dictCode, id)) {
            return null;
        }
        return this.getOne(new LambdaQueryWrapper<DictItem>()
                .eq(DictItem::getDictCode, dictCode)
                .eq(DictItem::getId, id)
                .last("limit 1"));
    }

    @Override
    public boolean saveItem(String dictCode, DictItem item) {
        if (StringUtils.isBlank(dictCode) || item == null) {
            return false;
        }
        applyDefaults(item);
        item.setDictCode(dictCode);
        return this.save(item);
    }

    @Override
    public boolean updateItem(String dictCode, String id, DictItem item) {
        if (StringUtils.isAnyBlank(dictCode, id) || item == null) {
            return false;
        }
        applyDefaults(item);
        item.setDictCode(dictCode);
        item.setId(id);
        return this.update(item, new LambdaUpdateWrapper<DictItem>()
                .eq(DictItem::getDictCode, dictCode)
                .eq(DictItem::getId, id));
    }

    @Override
    public boolean deleteByIds(String dictCode, String ids) {
        List<String> idList = splitIds(ids);
        if (StringUtils.isBlank(dictCode) || idList.isEmpty()) {
            return false;
        }
        return this.remove(new LambdaQueryWrapper<DictItem>()
                .eq(DictItem::getDictCode, dictCode)
                .in(DictItem::getId, idList));
    }

    @Override
    public boolean deleteByDictCodes(List<String> dictCodes) {
        if (dictCodes == null || dictCodes.isEmpty()) {
            return true;
        }
        return this.remove(new LambdaQueryWrapper<DictItem>().in(DictItem::getDictCode, dictCodes));
    }

    private LambdaQueryWrapper<DictItem> baseQuery(String dictCode, String keywords) {
        LambdaQueryWrapper<DictItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(dictCode), DictItem::getDictCode, dictCode);
        queryWrapper.and(StringUtils.isNotBlank(keywords), wrapper -> wrapper
                .like(DictItem::getLabel, keywords)
                .or()
                .like(DictItem::getValue, keywords));
        return queryWrapper;
    }

    private void applyDefaults(DictItem item) {
        if (item.getStatus() == null) {
            item.setStatus(STATUS_ENABLED);
        }
        if (item.getSort() == null) {
            item.setSort(1);
        }
        if (StringUtils.isBlank(item.getTagType())) {
            item.setTagType(TAG_TYPE_DEFAULT);
        }
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
