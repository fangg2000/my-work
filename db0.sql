/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50641
Source Host           : localhost:3306
Source Database       : db0

Target Server Type    : MYSQL
Target Server Version : 50641
File Encoding         : 65001

Date: 2022-03-29 12:41:22
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for black_info
-- ----------------------------
DROP TABLE IF EXISTS `black_info`;
CREATE TABLE `black_info` (
  `black_id` int(11) NOT NULL AUTO_INCREMENT,
  `black_code` varchar(16) NOT NULL COMMENT '黑名单编号，ip地址/手机标识/手机号等',
  `user_code` varchar(16) DEFAULT NULL COMMENT '用户编号',
  `username` varchar(16) DEFAULT NULL COMMENT '用户名',
  `descript` varchar(1000) NOT NULL COMMENT '原因',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '当前黑名单记录是否有效，0失效，1有效',
  `ip_addr` varchar(16) DEFAULT NULL COMMENT 'ip',
  `login_type` tinyint(4) DEFAULT NULL COMMENT '操作端类型，0WEB，1手机，2PAD',
  `create_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`black_id`),
  KEY `idx_uc` (`user_code`),
  KEY `idx_bc` (`black_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for chat_group
-- ----------------------------
DROP TABLE IF EXISTS `chat_group`;
CREATE TABLE `chat_group` (
  `chat_group_id` int(11) NOT NULL AUTO_INCREMENT,
  `chat_group_name` varchar(16) NOT NULL COMMENT '群名称',
  `chat_group_code` varchar(16) NOT NULL COMMENT '群编号',
  `group_master` varchar(16) NOT NULL COMMENT '群主编号',
  `group_member` varchar(512) DEFAULT NULL COMMENT '群成员',
  `group_grade` tinyint(4) DEFAULT '1' COMMENT '群等级，1到12级，默认1级',
  `group_apply` varchar(512) DEFAULT NULL COMMENT '入群申请，JSON格式[{userCode:"",descript:""}]',
  `apply_condition` tinyint(4) NOT NULL DEFAULT '0' COMMENT '申请条件，0自由加入，1管理员审核，2拒绝加入',
  `group_limit` int(11) NOT NULL DEFAULT '30' COMMENT '群成员上限',
  `group_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '群类型，从字典表中取（默认0其他）',
  `profile_picture` varchar(512) DEFAULT NULL COMMENT '群头像路径',
  `group_sign` varchar(512) DEFAULT NULL COMMENT '群签名/说明',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`chat_group_id`),
  KEY `idx_gn` (`chat_group_name`),
  KEY `idx_gc` (`chat_group_code`),
  KEY `idx_gm` (`group_master`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for chat_log_0
-- ----------------------------
DROP TABLE IF EXISTS `chat_log_0`;
CREATE TABLE `chat_log_0` (
  `chat_id` varchar(30) NOT NULL,
  `company_code` varchar(16) NOT NULL,
  `server` varchar(16) NOT NULL,
  `client` varchar(16) NOT NULL,
  `receiver` varchar(16) DEFAULT NULL COMMENT '接收者，方便查询未读信息',
  `type` char(1) NOT NULL DEFAULT '0' COMMENT '类型，0：服务端，1：客户端',
  `content` varchar(5120) DEFAULT NULL COMMENT '消息内容(加密后的内容)',
  `status` char(1) NOT NULL DEFAULT '1' COMMENT '0：未读，1：已读',
  `init_time` bigint(20) DEFAULT NULL COMMENT '新增时的时间戳',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`chat_id`),
  KEY `idx_server` (`server`) USING BTREE,
  KEY `idx_client` (`client`) USING BTREE,
  KEY `idx_receiver` (`receiver`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for chat_log_1
-- ----------------------------
DROP TABLE IF EXISTS `chat_log_1`;
CREATE TABLE `chat_log_1` (
  `chat_id` varchar(30) NOT NULL,
  `company_code` varchar(16) NOT NULL,
  `server` varchar(16) NOT NULL,
  `client` varchar(16) NOT NULL,
  `receiver` varchar(16) DEFAULT NULL COMMENT '接收者，方便查询未读信息',
  `type` char(1) NOT NULL DEFAULT '0' COMMENT '类型，0：服务端，1：客户端',
  `content` varchar(5120) DEFAULT NULL COMMENT '消息内容(加密后的内容)',
  `status` char(1) NOT NULL DEFAULT '1' COMMENT '0：未读，1：已读',
  `init_time` bigint(20) DEFAULT NULL COMMENT '新增时的时间戳',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`chat_id`),
  KEY `idx_server` (`server`) USING BTREE,
  KEY `idx_client` (`client`) USING BTREE,
  KEY `idx_receiver` (`receiver`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for chat_log_2
-- ----------------------------
DROP TABLE IF EXISTS `chat_log_2`;
CREATE TABLE `chat_log_2` (
  `chat_id` varchar(30) NOT NULL,
  `company_code` varchar(16) NOT NULL,
  `server` varchar(16) NOT NULL,
  `client` varchar(16) NOT NULL,
  `receiver` varchar(16) DEFAULT NULL COMMENT '接收者，方便查询未读信息',
  `type` char(1) NOT NULL DEFAULT '0' COMMENT '类型，0：服务端，1：客户端',
  `content` varchar(5120) DEFAULT NULL COMMENT '消息内容(加密后的内容)',
  `status` char(1) NOT NULL DEFAULT '1' COMMENT '0：未读，1：已读',
  `init_time` bigint(20) DEFAULT NULL COMMENT '新增时的时间戳',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`chat_id`),
  KEY `idx_server` (`server`) USING BTREE,
  KEY `idx_client` (`client`) USING BTREE,
  KEY `idx_receiver` (`receiver`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for chat_log_3
-- ----------------------------
DROP TABLE IF EXISTS `chat_log_3`;
CREATE TABLE `chat_log_3` (
  `chat_id` varchar(30) NOT NULL,
  `company_code` varchar(16) NOT NULL,
  `server` varchar(16) NOT NULL,
  `client` varchar(16) NOT NULL,
  `receiver` varchar(16) DEFAULT NULL COMMENT '接收者，方便查询未读信息',
  `type` char(1) NOT NULL DEFAULT '0' COMMENT '类型，0：服务端，1：客户端',
  `content` varchar(5120) DEFAULT NULL COMMENT '消息内容(加密后的内容)',
  `status` char(1) NOT NULL DEFAULT '1' COMMENT '0：未读，1：已读',
  `init_time` bigint(20) DEFAULT NULL COMMENT '新增时的时间戳',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`chat_id`),
  KEY `idx_server` (`server`) USING BTREE,
  KEY `idx_client` (`client`) USING BTREE,
  KEY `idx_receiver` (`receiver`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for chat_log_4
-- ----------------------------
DROP TABLE IF EXISTS `chat_log_4`;
CREATE TABLE `chat_log_4` (
  `chat_id` varchar(30) NOT NULL,
  `company_code` varchar(16) NOT NULL,
  `server` varchar(16) NOT NULL,
  `client` varchar(16) NOT NULL,
  `receiver` varchar(16) DEFAULT NULL COMMENT '接收者，方便查询未读信息',
  `type` char(1) NOT NULL DEFAULT '0' COMMENT '类型，0：服务端，1：客户端',
  `content` varchar(5120) DEFAULT NULL COMMENT '消息内容(加密后的内容)',
  `status` char(1) NOT NULL DEFAULT '1' COMMENT '0：未读，1：已读',
  `init_time` bigint(20) DEFAULT NULL COMMENT '新增时的时间戳',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`chat_id`),
  KEY `idx_server` (`server`) USING BTREE,
  KEY `idx_client` (`client`) USING BTREE,
  KEY `idx_receiver` (`receiver`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for chat_log_5
-- ----------------------------
DROP TABLE IF EXISTS `chat_log_5`;
CREATE TABLE `chat_log_5` (
  `chat_id` varchar(30) NOT NULL,
  `company_code` varchar(16) NOT NULL,
  `server` varchar(16) NOT NULL,
  `client` varchar(16) NOT NULL,
  `receiver` varchar(16) DEFAULT NULL COMMENT '接收者，方便查询未读信息',
  `type` char(1) NOT NULL DEFAULT '0' COMMENT '类型，0：服务端，1：客户端',
  `content` varchar(5120) DEFAULT NULL COMMENT '消息内容(加密后的内容)',
  `status` char(1) NOT NULL DEFAULT '1' COMMENT '0：未读，1：已读',
  `init_time` bigint(20) DEFAULT NULL COMMENT '新增时的时间戳',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`chat_id`),
  KEY `idx_server` (`server`) USING BTREE,
  KEY `idx_client` (`client`) USING BTREE,
  KEY `idx_receiver` (`receiver`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for chat_log_6
-- ----------------------------
DROP TABLE IF EXISTS `chat_log_6`;
CREATE TABLE `chat_log_6` (
  `chat_id` varchar(30) NOT NULL,
  `company_code` varchar(16) NOT NULL,
  `server` varchar(16) NOT NULL,
  `client` varchar(16) NOT NULL,
  `receiver` varchar(16) DEFAULT NULL COMMENT '接收者，方便查询未读信息',
  `type` char(1) NOT NULL DEFAULT '0' COMMENT '类型，0：服务端，1：客户端',
  `content` varchar(5120) DEFAULT NULL COMMENT '消息内容(加密后的内容)',
  `status` char(1) NOT NULL DEFAULT '1' COMMENT '0：未读，1：已读',
  `init_time` bigint(20) DEFAULT NULL COMMENT '新增时的时间戳',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`chat_id`),
  KEY `idx_server` (`server`) USING BTREE,
  KEY `idx_client` (`client`) USING BTREE,
  KEY `idx_receiver` (`receiver`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for focus_users_0
-- ----------------------------
DROP TABLE IF EXISTS `focus_users_0`;
CREATE TABLE `focus_users_0` (
  `focus_id` bigint(11) NOT NULL COMMENT 'id',
  `user_id` int(11) NOT NULL COMMENT '关注用户ID',
  `focus_user_code` varchar(16) NOT NULL COMMENT '被关注用户code',
  `focus_user_name` varchar(16) NOT NULL COMMENT '被关注用户名称',
  `create_time` datetime DEFAULT NULL COMMENT '关注开始时间',
  PRIMARY KEY (`focus_id`),
  KEY `idx_userid` (`user_id`),
  KEY `idx_fuc` (`focus_user_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='关注用户表';

-- ----------------------------
-- Table structure for focus_users_1
-- ----------------------------
DROP TABLE IF EXISTS `focus_users_1`;
CREATE TABLE `focus_users_1` (
  `focus_id` bigint(11) NOT NULL COMMENT 'id',
  `user_id` int(11) NOT NULL COMMENT '关注用户ID',
  `focus_user_code` varchar(16) NOT NULL COMMENT '被关注用户code',
  `focus_user_name` varchar(16) NOT NULL COMMENT '被关注用户名称',
  `create_time` datetime DEFAULT NULL COMMENT '关注开始时间',
  PRIMARY KEY (`focus_id`),
  KEY `idx_userid` (`user_id`),
  KEY `idx_fuc` (`focus_user_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='关注用户表';

-- ----------------------------
-- Table structure for focus_users_2
-- ----------------------------
DROP TABLE IF EXISTS `focus_users_2`;
CREATE TABLE `focus_users_2` (
  `focus_id` bigint(11) NOT NULL COMMENT 'id',
  `user_id` int(11) NOT NULL COMMENT '关注用户ID',
  `focus_user_code` varchar(16) NOT NULL COMMENT '被关注用户code',
  `focus_user_name` varchar(16) DEFAULT NULL COMMENT '被关注用户名称',
  `create_time` datetime DEFAULT NULL COMMENT '关注开始时间',
  PRIMARY KEY (`focus_id`),
  KEY `idx_userid` (`user_id`),
  KEY `idx_fuc` (`focus_user_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='关注用户表';

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_code` varchar(16) NOT NULL,
  `username` varchar(16) NOT NULL,
  `password` varchar(64) NOT NULL,
  `salt` varchar(64) NOT NULL,
  `ticket` varchar(16) DEFAULT NULL COMMENT '用户登录key',
  `role_id` int(11) DEFAULT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否禁用，0：否，1：禁用',
  `online_status` tinyint(4) NOT NULL COMMENT '在线状态，0不在，1在线',
  `mobile` varchar(13) DEFAULT NULL COMMENT '手机号',
  `sex` tinyint(4) DEFAULT NULL COMMENT '性别,1男，0女',
  `age` tinyint(4) DEFAULT NULL COMMENT '年龄',
  `user_sign` varchar(255) DEFAULT NULL COMMENT '用户签名',
  `profile_picture` varchar(255) DEFAULT NULL COMMENT '用户头像',
  `user_type` int(1) DEFAULT NULL COMMENT '用户类型，0客服，1用户，2其他待定',
  `like_num` int(11) DEFAULT '0' COMMENT '点赞数',
  `country` varchar(30) DEFAULT NULL COMMENT '国家',
  `province` varchar(100) DEFAULT NULL COMMENT '省份',
  `city` varchar(60) DEFAULT NULL COMMENT '城市',
  `login_ip` varchar(16) DEFAULT NULL COMMENT ' 最后一次登录IP',
  `login_type` tinyint(4) DEFAULT NULL COMMENT '最后一次登录类型，0：WEB端，1：手机端',
  `login_again` tinyint(4) DEFAULT NULL COMMENT '初次登录标识，0第一次登录，1再次登录',
  `email` varchar(64) DEFAULT NULL COMMENT '邮箱（绑定信息后不能修改，解绑时会通过邮件通知）',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `delete_flag` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除，0否，1是',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uni_user_code` (`user_code`) USING BTREE,
  UNIQUE KEY `idx_ticket` (`ticket`) USING BTREE,
  KEY `idx_username` (`username`) USING BTREE,
  KEY `idx_role_id` (`role_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user_cg_fk
-- ----------------------------
DROP TABLE IF EXISTS `user_cg_fk`;
CREATE TABLE `user_cg_fk` (
  `ucg_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_code` varchar(16) NOT NULL,
  `chat_group_code` varchar(16) NOT NULL COMMENT '群编号',
  `chat_group_name` varchar(16) DEFAULT NULL COMMENT '群名称',
  `msg_status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '消息状态，0屏蔽消息，1接收',
  `username` varchar(30) DEFAULT NULL COMMENT '用户名/昵称',
  `identity` tinyint(4) DEFAULT '0' COMMENT '身份/职位，0普通成员，1长老，2副群，3群主',
  `create_time` datetime NOT NULL COMMENT '加入群时间',
  PRIMARY KEY (`ucg_id`),
  UNIQUE KEY `uni_uc` (`user_code`,`chat_group_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COMMENT='用户与聊天群关联表';

-- ----------------------------
-- Table structure for user_config
-- ----------------------------
DROP TABLE IF EXISTS `user_config`;
CREATE TABLE `user_config` (
  `config_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_code` varchar(16) NOT NULL COMMENT '用户编号',
  `show_ball` tinyint(4) DEFAULT '0' COMMENT '是否禁止自动弹出泡泡提示，0否，1是',
  `ball_show_seconds` int(11) DEFAULT '0' COMMENT '泡泡显示时长（秒），默认0无限',
  `stranger_contact` tinyint(4) DEFAULT '0' COMMENT '是否禁止陌生人联系，0否，1是',
  `grade` tinyint(4) DEFAULT '0' COMMENT '等级，0普通用户，1VIP用户，2MVP用户，3SUP用户',
  `pp_avtive_type` tinyint(4) DEFAULT '0' COMMENT '来新消息时，头像动作类型，0晃动，1闪动',
  `out_confirm` tinyint(4) DEFAULT '1' COMMENT '用户退出确认提示，0否，1是',
  `bind_ip` varchar(256) DEFAULT NULL COMMENT '绑定IP或标签，JSON格式"{0:\\"192.168.0.1\\"}"，0为登录类型',
  `black_list` varchar(512) DEFAULT NULL COMMENT '黑名单用户',
  `over_time` datetime DEFAULT NULL COMMENT '过期时间，为空则无过期',
  PRIMARY KEY (`config_id`),
  UNIQUE KEY `idx_usercode` (`user_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=utf8 COMMENT='用户配置信息表';



-- ----------------------------
-- Table structure for city
-- ----------------------------
DROP TABLE IF EXISTS `city`;
CREATE TABLE `city` (
  `cid` int(11) NOT NULL AUTO_INCREMENT,
  `city` varchar(50) NOT NULL,
  `pid` int(11) DEFAULT NULL,
  PRIMARY KEY (`cid`),
  KEY `idx_pid` (`pid`)
) ENGINE=InnoDB AUTO_INCREMENT=392 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of city
-- ----------------------------
INSERT INTO `city` VALUES ('1', '北京市', '1');
INSERT INTO `city` VALUES ('2', '天津市', '2');
INSERT INTO `city` VALUES ('3', '上海市', '3');
INSERT INTO `city` VALUES ('4', '重庆市', '4');
INSERT INTO `city` VALUES ('5', '石家庄市', '5');
INSERT INTO `city` VALUES ('6', '唐山市', '5');
INSERT INTO `city` VALUES ('7', '秦皇岛市', '5');
INSERT INTO `city` VALUES ('8', '邯郸市', '5');
INSERT INTO `city` VALUES ('9', '邢台市', '5');
INSERT INTO `city` VALUES ('10', '保定市', '5');
INSERT INTO `city` VALUES ('11', '张家口市', '5');
INSERT INTO `city` VALUES ('12', '承德市', '5');
INSERT INTO `city` VALUES ('13', '沧州市', '5');
INSERT INTO `city` VALUES ('14', '廊坊市', '5');
INSERT INTO `city` VALUES ('15', '衡水市', '5');
INSERT INTO `city` VALUES ('16', '太原市', '6');
INSERT INTO `city` VALUES ('17', '大同市', '6');
INSERT INTO `city` VALUES ('18', '阳泉市', '6');
INSERT INTO `city` VALUES ('19', '长治市', '6');
INSERT INTO `city` VALUES ('20', '晋城市', '6');
INSERT INTO `city` VALUES ('21', '朔州市', '6');
INSERT INTO `city` VALUES ('22', '晋中市', '6');
INSERT INTO `city` VALUES ('23', '运城市', '6');
INSERT INTO `city` VALUES ('24', '忻州市', '6');
INSERT INTO `city` VALUES ('25', '临汾市', '6');
INSERT INTO `city` VALUES ('26', '吕梁市', '6');
INSERT INTO `city` VALUES ('27', '台北市', '7');
INSERT INTO `city` VALUES ('28', '高雄市', '7');
INSERT INTO `city` VALUES ('29', '基隆市', '7');
INSERT INTO `city` VALUES ('30', '台中市', '7');
INSERT INTO `city` VALUES ('31', '台南市', '7');
INSERT INTO `city` VALUES ('32', '新竹市', '7');
INSERT INTO `city` VALUES ('33', '嘉义市', '7');
INSERT INTO `city` VALUES ('34', '台北县', '7');
INSERT INTO `city` VALUES ('35', '宜兰县', '7');
INSERT INTO `city` VALUES ('36', '桃园县', '7');
INSERT INTO `city` VALUES ('37', '新竹县', '7');
INSERT INTO `city` VALUES ('38', '苗栗县', '7');
INSERT INTO `city` VALUES ('39', '台中县', '7');
INSERT INTO `city` VALUES ('40', '彰化县', '7');
INSERT INTO `city` VALUES ('41', '南投县', '7');
INSERT INTO `city` VALUES ('42', '云林县', '7');
INSERT INTO `city` VALUES ('43', '嘉义县', '7');
INSERT INTO `city` VALUES ('44', '台南县', '7');
INSERT INTO `city` VALUES ('45', '高雄县', '7');
INSERT INTO `city` VALUES ('46', '屏东县', '7');
INSERT INTO `city` VALUES ('47', '澎湖县', '7');
INSERT INTO `city` VALUES ('48', '台东县', '7');
INSERT INTO `city` VALUES ('49', '花莲县', '7');
INSERT INTO `city` VALUES ('50', '沈阳市', '8');
INSERT INTO `city` VALUES ('51', '大连市', '8');
INSERT INTO `city` VALUES ('52', '鞍山市', '8');
INSERT INTO `city` VALUES ('53', '抚顺市', '8');
INSERT INTO `city` VALUES ('54', '本溪市', '8');
INSERT INTO `city` VALUES ('55', '丹东市', '8');
INSERT INTO `city` VALUES ('56', '锦州市', '8');
INSERT INTO `city` VALUES ('57', '营口市', '8');
INSERT INTO `city` VALUES ('58', '阜新市', '8');
INSERT INTO `city` VALUES ('59', '辽阳市', '8');
INSERT INTO `city` VALUES ('60', '盘锦市', '8');
INSERT INTO `city` VALUES ('61', '铁岭市', '8');
INSERT INTO `city` VALUES ('62', '朝阳市', '8');
INSERT INTO `city` VALUES ('63', '葫芦岛市', '8');
INSERT INTO `city` VALUES ('64', '长春市', '9');
INSERT INTO `city` VALUES ('65', '吉林市', '9');
INSERT INTO `city` VALUES ('66', '四平市', '9');
INSERT INTO `city` VALUES ('67', '辽源市', '9');
INSERT INTO `city` VALUES ('68', '通化市', '9');
INSERT INTO `city` VALUES ('69', '白山市', '9');
INSERT INTO `city` VALUES ('70', '松原市', '9');
INSERT INTO `city` VALUES ('71', '白城市', '9');
INSERT INTO `city` VALUES ('72', '延边朝鲜族自治州', '9');
INSERT INTO `city` VALUES ('73', '哈尔滨市', '10');
INSERT INTO `city` VALUES ('74', '齐齐哈尔市', '10');
INSERT INTO `city` VALUES ('75', '鹤 岗 市', '10');
INSERT INTO `city` VALUES ('76', '双鸭山市', '10');
INSERT INTO `city` VALUES ('77', '鸡 西 市', '10');
INSERT INTO `city` VALUES ('78', '大 庆 市', '10');
INSERT INTO `city` VALUES ('79', '伊 春 市', '10');
INSERT INTO `city` VALUES ('80', '牡丹江市', '10');
INSERT INTO `city` VALUES ('81', '佳木斯市', '10');
INSERT INTO `city` VALUES ('82', '七台河市', '10');
INSERT INTO `city` VALUES ('83', '黑 河 市', '10');
INSERT INTO `city` VALUES ('84', '绥 化 市', '10');
INSERT INTO `city` VALUES ('85', '大兴安岭地区', '10');
INSERT INTO `city` VALUES ('86', '南京市', '11');
INSERT INTO `city` VALUES ('87', '无锡市', '11');
INSERT INTO `city` VALUES ('88', '徐州市', '11');
INSERT INTO `city` VALUES ('89', '常州市', '11');
INSERT INTO `city` VALUES ('90', '苏州市', '11');
INSERT INTO `city` VALUES ('91', '南通市', '11');
INSERT INTO `city` VALUES ('92', '连云港市', '11');
INSERT INTO `city` VALUES ('93', '淮安市', '11');
INSERT INTO `city` VALUES ('94', '盐城市', '11');
INSERT INTO `city` VALUES ('95', '扬州市', '11');
INSERT INTO `city` VALUES ('96', '镇江市', '11');
INSERT INTO `city` VALUES ('97', '泰州市', '11');
INSERT INTO `city` VALUES ('98', '宿迁市', '11');
INSERT INTO `city` VALUES ('99', '杭州市', '12');
INSERT INTO `city` VALUES ('100', '宁波市', '12');
INSERT INTO `city` VALUES ('101', '温州市', '12');
INSERT INTO `city` VALUES ('102', '嘉兴市', '12');
INSERT INTO `city` VALUES ('103', '湖州市', '12');
INSERT INTO `city` VALUES ('104', '绍兴市', '12');
INSERT INTO `city` VALUES ('105', '金华市', '12');
INSERT INTO `city` VALUES ('106', '衢州市', '12');
INSERT INTO `city` VALUES ('107', '舟山市', '12');
INSERT INTO `city` VALUES ('108', '台州市', '12');
INSERT INTO `city` VALUES ('109', '丽水市', '12');
INSERT INTO `city` VALUES ('110', '合肥市', '13');
INSERT INTO `city` VALUES ('111', '芜湖市', '13');
INSERT INTO `city` VALUES ('112', '蚌埠市', '13');
INSERT INTO `city` VALUES ('113', '淮南市', '13');
INSERT INTO `city` VALUES ('114', '马鞍山市', '13');
INSERT INTO `city` VALUES ('115', '淮北市', '13');
INSERT INTO `city` VALUES ('116', '铜陵市', '13');
INSERT INTO `city` VALUES ('117', '安庆市', '13');
INSERT INTO `city` VALUES ('118', '黄山市', '13');
INSERT INTO `city` VALUES ('119', '滁州市', '13');
INSERT INTO `city` VALUES ('120', '阜阳市', '13');
INSERT INTO `city` VALUES ('121', '宿州市', '13');
INSERT INTO `city` VALUES ('122', '巢湖市', '13');
INSERT INTO `city` VALUES ('123', '六安市', '13');
INSERT INTO `city` VALUES ('124', '亳州市', '13');
INSERT INTO `city` VALUES ('125', '池州市', '13');
INSERT INTO `city` VALUES ('126', '宣城市', '13');
INSERT INTO `city` VALUES ('127', '福州市', '14');
INSERT INTO `city` VALUES ('128', '厦门市', '14');
INSERT INTO `city` VALUES ('129', '莆田市', '14');
INSERT INTO `city` VALUES ('130', '三明市', '14');
INSERT INTO `city` VALUES ('131', '泉州市', '14');
INSERT INTO `city` VALUES ('132', '漳州市', '14');
INSERT INTO `city` VALUES ('133', '南平市', '14');
INSERT INTO `city` VALUES ('134', '龙岩市', '14');
INSERT INTO `city` VALUES ('135', '宁德市', '14');
INSERT INTO `city` VALUES ('136', '南昌市', '15');
INSERT INTO `city` VALUES ('137', '景德镇市', '15');
INSERT INTO `city` VALUES ('138', '萍乡市', '15');
INSERT INTO `city` VALUES ('139', '九江市', '15');
INSERT INTO `city` VALUES ('140', '新余市', '15');
INSERT INTO `city` VALUES ('141', '鹰潭市', '15');
INSERT INTO `city` VALUES ('142', '赣州市', '15');
INSERT INTO `city` VALUES ('143', '吉安市', '15');
INSERT INTO `city` VALUES ('144', '宜春市', '15');
INSERT INTO `city` VALUES ('145', '抚州市', '15');
INSERT INTO `city` VALUES ('146', '上饶市', '15');
INSERT INTO `city` VALUES ('147', '济南市', '16');
INSERT INTO `city` VALUES ('148', '青岛市', '16');
INSERT INTO `city` VALUES ('149', '淄博市', '16');
INSERT INTO `city` VALUES ('150', '枣庄市', '16');
INSERT INTO `city` VALUES ('151', '东营市', '16');
INSERT INTO `city` VALUES ('152', '烟台市', '16');
INSERT INTO `city` VALUES ('153', '潍坊市', '16');
INSERT INTO `city` VALUES ('154', '济宁市', '16');
INSERT INTO `city` VALUES ('155', '泰安市', '16');
INSERT INTO `city` VALUES ('156', '威海市', '16');
INSERT INTO `city` VALUES ('157', '日照市', '16');
INSERT INTO `city` VALUES ('158', '莱芜市', '16');
INSERT INTO `city` VALUES ('159', '临沂市', '16');
INSERT INTO `city` VALUES ('160', '德州市', '16');
INSERT INTO `city` VALUES ('161', '聊城市', '16');
INSERT INTO `city` VALUES ('162', '滨州市', '16');
INSERT INTO `city` VALUES ('163', '菏泽市', '16');
INSERT INTO `city` VALUES ('164', '郑州市', '17');
INSERT INTO `city` VALUES ('165', '开封市', '17');
INSERT INTO `city` VALUES ('166', '洛阳市', '17');
INSERT INTO `city` VALUES ('167', '平顶山市', '17');
INSERT INTO `city` VALUES ('168', '安阳市', '17');
INSERT INTO `city` VALUES ('169', '鹤壁市', '17');
INSERT INTO `city` VALUES ('170', '新乡市', '17');
INSERT INTO `city` VALUES ('171', '焦作市', '17');
INSERT INTO `city` VALUES ('172', '濮阳市', '17');
INSERT INTO `city` VALUES ('173', '许昌市', '17');
INSERT INTO `city` VALUES ('174', '漯河市', '17');
INSERT INTO `city` VALUES ('175', '三门峡市', '17');
INSERT INTO `city` VALUES ('176', '南阳市', '17');
INSERT INTO `city` VALUES ('177', '商丘市', '17');
INSERT INTO `city` VALUES ('178', '信阳市', '17');
INSERT INTO `city` VALUES ('179', '周口市', '17');
INSERT INTO `city` VALUES ('180', '驻马店市', '17');
INSERT INTO `city` VALUES ('181', '济源市', '17');
INSERT INTO `city` VALUES ('182', '武汉市', '18');
INSERT INTO `city` VALUES ('183', '黄石市', '18');
INSERT INTO `city` VALUES ('184', '十堰市', '18');
INSERT INTO `city` VALUES ('185', '荆州市', '18');
INSERT INTO `city` VALUES ('186', '宜昌市', '18');
INSERT INTO `city` VALUES ('187', '襄樊市', '18');
INSERT INTO `city` VALUES ('188', '鄂州市', '18');
INSERT INTO `city` VALUES ('189', '荆门市', '18');
INSERT INTO `city` VALUES ('190', '孝感市', '18');
INSERT INTO `city` VALUES ('191', '黄冈市', '18');
INSERT INTO `city` VALUES ('192', '咸宁市', '18');
INSERT INTO `city` VALUES ('193', '随州市', '18');
INSERT INTO `city` VALUES ('194', '仙桃市', '18');
INSERT INTO `city` VALUES ('195', '天门市', '18');
INSERT INTO `city` VALUES ('196', '潜江市', '18');
INSERT INTO `city` VALUES ('197', '神农架林区', '18');
INSERT INTO `city` VALUES ('198', '恩施土家族苗族自治州', '18');
INSERT INTO `city` VALUES ('199', '长沙市', '19');
INSERT INTO `city` VALUES ('200', '株洲市', '19');
INSERT INTO `city` VALUES ('201', '湘潭市', '19');
INSERT INTO `city` VALUES ('202', '衡阳市', '19');
INSERT INTO `city` VALUES ('203', '邵阳市', '19');
INSERT INTO `city` VALUES ('204', '岳阳市', '19');
INSERT INTO `city` VALUES ('205', '常德市', '19');
INSERT INTO `city` VALUES ('206', '张家界市', '19');
INSERT INTO `city` VALUES ('207', '益阳市', '19');
INSERT INTO `city` VALUES ('208', '郴州市', '19');
INSERT INTO `city` VALUES ('209', '永州市', '19');
INSERT INTO `city` VALUES ('210', '怀化市', '19');
INSERT INTO `city` VALUES ('211', '娄底市', '19');
INSERT INTO `city` VALUES ('212', '湘西土家族苗族自治州', '19');
INSERT INTO `city` VALUES ('213', '广州市', '20');
INSERT INTO `city` VALUES ('214', '深圳市', '20');
INSERT INTO `city` VALUES ('215', '珠海市', '20');
INSERT INTO `city` VALUES ('216', '汕头市', '20');
INSERT INTO `city` VALUES ('217', '韶关市', '20');
INSERT INTO `city` VALUES ('218', '佛山市', '20');
INSERT INTO `city` VALUES ('219', '江门市', '20');
INSERT INTO `city` VALUES ('220', '湛江市', '20');
INSERT INTO `city` VALUES ('221', '茂名市', '20');
INSERT INTO `city` VALUES ('222', '肇庆市', '20');
INSERT INTO `city` VALUES ('223', '惠州市', '20');
INSERT INTO `city` VALUES ('224', '梅州市', '20');
INSERT INTO `city` VALUES ('225', '汕尾市', '20');
INSERT INTO `city` VALUES ('226', '河源市', '20');
INSERT INTO `city` VALUES ('227', '阳江市', '20');
INSERT INTO `city` VALUES ('228', '清远市', '20');
INSERT INTO `city` VALUES ('229', '东莞市', '20');
INSERT INTO `city` VALUES ('230', '中山市', '20');
INSERT INTO `city` VALUES ('231', '潮州市', '20');
INSERT INTO `city` VALUES ('232', '揭阳市', '20');
INSERT INTO `city` VALUES ('233', '云浮市', '20');
INSERT INTO `city` VALUES ('234', '兰州市', '21');
INSERT INTO `city` VALUES ('235', '金昌市', '21');
INSERT INTO `city` VALUES ('236', '白银市', '21');
INSERT INTO `city` VALUES ('237', '天水市', '21');
INSERT INTO `city` VALUES ('238', '嘉峪关市', '21');
INSERT INTO `city` VALUES ('239', '武威市', '21');
INSERT INTO `city` VALUES ('240', '张掖市', '21');
INSERT INTO `city` VALUES ('241', '平凉市', '21');
INSERT INTO `city` VALUES ('242', '酒泉市', '21');
INSERT INTO `city` VALUES ('243', '庆阳市', '21');
INSERT INTO `city` VALUES ('244', '定西市', '21');
INSERT INTO `city` VALUES ('245', '陇南市', '21');
INSERT INTO `city` VALUES ('246', '临夏回族自治州', '21');
INSERT INTO `city` VALUES ('247', '甘南藏族自治州', '21');
INSERT INTO `city` VALUES ('248', '成都市', '22');
INSERT INTO `city` VALUES ('249', '自贡市', '22');
INSERT INTO `city` VALUES ('250', '攀枝花市', '22');
INSERT INTO `city` VALUES ('251', '泸州市', '22');
INSERT INTO `city` VALUES ('252', '德阳市', '22');
INSERT INTO `city` VALUES ('253', '绵阳市', '22');
INSERT INTO `city` VALUES ('254', '广元市', '22');
INSERT INTO `city` VALUES ('255', '遂宁市', '22');
INSERT INTO `city` VALUES ('256', '内江市', '22');
INSERT INTO `city` VALUES ('257', '乐山市', '22');
INSERT INTO `city` VALUES ('258', '南充市', '22');
INSERT INTO `city` VALUES ('259', '眉山市', '22');
INSERT INTO `city` VALUES ('260', '宜宾市', '22');
INSERT INTO `city` VALUES ('261', '广安市', '22');
INSERT INTO `city` VALUES ('262', '达州市', '22');
INSERT INTO `city` VALUES ('263', '雅安市', '22');
INSERT INTO `city` VALUES ('264', '巴中市', '22');
INSERT INTO `city` VALUES ('265', '资阳市', '22');
INSERT INTO `city` VALUES ('266', '阿坝藏族羌族自治州', '22');
INSERT INTO `city` VALUES ('267', '甘孜藏族自治州', '22');
INSERT INTO `city` VALUES ('268', '凉山彝族自治州', '22');
INSERT INTO `city` VALUES ('269', '贵阳市', '23');
INSERT INTO `city` VALUES ('270', '六盘水市', '23');
INSERT INTO `city` VALUES ('271', '遵义市', '23');
INSERT INTO `city` VALUES ('272', '安顺市', '23');
INSERT INTO `city` VALUES ('273', '铜仁地区', '23');
INSERT INTO `city` VALUES ('274', '毕节地区', '23');
INSERT INTO `city` VALUES ('275', '黔西南布依族苗族自治州', '23');
INSERT INTO `city` VALUES ('276', '黔东南苗族侗族自治州', '23');
INSERT INTO `city` VALUES ('277', '黔南布依族苗族自治州', '23');
INSERT INTO `city` VALUES ('278', '海口市', '24');
INSERT INTO `city` VALUES ('279', '三亚市', '24');
INSERT INTO `city` VALUES ('280', '五指山市', '24');
INSERT INTO `city` VALUES ('281', '琼海市', '24');
INSERT INTO `city` VALUES ('282', '儋州市', '24');
INSERT INTO `city` VALUES ('283', '文昌市', '24');
INSERT INTO `city` VALUES ('284', '万宁市', '24');
INSERT INTO `city` VALUES ('285', '东方市', '24');
INSERT INTO `city` VALUES ('286', '澄迈县', '24');
INSERT INTO `city` VALUES ('287', '定安县', '24');
INSERT INTO `city` VALUES ('288', '屯昌县', '24');
INSERT INTO `city` VALUES ('289', '临高县', '24');
INSERT INTO `city` VALUES ('290', '白沙黎族自治县', '24');
INSERT INTO `city` VALUES ('291', '昌江黎族自治县', '24');
INSERT INTO `city` VALUES ('292', '乐东黎族自治县', '24');
INSERT INTO `city` VALUES ('293', '陵水黎族自治县', '24');
INSERT INTO `city` VALUES ('294', '保亭黎族苗族自治县', '24');
INSERT INTO `city` VALUES ('295', '琼中黎族苗族自治县', '24');
INSERT INTO `city` VALUES ('296', '昆明市', '25');
INSERT INTO `city` VALUES ('297', '曲靖市', '25');
INSERT INTO `city` VALUES ('298', '玉溪市', '25');
INSERT INTO `city` VALUES ('299', '保山市', '25');
INSERT INTO `city` VALUES ('300', '昭通市', '25');
INSERT INTO `city` VALUES ('301', '丽江市', '25');
INSERT INTO `city` VALUES ('302', '思茅市', '25');
INSERT INTO `city` VALUES ('303', '临沧市', '25');
INSERT INTO `city` VALUES ('304', '文山壮族苗族自治州', '25');
INSERT INTO `city` VALUES ('305', '红河哈尼族彝族自治州', '25');
INSERT INTO `city` VALUES ('306', '西双版纳傣族自治州', '25');
INSERT INTO `city` VALUES ('307', '楚雄彝族自治州', '25');
INSERT INTO `city` VALUES ('308', '大理白族自治州', '25');
INSERT INTO `city` VALUES ('309', '德宏傣族景颇族自治州', '25');
INSERT INTO `city` VALUES ('310', '怒江傈傈族自治州', '25');
INSERT INTO `city` VALUES ('311', '迪庆藏族自治州', '25');
INSERT INTO `city` VALUES ('312', '西宁市', '26');
INSERT INTO `city` VALUES ('313', '海东地区', '26');
INSERT INTO `city` VALUES ('314', '海北藏族自治州', '26');
INSERT INTO `city` VALUES ('315', '黄南藏族自治州', '26');
INSERT INTO `city` VALUES ('316', '海南藏族自治州', '26');
INSERT INTO `city` VALUES ('317', '果洛藏族自治州', '26');
INSERT INTO `city` VALUES ('318', '玉树藏族自治州', '26');
INSERT INTO `city` VALUES ('319', '海西蒙古族藏族自治州', '26');
INSERT INTO `city` VALUES ('320', '西安市', '27');
INSERT INTO `city` VALUES ('321', '铜川市', '27');
INSERT INTO `city` VALUES ('322', '宝鸡市', '27');
INSERT INTO `city` VALUES ('323', '咸阳市', '27');
INSERT INTO `city` VALUES ('324', '渭南市', '27');
INSERT INTO `city` VALUES ('325', '延安市', '27');
INSERT INTO `city` VALUES ('326', '汉中市', '27');
INSERT INTO `city` VALUES ('327', '榆林市', '27');
INSERT INTO `city` VALUES ('328', '安康市', '27');
INSERT INTO `city` VALUES ('329', '商洛市', '27');
INSERT INTO `city` VALUES ('330', '南宁市', '28');
INSERT INTO `city` VALUES ('331', '柳州市', '28');
INSERT INTO `city` VALUES ('332', '桂林市', '28');
INSERT INTO `city` VALUES ('333', '梧州市', '28');
INSERT INTO `city` VALUES ('334', '北海市', '28');
INSERT INTO `city` VALUES ('335', '防城港市', '28');
INSERT INTO `city` VALUES ('336', '钦州市', '28');
INSERT INTO `city` VALUES ('337', '贵港市', '28');
INSERT INTO `city` VALUES ('338', '玉林市', '28');
INSERT INTO `city` VALUES ('339', '百色市', '28');
INSERT INTO `city` VALUES ('340', '贺州市', '28');
INSERT INTO `city` VALUES ('341', '河池市', '28');
INSERT INTO `city` VALUES ('342', '来宾市', '28');
INSERT INTO `city` VALUES ('343', '崇左市', '28');
INSERT INTO `city` VALUES ('344', '拉萨市', '29');
INSERT INTO `city` VALUES ('345', '那曲地区', '29');
INSERT INTO `city` VALUES ('346', '昌都地区', '29');
INSERT INTO `city` VALUES ('347', '山南地区', '29');
INSERT INTO `city` VALUES ('348', '日喀则地区', '29');
INSERT INTO `city` VALUES ('349', '阿里地区', '29');
INSERT INTO `city` VALUES ('350', '林芝地区', '29');
INSERT INTO `city` VALUES ('351', '银川市', '30');
INSERT INTO `city` VALUES ('352', '石嘴山市', '30');
INSERT INTO `city` VALUES ('353', '吴忠市', '30');
INSERT INTO `city` VALUES ('354', '固原市', '30');
INSERT INTO `city` VALUES ('355', '中卫市', '30');
INSERT INTO `city` VALUES ('356', '乌鲁木齐市', '31');
INSERT INTO `city` VALUES ('357', '克拉玛依市', '31');
INSERT INTO `city` VALUES ('358', '石河子市　', '31');
INSERT INTO `city` VALUES ('359', '阿拉尔市', '31');
INSERT INTO `city` VALUES ('360', '图木舒克市', '31');
INSERT INTO `city` VALUES ('361', '五家渠市', '31');
INSERT INTO `city` VALUES ('362', '吐鲁番市', '31');
INSERT INTO `city` VALUES ('363', '阿克苏市', '31');
INSERT INTO `city` VALUES ('364', '喀什市', '31');
INSERT INTO `city` VALUES ('365', '哈密市', '31');
INSERT INTO `city` VALUES ('366', '和田市', '31');
INSERT INTO `city` VALUES ('367', '阿图什市', '31');
INSERT INTO `city` VALUES ('368', '库尔勒市', '31');
INSERT INTO `city` VALUES ('369', '昌吉市　', '31');
INSERT INTO `city` VALUES ('370', '阜康市', '31');
INSERT INTO `city` VALUES ('371', '米泉市', '31');
INSERT INTO `city` VALUES ('372', '博乐市', '31');
INSERT INTO `city` VALUES ('373', '伊宁市', '31');
INSERT INTO `city` VALUES ('374', '奎屯市', '31');
INSERT INTO `city` VALUES ('375', '塔城市', '31');
INSERT INTO `city` VALUES ('376', '乌苏市', '31');
INSERT INTO `city` VALUES ('377', '阿勒泰市', '31');
INSERT INTO `city` VALUES ('378', '呼和浩特市', '32');
INSERT INTO `city` VALUES ('379', '包头市', '32');
INSERT INTO `city` VALUES ('380', '乌海市', '32');
INSERT INTO `city` VALUES ('381', '赤峰市', '32');
INSERT INTO `city` VALUES ('382', '通辽市', '32');
INSERT INTO `city` VALUES ('383', '鄂尔多斯市', '32');
INSERT INTO `city` VALUES ('384', '呼伦贝尔市', '32');
INSERT INTO `city` VALUES ('385', '巴彦淖尔市', '32');
INSERT INTO `city` VALUES ('386', '乌兰察布市', '32');
INSERT INTO `city` VALUES ('387', '锡林郭勒盟', '32');
INSERT INTO `city` VALUES ('388', '兴安盟', '32');
INSERT INTO `city` VALUES ('389', '阿拉善盟', '32');
INSERT INTO `city` VALUES ('390', '澳门特别行政区', '33');
INSERT INTO `city` VALUES ('391', '香港特别行政区', '34');

-- ----------------------------
-- Table structure for company_info
-- ----------------------------
DROP TABLE IF EXISTS `company_info`;
CREATE TABLE `company_info` (
  `company_id` int(11) NOT NULL AUTO_INCREMENT,
  `company_name` varchar(16) NOT NULL COMMENT '组织名称',
  `company_code` varchar(10) NOT NULL,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `status` int(1) NOT NULL DEFAULT '0' COMMENT '禁用状态，0禁用，1可用',
  `split_rows` bigint(11) DEFAULT '30' COMMENT '聊天记录表上限行数，默认1千万',
  `table_time_split` text COMMENT '通过两个时间分隔一个新表',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除，0否，1是',
  PRIMARY KEY (`company_id`),
  UNIQUE KEY `uni_code` (`company_code`) USING BTREE,
  KEY `idx_uid` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of company_info
-- ----------------------------
INSERT INTO `company_info` VALUES ('1', '默认', 'SYSTEM', '1', '0', '30', null, '2021-12-18 18:20:49', '0');
INSERT INTO `company_info` VALUES ('2', '奶茶妹', 'S20211219', '2', '0', '30', null, '2021-12-19 08:56:22', '0');
INSERT INTO `company_info` VALUES ('3', '原味蛋', 'S20220101', '2', '0', '30', null, '2021-12-19 08:57:38', '0');
INSERT INTO `company_info` VALUES ('4', '阳光科技有限公司', 'C20001010', '4', '0', '30', null, '2021-12-19 08:59:21', '0');
INSERT INTO `company_info` VALUES ('5', '广西新能源汽车公司', 'C19981215', '5', '0', '30', null, '2021-12-19 09:00:43', '0');
INSERT INTO `company_info` VALUES ('6', '元宇宙梦幻旗舰店', 'S20221101', '6', '0', '30', null, '2021-12-19 09:02:32', '0');

-- ----------------------------
-- Table structure for permission_func
-- ----------------------------
DROP TABLE IF EXISTS `permission_func`;
CREATE TABLE `permission_func` (
  `per_fun_id` int(11) NOT NULL AUTO_INCREMENT,
  `function_code` varchar(8) NOT NULL COMMENT '功能权限编号',
  `function_name` varchar(30) NOT NULL COMMENT '功能权限名称',
  `grade` tinyint(4) NOT NULL DEFAULT '0' COMMENT '权限等级(0普通用户，1VIP用户，2MVP用户，3SUPER用户)',
  `limit_num` int(11) DEFAULT NULL COMMENT '数量限制(须大于-2)，-1无限，默认NULL',
  PRIMARY KEY (`per_fun_id`),
  UNIQUE KEY `idx_fc` (`function_code`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='功能权限';

-- ----------------------------
-- Records of permission_func
-- ----------------------------
INSERT INTO `permission_func` VALUES ('1', 'F0001', '查看用户的关注用户', '1', null);
INSERT INTO `permission_func` VALUES ('2', 'F0002', '最近联系人15个', '1', '15');
INSERT INTO `permission_func` VALUES ('3', 'F0003', '最近联系人无限', '2', '-1');
INSERT INTO `permission_func` VALUES ('4', 'F0004', '最近联系人5个', '0', '5');

-- ----------------------------
-- Table structure for province
-- ----------------------------
DROP TABLE IF EXISTS `province`;
CREATE TABLE `province` (
  `pid` int(11) NOT NULL DEFAULT '0',
  `province` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of province
-- ----------------------------
INSERT INTO `province` VALUES ('1', '北京市');
INSERT INTO `province` VALUES ('2', '天津市');
INSERT INTO `province` VALUES ('3', '上海市');
INSERT INTO `province` VALUES ('4', '重庆市');
INSERT INTO `province` VALUES ('5', '河北省');
INSERT INTO `province` VALUES ('6', '山西省');
INSERT INTO `province` VALUES ('7', '台湾省');
INSERT INTO `province` VALUES ('8', '辽宁省');
INSERT INTO `province` VALUES ('9', '吉林省');
INSERT INTO `province` VALUES ('10', '黑龙江省');
INSERT INTO `province` VALUES ('11', '江苏省');
INSERT INTO `province` VALUES ('12', '浙江省');
INSERT INTO `province` VALUES ('13', '安徽省');
INSERT INTO `province` VALUES ('14', '福建省');
INSERT INTO `province` VALUES ('15', '江西省');
INSERT INTO `province` VALUES ('16', '山东省');
INSERT INTO `province` VALUES ('17', '河南省');
INSERT INTO `province` VALUES ('18', '湖北省');
INSERT INTO `province` VALUES ('19', '湖南省');
INSERT INTO `province` VALUES ('20', '广东省');
INSERT INTO `province` VALUES ('21', '甘肃省');
INSERT INTO `province` VALUES ('22', '四川省');
INSERT INTO `province` VALUES ('23', '贵州省');
INSERT INTO `province` VALUES ('24', '海南省');
INSERT INTO `province` VALUES ('25', '云南省');
INSERT INTO `province` VALUES ('26', '青海省');
INSERT INTO `province` VALUES ('27', '陕西省');
INSERT INTO `province` VALUES ('28', '广西壮族自治区');
INSERT INTO `province` VALUES ('29', '西藏自治区');
INSERT INTO `province` VALUES ('30', '宁夏回族自治区');
INSERT INTO `province` VALUES ('31', '新疆维吾尔自治区');
INSERT INTO `province` VALUES ('32', '内蒙古自治区');
INSERT INTO `province` VALUES ('33', '澳门特别行政区');
INSERT INTO `province` VALUES ('34', '香港特别行政区');

-- ----------------------------
-- Table structure for split_tb_config
-- ----------------------------
DROP TABLE IF EXISTS `split_tb_config`;
CREATE TABLE `split_tb_config` (
  `split_id` int(11) NOT NULL AUTO_INCREMENT,
  `db_ip` varchar(15) DEFAULT NULL COMMENT '数据库地址',
  `db_name` varchar(30) DEFAULT NULL COMMENT '数据库名',
  `table_name` varchar(30) NOT NULL COMMENT '分表虚名',
  `table_suffix` varchar(512) NOT NULL COMMENT '新增时操作表后缀，用“,”分隔(数量为2/3/5/6/7/9)',
  PRIMARY KEY (`split_id`),
  UNIQUE KEY `uni_tn` (`db_name`,`table_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='分表配置';

-- ----------------------------
-- Records of split_tb_config
-- ----------------------------
INSERT INTO `split_tb_config` VALUES ('1', '127.0.0.1', 'db0', 'chat_log', '0,1,2,3,4,5,6');

-- ----------------------------
-- Table structure for user_company_fk
-- ----------------------------
DROP TABLE IF EXISTS `user_company_fk`;
CREATE TABLE `user_company_fk` (
  `uc_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `company_id` int(11) NOT NULL,
  `company_code` varchar(10) NOT NULL COMMENT '企业信息编号',
  PRIMARY KEY (`uc_id`),
  UNIQUE KEY `uni_uc` (`user_id`,`company_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_company_fk
-- ----------------------------
INSERT INTO `user_company_fk` VALUES ('1', '7', '2', 'S20211219');
INSERT INTO `user_company_fk` VALUES ('2', '8', '3', 'S20220101');
INSERT INTO `user_company_fk` VALUES ('3', '9', '4', 'C20001010');
INSERT INTO `user_company_fk` VALUES ('4', '11', '5', 'C19981215');
INSERT INTO `user_company_fk` VALUES ('5', '12', '6', 'S20221101');
INSERT INTO `user_company_fk` VALUES ('6', '45', '6', 'S20221101');
INSERT INTO `user_company_fk` VALUES ('7', '47', '5', 'C19981215');
INSERT INTO `user_company_fk` VALUES ('8', '48', '4', 'C20001010');

INSERT INTO `sys_user` (`user_id`, `user_code`, `username`, `password`, `salt`, `ticket`, `role_id`, `status`, `online_status`, `mobile`, `sex`, `age`, `user_sign`, `profile_picture`, `user_type`, `like_num`, `country`, `province`, `city`, `login_ip`, `login_type`, `login_again`, `email`, `create_time`, `update_time`, `delete_flag`) VALUES ('7', 'S80d10cf7', '客服aae85', '3939d6dda2c2bd801cded0147169be83', '55b87f4915900af2c9f9abb02e4152b0', '23bfb920062f', NULL, '0', '0', NULL, NULL, NULL, NULL, NULL, '0', '0', NULL, NULL, NULL, '127.0.0.1', '0', '1', NULL, '2021-12-18 21:51:30', '2022-03-18 12:00:24', '0');
INSERT INTO `sys_user` (`user_id`, `user_code`, `username`, `password`, `salt`, `ticket`, `role_id`, `status`, `online_status`, `mobile`, `sex`, `age`, `user_sign`, `profile_picture`, `user_type`, `like_num`, `country`, `province`, `city`, `login_ip`, `login_type`, `login_again`, `email`, `create_time`, `update_time`, `delete_flag`) VALUES ('8', 'S4c1c72a9', '客服5c565', 'a5baa7e22d72a2b16a954cbdf8862ab8', 'e2ea48fec9d275eef072d408ff3e6762', 'a66921a88051', NULL, '0', '0', NULL, NULL, NULL, NULL, NULL, '0', '0', NULL, NULL, NULL, '127.0.0.1', '0', '1', NULL, '2021-12-18 21:52:13', '2022-03-25 17:42:49', '0');
INSERT INTO `sys_user` (`user_id`, `user_code`, `username`, `password`, `salt`, `ticket`, `role_id`, `status`, `online_status`, `mobile`, `sex`, `age`, `user_sign`, `profile_picture`, `user_type`, `like_num`, `country`, `province`, `city`, `login_ip`, `login_type`, `login_again`, `email`, `create_time`, `update_time`, `delete_flag`) VALUES ('9', 'S76472a47', '客服5a477', '226fc57bf4ad5f53a1bce5320fbd20d0', '44d3c91e139aec8a6041cdefd46f53f1', NULL, NULL, '0', '0', NULL, NULL, NULL, NULL, NULL, '0', '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-18 21:53:02', '2022-01-19 09:06:51', '0');
INSERT INTO `sys_user` (`user_id`, `user_code`, `username`, `password`, `salt`, `ticket`, `role_id`, `status`, `online_status`, `mobile`, `sex`, `age`, `user_sign`, `profile_picture`, `user_type`, `like_num`, `country`, `province`, `city`, `login_ip`, `login_type`, `login_again`, `email`, `create_time`, `update_time`, `delete_flag`) VALUES ('11', 'S517903a4', '客服63750', 'edca7180da5972ab1b63b20a88e7737f', 'dbbe7c9fba4cae1d27efd3020b14d9ee', NULL, NULL, '0', '0', NULL, NULL, NULL, NULL, NULL, '0', '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-18 21:55:11', '2022-01-19 09:06:51', '0');
INSERT INTO `sys_user` (`user_id`, `user_code`, `username`, `password`, `salt`, `ticket`, `role_id`, `status`, `online_status`, `mobile`, `sex`, `age`, `user_sign`, `profile_picture`, `user_type`, `like_num`, `country`, `province`, `city`, `login_ip`, `login_type`, `login_again`, `email`, `create_time`, `update_time`, `delete_flag`) VALUES ('12', 'S032f8dd7', '客服9e798', '842e75b56730c325c8b8dd80b47c4e73', '62f5971eb67ea9ce46b59554c333d394', NULL, NULL, '0', '0', NULL, NULL, NULL, NULL, NULL, '0', '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-18 21:55:45', '2022-01-19 09:06:51', '0');
INSERT INTO `sys_user` (`user_id`, `user_code`, `username`, `password`, `salt`, `ticket`, `role_id`, `status`, `online_status`, `mobile`, `sex`, `age`, `user_sign`, `profile_picture`, `user_type`, `like_num`, `country`, `province`, `city`, `login_ip`, `login_type`, `login_again`, `email`, `create_time`, `update_time`, `delete_flag`) VALUES ('45', 'S63027390', '客服849d2', 'cbc836b16eb91260702cda1314c9d2cf', '4fb4e5f57b5a6da7e3b494e81f47b58a', NULL, NULL, '0', '0', NULL, NULL, NULL, NULL, NULL, '0', '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-20 20:13:41', '2022-01-19 09:06:51', '0');
INSERT INTO `sys_user` (`user_id`, `user_code`, `username`, `password`, `salt`, `ticket`, `role_id`, `status`, `online_status`, `mobile`, `sex`, `age`, `user_sign`, `profile_picture`, `user_type`, `like_num`, `country`, `province`, `city`, `login_ip`, `login_type`, `login_again`, `email`, `create_time`, `update_time`, `delete_flag`) VALUES ('47', 'S8e4e2ae2', '客服8d357', '9a9c99b63b9415f345b9326a3237321e', '47fc62c33e4249f6b548161d4f8650bd', NULL, NULL, '0', '0', NULL, NULL, NULL, NULL, NULL, '0', '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-20 20:26:01', '2022-01-19 09:06:51', '0');
INSERT INTO `sys_user` (`user_id`, `user_code`, `username`, `password`, `salt`, `ticket`, `role_id`, `status`, `online_status`, `mobile`, `sex`, `age`, `user_sign`, `profile_picture`, `user_type`, `like_num`, `country`, `province`, `city`, `login_ip`, `login_type`, `login_again`, `email`, `create_time`, `update_time`, `delete_flag`) VALUES ('48', 'S85fd94b6', '客服5da80', '081afc3119a63f7335f5e4ff94d0b03a', 'a845f004f8c921db8d2e55246bd0aad6', NULL, NULL, '0', '0', NULL, NULL, NULL, NULL, NULL, '0', '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-21 07:46:59', '2022-01-19 09:06:51', '0');


