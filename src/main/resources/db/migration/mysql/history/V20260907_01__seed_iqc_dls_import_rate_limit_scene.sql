SET NAMES utf8mb4;

-- QualityConfigController declares this scene for DLS workbook preview/import.
INSERT INTO base_sys_ratelimit_scene
    (id, scene_code, scene_name, algorithm, dimensions, key_prefix, max_count, period,
     enabled, description, created_by, updated_by)
VALUES
    ('RL_IQC_DLS_IMPORT', 'iqc-dls-rule-import', 'IQC DLS 规则导入', 'COUNTER', 'IP',
     'iqc-dls-rule-import', 10, 60, 1, 'DLS XLSX 规则预检与导入限次', 'system', 'system')
ON DUPLICATE KEY UPDATE scene_name = VALUES(scene_name),
                        algorithm = VALUES(algorithm),
                        dimensions = VALUES(dimensions),
                        key_prefix = VALUES(key_prefix),
                        max_count = VALUES(max_count),
                        period = VALUES(period),
                        enabled = VALUES(enabled),
                        description = VALUES(description),
                        updated_time = CURRENT_TIMESTAMP(3),
                        updated_by = VALUES(updated_by);

INSERT INTO base_sys_usage_scene
    (id, object_type, object_id, usage_event, scene_name, source_app, enabled,
     description, created_by, updated_by)
VALUES
    (REPLACE(UUID(), '-', ''), 'RATE_LIMIT_SCENE', 'iqc-dls-rule-import',
     'RATE_LIMIT_CHECK', 'IQC DLS 规则导入限次检查', 'iqc-platform', 1,
     'DLS XLSX 规则预检与导入限次', 'system', 'system')
ON DUPLICATE KEY UPDATE scene_name = VALUES(scene_name),
                        source_app = VALUES(source_app),
                        enabled = VALUES(enabled),
                        description = VALUES(description),
                        updated_time = CURRENT_TIMESTAMP(3),
                        updated_by = VALUES(updated_by);
