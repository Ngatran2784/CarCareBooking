-- =============================================================================
-- Migration 002: Bổ sung hồ sơ nhân viên cho tài khoản quản trị viên (admin)
-- Dự án: CarCareBooking (carcare_db)
-- Đặc tả: Theo seed.sql, tài khoản admin (user_id = 1) là nhân sự quản trị (EMP-ADM01)
-- =============================================================================

USE `carcare_db`;

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

INSERT INTO `employees` (`employee_id`, `user_id`, `employee_code`, `full_name`, `phone`, `email`, `position`, `status`) 
VALUES (4, 1, 'EMP-ADM01', 'Quản Trị Viên Hệ Thống', '0901000001', 'admin@carcare.vn', 'Quản trị hệ thống', 'ACTIVE')
ON DUPLICATE KEY UPDATE 
    `user_id` = VALUES(`user_id`),
    `full_name` = VALUES(`full_name`),
    `position` = VALUES(`position`);
