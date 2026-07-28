USE os_base_sysadmin;
SET NAMES utf8mb4;

INSERT INTO base_sys_ratelimit_scene
    (id, scene_code, scene_name, algorithm, dimensions, key_prefix, max_count, period, enabled, description, created_by, updated_by)
VALUES
    ('RATE_LIMIT_CAPTCHA_IP', 'CAPTCHA_IP', '验证码-IP 限次', 'COUNTER', 'IP', 'captcha:ip', 5, 3600, 1, '所有验证码请求按客户端 IP 限次', 'system', 'system'),
    ('RATE_LIMIT_CAPTCHA_DEVICE', 'CAPTCHA_DEVICE', '验证码-设备限次', 'COUNTER', 'DEVICE', 'captcha:device', 5, 3600, 1, '所有验证码请求按设备限次', 'system', 'system'),
    ('RATE_LIMIT_CAPTCHA_LOGIN_IMAGE', 'CAPTCHA_LOGIN_IMAGE', '登录图形验证码-业务限次', 'COUNTER', 'BUSINESS', 'captcha:business:LOGIN_IMAGE', 100, 3600, 1, '登录图形验证码按业务标识限次', 'system', 'system'),
    ('RATE_LIMIT_CAPTCHA_REG_IMAGE', 'CAPTCHA_REGISTER_IMAGE', '注册图形验证码-业务限次', 'COUNTER', 'BUSINESS', 'captcha:business:REGISTER_IMAGE', 50, 3600, 1, '注册图形验证码按业务标识限次', 'system', 'system'),
    ('RATE_LIMIT_CAPTCHA_LOGIN_SMS', 'CAPTCHA_LOGIN_SMS', '登录短信验证码-业务限次', 'COUNTER', 'BUSINESS', 'captcha:business:LOGIN_SMS', 100, 3600, 1, '登录短信验证码按业务标识限次', 'system', 'system'),
    ('RATE_LIMIT_CAPTCHA_LOGIN_EMAIL', 'CAPTCHA_LOGIN_EMAIL', '登录邮箱验证码-业务限次', 'COUNTER', 'BUSINESS', 'captcha:business:LOGIN_EMAIL', 100, 3600, 1, '登录邮箱验证码按业务标识限次', 'system', 'system')
ON DUPLICATE KEY UPDATE scene_name = VALUES(scene_name),
                        algorithm = VALUES(algorithm),
                        dimensions = VALUES(dimensions),
                        key_prefix = VALUES(key_prefix),
                        max_count = VALUES(max_count),
                        period = VALUES(period),
                        enabled = VALUES(enabled),
                        description = VALUES(description),
                        updated_time = CURRENT_TIMESTAMP,
                        updated_by = VALUES(updated_by);
