-- Catalogue and maintenance require the base schema and migration 004.
USE carcare_db;
SET NAMES utf8mb4;
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
