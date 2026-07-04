SET NAMES utf8;

DROP DATABASE IF EXISTS os_base_sysadmin;
CREATE DATABASE os_base_sysadmin DEFAULT CHARSET utf8mb4;
USE os_base_sysadmin;

INSERT INTO `base_sys_captcha_scene` (`id`, `scene_code`, `scene_name`, `captcha_type`, `template_code`, `description`, `captcha_length`, `captcha_expire_time`, `captcha_attempts`, `min_interval`, `max_limit_count`, `enabled`, `created_by`, `updated_by`)
VALUES
('LOGIN_IMAGE', 'LOGIN_IMAGE', '登录时图形验证码', 'IMAGE', NULL, '登录时图形验证码', 4, 300, 1, 60, 100, 1, 'system', 'system'),
('REGISTER_IMAGE', 'REGISTER_IMAGE', '注册时图形验证码', 'IMAGE', NULL, '注册时图形验证码', 4, 60, 3, 60, 50, 1, 'system', 'system'),
('LOGIN_SMS', 'LOGIN_SMS', '登录时短信验证码', 'SMS', 'CAPTCHA', '登录时短信验证码', 6, 60, 2, 60, 100, 1, 'system', 'system'),
('LOGIN_EMAIL', 'LOGIN_EMAIL', '登录时邮箱验证码', 'EMAIL', 'CAPTCHA', '登录时邮箱验证码', 6, 300, 3, 60, 100, 1, 'system', 'system');

INSERT INTO `base_sys_dict_type` (`id`, `name`, `dict_code`, `status`, `remark`, `created_by`, `updated_by`)
VALUES
('DICT_GENDER', '性别', 'gender', 1, '用户性别', 'system', 'system'),
('DICT_NOTICE_LEVEL', '通知级别', 'notice_level', 1, '通知公告级别', 'system', 'system'),
('DICT_NOTICE_TYPE', '通知类型', 'notice_type', 1, '通知公告类型', 'system', 'system');

INSERT INTO `base_sys_dict_item` (`id`, `dict_code`, `label`, `value`, `status`, `sort`, `tag_type`, `created_by`, `updated_by`)
VALUES
('DICT_GENDER_M', 'gender', '男', 'M', 1, 1, 'P', 'system', 'system'),
('DICT_GENDER_F', 'gender', '女', 'F', 1, 2, 'D', 'system', 'system'),
('DICT_GENDER_UNKNOWN', 'gender', '保密', 'UNKNOWN', 1, 3, 'I', 'system', 'system'),
('DICT_NOTICE_LEVEL_L', 'notice_level', '低', 'L', 1, 1, 'I', 'system', 'system'),
('DICT_NOTICE_LEVEL_M', 'notice_level', '中', 'M', 1, 2, 'W', 'system', 'system'),
('DICT_NOTICE_LEVEL_H', 'notice_level', '高', 'H', 1, 3, 'D', 'system', 'system'),
('DICT_NOTICE_TYPE_UPGRADE', 'notice_type', '系统升级', 'SYSTEM_UPGRADE', 1, 1, 'S', 'system', 'system'),
('DICT_NOTICE_TYPE_MAINTENANCE', 'notice_type', '系统维护', 'SYSTEM_MAINTENANCE', 1, 2, 'P', 'system', 'system'),
('DICT_NOTICE_TYPE_SECURITY', 'notice_type', '安全警告', 'SECURITY_WARNING', 1, 3, 'D', 'system', 'system'),
('DICT_NOTICE_TYPE_HOLIDAY', 'notice_type', '假期通知', 'HOLIDAY_NOTICE', 1, 4, 'S', 'system', 'system'),
('DICT_NOTICE_TYPE_NEWS', 'notice_type', '公司新闻', 'COMPANY_NEWS', 1, 5, 'P', 'system', 'system'),
('DICT_NOTICE_TYPE_OTHER', 'notice_type', '其他', 'OTHER', 1, 99, 'I', 'system', 'system');
