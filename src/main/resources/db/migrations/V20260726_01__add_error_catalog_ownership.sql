ALTER TABLE `base_sys_error_catalog`
    ADD COLUMN `owner` varchar(128) NOT NULL DEFAULT '' COMMENT '错误码定义归属' AFTER `source_application`,
    ADD COLUMN `scope` varchar(32) NOT NULL DEFAULT 'APPLICATION' COMMENT 'COMMON或APPLICATION' AFTER `owner`;

UPDATE `base_sys_error_catalog`
SET `owner` = `source_application`
WHERE `owner` = '';

-- 0.6.0 上报的 framework 模块统一迁移为公共定义归属。
UPDATE `base_sys_error_catalog`
SET `owner` = 'opensabre-framework',
    `scope` = 'COMMON'
WHERE `module` = 'framework';
