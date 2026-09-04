SET NAMES utf8mb4;

-- IQC controllers declare named rate-limit scenes. Sysadmin requires every named scene
-- to exist and deliberately does not fall back to annotation values for unknown names.
INSERT INTO base_sys_ratelimit_scene
    (id, scene_code, scene_name, algorithm, dimensions, key_prefix, max_count, period, enabled, description, created_by, updated_by)
VALUES
    ('RL_IQC_CONV_IMPORT', 'iqc-conversation-import', 'IQC 单文件会话导入', 'COUNTER', 'IP', 'iqc-conversation-import', 30, 60, 1, '单文件 TXT 会话导入限次', 'system', 'system'),
    ('RL_IQC_CONV_BATCH_IMPORT', 'iqc-conversation-batch-import', 'IQC 批量会话导入', 'COUNTER', 'IP', 'iqc-conversation-batch-import', 10, 60, 1, '批量 TXT 会话导入限次', 'system', 'system'),
    ('RL_IQC_CONV_ZIP_IMPORT', 'iqc-conversation-zip-import', 'IQC ZIP 会话导入', 'COUNTER', 'IP', 'iqc-conversation-zip-import', 10, 60, 1, 'ZIP 会话导入限次', 'system', 'system'),
    ('RL_IQC_CONV_INGEST', 'iqc-conversation-ingest', 'IQC 单会话接口接入', 'COUNTER', 'IP', 'iqc-conversation-ingest', 120, 60, 1, '单会话接口接入限次', 'system', 'system'),
    ('RL_IQC_CONV_INGEST_BATCH', 'iqc-conversation-ingest-batch', 'IQC 批量会话接口接入', 'COUNTER', 'IP', 'iqc-conversation-ingest-batch', 30, 60, 1, '批量会话接口接入限次', 'system', 'system'),
    ('RL_IQC_CONV_QUERY', 'iqc-conversation-query', 'IQC 会话查询', 'COUNTER', 'IP', 'iqc-conversation-query', 60, 60, 1, '会话列表和详情查询限次', 'system', 'system'),
    ('RL_IQC_TASK_QUERY', 'iqc-task-query', 'IQC 任务查询', 'COUNTER', 'IP', 'iqc-task-query', 60, 60, 1, '质检任务查询限次', 'system', 'system'),
    ('RL_IQC_TASK_CREATE', 'iqc-task-create', 'IQC 任务创建', 'COUNTER', 'IP', 'iqc-task-create', 20, 60, 1, '质检任务创建限次', 'system', 'system'),
    ('RL_IQC_TASK_RUN', 'iqc-task-run', 'IQC 任务执行', 'COUNTER', 'IP', 'iqc-task-run', 10, 60, 1, '质检任务执行限次', 'system', 'system'),
    ('RL_IQC_RESULT_QUERY', 'iqc-result-query', 'IQC 结果查询', 'COUNTER', 'IP', 'iqc-result-query', 60, 60, 1, '质检结果查询限次', 'system', 'system'),
    ('RL_IQC_RESULT_EXPORT', 'iqc-result-export', 'IQC 结果导出', 'COUNTER', 'IP', 'iqc-result-export', 10, 60, 1, '质检结果导出限次', 'system', 'system'),
    ('RL_IQC_RESULT_FEEDBACK', 'iqc-result-feedback', 'IQC 结果反馈', 'COUNTER', 'IP', 'iqc-result-feedback', 60, 60, 1, '质检结果人工标注限次', 'system', 'system'),
    ('RL_IQC_BOOTSTRAP', 'iqc-bootstrap', 'IQC 基础状态查询', 'COUNTER', 'IP', 'iqc-bootstrap', 60, 60, 1, 'IQC 基础状态查询限次', 'system', 'system'),
    ('RL_IQC_DASHBOARD_QUERY', 'iqc-dashboard-query', 'IQC 总览查询', 'COUNTER', 'IP', 'iqc-dashboard-query', 30, 60, 1, '质检总览查询限次', 'system', 'system'),
    ('RL_IQC_TEMPLATE_QUERY', 'iqc-template-query', 'IQC 模板查询', 'COUNTER', 'IP', 'iqc-template-query', 60, 60, 1, '质检模板查询限次', 'system', 'system'),
    ('RL_IQC_TEMPLATE_MATERIALIZE', 'iqc-template-materialize', 'IQC 模板实例化', 'COUNTER', 'IP', 'iqc-template-materialize', 10, 60, 1, '从模板创建规则限次', 'system', 'system'),
    ('RL_IQC_SETTINGS_QUERY', 'iqc-settings-query', 'IQC 设置查询', 'COUNTER', 'IP', 'iqc-settings-query', 60, 60, 1, 'IQC 设置查询限次', 'system', 'system'),
    ('RL_IQC_DICTIONARY_QUERY', 'iqc-dictionary-query', 'IQC 字典查询', 'COUNTER', 'IP', 'iqc-dictionary-query', 60, 60, 1, 'IQC 字典选项查询限次', 'system', 'system'),
    ('RL_IQC_AGENT_CREATE', 'iqc-agent-create', 'IQC Agent 创建', 'COUNTER', 'IP', 'iqc-agent-create', 20, 60, 1, 'IQC Agent 创建限次', 'system', 'system'),
    ('RL_IQC_RULE_CREATE', 'iqc-rule-create', 'IQC 规则创建', 'COUNTER', 'IP', 'iqc-rule-create', 30, 60, 1, 'IQC 规则创建限次', 'system', 'system'),
    ('RL_IQC_RULE_TEST', 'iqc-rule-test', 'IQC 规则测试', 'COUNTER', 'IP', 'iqc-rule-test', 60, 60, 1, 'IQC 规则测试限次', 'system', 'system'),
    ('RL_IQC_SKILL_CREATE', 'iqc-skill-create', 'IQC Skill 创建', 'COUNTER', 'IP', 'iqc-skill-create', 20, 60, 1, 'IQC Skill 创建限次', 'system', 'system')
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
    (id, object_type, object_id, usage_event, scene_name, source_app, enabled, description, created_by, updated_by)
SELECT REPLACE(UUID(), '-', ''), 'RATE_LIMIT_SCENE', scene_code, 'RATE_LIMIT_CHECK',
       CONCAT(scene_name, '限次检查'), 'iqc-platform', enabled, description, 'system', 'system'
FROM base_sys_ratelimit_scene
WHERE scene_code LIKE 'iqc-%'
ON DUPLICATE KEY UPDATE scene_name = VALUES(scene_name),
                        source_app = VALUES(source_app),
                        enabled = VALUES(enabled),
                        description = VALUES(description),
                        updated_time = CURRENT_TIMESTAMP(3),
                        updated_by = VALUES(updated_by);
