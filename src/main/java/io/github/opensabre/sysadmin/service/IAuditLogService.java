package io.github.opensabre.sysadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.opensabre.sysadmin.model.param.AuditLogQueryParam;
import io.github.opensabre.sysadmin.model.po.AuditLog;

/**
 * 审计日志服务接口
 */
public interface IAuditLogService {

    /**
     * 保存审计日志
     *
     * @param auditLog 审计日志实体
     * @return 是否保存成功
     */
    boolean add(AuditLog auditLog);

    /**
     * 根据ID查询审计日志
     *
     * @param id 日志ID
     * @return 审计日志
     */
    AuditLog get(String id);

    /**
     * 分页查询审计日志
     *
     * @param page       分页参数
     * @param queryParam 查询参数
     * @return 分页结果
     */
    IPage<AuditLog> query(Page page, AuditLogQueryParam queryParam);


    /**
     * 清理过期审计日志
     *
     * @param days 保留天数
     * @return 删除的记录数
     */
    int cleanExpiredLogs(int days);
}