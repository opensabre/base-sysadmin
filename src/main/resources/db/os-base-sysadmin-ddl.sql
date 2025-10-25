SET NAMES utf8;
USE os_base_sysadmin;
-- 审计日志表
DROP TABLE IF EXISTS base_sys_audit_log;
CREATE TABLE IF NOT EXISTS `base_sys_audit_log` (
    `id` varchar(32) NOT NULL COMMENT '主键ID',
    `operation_type` varchar(50) NOT NULL COMMENT '操作类型',
    `operation_time` datetime NOT NULL COMMENT '操作时间',
    `operator_username` varchar(100) NOT NULL COMMENT '操作人用户名',
    `module` varchar(100) NOT NULL COMMENT '操作模块',
    `description` varchar(500) COMMENT '操作描述',
    `client_ip` varchar(50) COMMENT '操作IP地址',
    `target_key` varchar(50) COMMENT '操作目标关键key',
    `user_agent` varchar(500) COMMENT '用户代理',
    `request_method` varchar(10) COMMENT '请求方法',
    `request_url` varchar(500) COMMENT '请求URL',
    `request` text COMMENT '请求参数',
    `response` text COMMENT '操作结果',
    `error_message` text COMMENT '错误信息',
    `execution_time` bigint COMMENT '执行时间(毫秒)',
    `created_by` varchar(100) NOT NULL COMMENT '创建人',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` varchar(100) NOT NULL COMMENT '更新人',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_operator_username` (`operator_username`),
    KEY `idx_operation_time` (`operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审计日志表';
