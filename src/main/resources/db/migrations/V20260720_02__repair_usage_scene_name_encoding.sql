-- 修复 V20260720_01 在错误客户端连接字符集下执行时造成的固定中文后缀乱码。
-- 使用 UTF-8 十六进制字面量，避免再次受 mysql 客户端字符集影响。
UPDATE base_sys_usage_scene target
JOIN base_sys_captcha_scene source ON target.object_id = source.scene_code
SET target.scene_name = CONCAT(source.scene_name, CONVERT(0xE9AA8CE8AF81E7A081E7949FE68890 USING utf8mb4))
WHERE target.object_type = 'CAPTCHA_SCENE'
  AND target.usage_event = 'CAPTCHA_GENERATE'
  AND LOCATE(CONVERT(0xC3A9C2AA USING utf8mb4), target.scene_name) > 0;

UPDATE base_sys_usage_scene target
JOIN base_sys_captcha_scene source ON target.object_id = source.scene_code
SET target.scene_name = CONCAT(source.scene_name, CONVERT(0xE9AA8CE8AF81E7A081E6A0A1E9AA8C USING utf8mb4))
WHERE target.object_type = 'CAPTCHA_SCENE'
  AND target.usage_event = 'CAPTCHA_VERIFY'
  AND LOCATE(CONVERT(0xC3A9C2AA USING utf8mb4), target.scene_name) > 0;

UPDATE base_sys_usage_scene target
JOIN base_sys_ratelimit_scene source ON target.object_id = source.scene_code
SET target.scene_name = CONCAT(source.scene_name, CONVERT(0xE99990E6ACA1E6A380E69FA5 USING utf8mb4))
WHERE target.object_type = 'RATE_LIMIT_SCENE'
  AND target.usage_event = 'RATE_LIMIT_CHECK'
  AND LOCATE(CONVERT(0xC3A9C2AA USING utf8mb4), target.scene_name) > 0;

UPDATE base_sys_usage_scene target
JOIN base_sys_notification_scene source ON target.object_id = source.scene_code
SET target.scene_name = CONCAT(source.scene_name, CONVERT(0xE9809AE79FA5E58F91E98081 USING utf8mb4))
WHERE target.object_type = 'NOTIFICATION_SCENE'
  AND target.usage_event = 'NOTIFICATION_SEND'
  AND LOCATE(CONVERT(0xC3A9C2AA USING utf8mb4), target.scene_name) > 0;

UPDATE base_sys_usage_scene target
JOIN base_sys_notification_template source ON target.object_id = source.id
SET target.scene_name = CONCAT(source.template_name, CONVERT(0xE9809AE79FA5E58F91E98081 USING utf8mb4))
WHERE target.object_type = 'NOTIFICATION_TEMPLATE'
  AND target.usage_event = 'NOTIFICATION_SEND'
  AND LOCATE(CONVERT(0xC3A9C2AA USING utf8mb4), target.scene_name) > 0;
