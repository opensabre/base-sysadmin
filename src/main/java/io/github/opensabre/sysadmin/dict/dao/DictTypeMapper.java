package io.github.opensabre.sysadmin.dict.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.opensabre.sysadmin.dict.model.po.DictType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DictTypeMapper extends BaseMapper<DictType> {

    @Select("select source_application from base_sys_dict_type where dict_code = #{dictCode} limit 1")
    String selectSourceApplicationByDictCode(@Param("dictCode") String dictCode);
}
