/*
 Navicat Premium Data Transfer

 Source Server         : yjh
 Source Server Type    : MySQL
 Source Server Version : 80026
 Source Host           : localhost:3306
 Source Schema         : xywm

 Target Server Type    : MySQL
 Target Server Version : 80026
 File Encoding         : 65001

 Date: 08/04/2026 22:56:44
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_default` tinyint NULL DEFAULT 0,
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of address
-- ----------------------------
INSERT INTO `address` VALUES (1, 2, '张三', '13900000000', '学生公寓1号楼301室', 1, '2026-03-30 19:22:34', '2026-04-01 14:57:06');
INSERT INTO `address` VALUES (2, 2, '张三', '13900000000', '图书馆一楼大厅', 0, '2026-03-30 19:22:34', '2026-04-01 14:57:10');
INSERT INTO `address` VALUES (3, 13, '小李', '13900000001', '学生公寓3号楼502室', 1, '2026-03-30 19:22:34', '2026-03-30 19:22:34');
INSERT INTO `address` VALUES (4, 14, '王小明', '13900000002', '学生公寓5号楼108室', 1, '2026-03-30 19:22:34', '2026-03-30 19:22:34');

-- ----------------------------
-- Table structure for cart
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `merchant_id` bigint NOT NULL,
  `dish_id` bigint NOT NULL,
  `quantity` int NOT NULL DEFAULT 1,
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cart
-- ----------------------------

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `type` tinyint NOT NULL DEFAULT 1 COMMENT '0平台一级分类 1商家子分类',
  `parent_id` bigint NULL DEFAULT 0,
  `merchant_id` bigint NULL DEFAULT 0,
  `sort` int NULL DEFAULT 0,
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, '中式快餐', 0, 0, 0, 1, '2026-03-28 17:34:53', '2026-03-28 17:34:53', 0);
INSERT INTO `category` VALUES (2, '日韩料理', 0, 0, 0, 2, '2026-03-28 17:34:53', '2026-03-28 17:34:53', 0);
INSERT INTO `category` VALUES (3, '饮品甜点', 0, 0, 0, 3, '2026-03-28 17:34:53', '2026-03-28 17:34:53', 0);
INSERT INTO `category` VALUES (4, '西式快餐', 0, 0, 0, 4, '2026-03-28 17:34:53', '2026-03-28 17:34:53', 0);
INSERT INTO `category` VALUES (5, '小吃夜宵', 0, 0, 0, 5, '2026-03-28 17:34:53', '2026-03-28 17:34:53', 0);
INSERT INTO `category` VALUES (6, '招牌系列', 1, 0, 3, 1, '2026-03-28 18:08:33', '2026-03-28 18:12:18', 0);
INSERT INTO `category` VALUES (7, '夏季特惠', 1, 0, 3, 2, '2026-03-28 18:13:08', '2026-03-28 18:18:31', 0);
INSERT INTO `category` VALUES (8, '每日福利', 1, 0, 3, 3, '2026-03-28 18:35:19', '2026-03-28 18:35:19', 0);
INSERT INTO `category` VALUES (9, '招牌锅底', 1, 0, 5, 1, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `category` VALUES (10, '精选荤菜', 1, 0, 5, 2, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `category` VALUES (11, '蔬菜豆腐', 1, 0, 5, 3, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `category` VALUES (12, '主食小吃', 1, 0, 5, 4, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `category` VALUES (13, '经典拉面', 1, 0, 6, 1, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `category` VALUES (14, '盖浇饭', 1, 0, 6, 2, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `category` VALUES (15, '小食饮品', 1, 0, 6, 3, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `category` VALUES (16, '荤串', 1, 0, 7, 1, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `category` VALUES (17, '素串', 1, 0, 7, 2, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `category` VALUES (18, '主食', 1, 0, 7, 3, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `category` VALUES (19, '汉堡套餐', 1, 0, 8, 1, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `category` VALUES (20, '炸鸡系列', 1, 0, 8, 2, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `category` VALUES (21, '饮料', 1, 0, 8, 3, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `category` VALUES (22, '奶茶', 1, 0, 9, 1, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `category` VALUES (23, '冰淇淋', 1, 0, 9, 2, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `category` VALUES (24, '蛋糕甜点', 1, 0, 9, 3, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `category` VALUES (25, '盖饭', 1, 0, 10, 1, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `category` VALUES (26, '炒菜', 1, 0, 10, 2, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `category` VALUES (27, '汤品', 1, 0, 10, 3, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `category` VALUES (28, '寿司卷', 1, 0, 11, 1, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `category` VALUES (29, '刺身', 1, 0, 11, 2, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `category` VALUES (30, '热食', 1, 0, 11, 3, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `category` VALUES (31, '锅底', 1, 0, 12, 1, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `category` VALUES (32, '肉类', 1, 0, 12, 2, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `category` VALUES (33, '素菜', 1, 0, 12, 3, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);

-- ----------------------------
-- Table structure for coupon
-- ----------------------------
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `merchant_id` bigint NOT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '券名称',
  `type` tinyint NOT NULL DEFAULT 1 COMMENT '1满减券 2折扣券',
  `min_amount` decimal(10, 2) NOT NULL COMMENT '使用门槛',
  `discount_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '满减金额',
  `discount_rate` decimal(3, 1) NULL DEFAULT NULL COMMENT '折扣率 如8.5折',
  `total` int NOT NULL DEFAULT 100 COMMENT '发行总量',
  `received` int NULL DEFAULT 0 COMMENT '已领取数量',
  `status` tinyint NULL DEFAULT 1 COMMENT '1正常 0停用',
  `expire_days` int NULL DEFAULT 7 COMMENT '领取后几天过期',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of coupon
-- ----------------------------

-- ----------------------------
-- Table structure for discount_activity
-- ----------------------------
DROP TABLE IF EXISTS `discount_activity`;
CREATE TABLE `discount_activity`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `merchant_id` bigint NOT NULL,
  `min_amount` decimal(10, 2) NOT NULL COMMENT '满多少',
  `discount_amount` decimal(10, 2) NOT NULL COMMENT '减多少',
  `status` tinyint NULL DEFAULT 1 COMMENT '1启用 0禁用',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of discount_activity
-- ----------------------------

-- ----------------------------
-- Table structure for dish
-- ----------------------------
DROP TABLE IF EXISTS `dish`;
CREATE TABLE `dish`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `merchant_id` bigint NOT NULL,
  `category_id` bigint NOT NULL,
  `price` decimal(10, 2) NOT NULL,
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` tinyint NULL DEFAULT 1 COMMENT '0下架 1上架',
  `stock` int NULL DEFAULT 999,
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 57 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dish
-- ----------------------------
INSERT INTO `dish` VALUES (1, '好喝的', 3, 6, 123.00, 'http://localhost:8080/uploads/668ced5c8a5a4716a515082901456099.png', '随机好喝的', 1, 999, '2026-03-28 18:38:02', '2026-03-28 18:53:44', 0);
INSERT INTO `dish` VALUES (2, '番茄锅底', 5, 9, 6.00, 'https://images.unsplash.com/photo-1569050467447-ce54b3bbc37d?w=400', '酸甜开胃，汤底清澈', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (3, '麻辣锅底', 5, 9, 8.00, 'https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=400', '麻辣鲜香，越吃越爽', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (4, '牛肉丸', 5, 10, 6.00, 'https://images.unsplash.com/photo-1607116667981-ff7f4ce5a04b?w=400', 'Q弹爽口，真材实料', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (5, '午餐肉', 5, 10, 5.00, 'https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=400', '经典搭档，必点单品', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (6, '鸭血', 5, 10, 4.00, 'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=400', '嫩滑入味，吸汤一流', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (7, '嫩豆腐', 5, 11, 3.00, 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400', '嫩滑爽口，健康低卡', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (8, '土豆片', 5, 11, 2.00, 'https://images.unsplash.com/photo-1518977676601-b53f82aba655?w=400', '绵软入味，家常首选', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (9, '宽粉', 5, 12, 3.00, 'https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=400', '顺滑爽口，吸汤满分', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (10, '浓厚豚骨拉面', 6, 13, 28.00, 'https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=400', '18小时熬制骨汤，浓郁醇厚', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (11, '酱油叉烧面', 6, 13, 24.00, 'https://images.unsplash.com/photo-1557872943-16a5ac26437e?w=400', '经典酱香，叉烧厚切', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (12, '味噌拉面', 6, 13, 26.00, 'https://images.unsplash.com/photo-1552611052-33e04de081de?w=400', '北海道味噌，暖胃首选', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (13, '亲子丼', 6, 14, 22.00, 'https://images.unsplash.com/photo-1617196034183-421b4040ed20?w=400', '鸡肉鸡蛋盖饭，日式经典', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (14, '牛肉咖喱饭', 6, 14, 25.00, 'https://images.unsplash.com/photo-1604152135912-04a022e23696?w=400', '浓郁咖喱，配米饭绝了', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (15, '抹茶拿铁', 6, 15, 15.00, 'https://images.unsplash.com/photo-1536304993881-ff86e6fba0d1?w=400', '日式抹茶，丝滑奶香', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (16, '羊肉串', 7, 16, 4.00, 'https://images.unsplash.com/photo-1529042410759-befb1204b468?w=400', '新疆羊肉，孜然飘香', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (17, '鸡翅', 7, 16, 6.00, 'https://images.unsplash.com/photo-1527477396000-e27163b481c2?w=400', '外酥里嫩，撸串必点', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (18, '烤鱿鱼', 7, 16, 12.00, 'https://images.unsplash.com/photo-1559410545-0bdcd187e0a6?w=400', '整只鱿鱼，海鲜鲜甜', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (19, '玉米', 7, 17, 4.00, 'https://images.unsplash.com/photo-1473093226795-af9932fe5856?w=400', '烤玉米，香甜软糯', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (20, '金针菇', 7, 17, 5.00, 'https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=400', '锡纸包裹，汁水丰富', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (21, '烤冷面', 7, 18, 10.00, 'https://images.unsplash.com/photo-1563245372-f21724e3856d?w=400', '东北特色，酸甜辣香', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (22, '蒜香烤馒头', 7, 18, 3.00, 'https://images.unsplash.com/photo-1509440159596-0249088772ff?w=400', '外脆内软，蒜香扑鼻', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (23, '双层牛肉堡套餐', 8, 19, 32.00, 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400', '双层安格斯牛肉饼，附薯条+饮料', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (24, '鸡腿堡套餐', 8, 19, 28.00, 'https://images.unsplash.com/photo-1606755962773-d324e0a13086?w=400', '香脆鸡腿，经典组合', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (25, '香辣鸡腿堡', 8, 19, 22.00, 'https://images.unsplash.com/photo-1553979459-d2229ba7433b?w=400', '辣度可选，够劲够味', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (26, '黄金脆鸡块(6块)', 8, 20, 15.00, 'https://images.unsplash.com/photo-1562967914-608f82629710?w=400', '外酥内嫩，蘸酱更香', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (27, '爆辣鸡米花', 8, 20, 12.00, 'https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=400', '颗颗入味，停不下来', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (28, '可乐(大)', 8, 21, 8.00, 'https://images.unsplash.com/photo-1561758033-d89a9ad46330?w=400', '冰爽畅饮，解腻必备', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (29, '奥利奥奶昔', 8, 21, 16.00, 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400', '浓郁奶香，饼干碎满满', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (30, '芋泥波波奶茶', 9, 22, 18.00, 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400', '芋泥满满，珍珠Q弹', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (31, '草莓奶茶', 9, 22, 16.00, 'https://images.unsplash.com/photo-1544145945-f90425340c7e?w=400', '真实草莓果粒，酸甜可口', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (32, '乌龙拿铁', 9, 22, 19.00, 'https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=400', '茶香浓郁，奶感醇厚', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (33, '抹茶冰淇淋', 9, 23, 14.00, 'https://images.unsplash.com/photo-1501443762994-82bd5dace89a?w=400', '宇治抹茶，微苦回甘', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (34, '草莓圣代', 9, 23, 16.00, 'https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=400', '新鲜草莓淋酱，颜值爆表', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (35, '提拉米苏', 9, 24, 22.00, 'https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=400', '意式经典，咖啡香浓', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (36, '草莓千层蛋糕', 9, 24, 28.00, 'https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=400', '20层薄饼，奶油绵密', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (37, '红烧肉盖饭', 10, 25, 18.00, 'https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=400', '五花肉慢炖，入口即化', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (38, '鱼香肉丝盖饭', 10, 25, 15.00, 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400', '川味经典，下饭神菜', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (39, '宫保鸡丁盖饭', 10, 25, 16.00, 'https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=400', '鸡丁脆嫩，花生香脆', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (40, '番茄炒蛋', 10, 26, 10.00, 'https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=400', '家常味道，百吃不厌', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (41, '地三鲜', 10, 26, 12.00, 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400', '茄子土豆青椒，素食首选', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (42, '蛋花汤', 10, 27, 5.00, 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=400', '清淡滋补，温暖肠胃', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (43, '紫菜虾皮汤', 10, 27, 6.00, 'https://images.unsplash.com/photo-1534482421-64566f976cfa?w=400', '鲜味十足，营养丰富', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (44, '三文鱼寿司(2件)', 11, 28, 22.00, 'https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=400', '新鲜三文鱼，入口即化', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (45, '虾天妇罗卷', 11, 28, 25.00, 'https://images.unsplash.com/photo-1617196034183-421b4040ed20?w=400', '炸虾+牛油果，口感丰富', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (46, '加州卷(6件)', 11, 28, 28.00, 'https://images.unsplash.com/photo-1562802378-063ec186a863?w=400', '蟹肉牛油果，经典美式', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (47, '金枪鱼刺身', 11, 29, 35.00, 'https://images.unsplash.com/photo-1534482421-64566f976cfa?w=400', '深海金枪鱼，鲜嫩多汁', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (48, '北极贝刺身', 11, 29, 30.00, 'https://images.unsplash.com/photo-1580822184713-fc5400e7fe10?w=400', '来自北极的鲜甜贝肉', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (49, '茶碗蒸', 11, 30, 12.00, 'https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=400', '嫩滑如豆腐，日式精致', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (50, '味噌汤', 11, 30, 8.00, 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=400', '日式经典，暖胃必备', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (51, '番茄牛腩锅底', 12, 31, 12.00, 'https://images.unsplash.com/photo-1569050467447-ce54b3bbc37d?w=400', '酸甜番茄，越煮越鲜', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (52, '清汤骨汤锅底', 12, 31, 10.00, 'https://images.unsplash.com/photo-1557872943-16a5ac26437e?w=400', '大骨熬制，汤色奶白', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (53, '肥牛卷', 12, 32, 22.00, 'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=400', '雪花肥牛，涮10秒即食', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (54, '羔羊肉卷', 12, 32, 20.00, 'https://images.unsplash.com/photo-1529042410759-befb1204b468?w=400', '内蒙羊肉，无膻味', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (55, '虾滑', 12, 32, 18.00, 'https://images.unsplash.com/photo-1607116667981-ff7f4ce5a04b?w=400', '鲜虾手打，Q弹爽口', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (56, '菌菇拼盘', 12, 33, 15.00, 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400', '金针菇+茶树菇+杏鲍菇', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);
INSERT INTO `dish` VALUES (57, '冻豆腐', 12, 33, 8.00, 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400', '吸汤满分，素食最爱', 1, 999, '2026-03-30 19:22:34', '2026-03-30 19:22:34', 0);

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` tinyint NULL DEFAULT 1 COMMENT '1显示 0隐藏',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notice
-- ----------------------------
INSERT INTO `notice` VALUES (1, '测试专用', '欢迎大家测试', 1, '2026-04-02 17:52:12', '2026-04-02 17:52:12');

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `dish_id` bigint NOT NULL,
  `dish_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `dish_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `price` decimal(10, 2) NOT NULL,
  `quantity` int NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (1, 1, 2, '番茄锅底', 'https://images.unsplash.com/photo-1569050467447-ce54b3bbc37d?w=400', 6.00, 2);
INSERT INTO `order_item` VALUES (2, 1, 4, '牛肉丸', 'https://images.unsplash.com/photo-1607116667981-ff7f4ce5a04b?w=400', 6.00, 1);
INSERT INTO `order_item` VALUES (3, 1, 5, '午餐肉', 'https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=400', 5.00, 1);
INSERT INTO `order_item` VALUES (4, 1, 6, '鸭血', 'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=400', 4.00, 1);
INSERT INTO `order_item` VALUES (5, 1, 7, '嫩豆腐', 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400', 3.00, 1);
INSERT INTO `order_item` VALUES (6, 1, 8, '土豆片', 'https://images.unsplash.com/photo-1518977676601-b53f82aba655?w=400', 2.00, 1);
INSERT INTO `order_item` VALUES (7, 1, 9, '宽粉', 'https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=400', 3.00, 2);
INSERT INTO `order_item` VALUES (8, 2, 30, '芋泥波波奶茶', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400', 18.00, 1);

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` bigint NOT NULL,
  `merchant_id` bigint NOT NULL,
  `address_id` bigint NULL DEFAULT NULL,
  `address_snapshot` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `total_amount` decimal(10, 2) NOT NULL,
  `actual_amount` decimal(10, 2) NOT NULL,
  `status` tinyint NULL DEFAULT 0 COMMENT '0待付款 1待接单 2已接单 3配送中 4已完成 5已取消',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 'e4ab661aea6340ba8143a45413c81103', 2, 5, 1, '张三 13900000000 学生公寓1号楼301室', 38.00, 38.00, 4, '', '2026-03-31 13:00:49', '2026-04-01 14:39:53', 0);
INSERT INTO `orders` VALUES (2, '6fba80fbdecf4d57acaae7422a692653', 2, 9, 2, '张三 13900000000 图书馆一楼大厅', 18.00, 18.00, 1, '', '2026-04-02 17:54:39', '2026-04-02 17:54:39', 0);

-- ----------------------------
-- Table structure for review
-- ----------------------------
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `merchant_id` bigint NOT NULL,
  `rating` tinyint NOT NULL DEFAULT 5,
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of review
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称/店铺名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `role` tinyint NOT NULL COMMENT '角色: 0-管理员, 1-商家, 2-学生',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常, 2-待审核',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `license_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '营业执照(商家专属)',
  `category_id` bigint NULL DEFAULT NULL COMMENT '商家所属平台分类ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '超级管理员', NULL, 0, 1, NULL, '2026-03-28 12:25:44', '2026-03-28 12:25:44', 0, NULL, NULL);
INSERT INTO `user` VALUES (2, 'zhangsan', '25f9e794323b453885f5181f1b624d0b', '张三', '15911111111', 2, 1, '', '2026-03-28 12:35:27', '2026-03-28 12:35:27', 0, NULL, NULL);
INSERT INTO `user` VALUES (3, '商家', 'e10adc3949ba59abbe56e057f20f883e', '蜜雪', '15900000000', 1, 1, 'https://images.unsplash.com/photo-1563245372-f21724e3856d?w=600', '2026-03-28 12:37:53', '2026-03-30 19:32:57', 0, NULL, 3);
INSERT INTO `user` VALUES (4, '商家1', 'e10adc3949ba59abbe56e057f20f883e', '云语蜜茶', '15922222222', 1, 1, 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600', '2026-03-28 13:00:36', '2026-03-30 19:32:57', 0, 'http://localhost:8080/uploads/081a555fa9144899ad580417a52cf202.png', 3);
INSERT INTO `user` VALUES (5, 'malatang', 'e10adc3949ba59abbe56e057f20f883e', '辣爽麻辣烫', '13811110001', 1, 1, 'https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=600', '2026-03-30 19:22:33', '2026-03-30 19:32:57', 0, NULL, 1);
INSERT INTO `user` VALUES (6, 'ramen001', 'e10adc3949ba59abbe56e057f20f883e', '一风堂拉面', '13811110002', 1, 1, 'https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=600', '2026-03-30 19:22:33', '2026-03-30 19:32:58', 0, NULL, 2);
INSERT INTO `user` VALUES (7, 'bbq_night', 'e10adc3949ba59abbe56e057f20f883e', '深夜烤串摊', '13811110003', 1, 1, 'https://images.unsplash.com/photo-1529042410759-befb1204b468?w=600', '2026-03-30 19:22:33', '2026-03-30 19:32:58', 0, NULL, 5);
INSERT INTO `user` VALUES (8, 'burger_king2', 'e10adc3949ba59abbe56e057f20f883e', '校园汉堡屋', '13811110004', 1, 1, 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600', '2026-03-30 19:22:33', '2026-03-30 19:32:58', 0, NULL, 4);
INSERT INTO `user` VALUES (9, 'dessert_hut', 'e10adc3949ba59abbe56e057f20f883e', '甜蜜一刻甜品', '13811110005', 1, 1, 'https://images.unsplash.com/photo-1501443762994-82bd5dace89a?w=600', '2026-03-30 19:22:33', '2026-03-30 19:32:58', 0, NULL, 3);
INSERT INTO `user` VALUES (10, 'rice_box', 'e10adc3949ba59abbe56e057f20f883e', '饭盒快餐', '13811110006', 1, 1, 'https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=600', '2026-03-30 19:22:33', '2026-03-30 19:32:58', 0, NULL, 1);
INSERT INTO `user` VALUES (11, 'sushi_go', 'e10adc3949ba59abbe56e057f20f883e', '旋转寿司', '13811110007', 1, 1, 'https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=600', '2026-03-30 19:22:33', '2026-03-30 19:32:58', 0, NULL, 2);
INSERT INTO `user` VALUES (12, 'hotpot_mini', 'e10adc3949ba59abbe56e057f20f883e', '小锅涮涮锅', '13811110008', 1, 1, 'https://images.unsplash.com/photo-1569050467447-ce54b3bbc37d?w=600', '2026-03-30 19:22:33', '2026-03-30 19:32:58', 0, NULL, 1);
INSERT INTO `user` VALUES (13, 'student_li', 'e10adc3949ba59abbe56e057f20f883e', '小李同学', '13900000001', 2, 1, NULL, '2026-03-30 19:22:33', '2026-03-30 19:22:33', 0, NULL, NULL);
INSERT INTO `user` VALUES (14, 'student_wang', 'e10adc3949ba59abbe56e057f20f883e', '王小明', '13900000002', 2, 1, NULL, '2026-03-30 19:22:33', '2026-03-30 19:22:33', 0, NULL, NULL);
INSERT INTO `user` VALUES (15, 'student_zhao', 'e10adc3949ba59abbe56e057f20f883e', '赵同学', '13900000003', 2, 1, NULL, '2026-03-30 19:22:33', '2026-03-30 19:22:33', 0, NULL, NULL);

-- ----------------------------
-- Table structure for user_coupon
-- ----------------------------
DROP TABLE IF EXISTS `user_coupon`;
CREATE TABLE `user_coupon`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `coupon_id` bigint NOT NULL,
  `merchant_id` bigint NOT NULL,
  `status` tinyint NULL DEFAULT 0 COMMENT '0未使用 1已使用 2已过期',
  `receive_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `expire_time` datetime(0) NULL DEFAULT NULL,
  `use_time` datetime(0) NULL DEFAULT NULL,
  `order_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_coupon
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
