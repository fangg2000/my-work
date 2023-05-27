/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50641
Source Host           : localhost:3306
Source Database       : db1

Target Server Type    : MYSQL
Target Server Version : 50641
File Encoding         : 65001

Date: 2022-03-29 11:10:11
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for daily_record_0
-- ----------------------------
DROP TABLE IF EXISTS `daily_record_0`;
CREATE TABLE `daily_record_0` (
  `record_id` bigint(20) NOT NULL,
  `user_code` varchar(16) NOT NULL COMMENT '用户编号',
  `title` varchar(30) NOT NULL COMMENT '标题',
  `content` longtext NOT NULL COMMENT '日记内容',
  `record_status` tinyint(1) NOT NULL COMMENT '日记状态，0草稿,  1公开，2好友可见，3保密',
  `record_group_id` int(11) NOT NULL COMMENT '日记分组（没分组则为默认分组）',
  `review_num` int(11) DEFAULT '0' COMMENT '浏览数量',
  `collect_num` int(11) DEFAULT '0' COMMENT '收藏数量',
  `discuss_num` int(11) DEFAULT '0' COMMENT '评论数量',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `delete_flag` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标识，0否，1是',
  PRIMARY KEY (`record_id`),
  KEY `idx_uc` (`user_code`) USING BTREE,
  KEY `idx_tt` (`title`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for daily_record_1
-- ----------------------------
DROP TABLE IF EXISTS `daily_record_1`;
CREATE TABLE `daily_record_1` (
  `record_id` bigint(20) NOT NULL,
  `user_code` varchar(16) NOT NULL COMMENT '用户编号',
  `title` varchar(30) NOT NULL COMMENT '标题',
  `content` longtext NOT NULL COMMENT '日记内容',
  `record_status` tinyint(1) NOT NULL COMMENT '日记状态，0草稿,  1公开，2好友可见，3保密',
  `record_group_id` int(11) NOT NULL COMMENT '日记分组（没分组则为默认分组）',
  `review_num` int(11) DEFAULT '0' COMMENT '浏览数量',
  `collect_num` int(11) DEFAULT '0' COMMENT '收藏数量',
  `discuss_num` int(11) DEFAULT '0' COMMENT '评论数量',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `delete_flag` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标识，0否，1是',
  PRIMARY KEY (`record_id`),
  KEY `idx_uc` (`user_code`) USING BTREE,
  KEY `idx_tt` (`title`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for daily_record_2
-- ----------------------------
DROP TABLE IF EXISTS `daily_record_2`;
CREATE TABLE `daily_record_2` (
  `record_id` bigint(20) NOT NULL,
  `user_code` varchar(16) NOT NULL COMMENT '用户编号',
  `title` varchar(30) NOT NULL COMMENT '标题',
  `content` longtext NOT NULL COMMENT '日记内容',
  `record_status` tinyint(1) NOT NULL COMMENT '日记状态，0草稿,  1公开，2好友可见，3保密',
  `record_group_id` int(11) NOT NULL COMMENT '日记分组（没分组则为默认分组）',
  `review_num` int(11) DEFAULT '0' COMMENT '浏览数量',
  `collect_num` int(11) DEFAULT '0' COMMENT '收藏数量',
  `discuss_num` int(11) DEFAULT '0' COMMENT '评论数量',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `delete_flag` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标识，0否，1是',
  PRIMARY KEY (`record_id`),
  KEY `idx_uc` (`user_code`) USING BTREE,
  KEY `idx_tt` (`title`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for discuss_info_0
-- ----------------------------
DROP TABLE IF EXISTS `discuss_info_0`;
CREATE TABLE `discuss_info_0` (
  `discuss_id` bigint(20) NOT NULL COMMENT '评论ID',
  `record_id` bigint(20) NOT NULL,
  `user_code` varchar(16) NOT NULL,
  `username` varchar(16) DEFAULT NULL COMMENT '用户名',
  `content` varchar(512) NOT NULL,
  `agree_num` int(11) DEFAULT '0' COMMENT '点赞数量',
  `parent_id` varchar(18) DEFAULT NULL COMMENT '回复上级ID',
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`discuss_id`),
  KEY `idx_uc` (`user_code`),
  KEY `idx_record_id` (`record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for discuss_info_1
-- ----------------------------
DROP TABLE IF EXISTS `discuss_info_1`;
CREATE TABLE `discuss_info_1` (
  `discuss_id` bigint(20) NOT NULL COMMENT '评论ID',
  `record_id` bigint(20) NOT NULL,
  `user_code` varchar(16) NOT NULL,
  `username` varchar(16) DEFAULT NULL COMMENT '用户名',
  `content` varchar(512) NOT NULL,
  `agree_num` int(11) DEFAULT '0' COMMENT '点赞数量',
  `parent_id` varchar(18) DEFAULT NULL COMMENT '回复上级ID',
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`discuss_id`),
  KEY `idx_uc` (`user_code`),
  KEY `idx_record_id` (`record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for discuss_info_2
-- ----------------------------
DROP TABLE IF EXISTS `discuss_info_2`;
CREATE TABLE `discuss_info_2` (
  `discuss_id` bigint(20) NOT NULL COMMENT '评论ID',
  `record_id` bigint(20) NOT NULL,
  `user_code` varchar(16) NOT NULL,
  `username` varchar(16) DEFAULT NULL COMMENT '用户名',
  `content` varchar(512) NOT NULL,
  `agree_num` int(11) DEFAULT '0' COMMENT '点赞数量',
  `parent_id` varchar(18) DEFAULT NULL COMMENT '回复上级ID',
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`discuss_id`),
  KEY `idx_uc` (`user_code`),
  KEY `idx_record_id` (`record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for discuss_info_3
-- ----------------------------
DROP TABLE IF EXISTS `discuss_info_3`;
CREATE TABLE `discuss_info_3` (
  `discuss_id` bigint(20) NOT NULL COMMENT '评论ID',
  `record_id` bigint(20) NOT NULL,
  `user_code` varchar(16) NOT NULL,
  `username` varchar(16) DEFAULT NULL COMMENT '用户名',
  `content` varchar(512) NOT NULL,
  `agree_num` int(11) DEFAULT '0' COMMENT '点赞数量',
  `parent_id` varchar(18) DEFAULT NULL COMMENT '回复上级ID',
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`discuss_id`),
  KEY `idx_uc` (`user_code`),
  KEY `idx_record_id` (`record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for discuss_info_4
-- ----------------------------
DROP TABLE IF EXISTS `discuss_info_4`;
CREATE TABLE `discuss_info_4` (
  `discuss_id` bigint(20) NOT NULL COMMENT '评论ID',
  `record_id` bigint(20) NOT NULL,
  `user_code` varchar(16) NOT NULL,
  `username` varchar(16) DEFAULT NULL COMMENT '用户名',
  `content` varchar(512) NOT NULL,
  `agree_num` int(11) DEFAULT '0' COMMENT '点赞数量',
  `parent_id` varchar(18) DEFAULT NULL COMMENT '回复上级ID',
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`discuss_id`),
  KEY `idx_uc` (`user_code`),
  KEY `idx_record_id` (`record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for record_group
-- ----------------------------
DROP TABLE IF EXISTS `record_group`;
CREATE TABLE `record_group` (
  `record_group_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_code` varchar(16) NOT NULL,
  `group_name` varchar(16) NOT NULL,
  `group_code` varchar(16) NOT NULL COMMENT '分组编号(默认分组编号为"DEFAULT")',
  PRIMARY KEY (`record_group_id`),
  UNIQUE KEY `uni_uc_gc` (`user_code`,`group_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
