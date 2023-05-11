/*
Navicat MySQL Data Transfer

Source Server         : yc
Source Server Version : 50555
Source Host           : localhost:3306
Source Database       : fresh

Target Server Type    : MYSQL
Target Server Version : 50555
File Encoding         : 65001

Date: 2020-03-28 22:27:27
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `addrinfo`
-- ----------------------------
DROP TABLE IF EXISTS `addrinfo`;
CREATE TABLE `addrinfo` (
  `ano` varchar(100) COLLATE utf8_bin NOT NULL,
  `mno` int(11) DEFAULT NULL,
  `name` varchar(100) COLLATE utf8_bin NOT NULL,
  `tel` varchar(15) COLLATE utf8_bin NOT NULL,
  `province` varchar(100) COLLATE utf8_bin NOT NULL,
  `city` varchar(100) COLLATE utf8_bin NOT NULL,
  `area` varchar(100) COLLATE utf8_bin NOT NULL,
  `addr` varchar(100) COLLATE utf8_bin NOT NULL,
  `flag` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`ano`),
  KEY `FK_addrInfo_mno` (`mno`),
  CONSTRAINT `FK_addrInfo_mno` FOREIGN KEY (`mno`) REFERENCES `memberinfo` (`mno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of addrinfo
-- ----------------------------

-- ----------------------------
-- Table structure for `admininfo`
-- ----------------------------
DROP TABLE IF EXISTS `admininfo`;
CREATE TABLE `admininfo` (
  `aid` int(11) NOT NULL AUTO_INCREMENT,
  `aname` varchar(100) COLLATE utf8_bin NOT NULL,
  `pwd` varchar(100) COLLATE utf8_bin NOT NULL,
  `tel` varchar(15) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`aid`),
  UNIQUE KEY `aname` (`aname`),
  UNIQUE KEY `tel` (`tel`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of admininfo
-- ----------------------------
INSERT INTO admininfo VALUES ('1', 'navy', '*23AE809DDACAF96AF0FD78ED04B6A265E05AA257', '15096098010');

-- ----------------------------
-- Table structure for `cartinfo`
-- ----------------------------
DROP TABLE IF EXISTS `cartinfo`;
CREATE TABLE `cartinfo` (
  `cno` varchar(100) COLLATE utf8_bin NOT NULL,
  `mno` int(11) DEFAULT NULL,
  `gno` int(11) DEFAULT NULL,
  `num` int(11) DEFAULT NULL,
  PRIMARY KEY (`cno`),
  KEY `FK_cartInfo_mno` (`mno`),
  KEY `FK_cartInfo_gno` (`gno`),
  CONSTRAINT `FK_cartInfo_gno` FOREIGN KEY (`gno`) REFERENCES `goodsinfo` (`gno`),
  CONSTRAINT `FK_cartInfo_mno` FOREIGN KEY (`mno`) REFERENCES `memberinfo` (`mno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of cartinfo
-- ----------------------------
INSERT INTO cartinfo VALUES ('11', '1', '2', '6');
INSERT INTO cartinfo VALUES ('12', '1', '5', '4');
INSERT INTO cartinfo VALUES ('366c0b3836dc4774a11228a359ced9de', '1', '1', '4');
INSERT INTO cartinfo VALUES ('52427ce1641e499486eb1383d6f24174', '1', '4', '3');
INSERT INTO cartinfo VALUES ('b5e2d3e997e641a8b3e1dd43ae8f53be', '1', '6', '2');

-- ----------------------------
-- Table structure for `goodsinfo`
-- ----------------------------
DROP TABLE IF EXISTS `goodsinfo`;
CREATE TABLE `goodsinfo` (
  `gno` int(11) NOT NULL AUTO_INCREMENT,
  `gname` varchar(100) COLLATE utf8_bin NOT NULL,
  `tno` int(11) DEFAULT NULL,
  `price` decimal(8,2) DEFAULT NULL,
  `intro` varchar(400) COLLATE utf8_bin DEFAULT NULL,
  `balance` int(11) DEFAULT NULL,
  `pics` varchar(1000) COLLATE utf8_bin DEFAULT NULL,
  `unit` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `qperied` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `weight` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `descr` text COLLATE utf8_bin,
  PRIMARY KEY (`gno`),
  KEY `FK_goodsInfo_tno` (`tno`),
  CONSTRAINT `FK_goodsInfo_tno` FOREIGN KEY (`tno`) REFERENCES `goodstype` (`tno`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of goodsinfo
-- ----------------------------
INSERT INTO goodsinfo VALUES ('1', '红富士', '1', '68.00', '很甜，很好吃', '100', 'images/goods02.jpg', '箱', '1个月', '5KG', null);
INSERT INTO goodsinfo VALUES ('2', '草莓', '1', '98.00', '很甜，很好吃哦', '20', 'images/goods/goods003.jpg;images/goods/goods011.jpg', '件', '5天', '2.5KG', null);
INSERT INTO goodsinfo VALUES ('3', '新疆葡萄', '1', '48.00', '酸酸甜甜就是你啦', '50', 'images/goods/goods002.jpg', '箱', '10天', '5KG', null);
INSERT INTO goodsinfo VALUES ('4', '柠檬', '1', '60.00', '酸酸甜甜就是你啦', '100', 'images/goods/goods001.jpg', '盒', '20天', '10个', null);
INSERT INTO goodsinfo VALUES ('5', '脐橙', '1', '38.00', '酸酸甜甜就是你啦', '100', 'images/goods/goods014.jpg', '箱', '30天', '2.5KG', null);
INSERT INTO goodsinfo VALUES ('6', '猕猴桃', '1', '88.00', '酸酸甜甜就是你啦', '60', 'images/goods/goods012.jpg', '箱', '15天', '2.5KG', null);
INSERT INTO goodsinfo VALUES ('7', '基围虾肉', '2', '92.00', '很鲜嫩，超值', '120', 'images/goods/goods018.jpg', '包', '5天', '2.5KG', null);
INSERT INTO goodsinfo VALUES ('8', '河蚌', '2', '120.00', '很鲜嫩，超实惠', '40', 'images/goods/goods019.jpg', '件', '5天', '2.5KG', null);
INSERT INTO goodsinfo VALUES ('9', '精品牛肉', '3', '38.00', '很鲜嫩，你值得拥有', '200', 'images/goods/goods31.jpg', '斤', '10天', '500G', null);
INSERT INTO goodsinfo VALUES ('10', '精品五花肉', '3', '28.00', '很新鲜，超实惠', '100', 'images/goods/goods32.jpg', '斤', '10天', '500G', null);
INSERT INTO goodsinfo VALUES ('11', '农家土鸡蛋', '4', '66.00', '纯天然，无污染，生态喂养', '100', 'images/goods/goods401.jpg', '盒', '30天', '30个', null);
INSERT INTO goodsinfo VALUES ('12', '天然鹌鹑蛋', '4', '5.00', '生态喂养，天然污染', '100', 'images/goods/goods402.jpg', '斤', '30天', '500G', null);
INSERT INTO goodsinfo VALUES ('13', '有机芹菜', '5', '8.00', '生态种植，天然无污染', '100', 'images/goods/goods501.jpg;images/goods/goods502.jpg', '斤', '10天', '500G', null);
INSERT INTO goodsinfo VALUES ('14', '有机莴笋', '5', '6.00', '纯天然，无污染，生态种植', '100', 'images/goods/goods503.jpg', '斤', '10天', '500G', null);
INSERT INTO goodsinfo VALUES ('15', '水饺', '6', '30.00', '蔬菜水饺，美味健康', '100', 'images/goods/goods601.jpg;images/goods/goods602.jpg', '包', '20天', '2.5KG', null);
INSERT INTO goodsinfo VALUES ('16', '芝麻汤圆', '6', '28.00', '顺滑养颜，健康美味', '100', 'images/goods/goods603.jpg', '包', '10天', '3KG', null);

-- ----------------------------
-- Table structure for `goodstype`
-- ----------------------------
DROP TABLE IF EXISTS `goodstype`;
CREATE TABLE `goodstype` (
  `tno` int(11) NOT NULL AUTO_INCREMENT,
  `tname` varchar(100) COLLATE utf8_bin NOT NULL,
  `pic` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`tno`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of goodstype
-- ----------------------------
INSERT INTO goodstype VALUES ('1', '新鲜水果', 'images/banner01.jpg', '1');
INSERT INTO goodstype VALUES ('2', '海鲜水产', 'images/banner02.jpg', '1');
INSERT INTO goodstype VALUES ('3', '猪牛羊肉', 'images/banner03.jpg', '1');
INSERT INTO goodstype VALUES ('4', '禽类蛋品', 'images/banner04.jpg', '1');
INSERT INTO goodstype VALUES ('5', '新鲜蔬菜', 'images/banner05.jpg', '1');
INSERT INTO goodstype VALUES ('6', '速冻食品', 'images/banner06.jpg', '1');

-- ----------------------------
-- Table structure for `memberinfo`
-- ----------------------------
DROP TABLE IF EXISTS `memberinfo`;
CREATE TABLE `memberinfo` (
  `mno` int(11) NOT NULL AUTO_INCREMENT,
  `nickName` varchar(100) COLLATE utf8_bin NOT NULL,
  `realName` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `pwd` varchar(100) COLLATE utf8_bin NOT NULL,
  `tel` varchar(15) COLLATE utf8_bin NOT NULL,
  `email` varchar(100) COLLATE utf8_bin NOT NULL,
  `photo` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `regDate` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`mno`),
  UNIQUE KEY `nickName` (`nickName`),
  UNIQUE KEY `tel` (`tel`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of memberinfo
-- ----------------------------
INSERT INTO memberinfo VALUES ('1', 'navy', '', 'c8837b23ff8aaa8a2dde915473ce0991', '15096098010', '475651857@qq.com', '', '2019-10-03 15:56:13', '1');
INSERT INTO memberinfo VALUES ('2', 'yc', '', 'c8837b23ff8aaa8a2dde915473ce0991', '15096098012', '1293580602@qq.com', '', '2019-10-03 16:41:50', '1');

-- ----------------------------
-- Table structure for `orderinfo`
-- ----------------------------
DROP TABLE IF EXISTS `orderinfo`;
CREATE TABLE `orderinfo` (
  `ono` varchar(100) COLLATE utf8_bin NOT NULL,
  `odate` datetime DEFAULT NULL,
  `ano` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `sdate` datetime DEFAULT NULL,
  `rdate` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `invoice` int(11) DEFAULT NULL,
  PRIMARY KEY (`ono`),
  KEY `FK_orderInfo_ano` (`ano`),
  CONSTRAINT `FK_orderInfo_ano` FOREIGN KEY (`ano`) REFERENCES `addrinfo` (`ano`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of orderinfo
-- ----------------------------

-- ----------------------------
-- Table structure for `orderiteminfo`
-- ----------------------------
DROP TABLE IF EXISTS `orderiteminfo`;
CREATE TABLE `orderiteminfo` (
  `ino` int(11) NOT NULL AUTO_INCREMENT,
  `ono` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `gno` int(11) DEFAULT NULL,
  `nums` int(11) DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`ino`),
  KEY `FK_orderItemInfo_gno` (`gno`),
  KEY `FK_orderItemInfo_ono` (`ono`),
  CONSTRAINT `FK_orderItemInfo_gno` FOREIGN KEY (`gno`) REFERENCES `goodsinfo` (`gno`),
  CONSTRAINT `FK_orderItemInfo_ono` FOREIGN KEY (`ono`) REFERENCES `orderinfo` (`ono`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of orderiteminfo
-- ----------------------------
