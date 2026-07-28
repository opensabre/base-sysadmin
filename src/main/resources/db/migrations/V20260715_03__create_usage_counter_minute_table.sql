CREATE TABLE IF NOT EXISTS `base_sys_usage_counter_minute` (
    `id` varchar(32) NOT NULL COMMENT '主键ID',
    `bucket_start` datetime NOT NULL COMMENT '分钟统计起点',
    `object_type` varchar(64) NOT NULL COMMENT '对象类型',
    `object_id` varchar(128) NOT NULL COMMENT '对象ID',
    `usage_event` varchar(64) NOT NULL COMMENT '使用事件',
    `attempt_count` bigint unsigned NOT NULL DEFAULT 0 COMMENT '发起次数',
    `success_count` bigint unsigned NOT NULL DEFAULT 0 COMMENT '成功次数',
    `failure_count` bigint unsigned NOT NULL DEFAULT 0 COMMENT '失败次数',
    `created_by` varchar(100) NOT NULL DEFAULT 'system' COMMENT '创建人',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` varchar(100) NOT NULL DEFAULT 'system' COMMENT '更新人',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_bucket_object_event` (`bucket_start`, `object_type`, `object_id`, `usage_event`),
    KEY `idx_bucket_start` (`bucket_start`),
    KEY `idx_object_event_bucket` (`object_type`, `object_id`, `usage_event`, `bucket_start`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='对象使用分钟计次表';
