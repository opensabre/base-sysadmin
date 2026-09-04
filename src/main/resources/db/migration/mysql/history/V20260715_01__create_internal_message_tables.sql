SET NAMES utf8mb4;

-- 站内信：消息主体与收件人快照。发布时由应用将目标用户名固化为收件人记录。
CREATE TABLE IF NOT EXISTS `base_sys_internal_message` (
    `id` varchar(32) NOT NULL COMMENT '主键ID',
    `kind` varchar(32) NOT NULL COMMENT '消息类型：ANNOUNCEMENT/NOTIFICATION',
    `title` varchar(255) NOT NULL COMMENT '标题',
    `content` text NOT NULL COMMENT '富文本内容',
    `level` varchar(16) NOT NULL DEFAULT 'L' COMMENT '消息等级',
    `target_scope` varchar(32) NOT NULL COMMENT '目标范围',
    `target_usernames` text NOT NULL COMMENT '草稿目标用户名快照，逗号分隔',
    `target_url` varchar(500) DEFAULT NULL COMMENT '站内跳转地址',
    `status` varchar(32) NOT NULL COMMENT '状态：DRAFT/PUBLISHED/REVOKED',
    `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
    `expire_time` datetime DEFAULT NULL COMMENT '到期时间',
    `created_by` varchar(100) NOT NULL COMMENT '创建人',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` varchar(100) NOT NULL COMMENT '更新人',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status_publish_time` (`status`, `publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站内信主体表';

CREATE TABLE IF NOT EXISTS `base_sys_internal_message_recipient` (
    `id` varchar(32) NOT NULL COMMENT '主键ID',
    `message_id` varchar(32) NOT NULL COMMENT '站内信ID',
    `username` varchar(100) NOT NULL COMMENT '接收用户名',
    `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
    `created_by` varchar(100) NOT NULL COMMENT '创建人',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` varchar(100) NOT NULL COMMENT '更新人',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_message_username` (`message_id`, `username`),
    KEY `idx_username_read_time` (`username`, `read_time`, `created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站内信收件人表';
