-- Safe on existing installations; preserves configured values.
USE carcare_db;
SET NAMES utf8mb4;
CREATE TABLE IF NOT EXISTS `operating_settings` (
    `setting_id` INT AUTO_INCREMENT PRIMARY KEY,
    `setting_key` VARCHAR(50) NOT NULL UNIQUE,
    `setting_value` VARCHAR(255) NOT NULL,
    `description` VARCHAR(255) NULL,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `operating_settings` (`setting_key`, `setting_value`, `description`) VALUES
('OPENING_TIME', '07:30:00', 'Giờ mở cửa đón tiếp nhận xe hàng ngày'),
('CLOSING_TIME', '18:30:00', 'Giờ đóng cửa trung tâm chăm sóc xe'),
('BUFFER_MINUTES', '15', 'Thời gian đệm dọn vệ sinh khoang giữa 2 lượt dịch vụ (phút)'),
('SLOT_INTERVAL_MINUTES', '30', 'Khoảng cách giữa các khung giờ đặt hẹn (phút)'),
('MAX_ADVANCE_BOOKING_DAYS', '30', 'Số ngày tối đa cho phép khách đặt hẹn trước')
ON DUPLICATE KEY UPDATE `setting_key` = VALUES(`setting_key`);

-- 2. Bảng lịch nghỉ lễ / ngày ngừng nhận xe (Holiday Schedules)
CREATE TABLE IF NOT EXISTS `holiday_schedules` (
    `holiday_id` INT AUTO_INCREMENT PRIMARY KEY,
    `holiday_date` DATE NOT NULL UNIQUE,
    `holiday_name` VARCHAR(100) NOT NULL,
    `is_closed` BOOLEAN DEFAULT TRUE,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

