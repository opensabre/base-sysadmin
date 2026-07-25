CREATE TABLE IF NOT EXISTS `base_sys_error_catalog` (
    `id` varchar(32) NOT NULL COMMENT '主键ID',
    `code` varchar(64) NOT NULL COMMENT '全局错误码',
    `default_message` varchar(500) NOT NULL COMMENT '默认文案',
    `source_application` varchar(128) NOT NULL COMMENT '声明应用',
    `module` varchar(128) NOT NULL COMMENT '所属模块',
    `source_version` varchar(64) DEFAULT NULL COMMENT '声明版本',
    `http_status` int DEFAULT NULL COMMENT 'HTTP状态码',
    `public_visible` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否对外展示',
    `deprecated` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已废弃',
    `description` varchar(1000) DEFAULT NULL COMMENT '说明',
    `created_by` varchar(100) NOT NULL DEFAULT 'system',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_by` varchar(100) NOT NULL DEFAULT 'system',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`), UNIQUE KEY `uk_error_catalog_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='全局错误码目录';
