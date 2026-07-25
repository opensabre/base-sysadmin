package io.github.opensabre.sysadmin.errorcatalog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.opensabre.sysadmin.errorcatalog.model.ErrorCatalog;
import org.apache.ibatis.annotations.Mapper;

/** Error catalog data access. */
@Mapper
public interface ErrorCatalogMapper extends BaseMapper<ErrorCatalog> { }
