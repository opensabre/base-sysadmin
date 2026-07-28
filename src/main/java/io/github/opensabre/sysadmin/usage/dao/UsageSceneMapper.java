package io.github.opensabre.sysadmin.usage.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.opensabre.sysadmin.usage.model.UsageScene;
import org.apache.ibatis.annotations.Mapper;

/** 计次场景登记数据访问接口。 */
@Mapper
public interface UsageSceneMapper extends BaseMapper<UsageScene> {
}
