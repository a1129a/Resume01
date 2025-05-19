-- MySQL数据库初始化脚本 - 简历一键生成与投递助手项目
-- 创建时间: 2025-05-19

-- -----------------------------------------------------
-- 删除数据库如果已存在，并创建新数据库
-- -----------------------------------------------------
DROP DATABASE IF EXISTS resume_assistant;
CREATE DATABASE resume_assistant DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE resume_assistant;

-- -----------------------------------------------------
-- 用户表
-- -----------------------------------------------------
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码(加密后)',
  `email` VARCHAR(100) NULL COMMENT '邮箱',
  `phone` VARCHAR(20) NULL COMMENT '手机号',
  `real_name` VARCHAR(50) NULL COMMENT '真实姓名',
  `avatar` VARCHAR(255) NULL COMMENT '头像URL',
  `wechat_id` VARCHAR(100) NULL COMMENT '微信唯一标识',
  `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色(USER/ADMIN)',
  `is_enabled` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
  `created_time` DATETIME NOT NULL COMMENT '创建时间',
  `updated_time` DATETIME NULL COMMENT '更新时间',
  `last_login_time` DATETIME NULL COMMENT '最后登录时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC),
  UNIQUE INDEX `phone_UNIQUE` (`phone` ASC),
  UNIQUE INDEX `wechat_id_UNIQUE` (`wechat_id` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- -----------------------------------------------------
-- 简历基本信息表
-- -----------------------------------------------------
CREATE TABLE `resume` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '简历ID',
  `user_id` BIGINT NOT NULL COMMENT '所属用户ID',
  `title` VARCHAR(100) NOT NULL COMMENT '简历标题',
  `target_position` VARCHAR(100) NULL COMMENT '目标职位',
  `description` TEXT NULL COMMENT '简历描述',
  `is_public` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否公开',
  `status` VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态(DRAFT/PUBLISHED)',
  `score` INT NULL COMMENT 'AI评分',
  `created_time` DATETIME NOT NULL COMMENT '创建时间',
  `updated_time` DATETIME NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `fk_resume_user_idx` (`user_id` ASC),
  CONSTRAINT `fk_resume_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历基本信息表';

