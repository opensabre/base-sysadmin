-- 可重复执行：存量管理员字典保留空归属，首次应用注册不能自动接管。
SET @dictionary_owner_column_exists = (
    SELECT COUNT(1)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'base_sys_dict_type'
      AND column_name = 'source_application'
);
SET @dictionary_owner_column_sql = IF(
    @dictionary_owner_column_exists = 0,
    'ALTER TABLE `base_sys_dict_type` ADD COLUMN `source_application` varchar(128) DEFAULT NULL COMMENT ''字典定义所属应用，空值表示管理员维护'' AFTER `dict_code`',
    'SELECT 1'
);
PREPARE dictionary_owner_column_statement FROM @dictionary_owner_column_sql;
EXECUTE dictionary_owner_column_statement;
DEALLOCATE PREPARE dictionary_owner_column_statement;

SET @dictionary_owner_index_exists = (
    SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'base_sys_dict_type'
      AND index_name = 'idx_dict_source_application'
);
SET @dictionary_owner_index_sql = IF(
    @dictionary_owner_index_exists = 0,
    'CREATE INDEX `idx_dict_source_application` ON `base_sys_dict_type` (`source_application`)',
    'SELECT 1'
);
PREPARE dictionary_owner_index_statement FROM @dictionary_owner_index_sql;
EXECUTE dictionary_owner_index_statement;
DEALLOCATE PREPARE dictionary_owner_index_statement;
