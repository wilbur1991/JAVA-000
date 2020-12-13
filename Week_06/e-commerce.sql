
CREATE DATABASE IF NOT EXISTS e_commerce;

-- ----------------------------
-- Table structure for commodity
-- ----------------------------
CREATE TABLE IF NOT EXISTS `commodity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(255) DEFAULT NULL COMMENT '商品名称',
  `origin_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
  `price` decimal(10,2) DEFAULT NULL COMMENT '实际售价',
  `desc` varchar(2048) DEFAULT NULL COMMENT '商品描述',
  `status` tinyint(4) DEFAULT NULL COMMENT '商品状态：0-待发售，1-上架，2-下架',
  `first_pic` varchar(1024) DEFAULT NULL COMMENT '商品封面图url',
  `pics` varchar(2048) DEFAULT NULL COMMENT '图片url列表',
  `date_created` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for order
-- ----------------------------
CREATE TABLE IF NOT EXISTS `t_order` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `order_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `total_amount` decimal(10,0) DEFAULT NULL COMMENT '总金额',
  `actual_amount` decimal(10,0) DEFAULT NULL COMMENT '实付金额',
  `date_created` datetime DEFAULT NULL COMMENT '创建时间',
  `status` tinyint(4) DEFAULT NULL COMMENT '订单状态：0-待支付，1-已支付，2-已取消',
  `deliver_status` tinyint(4) DEFAULT NULL COMMENT '订单状态：0-初始状态，1-待配送，2-配送中，3配送成功，4已完成',
  `last_updated` datetime DEFAULT NULL COMMENT '最近更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
CREATE TABLE IF NOT EXISTS `t_order_item` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(20) unsigned DEFAULT NULL COMMENT '订单ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `commodity_id` bigint(20) unsigned DEFAULT NULL COMMENT '商品ID',
  `num` int(10) DEFAULT NULL COMMENT '购买商品数量',
  `origin_price` decimal(10,2) DEFAULT NULL COMMENT '商品原价',
  `actual_price` decimal(10,2) DEFAULT NULL COMMENT '商品实际价格',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for user
-- ----------------------------
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) DEFAULT NULL COMMENT '昵称',
  `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(512) DEFAULT NULL COMMENT '头像链接',
  `date_created` datetime DEFAULT NULL COMMENT '创建时间',
  `last_updated` datetime DEFAULT NULL COMMENT '最近更新时间',
  `phone` varchar(11) DEFAULT NULL COMMENT '手机号',
  `password` varchar(255) DEFAULT NULL COMMENT '加密后的密码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

