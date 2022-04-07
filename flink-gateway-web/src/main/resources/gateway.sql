SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE `tenant_region` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '上级区域 id',
  `region` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '区域名称',
  `avatar` varchar(256) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '头像',
  `desc` varchar(256) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '描述',
  `creator` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'system' COMMENT '创建人 ',
  `updater` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'system' COMMENT '修改者',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识。0: 未删除, 1: 已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `comments` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_region` (`region`),
  KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB COMMENT='区域';

CREATE TABLE `tenant_namespace` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `namespace` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '区域名称',
  `region_id` bigint NOT NULL COMMENT '区域 id',
  `desc` varchar(256) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '描述',
  `creator` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'system' COMMENT '创建人 ',
  `updater` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'system' COMMENT '修改者',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识。0: 未删除, 1: 已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `comments` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_namespace` (`namespace`),
  KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB COMMENT='命名空间';


CREATE TABLE `image_registry` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `registry` varchar(64) COMMENT '镜像注册中心',
    `desc` varchar(256) NOT NULL DEFAULT '' COMMENT '描述',
    `deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标识。0: 未删除, 1: 已删除',
    `creator` varchar(255) NOT NULL DEFAULT 'system' COMMENT '创建人 ',
    `updater` varchar(255) NOT NULL DEFAULT 'system' COMMENT '修改者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `comments` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB COMMENT='flink 部署配置';

CREATE TABLE `flink_setting` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `flink_version` varchar(16) COMMENT 'flink 版本',
    `scala_version` varchar(16) COMMENT 'scala 版本',
    `java_version` varchar(16) COMMENT 'java 版本',
    `flink_home` varchar(256) COMMENT 'flink 配置目录。支持 file、hdfs、s3 协议',
    `desc` varchar(256) NOT NULL DEFAULT '' COMMENT '描述',
    `deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标识。0: 未删除, 1: 已删除',
    `creator` varchar(64) NOT NULL DEFAULT 'system' COMMENT '创建人 ',
    `updater` varchar(64) NOT NULL DEFAULT 'system' COMMENT '修改者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `comments` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB COMMENT='flink 版本配置';


-- 如何部署。在物理机上或者容器中，使用本地或者远程的数据执行用于部署 flink job。
CREATE TABLE `flink_deploy_config` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `resource_provider` tinyint(4) NOT NULL COMMENT 'Flink Resource 方式。0: Standalone, 1: Native Kubernetes, 2: YARN',
    `deploy_mode` tinyint(4) NOT NULL COMMENT 'Flink 部署模式。0: Application, 1: Per-Job, 2: Session',
    `flink_version_config_id` bigint(20) COMMENT 'flink 版本配置 id',
    `deploy_context` bigint NOT NULL COMMENT 'yarn 配置地址或 kubernetes context 地址。支持 file、hdfs、s3 协议',
    `desc` varchar(256) NOT NULL DEFAULT '' COMMENT '描述',
    `deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标识。0: 未删除, 1: 已删除',
    `creator` varchar(255) NOT NULL DEFAULT 'system' COMMENT '创建人 ',
    `updater` varchar(255) NOT NULL DEFAULT 'system' COMMENT '修改者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `comments` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB COMMENT='flink 部署配置';


-- 部署的集群地址
CREATE TABLE `flink_deploy_log` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `deploy_config_id` tinyint(4) NOT NULL COMMENT '部署配置 id',
  `cluster_id` varchar(64) COMMENT 'flink cluster id',
  `web_interface_url` bigint NOT NULL COMMENT 'flink web-ui 地址',
  `status` tinyint(4) NOT NULL COMMENT '集群状态。运行或者关闭',
  `deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标识。0: 未删除, 1: 已删除',
  `creator` varchar(255) NOT NULL DEFAULT 'system' COMMENT '创建人 ',
  `updater` varchar(255) NOT NULL DEFAULT 'system' COMMENT '修改者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `comments` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB COMMENT='flink 部署日志';


-- 任务本身的信息: jar，entryClass，classpath，并行度
-- savepoint:
-- checkpoint:
-- 资源信息: cpu/memory，jobmanager/taskmanager
create table flink_job (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` tinyint(4) NOT NULL COMMENT '任务名称',
  `deploy_log_id` varchar(64) COMMENT '集群地址',
  `web_interface_url` bigint NOT NULL COMMENT 'flink web-ui 地址',
  `status` tinyint(4) NOT NULL COMMENT '集群状态。运行或者关闭',
  `deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标识。0: 未删除, 1: 已删除',
  `creator` varchar(255) NOT NULL DEFAULT 'system' COMMENT '创建人 ',
  `updater` varchar(255) NOT NULL DEFAULT 'system' COMMENT '修改者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `comments` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',

)ENGINE=InnoDB COMMENT='flink 任务';


-- 任务信息

create table flink_job_log (

`id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `deploy_config_id` tinyint(4) NOT NULL COMMENT '部署配置 id',
  `cluster_id` varchar(64) COMMENT 'flink cluster id',
  `web_interface_url` bigint NOT NULL COMMENT 'flink web-ui 地址',
  `status` tinyint(4) NOT NULL COMMENT '集群状态。运行或者关闭',
  `deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标识。0: 未删除, 1: 已删除',
  `creator` varchar(255) NOT NULL DEFAULT 'system' COMMENT '创建人 ',
  `updater` varchar(255) NOT NULL DEFAULT 'system' COMMENT '修改者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `comments` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB COMMENT='flink 任务日志';

SET FOREIGN_KEY_CHECKS = 1;
