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
