-- =============================================================================
-- Migration 001: Bổ sung bảng password_reset_tokens cho chức năng quên mật khẩu
-- Dự án: CarCareBooking (carcare_db)
-- Đặc tả: Token ngẫu nhiên được băm SHA-256 trước khi lưu, có thời hạn và dùng 1 lần
-- =============================================================================

USE `carcare_db`;

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

CREATE TABLE IF NOT EXISTS `password_reset_tokens` (
    `token_id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `token_hash` CHAR(64) NOT NULL UNIQUE,
    `expires_at` DATETIME NOT NULL,
    `used_at` DATETIME NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_prt_user` (`user_id`),
    CONSTRAINT `fk_prt_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
