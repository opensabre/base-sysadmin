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

DROP TABLE IF EXISTS base_sys_captcha_scene;
CREATE TABLE IF NOT EXISTS `base_sys_captcha_scene` (
    `id` varchar(32) NOT NULL COMMENT '主键ID',
    `scene_code` varchar(64) NOT NULL COMMENT '场景编码',
    `scene_name` varchar(128) NOT NULL COMMENT '场景名称',
    `captcha_type` varchar(32) NOT NULL COMMENT '验证码类型',
    `template_code` varchar(64) DEFAULT NULL COMMENT '消息模板编码',
    `description` varchar(255) DEFAULT NULL COMMENT '描述',
    `captcha_length` int NOT NULL DEFAULT 4 COMMENT '验证码长度',
    `captcha_expire_time` int NOT NULL DEFAULT 300 COMMENT '过期时间(秒)',
    `captcha_attempts` int NOT NULL DEFAULT 3 COMMENT '最大尝试次数',
    `min_interval` int NOT NULL DEFAULT 60 COMMENT '最小间隔(秒)',
    `max_limit_count` int NOT NULL DEFAULT 100 COMMENT '单用户生成限制次数',
    `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    `created_by` varchar(100) NOT NULL COMMENT '创建人',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` varchar(100) NOT NULL COMMENT '更新人',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_scene_code` (`scene_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='验证码场景表';

DROP TABLE IF EXISTS base_sys_ratelimit_scene;
CREATE TABLE IF NOT EXISTS `base_sys_ratelimit_scene` (
    `id` varchar(32) NOT NULL COMMENT '主键ID',
    `scene_code` varchar(64) NOT NULL COMMENT '场景编码',
    `scene_name` varchar(128) NOT NULL COMMENT '场景名称',
    `algorithm` varchar(32) NOT NULL COMMENT '限次算法',
    `dimensions` varchar(255) DEFAULT NULL COMMENT '维度代码，逗号分隔',
    `key_prefix` varchar(100) DEFAULT NULL COMMENT '业务前缀',
    `max_count` int NOT NULL DEFAULT 5 COMMENT '最大次数',
    `period` int NOT NULL DEFAULT 60 COMMENT '时间窗口(秒)',
    `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    `description` varchar(255) DEFAULT NULL COMMENT '描述',
    `created_by` varchar(100) NOT NULL COMMENT '创建人',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` varchar(100) NOT NULL COMMENT '更新人',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_scene_code` (`scene_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='限次场景表';

DROP TABLE IF EXISTS base_sys_notification_record;
DROP TABLE IF EXISTS base_sys_notification_template;
DROP TABLE IF EXISTS base_sys_notification_scene;
CREATE TABLE IF NOT EXISTS `base_sys_notification_scene` (
    `id` varchar(32) NOT NULL COMMENT '主键ID',
    `scene_code` varchar(64) NOT NULL COMMENT '场景编码',
    `scene_name` varchar(128) NOT NULL COMMENT '场景名称',
    `description` varchar(255) DEFAULT NULL COMMENT '描述',
    `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    `created_by` varchar(100) NOT NULL COMMENT '创建人',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` varchar(100) NOT NULL COMMENT '更新人',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_scene_code` (`scene_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知场景表';

CREATE TABLE IF NOT EXISTS `base_sys_notification_template` (
    `id` varchar(32) NOT NULL COMMENT '主键ID',
    `scene_code` varchar(64) NOT NULL COMMENT '场景编码',
    `channel` varchar(32) NOT NULL COMMENT '发送渠道',
    `template_name` varchar(128) NOT NULL COMMENT '模板名称',
    `title` varchar(255) DEFAULT NULL COMMENT '标题',
    `content` text NOT NULL COMMENT '模板内容',
    `param_schema` varchar(1000) DEFAULT NULL COMMENT '参数说明',
    `sort` int NOT NULL DEFAULT 1 COMMENT '排序',
    `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    `created_by` varchar(100) NOT NULL COMMENT '创建人',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` varchar(100) NOT NULL COMMENT '更新人',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_scene_channel` (`scene_code`, `channel`),
    KEY `idx_scene_enabled_sort` (`scene_code`, `enabled`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知渠道模板表';

CREATE TABLE IF NOT EXISTS `base_sys_notification_record` (
    `id` varchar(32) NOT NULL COMMENT '主键ID',
    `scene_code` varchar(64) NOT NULL COMMENT '场景编码',
    `channel` varchar(32) NOT NULL COMMENT '发送渠道',
    `target` varchar(255) NOT NULL COMMENT '发送目标',
    `template_id` varchar(32) DEFAULT NULL COMMENT '模板ID',
    `template_title` varchar(255) DEFAULT NULL COMMENT '标题快照',
    `template_content` text COMMENT '内容快照',
    `args_json` text COMMENT '参数JSON',
    `status` varchar(32) NOT NULL COMMENT '发送状态',
    `message_id` varchar(128) DEFAULT NULL COMMENT '消息ID',
    `failure_reason` text COMMENT '失败原因',
    `retry_count` int NOT NULL DEFAULT 0 COMMENT '重试次数',
    `next_retry_time` datetime DEFAULT NULL COMMENT '下次重试时间',
    `sent_time` datetime DEFAULT NULL COMMENT '发送时间',
    `created_by` varchar(100) NOT NULL COMMENT '创建人',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` varchar(100) NOT NULL COMMENT '更新人',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_scene_channel_status` (`scene_code`, `channel`, `status`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知发送记录表';

DROP TABLE IF EXISTS base_sys_dict_item;
DROP TABLE IF EXISTS base_sys_dict_type;
CREATE TABLE IF NOT EXISTS `base_sys_dict_type` (
    `id` varchar(32) NOT NULL COMMENT '主键ID',
    `name` varchar(128) NOT NULL COMMENT '字典名称',
    `dict_code` varchar(64) NOT NULL COMMENT '字典编码',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(1:启用;0:禁用)',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `created_by` varchar(100) NOT NULL COMMENT '创建人',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` varchar(100) NOT NULL COMMENT '更新人',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_code` (`dict_code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典类型表';

CREATE TABLE IF NOT EXISTS `base_sys_dict_item` (
    `id` varchar(32) NOT NULL COMMENT '主键ID',
    `dict_code` varchar(64) NOT NULL COMMENT '字典编码',
    `label` varchar(128) NOT NULL COMMENT '字典项标签',
    `value` varchar(128) NOT NULL COMMENT '字典项值',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(1:启用;0:禁用)',
    `sort` int NOT NULL DEFAULT 1 COMMENT '排序',
    `tag_type` varchar(16) NOT NULL DEFAULT 'N' COMMENT '标签类型(N/P/S/W/I/D)',
    `created_by` varchar(100) NOT NULL COMMENT '创建人',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` varchar(100) NOT NULL COMMENT '更新人',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_value` (`dict_code`, `value`),
    KEY `idx_dict_status_sort` (`dict_code`, `status`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典项表';
