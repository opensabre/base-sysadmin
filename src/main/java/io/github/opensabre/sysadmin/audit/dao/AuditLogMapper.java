package io.github.opensabre.sysadmin.audit.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.opensabre.sysadmin.audit.model.po.AuditLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审计日志数据访问接口
 */
@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLog> {
}