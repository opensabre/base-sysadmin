package io.github.opensabre.sysadmin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.opensabre.sysadmin.model.po.AuditLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审计日志数据访问接口
 */
@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLog> {
}