CREATE TABLE IF NOT EXISTS `base_sys_usage_scene` (
    `id` varchar(32) NOT NULL COMMENT '主键ID',
    `object_type` varchar(64) NOT NULL COMMENT '对象类型',
    `object_id` varchar(128) NOT NULL COMMENT '对象ID',
    `usage_event` varchar(64) NOT NULL COMMENT '使用事件',
    `scene_name` varchar(128) NOT NULL COMMENT '场景名称',
    `source_app` varchar(64) DEFAULT NULL COMMENT '所属应用',
    `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否允许计次',
    `description` varchar(255) DEFAULT NULL COMMENT '描述',
    `created_by` varchar(100) NOT NULL DEFAULT 'system',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_by` varchar(100) NOT NULL DEFAULT 'system',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_usage_scene` (`object_type`, `object_id`, `usage_event`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='计次场景登记表';

INSERT INTO base_sys_usage_scene (id, object_type, object_id, usage_event, scene_name, source_app, enabled, description, created_by, updated_by)
SELECT REPLACE(UUID(), '-', ''), 'CAPTCHA_SCENE', scene_code, 'CAPTCHA_GENERATE', CONCAT(scene_name, '验证码生成'), 'base-sysadmin', enabled, description, 'system', 'system' FROM base_sys_captcha_scene
ON DUPLICATE KEY UPDATE scene_name = VALUES(scene_name), enabled = VALUES(enabled), description = VALUES(description), updated_by = 'system';
INSERT INTO base_sys_usage_scene (id, object_type, object_id, usage_event, scene_name, source_app, enabled, description, created_by, updated_by)
SELECT REPLACE(UUID(), '-', ''), 'CAPTCHA_SCENE', scene_code, 'CAPTCHA_VERIFY', CONCAT(scene_name, '验证码校验'), 'base-sysadmin', enabled, description, 'system', 'system' FROM base_sys_captcha_scene
ON DUPLICATE KEY UPDATE scene_name = VALUES(scene_name), enabled = VALUES(enabled), description = VALUES(description), updated_by = 'system';
INSERT INTO base_sys_usage_scene (id, object_type, object_id, usage_event, scene_name, source_app, enabled, description, created_by, updated_by)
SELECT REPLACE(UUID(), '-', ''), 'RATE_LIMIT_SCENE', scene_code, 'RATE_LIMIT_CHECK', CONCAT(scene_name, '限次检查'), 'base-sysadmin', enabled, description, 'system', 'system' FROM base_sys_ratelimit_scene
ON DUPLICATE KEY UPDATE scene_name = VALUES(scene_name), enabled = VALUES(enabled), description = VALUES(description), updated_by = 'system';
INSERT INTO base_sys_usage_scene (id, object_type, object_id, usage_event, scene_name, source_app, enabled, description, created_by, updated_by)
SELECT REPLACE(UUID(), '-', ''), 'NOTIFICATION_SCENE', scene_code, 'NOTIFICATION_SEND', CONCAT(scene_name, '通知发送'), 'base-sysadmin', enabled, description, 'system', 'system' FROM base_sys_notification_scene
ON DUPLICATE KEY UPDATE scene_name = VALUES(scene_name), enabled = VALUES(enabled), description = VALUES(description), updated_by = 'system';
INSERT INTO base_sys_usage_scene (id, object_type, object_id, usage_event, scene_name, source_app, enabled, description, created_by, updated_by)
SELECT REPLACE(UUID(), '-', ''), 'NOTIFICATION_TEMPLATE', id, 'NOTIFICATION_SEND', CONCAT(template_name, '通知发送'), 'base-sysadmin', enabled, NULL, 'system', 'system' FROM base_sys_notification_template
ON DUPLICATE KEY UPDATE scene_name = VALUES(scene_name), enabled = VALUES(enabled), updated_by = 'system';
