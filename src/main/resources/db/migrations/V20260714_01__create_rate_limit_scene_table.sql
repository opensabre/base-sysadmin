USE os_base_sysadmin;
SET NAMES utf8mb4;

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
