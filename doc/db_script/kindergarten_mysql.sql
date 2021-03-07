/*
Navicat MySQL Data Transfer

Source Server         : 120.79.28.199
Source Server Version : 8.0.19
Source Host           : 120.79.28.199:3306
Source Database       : kindergarten

Target Server Type    : MYSQL
Target Server Version : 8.0.19
File Encoding         : 65001

Date: 2020-01-17 15:35:01
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tb_address
-- ----------------------------
DROP TABLE IF EXISTS `kd_wechat`;
CREATE TABLE `kd_wechat` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键序号',
  `app_id` varchar(25) NOT NULL COMMENT '公众号id',
  `app_secret` varchar(255) NOT NULL COMMENT '公众号密钥',
  `encoding_aes_key` varchar(255) DEFAULT NULL COMMENT 'AES加密密钥',
  `access_token` varchar(255) DEFAULT NULL COMMENT '访问token',
  `refresh_token` varchar(255) DEFAULT NULL COMMENT '刷新token',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of tb_address
-- ----------------------------
INSERT INTO `kd_wechat`(app_id,app_secret,encoding_aes_key,create_time) VALUES ('wx1b40edaafb411fcb', '88bd7e6ec57d613f2676b2e395c26dac', 'Com4zeghDIszyutlJGY5yqzyQUm9YR2vaVW0GNfYOtp',current_timestamp );