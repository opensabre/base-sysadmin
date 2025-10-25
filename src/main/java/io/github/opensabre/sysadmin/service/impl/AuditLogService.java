package io.github.opensabre.sysadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.opensabre.sysadmin.model.po.AuditLog;
import io.github.opensabre.sysadmin.dao.AuditLogMapper;
import io.github.opensabre.sysadmin.model.param.AuditLogQueryParam;
import io.github.opensabre.sysadmin.service.IAuditLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 审计日志服务实现
 */
@Slf4j
@Service
public class AuditLogService extends ServiceImpl<AuditLogMapper, AuditLog> implements IAuditLogService {

    @Override
    public boolean add(AuditLog auditLog) {
        try {
            return this.save(auditLog);
        } catch (Exception e) {
            log.error("保存审计日志失败: {}", auditLog, e);
            return false;
        }
    }

    @Override
    public AuditLog get(String id) {
        return this.getById(id);
    }

    @Override
    public IPage<AuditLog> query(Page page, AuditLogQueryParam queryParam) {
        QueryWrapper<AuditLog> queryWrapper = new QueryWrapper<>();
        // 操作类型
        queryWrapper.eq(queryParam.getOperationType() != null, "operation_type", queryParam.getOperationType());
        // 操作模块
        queryWrapper.eq(StringUtils.isNotBlank(queryParam.getModule()), "module", queryParam.getModule());
        // 操作人用户名
        queryWrapper.eq(StringUtils.isNotBlank(queryParam.getOperatorUsername()), "operator_username", queryParam.getOperatorUsername());
        // 操作时间范围
        queryWrapper.ge(queryParam.getOperationStartTime() != null, "operation_time", queryParam.getOperationStartTime());
        queryWrapper.le(queryParam.getOperationEndTime() != null, "operation_time", queryParam.getOperationEndTime());
        // 客户端IP
        queryWrapper.eq(StringUtils.isNotBlank(queryParam.getClientIp()), "client_ip", queryParam.getClientIp());
        // 按操作时间倒序排列
        queryWrapper.orderByDesc("operation_time");
        return this.page(page, queryWrapper);
    }

    @Override
    public int cleanExpiredLogs(int days) {
        LocalDateTime expireTime = LocalDateTime.now().minusDays(days);
        QueryWrapper<AuditLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.lt("operation_time", expireTime);

        int deletedCount = this.baseMapper.delete(queryWrapper);
        log.info("清理过期审计日志完成，删除记录数: {}, 过期时间: {}", deletedCount, expireTime);
        return deletedCount;
    }
}