package io.github.opensabre.sysadmin.ratelimit.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.opensabre.sysadmin.ratelimit.model.RateLimitScene;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RateLimitSceneMapper extends BaseMapper<RateLimitScene> {
}
