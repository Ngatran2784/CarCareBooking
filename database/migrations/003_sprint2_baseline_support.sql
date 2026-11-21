-- =============================================================================
-- Migration 003: Dữ liệu nền Sprint 2 phục vụ đặt lịch (S2-01)
-- Dự án: CarCareBooking (carcare_db)
-- Mục tiêu:
-- 1. Bổ sung bảng cấu hình vận hành (operating_settings) cho S2-06
-- 2. Bổ sung bảng lịch nghỉ lễ (holiday_schedules) cho S2-06
-- 3. Bổ sung bảng lịch bảo trì khoang (bay_maintenance_schedules) cho S2-04
-- 4. Bổ sung dữ liệu mẫu phương tiện (vehicles) cho khách hàng mẫu (S2-02)
-- 5. Bổ sung dữ liệu mẫu kỹ năng thợ và ca làm việc (S2-05)
-- =============================================================================

USE `carcare_db`;

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- Chuẩn hóa và mở rộng danh mục phân loại xe (SEDAN, SUV, CUV, MPV, HATCHBACK, PICKUP, OTHER) theo yêu cầu S2-01
ALTER TABLE `vehicles` MODIFY COLUMN `vehicle_type` ENUM('SEDAN', 'SUV', 'CUV', 'MPV', 'HATCHBACK', 'PICKUP', 'OTHER', 'TRUCK', 'VAN') NOT NULL DEFAULT 'SEDAN';

-- 1. Bảng cấu hình vận hành trung tâm (Operating Settings: Giờ mở cửa, thời gian đệm)
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
ON DUPLICATE KEY UPDATE `setting_value` = VALUES(`setting_value`), `description` = VALUES(`description`);

-- 2. Bảng lịch nghỉ lễ / ngày ngừng nhận xe (Holiday Schedules)
CREATE TABLE IF NOT EXISTS `holiday_schedules` (
    `holiday_id` INT AUTO_INCREMENT PRIMARY KEY,
    `holiday_date` DATE NOT NULL UNIQUE,
    `holiday_name` VARCHAR(100) NOT NULL,
    `is_closed` BOOLEAN DEFAULT TRUE,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `holiday_schedules` (`holiday_date`, `holiday_name`, `is_closed`) VALUES
('2026-01-01', 'Tết Dương Lịch 2026', TRUE),
('2026-04-30', 'Ngày Giải phóng Miền Nam', TRUE),
('2026-05-01', 'Ngày Quốc tế Lao động', TRUE),
('2026-09-02', 'Ngày Quốc khánh', TRUE)
ON DUPLICATE KEY UPDATE `holiday_name` = VALUES(`holiday_name`);

-- 3. Bảng lịch bảo trì khoang dịch vụ (Bay Maintenance Schedules)
CREATE TABLE IF NOT EXISTS `bay_maintenance_schedules` (
    `maintenance_id` INT AUTO_INCREMENT PRIMARY KEY,
    `bay_id` INT NOT NULL,
    `start_time` DATETIME NOT NULL,
    `end_time` DATETIME NOT NULL,
    `reason` VARCHAR(255) NULL,
    `status` ENUM('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'SCHEDULED',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_bms_bay` (`bay_id`),
    CONSTRAINT `fk_bms_bay` FOREIGN KEY (`bay_id`) REFERENCES `service_bays` (`bay_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Dữ liệu mẫu Phương tiện (Vehicles) cho khách hàng mẫu customer (customer_id = 1)
INSERT INTO `vehicles` (`vehicle_id`, `customer_id`, `license_plate`, `brand`, `model`, `year`, `color`, `vehicle_type`) VALUES
(1, 1, '51G-123.45', 'Toyota', 'Camry 2.5Q', 2021, 'Đen', 'SEDAN'),
(2, 1, '51H-678.90', 'Mazda', 'CX-5 Premium', 2022, 'Trắng', 'SUV'),
(3, 1, '51K-999.88', 'Mercedes-Benz', 'C300 AMG', 2023, 'Đỏ', 'SEDAN')
ON DUPLICATE KEY UPDATE 
    `brand` = VALUES(`brand`),
    `model` = VALUES(`model`),
    `year` = VALUES(`year`),
    `color` = VALUES(`color`),
    `vehicle_type` = VALUES(`vehicle_type`);

-- 5. Dữ liệu mẫu Kỹ năng Kỹ thuật viên (Technician Skills cho staff_id = 1)
INSERT INTO `technician_skills` (`staff_id`, `service_id`, `proficiency_level`) VALUES
(1, 1, 'EXPERT'),
(1, 2, 'ADVANCED'),
(1, 3, 'INTERMEDIATE'),
(1, 4, 'INTERMEDIATE'),
(1, 5, 'ADVANCED')
ON DUPLICATE KEY UPDATE `proficiency_level` = VALUES(`proficiency_level`);

-- 6. Dữ liệu mẫu Ca làm việc (Work Shifts)
INSERT INTO `work_shifts` (`shift_id`, `staff_id`, `shift_date`, `start_time`, `end_time`, `status`) VALUES
(1, 1, CURDATE(), '08:00:00', '12:00:00', 'SCHEDULED'),
(2, 1, CURDATE(), '13:30:00', '17:30:00', 'SCHEDULED'),
(3, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '08:00:00', '12:00:00', 'SCHEDULED'),
(4, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '13:30:00', '17:30:00', 'SCHEDULED')
ON DUPLICATE KEY UPDATE `status` = VALUES(`status`);
