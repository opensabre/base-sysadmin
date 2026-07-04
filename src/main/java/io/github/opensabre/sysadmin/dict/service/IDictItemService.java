package io.github.opensabre.sysadmin.dict.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import io.github.opensabre.sysadmin.dict.model.po.DictItem;
import io.github.opensabre.sysadmin.dict.model.vo.DictItemOption;

import java.util.List;

public interface IDictItemService extends IService<DictItem> {

    IPage<DictItem> pageItems(String dictCode, long pageNum, long pageSize, String keywords);

    List<DictItemOption> listOptions(String dictCode);

    DictItem getFormData(String dictCode, String id);

    boolean saveItem(String dictCode, DictItem item);

    boolean updateItem(String dictCode, String id, DictItem item);

    boolean deleteByIds(String dictCode, String ids);

    boolean deleteByDictCodes(List<String> dictCodes);
}
