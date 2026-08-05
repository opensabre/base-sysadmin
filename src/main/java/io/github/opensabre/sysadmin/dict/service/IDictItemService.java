package io.github.opensabre.sysadmin.dict.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import io.github.opensabre.sysadmin.dict.model.po.DictItem;
import io.github.opensabre.sysadmin.dict.model.vo.DictItemOption;

import java.util.List;
import java.util.Map;

public interface IDictItemService extends IService<DictItem> {

    IPage<DictItem> pageItems(String dictCode, long pageNum, long pageSize, String keywords);

    List<DictItemOption> listOptions(String dictCode);

    /**
     * 批量查询启用的字典项。
     *
     * @param dictCodes 字典编码
     * @return 按请求编码分组的字典项
     */
    Map<String, List<DictItemOption>> listOptions(List<String> dictCodes);

    /**
     * 查询完整字典项，包含停用项。
     */
    List<DictItem> listAll(String dictCode);

    DictItem getFormData(String dictCode, String id);

    boolean saveItem(String dictCode, DictItem item);

    boolean updateItem(String dictCode, String id, DictItem item);

    boolean deleteByIds(String dictCode, String ids);

    boolean deleteByDictCodes(List<String> dictCodes);
}
