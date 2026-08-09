package io.github.opensabre.sysadmin.dict.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import io.github.opensabre.sysadmin.dict.model.po.DictType;
import io.github.opensabre.sysadmin.dict.model.vo.OptionItem;

import java.util.List;

public interface IDictTypeService extends IService<DictType> {

    IPage<DictType> pageDicts(long pageNum, long pageSize, String keywords, Integer status);

    List<OptionItem> listOptions();

    DictType getFormData(String id);

    boolean saveDict(DictType dictType);

    boolean updateDict(String id, DictType dictType);

    boolean deleteByIds(String ids);
}
