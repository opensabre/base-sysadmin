-- Generated from the complete verified migration history.
-- Regenerate with base-k8s/scripts/generate-flyway-baselines.sh; do not edit manually.

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
DROP TABLE IF EXISTS `base_sys_audit_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `base_sys_audit_log` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `operation_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作类型',
  `operation_time` datetime(3) NOT NULL COMMENT '操作时间',
  `operator_username` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作人用户名',
  `module` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作模块',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作描述',
  `client_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作IP地址',
  `target_key` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作目标关键key',
  `user_agent` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户代理',
  `request_method` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求方法',
  `request_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求URL',
  `request` text COLLATE utf8mb4_unicode_ci COMMENT '请求参数',
  `response` text COLLATE utf8mb4_unicode_ci COMMENT '操作结果',
  `error_message` text COLLATE utf8mb4_unicode_ci COMMENT '错误信息',
  `execution_time` bigint DEFAULT NULL COMMENT '执行时间(毫秒)',
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建人',
  `created_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新人',
  `updated_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_operator_username` (`operator_username`),
  KEY `idx_operation_time` (`operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审计日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `base_sys_captcha_scene`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `base_sys_captcha_scene` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `scene_code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '场景编码',
  `scene_name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '场景名称',
  `captcha_type` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '验证码类型',
  `template_code` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息模板编码',
  `notification_template_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '通知模板ID',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `captcha_length` int NOT NULL DEFAULT '4' COMMENT '验证码长度',
  `captcha_expire_time` int NOT NULL DEFAULT '300' COMMENT '过期时间(秒)',
  `captcha_attempts` int NOT NULL DEFAULT '3' COMMENT '最大尝试次数',
  `min_interval` int NOT NULL DEFAULT '60' COMMENT '最小间隔(秒)',
  `max_limit_count` int NOT NULL DEFAULT '100' COMMENT '单用户生成限制次数',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建人',
  `created_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新人',
  `updated_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_scene_code` (`scene_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='验证码场景表';
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `base_sys_dict_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `base_sys_dict_item` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `dict_code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典编码',
  `label` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典项标签',
  `value` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典项值',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态(1:启用;0:禁用)',
  `sort` int NOT NULL DEFAULT '1' COMMENT '排序',
  `tag_type` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '标签类型(N/P/S/W/I/D)',
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建人',
  `created_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新人',
  `updated_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dict_value` (`dict_code`,`value`),
  KEY `idx_dict_status_sort` (`dict_code`,`status`,`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典项表';
/*!40101 SET character_set_client = @saved_cs_client */;

INSERT INTO `base_sys_dict_item` VALUES ('DICT_IQC_BUSINESS_COMPLAINT','iqc_business_type','投诉','COMPLAINT',1,30,'D','system','2026-09-04 05:00:37.971','system','2026-09-04 05:00:37.971'),('DICT_IQC_BUSINESS_ORDER','iqc_business_type','订单','ORDER',1,10,'P','system','2026-09-04 05:00:37.971','system','2026-09-04 05:00:37.971'),('DICT_IQC_BUSINESS_TICKET','iqc_business_type','工单','TICKET',1,20,'W','system','2026-09-04 05:00:37.971','system','2026-09-04 05:00:37.971'),('DICT_IQC_CHANNEL_APP','iqc_conversation_channel','App','APP',1,40,'S','system','2026-09-04 05:00:37.971','system','2026-09-04 05:00:37.971'),('DICT_IQC_CHANNEL_OTHER','iqc_conversation_channel','其他','OTHER',1,99,'N','system','2026-09-04 05:00:37.971','system','2026-09-04 05:00:37.971'),('DICT_IQC_CHANNEL_PHONE','iqc_conversation_channel','电话','PHONE',1,20,'S','system','2026-09-04 05:00:37.971','system','2026-09-04 05:00:37.971'),('DICT_IQC_CHANNEL_WEB','iqc_conversation_channel','网页','WEB',1,10,'P','system','2026-09-04 05:00:37.971','system','2026-09-04 05:00:37.971'),('DICT_IQC_CHANNEL_WECHAT','iqc_conversation_channel','微信','WECHAT',1,30,'P','system','2026-09-04 05:00:37.971','system','2026-09-04 05:00:37.971'),('DICT_IQC_RULE_CAT_COMPLIANCE','iqc_rule_category','合规审查','COMPLIANCE',1,20,'W','system','2026-09-04 05:00:37.969','system','2026-09-04 05:00:37.969'),('DICT_IQC_RULE_CAT_CUSTOM','iqc_rule_category','自定义','CUSTOM',1,99,'N','system','2026-09-04 05:00:37.969','system','2026-09-04 05:00:37.969'),('DICT_IQC_RULE_CAT_PRIVACY','iqc_rule_category','数据与隐私','DATA_PRIVACY',1,50,'I','system','2026-09-04 05:00:37.969','system','2026-09-04 05:00:37.969'),('DICT_IQC_RULE_CAT_RISK','iqc_rule_category','风险控制','RISK_CONTROL',1,40,'D','system','2026-09-04 05:00:37.969','system','2026-09-04 05:00:37.969'),('DICT_IQC_RULE_CAT_SALES','iqc_rule_category','销售规范','SALES',1,30,'S','system','2026-09-04 05:00:37.969','system','2026-09-04 05:00:37.969'),('DICT_IQC_RULE_CAT_SERVICE','iqc_rule_category','服务质量','SERVICE_QUALITY',1,10,'P','system','2026-09-04 05:00:37.969','system','2026-09-04 05:00:37.969');
DROP TABLE IF EXISTS `base_sys_dict_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `base_sys_dict_type` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典名称',
  `dict_code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典编码',
  `source_application` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '字典定义所属应用，空值表示管理员维护',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态(1:启用;0:禁用)',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建人',
  `created_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新人',
  `updated_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dict_code` (`dict_code`),
  KEY `idx_status` (`status`),
  KEY `idx_dict_source_application` (`source_application`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

INSERT INTO `base_sys_dict_type` VALUES ('DICT_IQC_BUSINESS_TYPE','业务类型','iqc_business_type',NULL,1,'IQC 业务类型（管理员可维护）','system','2026-09-04 05:00:37.970','system','2026-09-04 05:00:37.970'),('DICT_IQC_CONVERSATION_CHANNEL','会话渠道','iqc_conversation_channel','iqc-platform',1,'IQC 会话来源渠道（系统枚举）','system','2026-09-04 05:00:37.970','system','2026-09-04 05:00:37.970'),('DICT_IQC_RULE_CATEGORY','IQC 规则分类','iqc_rule_category',NULL,1,'IQC 规则业务分类（管理员可维护）','system','2026-09-04 05:00:37.968','system','2026-09-04 05:00:37.968');
DROP TABLE IF EXISTS `base_sys_error_catalog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `base_sys_error_catalog` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '全局错误码',
  `default_message` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '默认文案',
  `source_application` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '声明应用',
  `owner` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '错误码定义归属',
  `scope` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'APPLICATION' COMMENT 'COMMON或APPLICATION',
  `module` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所属模块',
  `source_version` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '声明版本',
  `http_status` int DEFAULT NULL COMMENT 'HTTP状态码',
  `public_visible` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否对外展示',
  `deprecated` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已废弃',
  `description` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '说明',
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'system',
  `created_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'system',
  `updated_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_error_catalog_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='全局错误码目录';
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `base_sys_internal_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `base_sys_internal_message` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `kind` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息类型：ANNOUNCEMENT/NOTIFICATION',
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '富文本内容',
  `level` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'L' COMMENT '消息等级',
  `target_scope` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标范围',
  `target_usernames` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '草稿目标用户名快照，逗号分隔',
  `target_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '站内跳转地址',
  `status` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '状态：DRAFT/PUBLISHED/REVOKED',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `expire_time` datetime DEFAULT NULL COMMENT '到期时间',
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建人',
  `created_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新人',
  `updated_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status_publish_time` (`status`,`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站内信主体表';
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `base_sys_internal_message_recipient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `base_sys_internal_message_recipient` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `message_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '站内信ID',
  `username` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '接收用户名',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建人',
  `created_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新人',
  `updated_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_message_username` (`message_id`,`username`),
  KEY `idx_username_read_time` (`username`,`read_time`,`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站内信收件人表';
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `base_sys_notification_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `base_sys_notification_record` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `scene_code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '场景编码',
  `channel` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送渠道',
  `target` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送目标',
  `template_id` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模板ID',
  `template_title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题快照',
  `template_content` text COLLATE utf8mb4_unicode_ci COMMENT '内容快照',
  `args_json` text COLLATE utf8mb4_unicode_ci COMMENT '参数JSON',
  `status` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送状态',
  `message_id` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息ID',
  `failure_reason` text COLLATE utf8mb4_unicode_ci COMMENT '失败原因',
  `retry_count` int NOT NULL DEFAULT '0' COMMENT '重试次数',
  `next_retry_time` datetime DEFAULT NULL COMMENT '下次重试时间',
  `sent_time` datetime DEFAULT NULL COMMENT '发送时间',
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建人',
  `created_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新人',
  `updated_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_scene_channel_status` (`scene_code`,`channel`,`status`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知发送记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `base_sys_notification_scene`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `base_sys_notification_scene` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `scene_code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '场景编码',
  `scene_name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '场景名称',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建人',
  `created_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新人',
  `updated_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_scene_code` (`scene_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知场景表';
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `base_sys_notification_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `base_sys_notification_template` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `scene_code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '场景编码',
  `channel` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送渠道',
  `template_name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板名称',
  `title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板内容',
  `param_schema` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '参数说明',
  `sort` int NOT NULL DEFAULT '1' COMMENT '排序',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建人',
  `created_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新人',
  `updated_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_scene_channel` (`scene_code`,`channel`),
  KEY `idx_scene_enabled_sort` (`scene_code`,`enabled`,`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知渠道模板表';
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `base_sys_ratelimit_scene`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `base_sys_ratelimit_scene` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `scene_code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '场景编码',
  `scene_name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '场景名称',
  `algorithm` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '限次算法',
  `dimensions` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '维度代码，逗号分隔',
  `key_prefix` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务前缀',
  `max_count` int NOT NULL DEFAULT '5' COMMENT '最大次数',
  `period` int NOT NULL DEFAULT '60' COMMENT '时间窗口(秒)',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建人',
  `created_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新人',
  `updated_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_scene_code` (`scene_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='限次场景表';
/*!40101 SET character_set_client = @saved_cs_client */;

INSERT INTO `base_sys_ratelimit_scene` VALUES ('RATE_LIMIT_CAPTCHA_DEVICE','CAPTCHA_DEVICE','验证码-设备限次','COUNTER','DEVICE','captcha:device',5,3600,1,'所有验证码请求按设备限次','system','2026-09-04 05:00:36.000','system','2026-09-04 05:00:36.000'),('RATE_LIMIT_CAPTCHA_IP','CAPTCHA_IP','验证码-IP 限次','COUNTER','IP','captcha:ip',5,3600,1,'所有验证码请求按客户端 IP 限次','system','2026-09-04 05:00:36.000','system','2026-09-04 05:00:36.000'),('RATE_LIMIT_CAPTCHA_LOGIN_EMAIL','CAPTCHA_LOGIN_EMAIL','登录邮箱验证码-业务限次','COUNTER','BUSINESS','captcha:business:LOGIN_EMAIL',100,3600,1,'登录邮箱验证码按业务标识限次','system','2026-09-04 05:00:36.000','system','2026-09-04 05:00:36.000'),('RATE_LIMIT_CAPTCHA_LOGIN_IMAGE','CAPTCHA_LOGIN_IMAGE','登录图形验证码-业务限次','COUNTER','BUSINESS','captcha:business:LOGIN_IMAGE',100,3600,1,'登录图形验证码按业务标识限次','system','2026-09-04 05:00:36.000','system','2026-09-04 05:00:36.000'),('RATE_LIMIT_CAPTCHA_LOGIN_SMS','CAPTCHA_LOGIN_SMS','登录短信验证码-业务限次','COUNTER','BUSINESS','captcha:business:LOGIN_SMS',100,3600,1,'登录短信验证码按业务标识限次','system','2026-09-04 05:00:36.000','system','2026-09-04 05:00:36.000'),('RATE_LIMIT_CAPTCHA_REG_IMAGE','CAPTCHA_REGISTER_IMAGE','注册图形验证码-业务限次','COUNTER','BUSINESS','captcha:business:REGISTER_IMAGE',50,3600,1,'注册图形验证码按业务标识限次','system','2026-09-04 05:00:36.000','system','2026-09-04 05:00:36.000'),('RL_IQC_AGENT_CREATE','iqc-agent-create','IQC Agent 创建','COUNTER','IP','iqc-agent-create',20,60,1,'IQC Agent 创建限次','system','2026-09-04 05:00:37.996','system','2026-09-04 05:00:37.996'),('RL_IQC_BOOTSTRAP','iqc-bootstrap','IQC 基础状态查询','COUNTER','IP','iqc-bootstrap',60,60,1,'IQC 基础状态查询限次','system','2026-09-04 05:00:37.996','system','2026-09-04 05:00:37.996'),('RL_IQC_CONV_BATCH_IMPORT','iqc-conversation-batch-import','IQC 批量会话导入','COUNTER','IP','iqc-conversation-batch-import',10,60,1,'批量 TXT 会话导入限次','system','2026-09-04 05:00:37.996','system','2026-09-04 05:00:37.996'),('RL_IQC_CONV_IMPORT','iqc-conversation-import','IQC 单文件会话导入','COUNTER','IP','iqc-conversation-import',30,60,1,'单文件 TXT 会话导入限次','system','2026-09-04 05:00:37.996','system','2026-09-04 05:00:37.996'),('RL_IQC_CONV_INGEST','iqc-conversation-ingest','IQC 单会话接口接入','COUNTER','IP','iqc-conversation-ingest',120,60,1,'单会话接口接入限次','system','2026-09-04 05:00:37.996','system','2026-09-04 05:00:37.996'),('RL_IQC_CONV_INGEST_BATCH','iqc-conversation-ingest-batch','IQC 批量会话接口接入','COUNTER','IP','iqc-conversation-ingest-batch',30,60,1,'批量会话接口接入限次','system','2026-09-04 05:00:37.996','system','2026-09-04 05:00:37.996'),('RL_IQC_CONV_QUERY','iqc-conversation-query','IQC 会话查询','COUNTER','IP','iqc-conversation-query',60,60,1,'会话列表和详情查询限次','system','2026-09-04 05:00:37.996','system','2026-09-04 05:00:37.996'),('RL_IQC_CONV_ZIP_IMPORT','iqc-conversation-zip-import','IQC ZIP 会话导入','COUNTER','IP','iqc-conversation-zip-import',10,60,1,'ZIP 会话导入限次','system','2026-09-04 05:00:37.996','system','2026-09-04 05:00:37.996'),('RL_IQC_DASHBOARD_QUERY','iqc-dashboard-query','IQC 总览查询','COUNTER','IP','iqc-dashboard-query',30,60,1,'质检总览查询限次','system','2026-09-04 05:00:37.996','system','2026-09-04 05:00:37.996'),('RL_IQC_DICTIONARY_QUERY','iqc-dictionary-query','IQC 字典查询','COUNTER','IP','iqc-dictionary-query',60,60,1,'IQC 字典选项查询限次','system','2026-09-04 05:00:37.996','system','2026-09-04 05:00:37.996'),('RL_IQC_RESULT_EXPORT','iqc-result-export','IQC 结果导出','COUNTER','IP','iqc-result-export',10,60,1,'质检结果导出限次','system','2026-09-04 05:00:37.996','system','2026-09-04 05:00:37.996'),('RL_IQC_RESULT_FEEDBACK','iqc-result-feedback','IQC 结果反馈','COUNTER','IP','iqc-result-feedback',60,60,1,'质检结果人工标注限次','system','2026-09-04 05:00:37.996','system','2026-09-04 05:00:37.996'),('RL_IQC_RESULT_QUERY','iqc-result-query','IQC 结果查询','COUNTER','IP','iqc-result-query',60,60,1,'质检结果查询限次','system','2026-09-04 05:00:37.996','system','2026-09-04 05:00:37.996'),('RL_IQC_RULE_CREATE','iqc-rule-create','IQC 规则创建','COUNTER','IP','iqc-rule-create',30,60,1,'IQC 规则创建限次','system','2026-09-04 05:00:37.996','system','2026-09-04 05:00:37.996'),('RL_IQC_RULE_TEST','iqc-rule-test','IQC 规则测试','COUNTER','IP','iqc-rule-test',60,60,1,'IQC 规则测试限次','system','2026-09-04 05:00:37.996','system','2026-09-04 05:00:37.996'),('RL_IQC_SETTINGS_QUERY','iqc-settings-query','IQC 设置查询','COUNTER','IP','iqc-settings-query',60,60,1,'IQC 设置查询限次','system','2026-09-04 05:00:37.996','system','2026-09-04 05:00:37.996'),('RL_IQC_SKILL_CREATE','iqc-skill-create','IQC Skill 创建','COUNTER','IP','iqc-skill-create',20,60,1,'IQC Skill 创建限次','system','2026-09-04 05:00:37.996','system','2026-09-04 05:00:37.996'),('RL_IQC_TASK_CREATE','iqc-task-create','IQC 任务创建','COUNTER','IP','iqc-task-create',20,60,1,'质检任务创建限次','system','2026-09-04 05:00:37.996','system','2026-09-04 05:00:37.996'),('RL_IQC_TASK_QUERY','iqc-task-query','IQC 任务查询','COUNTER','IP','iqc-task-query',60,60,1,'质检任务查询限次','system','2026-09-04 05:00:37.996','system','2026-09-04 05:00:37.996'),('RL_IQC_TASK_RUN','iqc-task-run','IQC 任务执行','COUNTER','IP','iqc-task-run',10,60,1,'质检任务执行限次','system','2026-09-04 05:00:37.996','system','2026-09-04 05:00:37.996'),('RL_IQC_TEMPLATE_MATERIALIZE','iqc-template-materialize','IQC 模板实例化','COUNTER','IP','iqc-template-materialize',10,60,1,'从模板创建规则限次','system','2026-09-04 05:00:37.996','system','2026-09-04 05:00:37.996'),('RL_IQC_TEMPLATE_QUERY','iqc-template-query','IQC 模板查询','COUNTER','IP','iqc-template-query',60,60,1,'质检模板查询限次','system','2026-09-04 05:00:37.996','system','2026-09-04 05:00:37.996');
DROP TABLE IF EXISTS `base_sys_usage_counter_minute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `base_sys_usage_counter_minute` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `bucket_start` datetime NOT NULL COMMENT '分钟统计起点',
  `object_type` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '对象类型',
  `object_id` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '对象ID',
  `usage_event` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '使用事件',
  `attempt_count` bigint unsigned NOT NULL DEFAULT '0' COMMENT '发起次数',
  `success_count` bigint unsigned NOT NULL DEFAULT '0' COMMENT '成功次数',
  `failure_count` bigint unsigned NOT NULL DEFAULT '0' COMMENT '失败次数',
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'system' COMMENT '创建人',
  `created_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'system' COMMENT '更新人',
  `updated_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bucket_object_event` (`bucket_start`,`object_type`,`object_id`,`usage_event`),
  KEY `idx_bucket_start` (`bucket_start`),
  KEY `idx_object_event_bucket` (`object_type`,`object_id`,`usage_event`,`bucket_start`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='对象使用分钟计次表';
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `base_sys_usage_scene`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `base_sys_usage_scene` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `object_type` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '对象类型',
  `object_id` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '对象ID',
  `usage_event` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '使用事件',
  `scene_name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '场景名称',
  `source_app` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属应用',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否允许计次',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'system',
  `created_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'system',
  `updated_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_usage_scene` (`object_type`,`object_id`,`usage_event`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='计次场景登记表';
/*!40101 SET character_set_client = @saved_cs_client */;

INSERT INTO `base_sys_usage_scene` VALUES ('9124a08aa81d11f1824646235496ecdd','RATE_LIMIT_SCENE','CAPTCHA_DEVICE','RATE_LIMIT_CHECK','验证码-设备限次限次检查','base-sysadmin',1,'所有验证码请求按设备限次','system','2026-09-04 05:00:36.780','system','2026-09-04 05:00:36.780'),('9124a66fa81d11f1824646235496ecdd','RATE_LIMIT_SCENE','CAPTCHA_IP','RATE_LIMIT_CHECK','验证码-IP 限次限次检查','base-sysadmin',1,'所有验证码请求按客户端 IP 限次','system','2026-09-04 05:00:36.780','system','2026-09-04 05:00:36.780'),('9124a982a81d11f1824646235496ecdd','RATE_LIMIT_SCENE','CAPTCHA_LOGIN_EMAIL','RATE_LIMIT_CHECK','登录邮箱验证码-业务限次限次检查','base-sysadmin',1,'登录邮箱验证码按业务标识限次','system','2026-09-04 05:00:36.780','system','2026-09-04 05:00:36.780'),('9124ab99a81d11f1824646235496ecdd','RATE_LIMIT_SCENE','CAPTCHA_LOGIN_IMAGE','RATE_LIMIT_CHECK','登录图形验证码-业务限次限次检查','base-sysadmin',1,'登录图形验证码按业务标识限次','system','2026-09-04 05:00:36.780','system','2026-09-04 05:00:36.780'),('9124ad9aa81d11f1824646235496ecdd','RATE_LIMIT_SCENE','CAPTCHA_LOGIN_SMS','RATE_LIMIT_CHECK','登录短信验证码-业务限次限次检查','base-sysadmin',1,'登录短信验证码按业务标识限次','system','2026-09-04 05:00:36.780','system','2026-09-04 05:00:36.780'),('9124af8aa81d11f1824646235496ecdd','RATE_LIMIT_SCENE','CAPTCHA_REGISTER_IMAGE','RATE_LIMIT_CHECK','注册图形验证码-业务限次限次检查','base-sysadmin',1,'注册图形验证码按业务标识限次','system','2026-09-04 05:00:36.780','system','2026-09-04 05:00:36.780'),('91def5eda81d11f1824646235496ecdd','RATE_LIMIT_SCENE','iqc-agent-create','RATE_LIMIT_CHECK','IQC Agent 创建限次检查','iqc-platform',1,'IQC Agent 创建限次','system','2026-09-04 05:00:38.002','system','2026-09-04 05:00:38.002'),('91def9d6a81d11f1824646235496ecdd','RATE_LIMIT_SCENE','iqc-bootstrap','RATE_LIMIT_CHECK','IQC 基础状态查询限次检查','iqc-platform',1,'IQC 基础状态查询限次','system','2026-09-04 05:00:38.002','system','2026-09-04 05:00:38.002'),('91defc8ea81d11f1824646235496ecdd','RATE_LIMIT_SCENE','iqc-conversation-batch-import','RATE_LIMIT_CHECK','IQC 批量会话导入限次检查','iqc-platform',1,'批量 TXT 会话导入限次','system','2026-09-04 05:00:38.002','system','2026-09-04 05:00:38.002'),('91df035da81d11f1824646235496ecdd','RATE_LIMIT_SCENE','iqc-conversation-import','RATE_LIMIT_CHECK','IQC 单文件会话导入限次检查','iqc-platform',1,'单文件 TXT 会话导入限次','system','2026-09-04 05:00:38.002','system','2026-09-04 05:00:38.002'),('91df0593a81d11f1824646235496ecdd','RATE_LIMIT_SCENE','iqc-conversation-ingest','RATE_LIMIT_CHECK','IQC 单会话接口接入限次检查','iqc-platform',1,'单会话接口接入限次','system','2026-09-04 05:00:38.002','system','2026-09-04 05:00:38.002'),('91df0778a81d11f1824646235496ecdd','RATE_LIMIT_SCENE','iqc-conversation-ingest-batch','RATE_LIMIT_CHECK','IQC 批量会话接口接入限次检查','iqc-platform',1,'批量会话接口接入限次','system','2026-09-04 05:00:38.002','system','2026-09-04 05:00:38.002'),('91df0bb8a81d11f1824646235496ecdd','RATE_LIMIT_SCENE','iqc-conversation-query','RATE_LIMIT_CHECK','IQC 会话查询限次检查','iqc-platform',1,'会话列表和详情查询限次','system','2026-09-04 05:00:38.002','system','2026-09-04 05:00:38.002'),('91df0df9a81d11f1824646235496ecdd','RATE_LIMIT_SCENE','iqc-conversation-zip-import','RATE_LIMIT_CHECK','IQC ZIP 会话导入限次检查','iqc-platform',1,'ZIP 会话导入限次','system','2026-09-04 05:00:38.002','system','2026-09-04 05:00:38.002'),('91df100aa81d11f1824646235496ecdd','RATE_LIMIT_SCENE','iqc-dashboard-query','RATE_LIMIT_CHECK','IQC 总览查询限次检查','iqc-platform',1,'质检总览查询限次','system','2026-09-04 05:00:38.002','system','2026-09-04 05:00:38.002'),('91df121ca81d11f1824646235496ecdd','RATE_LIMIT_SCENE','iqc-dictionary-query','RATE_LIMIT_CHECK','IQC 字典查询限次检查','iqc-platform',1,'IQC 字典选项查询限次','system','2026-09-04 05:00:38.002','system','2026-09-04 05:00:38.002'),('91df141da81d11f1824646235496ecdd','RATE_LIMIT_SCENE','iqc-result-export','RATE_LIMIT_CHECK','IQC 结果导出限次检查','iqc-platform',1,'质检结果导出限次','system','2026-09-04 05:00:38.002','system','2026-09-04 05:00:38.002'),('91df1629a81d11f1824646235496ecdd','RATE_LIMIT_SCENE','iqc-result-feedback','RATE_LIMIT_CHECK','IQC 结果反馈限次检查','iqc-platform',1,'质检结果人工标注限次','system','2026-09-04 05:00:38.002','system','2026-09-04 05:00:38.002'),('91df18e9a81d11f1824646235496ecdd','RATE_LIMIT_SCENE','iqc-result-query','RATE_LIMIT_CHECK','IQC 结果查询限次检查','iqc-platform',1,'质检结果查询限次','system','2026-09-04 05:00:38.002','system','2026-09-04 05:00:38.002'),('91df1b46a81d11f1824646235496ecdd','RATE_LIMIT_SCENE','iqc-rule-create','RATE_LIMIT_CHECK','IQC 规则创建限次检查','iqc-platform',1,'IQC 规则创建限次','system','2026-09-04 05:00:38.002','system','2026-09-04 05:00:38.002'),('91df1d75a81d11f1824646235496ecdd','RATE_LIMIT_SCENE','iqc-rule-test','RATE_LIMIT_CHECK','IQC 规则测试限次检查','iqc-platform',1,'IQC 规则测试限次','system','2026-09-04 05:00:38.002','system','2026-09-04 05:00:38.002'),('91df1f8ba81d11f1824646235496ecdd','RATE_LIMIT_SCENE','iqc-settings-query','RATE_LIMIT_CHECK','IQC 设置查询限次检查','iqc-platform',1,'IQC 设置查询限次','system','2026-09-04 05:00:38.002','system','2026-09-04 05:00:38.002'),('91df21aba81d11f1824646235496ecdd','RATE_LIMIT_SCENE','iqc-skill-create','RATE_LIMIT_CHECK','IQC Skill 创建限次检查','iqc-platform',1,'IQC Skill 创建限次','system','2026-09-04 05:00:38.002','system','2026-09-04 05:00:38.002'),('91df23b8a81d11f1824646235496ecdd','RATE_LIMIT_SCENE','iqc-task-create','RATE_LIMIT_CHECK','IQC 任务创建限次检查','iqc-platform',1,'质检任务创建限次','system','2026-09-04 05:00:38.002','system','2026-09-04 05:00:38.002'),('91df25dda81d11f1824646235496ecdd','RATE_LIMIT_SCENE','iqc-task-query','RATE_LIMIT_CHECK','IQC 任务查询限次检查','iqc-platform',1,'质检任务查询限次','system','2026-09-04 05:00:38.002','system','2026-09-04 05:00:38.002'),('91df2805a81d11f1824646235496ecdd','RATE_LIMIT_SCENE','iqc-task-run','RATE_LIMIT_CHECK','IQC 任务执行限次检查','iqc-platform',1,'质检任务执行限次','system','2026-09-04 05:00:38.002','system','2026-09-04 05:00:38.002'),('91df32a8a81d11f1824646235496ecdd','RATE_LIMIT_SCENE','iqc-template-materialize','RATE_LIMIT_CHECK','IQC 模板实例化限次检查','iqc-platform',1,'从模板创建规则限次','system','2026-09-04 05:00:38.002','system','2026-09-04 05:00:38.002'),('91df35c8a81d11f1824646235496ecdd','RATE_LIMIT_SCENE','iqc-template-query','RATE_LIMIT_CHECK','IQC 模板查询限次检查','iqc-platform',1,'质检模板查询限次','system','2026-09-04 05:00:38.002','system','2026-09-04 05:00:38.002');
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