-- -----------------------------------------------------
-- 基本信息表
-- -----------------------------------------------------
CREATE TABLE `personal_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '基本信息ID',
  `resume_id` BIGINT NOT NULL COMMENT '所属简历ID',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `gender` VARCHAR(10) NULL COMMENT '性别',
  `date_of_birth` DATE NULL COMMENT '出生日期',
  `email` VARCHAR(100) NOT NULL COMMENT '邮箱',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
  `address` VARCHAR(255) NULL COMMENT '地址',
  `photo_url` VARCHAR(255) NULL COMMENT '照片URL',
  `website` VARCHAR(255) NULL COMMENT '个人网站',
  `linkedin` VARCHAR(255) NULL COMMENT 'LinkedIn链接',
  `github` VARCHAR(255) NULL COMMENT 'GitHub链接',
  `self_description` TEXT NULL COMMENT '自我描述',
  PRIMARY KEY (`id`),
  INDEX `fk_personal_info_resume_idx` (`resume_id` ASC),
  CONSTRAINT `fk_personal_info_resume` FOREIGN KEY (`resume_id`) REFERENCES `resume` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='基本信息表';

-- -----------------------------------------------------
-- 教育经历表
-- -----------------------------------------------------
CREATE TABLE `education` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '教育经历ID',
  `resume_id` BIGINT NOT NULL COMMENT '所属简历ID',
  `school` VARCHAR(100) NOT NULL COMMENT '学校名称',
  `degree` VARCHAR(50) NOT NULL COMMENT '学位',
  `major` VARCHAR(100) NOT NULL COMMENT '专业',
  `start_date` DATE NOT NULL COMMENT '开始日期',
  `end_date` DATE NULL COMMENT '结束日期',
  `gpa` VARCHAR(20) NULL COMMENT 'GPA',
  `description` TEXT NULL COMMENT '描述',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
  PRIMARY KEY (`id`),
  INDEX `fk_education_resume_idx` (`resume_id` ASC),
  CONSTRAINT `fk_education_resume` FOREIGN KEY (`resume_id`) REFERENCES `resume` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教育经历表';

-- -----------------------------------------------------
-- 工作经历表
-- -----------------------------------------------------
CREATE TABLE `work_experience` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '工作经历ID',
  `resume_id` BIGINT NOT NULL COMMENT '所属简历ID',
  `company` VARCHAR(100) NOT NULL COMMENT '公司名称',
  `position` VARCHAR(100) NOT NULL COMMENT '职位',
  `start_date` DATE NOT NULL COMMENT '开始日期',
  `end_date` DATE NULL COMMENT '结束日期',
  `is_current` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否当前工作',
  `location` VARCHAR(100) NULL COMMENT '工作地点',
  `description` TEXT NULL COMMENT '工作描述',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
  PRIMARY KEY (`id`),
  INDEX `fk_work_experience_resume_idx` (`resume_id` ASC),
  CONSTRAINT `fk_work_experience_resume` FOREIGN KEY (`resume_id`) REFERENCES `resume` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作经历表';

-- -----------------------------------------------------
-- 项目经历表
-- -----------------------------------------------------
CREATE TABLE `project` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `resume_id` BIGINT NOT NULL COMMENT '所属简历ID',
  `name` VARCHAR(100) NOT NULL COMMENT '项目名称',
  `role` VARCHAR(100) NULL COMMENT '担任角色',
  `start_date` DATE NOT NULL COMMENT '开始日期',
  `end_date` DATE NULL COMMENT '结束日期',
  `is_current` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否当前项目',
  `description` TEXT NULL COMMENT '项目描述',
  `url` VARCHAR(255) NULL COMMENT '项目链接',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
  PRIMARY KEY (`id`),
  INDEX `fk_project_resume_idx` (`resume_id` ASC),
  CONSTRAINT `fk_project_resume` FOREIGN KEY (`resume_id`) REFERENCES `resume` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目经历表';

-- -----------------------------------------------------
-- 技能表
-- -----------------------------------------------------
CREATE TABLE `skill` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '技能ID',
  `resume_id` BIGINT NOT NULL COMMENT '所属简历ID',
  `name` VARCHAR(100) NOT NULL COMMENT '技能名称',
  `level` INT NULL COMMENT '技能等级(1-5)',
  `category` VARCHAR(50) NULL COMMENT '技能分类',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
  PRIMARY KEY (`id`),
  INDEX `fk_skill_resume_idx` (`resume_id` ASC),
  CONSTRAINT `fk_skill_resume` FOREIGN KEY (`resume_id`) REFERENCES `resume` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能表';

-- -----------------------------------------------------
-- 模板表
-- -----------------------------------------------------
CREATE TABLE `template` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `name` VARCHAR(100) NOT NULL COMMENT '模板名称',
  `description` TEXT NULL COMMENT '模板描述',
  `preview_url` VARCHAR(255) NULL COMMENT '预览图URL',
  `html_content` LONGTEXT NULL COMMENT 'HTML内容',
  `css_content` LONGTEXT NULL COMMENT 'CSS样式',
  `is_premium` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否高级模板',
  `category` VARCHAR(50) NULL COMMENT '模板分类',
  `created_time` DATETIME NOT NULL COMMENT '创建时间',
  `updated_time` DATETIME NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板表';

-- -----------------------------------------------------
-- 投递记录表
-- -----------------------------------------------------
CREATE TABLE `delivery` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '投递ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `resume_id` BIGINT NOT NULL COMMENT '简历ID',
  `platform` VARCHAR(50) NOT NULL COMMENT '投递平台',
  `position` VARCHAR(100) NOT NULL COMMENT '投递职位',
  `company` VARCHAR(100) NOT NULL COMMENT '投递公司',
  `status` VARCHAR(20) NOT NULL COMMENT '状态(PENDING/SUCCESS/FAILED)',
  `delivery_time` DATETIME NOT NULL COMMENT '投递时间',
  `result` VARCHAR(50) NULL COMMENT '投递结果',
  `feedback` TEXT NULL COMMENT '反馈信息',
  PRIMARY KEY (`id`),
  INDEX `fk_delivery_user_idx` (`user_id` ASC),
  INDEX `fk_delivery_resume_idx` (`resume_id` ASC),
  CONSTRAINT `fk_delivery_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_delivery_resume` FOREIGN KEY (`resume_id`) REFERENCES `resume` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投递记录表';

-- -----------------------------------------------------
-- 插入测试管理员账户
-- -----------------------------------------------------
INSERT INTO `user` (
  `username`, 
  `password`, 
  `email`, 
  `phone`, 
  `real_name`, 
  `role`, 
  `is_enabled`, 
  `created_time`, 
  `last_login_time`
) VALUES (
  'admin',
  -- 密码为"admin123"的BCrypt加密结果
  '$2a$10$rSCPRZI2yJrBiDBHFvd/G.ITZ6CwHXgEaXMRr6.ZoleKXrF.sDtVu',
  'admin@example.com',
  '13800000000',
  '系统管理员',
  'ADMIN',
  TRUE,
  NOW(),
  NOW()
);

-- -----------------------------------------------------
-- 插入测试用户账户
-- -----------------------------------------------------
INSERT INTO `user` (
  `username`, 
  `password`, 
  `email`, 
  `phone`, 
  `real_name`, 
  `role`, 
  `is_enabled`, 
  `created_time`, 
  `last_login_time`
) VALUES (
  'test',
  -- 密码为"test123"的BCrypt加密结果
  '$2a$10$0NjNNjfJGm8.Ix.WVJVPROUcqqJsUT8QEv33adCful7DnDDl2mRHG',
  'test@example.com',
  '13900000000',
  '测试用户',
  'USER',
  TRUE,
  NOW(),
  NOW()
);

-- -----------------------------------------------------
-- 插入示例简历
-- -----------------------------------------------------
INSERT INTO `resume` (
  `user_id`,
  `title`,
  `target_position`,
  `description`,
  `is_public`,
  `status`,
  `created_time`
) VALUES (
  2, -- 测试用户ID
  '前端开发工程师简历',
  '高级前端开发工程师',
  '5年前端开发经验，精通Vue3、React等前端框架',
  FALSE,
  'PUBLISHED',
  NOW()
);

-- -----------------------------------------------------
-- 插入示例个人信息
-- -----------------------------------------------------
INSERT INTO `personal_info` (
  `resume_id`,
  `name`,
  `gender`,
  `date_of_birth`,
  `email`,
  `phone`,
  `address`,
  `self_description`
) VALUES (
  1, -- 简历ID
  '张三',
  '男',
  '1995-01-15',
  'test@example.com',
  '13900000000',
  '北京市朝阳区',
  '富有创造力和责任心的前端开发工程师，拥有5年Web开发经验。精通Vue3、React等前端框架，对性能优化和用户体验有深入研究。善于团队协作，能快速适应新技术和环境。'
);

-- -----------------------------------------------------
-- 插入示例模板
-- -----------------------------------------------------
INSERT INTO `template` (
  `name`,
  `description`,
  `preview_url`,
  `is_premium`,
  `category`,
  `created_time`
) VALUES (
  '简约专业模板',
  '简洁大方的专业简历模板，适合各行各业求职者使用',
  '/templates/preview/simple-professional.jpg',
  FALSE,
  '通用',
  NOW()
),
(
  '创意设计模板',
  '富有创意的设计风格简历模板，适合设计、艺术类职位',
  '/templates/preview/creative-design.jpg',
  TRUE,
  '设计',
  NOW()
),
(
  'IT技术模板',
  '突出技术能力的简历模板，适合程序员、工程师等技术岗位',
  '/templates/preview/tech-professional.jpg',
  FALSE,
  '技术',
  NOW()
);

-- 确认数据库设置完成
SELECT 'Resume Assistant数据库初始化完成!' as '✅ 初始化状态';
