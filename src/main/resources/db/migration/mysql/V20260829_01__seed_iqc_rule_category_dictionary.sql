SET NAMES utf8mb4;

-- 修复历史上以 latin1 连接写入的 IQC 分类中文值（幂等）。
UPDATE `base_sys_dict_item`
SET `label` = CONVERT(BINARY CONVERT(`label` USING latin1) USING utf8mb4)
WHERE `dict_code` = 'iqc_rule_category' AND HEX(`label`) LIKE 'C3%';

-- IQC 规则分类是后台维护字典，允许管理员在字典管理中新增、修改和停用分类。
INSERT INTO `base_sys_dict_type`
    (`id`, `name`, `dict_code`, `source_application`, `status`, `remark`, `created_by`, `updated_by`)
VALUES
    ('DICT_IQC_RULE_CATEGORY', 'IQC 规则分类', 'iqc_rule_category', NULL, 1,
     'IQC 规则业务分类（管理员可维护）', 'system', 'system')
ON DUPLICATE KEY UPDATE
    `name` = VALUES(`name`),
    `source_application` = NULL,
    `status` = 1,
    `remark` = VALUES(`remark`),
    `updated_by` = 'system';

INSERT INTO `base_sys_dict_item`
    (`id`, `dict_code`, `label`, `value`, `status`, `sort`, `tag_type`, `created_by`, `updated_by`)
VALUES
    ('DICT_IQC_RULE_CAT_SERVICE', 'iqc_rule_category', '服务质量', 'SERVICE_QUALITY', 1, 10, 'P', 'system', 'system'),
    ('DICT_IQC_RULE_CAT_COMPLIANCE', 'iqc_rule_category', '合规审查', 'COMPLIANCE', 1, 20, 'W', 'system', 'system'),
    ('DICT_IQC_RULE_CAT_SALES', 'iqc_rule_category', '销售规范', 'SALES', 1, 30, 'S', 'system', 'system'),
    ('DICT_IQC_RULE_CAT_RISK', 'iqc_rule_category', '风险控制', 'RISK_CONTROL', 1, 40, 'D', 'system', 'system'),
    ('DICT_IQC_RULE_CAT_PRIVACY', 'iqc_rule_category', '数据与隐私', 'DATA_PRIVACY', 1, 50, 'I', 'system', 'system'),
    ('DICT_IQC_RULE_CAT_CUSTOM', 'iqc_rule_category', '自定义', 'CUSTOM', 1, 99, 'N', 'system', 'system')
ON DUPLICATE KEY UPDATE
    `label` = VALUES(`label`),
    `status` = 1,
    `sort` = VALUES(`sort`),
    `tag_type` = VALUES(`tag_type`),
    `updated_by` = 'system';

INSERT INTO `base_sys_dict_type`
    (`id`, `name`, `dict_code`, `source_application`, `status`, `remark`, `created_by`, `updated_by`)
VALUES
    ('DICT_IQC_CONVERSATION_CHANNEL', '会话渠道', 'iqc_conversation_channel', 'iqc-platform', 1, 'IQC 会话来源渠道（系统枚举）', 'system', 'system'),
    ('DICT_IQC_BUSINESS_TYPE', '业务类型', 'iqc_business_type', NULL, 1, 'IQC 业务类型（管理员可维护）', 'system', 'system')
ON DUPLICATE KEY UPDATE
    `name` = VALUES(`name`), `status` = 1, `remark` = VALUES(`remark`), `updated_by` = 'system';

INSERT INTO `base_sys_dict_item`
    (`id`, `dict_code`, `label`, `value`, `status`, `sort`, `tag_type`, `created_by`, `updated_by`)
VALUES
    ('DICT_IQC_CHANNEL_WEB', 'iqc_conversation_channel', '网页', 'WEB', 1, 10, 'P', 'system', 'system'),
    ('DICT_IQC_CHANNEL_PHONE', 'iqc_conversation_channel', '电话', 'PHONE', 1, 20, 'S', 'system', 'system'),
    ('DICT_IQC_CHANNEL_WECHAT', 'iqc_conversation_channel', '微信', 'WECHAT', 1, 30, 'P', 'system', 'system'),
    ('DICT_IQC_CHANNEL_APP', 'iqc_conversation_channel', 'App', 'APP', 1, 40, 'S', 'system', 'system'),
    ('DICT_IQC_CHANNEL_OTHER', 'iqc_conversation_channel', '其他', 'OTHER', 1, 99, 'N', 'system', 'system'),
    ('DICT_IQC_BUSINESS_ORDER', 'iqc_business_type', '订单', 'ORDER', 1, 10, 'P', 'system', 'system'),
    ('DICT_IQC_BUSINESS_TICKET', 'iqc_business_type', '工单', 'TICKET', 1, 20, 'W', 'system', 'system'),
    ('DICT_IQC_BUSINESS_COMPLAINT', 'iqc_business_type', '投诉', 'COMPLAINT', 1, 30, 'D', 'system', 'system')
ON DUPLICATE KEY UPDATE
    `label` = VALUES(`label`), `status` = 1, `sort` = VALUES(`sort`), `tag_type` = VALUES(`tag_type`), `updated_by` = 'system';
