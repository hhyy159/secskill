-- 2026.5.28 余浩洋
-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS shop;
USE shop;

-- 用户表
CREATE TABLE `user` (
    `user_id` INT NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键',
    `account` VARCHAR(50) NOT NULL COMMENT '账号',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_account` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 商品表
CREATE TABLE `product` (
    `product_id` INT NOT NULL AUTO_INCREMENT COMMENT '商品ID，主键',
    `product_name` VARCHAR(100) NOT NULL COMMENT '商品名字',
    `description` TEXT COMMENT '商品描述',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '库存',
    `start_time` DATETIME NOT NULL COMMENT '秒杀开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '秒杀结束时间',
    PRIMARY KEY (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 订单表
CREATE TABLE `order` (
    `order_id` INT NOT NULL AUTO_INCREMENT COMMENT '订单ID，主键',
    `user_id` INT NOT NULL COMMENT '用户ID，外键，关联用户表',
    `product_id` INT NOT NULL COMMENT '商品ID',
    PRIMARY KEY (`order_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`product_id`) REFERENCES `product`(`product_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';
