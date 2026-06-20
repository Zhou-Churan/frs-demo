CREATE DATABASE IF NOT EXISTS frs_attendance DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE frs_attendance;

-- 用户表
CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(128) NOT NULL COMMENT '密码(BCrypt)',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    face_id VARCHAR(64) DEFAULT NULL COMMENT 'FRS人脸ID',
    external_image_id VARCHAR(64) DEFAULT NULL COMMENT '外部图片ID',
    face_image_url VARCHAR(512) DEFAULT NULL COMMENT '人脸照片路径',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0禁用1正常',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_phone (phone),
    INDEX idx_face_id (face_id),
    INDEX idx_external_image_id (external_image_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 签到记录表
CREATE TABLE IF NOT EXISTS t_attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    sign_date DATE NOT NULL COMMENT '签到日期',
    sign_time DATETIME NOT NULL COMMENT '签到时间',
    similarity DOUBLE DEFAULT NULL COMMENT '人脸匹配相似度',
    device_type VARCHAR(20) DEFAULT NULL COMMENT '设备类型:MOBILE/PC',
    ip_address VARCHAR(64) DEFAULT NULL COMMENT '签到IP',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_date (user_id, sign_date),
    INDEX idx_user_id (user_id),
    INDEX idx_sign_date (sign_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到记录表';