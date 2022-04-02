SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(64) NOT NULL COMMENT '部门名称',
  `code` varchar(64) NOT NULL COMMENT '部门标识',
  `organization_id` bigint NOT NULL COMMENT '组织id',
  `parent_id` bigint DEFAULT NULL COMMENT '上级部门id',
  `avatar` varchar(256) NOT NULL DEFAULT '' COMMENT '头像',
  `desc` varchar(256) NOT NULL DEFAULT '' COMMENT '描述',
  `deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标识。0: 未删除, 1: 已删除',
  `creator` varchar(255) NOT NULL DEFAULT 'system' COMMENT '创建人 ',
  `updater` varchar(255) NOT NULL DEFAULT 'system' COMMENT '修改者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `comments` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB COMMENT='部门表';

SET FOREIGN_KEY_CHECKS = 1;
