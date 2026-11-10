-- =============================================================================
-- Hệ thống Quản lý Đặt lịch và Điều phối Dịch vụ Chăm sóc Xe (CarCareBooking)
-- DDL Schema cơ sở dữ liệu MySQL 8.x
-- =============================================================================

CREATE DATABASE IF NOT EXISTS `carcare_db` 
  CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci;

USE `carcare_db`;

-- 1. Bảng Vai trò người dùng (Roles)
CREATE TABLE IF NOT EXISTS `roles` (
    `role_id` INT AUTO_INCREMENT PRIMARY KEY,
    `role_name` VARCHAR(50) NOT NULL UNIQUE,
    `description` VARCHAR(255) NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Bảng Người dùng hệ thống (Users)
CREATE TABLE IF NOT EXISTS `users` (
    `user_id` INT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `full_name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `phone` VARCHAR(20) NOT NULL UNIQUE,
    `role_id` INT NULL,
    `status` ENUM('ACTIVE', 'INACTIVE', 'LOCKED') DEFAULT 'ACTIVE',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_users_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Bảng Liên kết Tài khoản và Vai trò (User Roles: N - N)
CREATE TABLE IF NOT EXISTS `user_roles` (
    `user_id` INT NOT NULL,
    `role_id` INT NOT NULL,
    PRIMARY KEY (`user_id`, `role_id`),
    CONSTRAINT `fk_ur_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_ur_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Bảng Khách hàng (Customers: 1 - 1 với User, nullable, không trùng)
CREATE TABLE IF NOT EXISTS `customers` (
    `customer_id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NULL UNIQUE,
    `customer_code` VARCHAR(20) NULL UNIQUE,
    `full_name` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(20) NOT NULL UNIQUE,
    `email` VARCHAR(100) NULL,
    `address` VARCHAR(255) NULL,
    `membership_level` ENUM('STANDARD', 'SILVER', 'GOLD', 'PLATINUM') DEFAULT 'STANDARD',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_customers_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Bảng Nhân viên (Employees: 1 - 1 với User, nullable, không trùng)
CREATE TABLE IF NOT EXISTS `employees` (
    `employee_id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NULL UNIQUE,
    `employee_code` VARCHAR(20) NOT NULL UNIQUE,
    `full_name` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(20) NOT NULL UNIQUE,
    `email` VARCHAR(100) NULL,
    `position` VARCHAR(50) NOT NULL,
    `status` ENUM('ACTIVE', 'INACTIVE', 'OFF_DUTY') DEFAULT 'ACTIVE',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_employees_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Bảng Nhân sự kỹ thuật xưởng (Staff)
CREATE TABLE IF NOT EXISTS `staff` (
    `staff_id` INT AUTO_INCREMENT PRIMARY KEY,
    `employee_id` INT NULL,
    `user_id` INT NOT NULL UNIQUE,
    `position` VARCHAR(50) NOT NULL,
    `experience_level` VARCHAR(50) NULL,
    `status` ENUM('AVAILABLE', 'BUSY', 'OFF_DUTY') DEFAULT 'AVAILABLE',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_staff_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_staff_employee` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Bảng Phương tiện (Vehicles)
CREATE TABLE IF NOT EXISTS `vehicles` (
    `vehicle_id` INT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` INT NOT NULL,
    `license_plate` VARCHAR(20) NOT NULL UNIQUE,
    `brand` VARCHAR(50) NOT NULL,
    `model` VARCHAR(50) NOT NULL,
    `year` INT NULL,
    `color` VARCHAR(30) NULL,
    `vehicle_type` ENUM('SEDAN', 'SUV', 'HATCHBACK', 'TRUCK', 'VAN') NOT NULL DEFAULT 'SEDAN',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_vehicles_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. Bảng Dịch vụ chăm sóc xe (Services)
CREATE TABLE IF NOT EXISTS `services` (
    `service_id` INT AUTO_INCREMENT PRIMARY KEY,
    `service_name` VARCHAR(100) NOT NULL,
    `description` TEXT NULL,
    `base_price` DECIMAL(12,2) NOT NULL,
    `estimated_duration_minutes` INT NOT NULL DEFAULT 60,
    `status` ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. Bảng Lịch sử giá dịch vụ (Service Price History)
CREATE TABLE IF NOT EXISTS `service_price_history` (
    `history_id` INT AUTO_INCREMENT PRIMARY KEY,
    `service_id` INT NOT NULL,
    `old_price` DECIMAL(12,2) NOT NULL,
    `new_price` DECIMAL(12,2) NOT NULL,
    `effective_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `changed_by` INT NULL,
    CONSTRAINT `fk_price_service` FOREIGN KEY (`service_id`) REFERENCES `services` (`service_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. Kỹ năng kỹ thuật viên (Technician Skills)
CREATE TABLE IF NOT EXISTS `technician_skills` (
    `skill_id` INT AUTO_INCREMENT PRIMARY KEY,
    `staff_id` INT NOT NULL,
    `service_id` INT NOT NULL,
    `proficiency_level` ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT') DEFAULT 'INTERMEDIATE',
    CONSTRAINT `fk_skill_staff` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`staff_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_skill_service` FOREIGN KEY (`service_id`) REFERENCES `services` (`service_id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_staff_service` (`staff_id`, `service_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. Ca làm việc (Work Shifts)
CREATE TABLE IF NOT EXISTS `work_shifts` (
    `shift_id` INT AUTO_INCREMENT PRIMARY KEY,
    `staff_id` INT NOT NULL,
    `shift_date` DATE NOT NULL,
    `start_time` TIME NOT NULL,
    `end_time` TIME NOT NULL,
    `status` ENUM('SCHEDULED', 'ATTENDED', 'ABSENT') DEFAULT 'SCHEDULED',
    CONSTRAINT `fk_shift_staff` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`staff_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. Khoang/Cầu dịch vụ (Service Bays)
CREATE TABLE IF NOT EXISTS `service_bays` (
    `bay_id` INT AUTO_INCREMENT PRIMARY KEY,
    `bay_name` VARCHAR(50) NOT NULL UNIQUE,
    `bay_type` VARCHAR(50) NOT NULL DEFAULT 'GENERAL',
    `status` ENUM('AVAILABLE', 'OCCUPIED', 'MAINTENANCE') DEFAULT 'AVAILABLE',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 13. Đặt lịch (Bookings)
CREATE TABLE IF NOT EXISTS `bookings` (
    `booking_id` INT AUTO_INCREMENT PRIMARY KEY,
    `booking_code` VARCHAR(20) NOT NULL UNIQUE,
    `customer_id` INT NOT NULL,
    `vehicle_id` INT NOT NULL,
    `booking_time` DATETIME NOT NULL,
    `status` ENUM('PENDING', 'CONFIRMED', 'CHECKED_IN', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    `notes` TEXT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_bookings_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    CONSTRAINT `fk_bookings_vehicle` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`vehicle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 14. Chi tiết dịch vụ đặt (Booking Services)
CREATE TABLE IF NOT EXISTS `booking_services` (
    `booking_service_id` INT AUTO_INCREMENT PRIMARY KEY,
    `booking_id` INT NOT NULL,
    `service_id` INT NOT NULL,
    `price` DECIMAL(12,2) NOT NULL,
    `notes` VARCHAR(255) NULL,
    CONSTRAINT `fk_bs_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_bs_service` FOREIGN KEY (`service_id`) REFERENCES `services` (`service_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 15. Lịch sử trạng thái đặt lịch (Booking History)
CREATE TABLE IF NOT EXISTS `booking_history` (
    `history_id` INT AUTO_INCREMENT PRIMARY KEY,
    `booking_id` INT NOT NULL,
    `previous_status` VARCHAR(50) NULL,
    `new_status` VARCHAR(50) NOT NULL,
    `changed_by` INT NULL,
    `change_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `comment` TEXT NULL,
    CONSTRAINT `fk_bh_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 16. Phân công điều phối (Assignments)
CREATE TABLE IF NOT EXISTS `assignments` (
    `assignment_id` INT AUTO_INCREMENT PRIMARY KEY,
    `booking_id` INT NOT NULL,
    `bay_id` INT NOT NULL,
    `technician_id` INT NOT NULL,
    `start_time` DATETIME NOT NULL,
    `estimated_end_time` DATETIME NOT NULL,
    `actual_end_time` DATETIME NULL,
    `status` ENUM('ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'REASSIGNED') DEFAULT 'ASSIGNED',
    CONSTRAINT `fk_as_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`),
    CONSTRAINT `fk_as_bay` FOREIGN KEY (`bay_id`) REFERENCES `service_bays` (`bay_id`),
    CONSTRAINT `fk_as_tech` FOREIGN KEY (`technician_id`) REFERENCES `staff` (`staff_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 17. Thẻ công việc (Job Cards)
CREATE TABLE IF NOT EXISTS `job_cards` (
    `job_id` INT AUTO_INCREMENT PRIMARY KEY,
    `booking_id` INT NOT NULL UNIQUE,
    `assignment_id` INT NULL,
    `status` ENUM('CREATED', 'INSPECTING', 'IN_SERVICE', 'QC_PENDING', 'QC_PASSED', 'DONE') DEFAULT 'CREATED',
    `start_time` DATETIME NULL,
    `completed_time` DATETIME NULL,
    `notes` TEXT NULL,
    CONSTRAINT `fk_job_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`),
    CONSTRAINT `fk_job_assignment` FOREIGN KEY (`assignment_id`) REFERENCES `assignments` (`assignment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 18. Biên bản kiểm tra xe vào xưởng (Vehicle Inspection)
CREATE TABLE IF NOT EXISTS `vehicle_inspections` (
    `inspection_id` INT AUTO_INCREMENT PRIMARY KEY,
    `job_id` INT NOT NULL,
    `inspector_id` INT NOT NULL,
    `odometer_km` INT NULL,
    `fuel_level` VARCHAR(20) NULL,
    `exterior_condition` TEXT NULL,
    `belongings` TEXT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_vi_job` FOREIGN KEY (`job_id`) REFERENCES `job_cards` (`job_id`),
    CONSTRAINT `fk_vi_inspector` FOREIGN KEY (`inspector_id`) REFERENCES `staff` (`staff_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 19. Hình ảnh kiểm tra xe (Inspection Photos)
CREATE TABLE IF NOT EXISTS `inspection_photos` (
    `photo_id` INT AUTO_INCREMENT PRIMARY KEY,
    `inspection_id` INT NOT NULL,
    `photo_url` VARCHAR(255) NOT NULL,
    `caption` VARCHAR(255) NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_photo_insp` FOREIGN KEY (`inspection_id`) REFERENCES `vehicle_inspections` (`inspection_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 20. Yêu cầu dịch vụ phát sinh (Additional Service Requests)
CREATE TABLE IF NOT EXISTS `additional_service_requests` (
    `request_id` INT AUTO_INCREMENT PRIMARY KEY,
    `job_id` INT NOT NULL,
    `service_id` INT NOT NULL,
    `reason` TEXT NOT NULL,
    `estimated_cost` DECIMAL(12,2) NOT NULL,
    `customer_approved` ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    `approval_time` DATETIME NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_asr_job` FOREIGN KEY (`job_id`) REFERENCES `job_cards` (`job_id`),
    CONSTRAINT `fk_asr_service` FOREIGN KEY (`service_id`) REFERENCES `services` (`service_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 21. Kiểm định chất lượng nghiệm thu (Quality Checks)
CREATE TABLE IF NOT EXISTS `quality_checks` (
    `qc_id` INT AUTO_INCREMENT PRIMARY KEY,
    `job_id` INT NOT NULL,
    `checker_id` INT NOT NULL,
    `status` ENUM('PASSED', 'FAILED', 'REWORK_REQUIRED') DEFAULT 'PASSED',
    `checklist_result` TEXT NULL,
    `notes` TEXT NULL,
    `checked_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_qc_job` FOREIGN KEY (`job_id`) REFERENCES `job_cards` (`job_id`),
    CONSTRAINT `fk_qc_checker` FOREIGN KEY (`checker_id`) REFERENCES `staff` (`staff_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 22. Hóa đơn thanh toán (Invoices)
CREATE TABLE IF NOT EXISTS `invoices` (
    `invoice_id` INT AUTO_INCREMENT PRIMARY KEY,
    `booking_id` INT NOT NULL UNIQUE,
    `subtotal` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    `discount_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    `tax_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    `total_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    `status` ENUM('UNPAID', 'PARTIALLY_PAID', 'PAID', 'CANCELLED') DEFAULT 'UNPAID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_inv_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 23. Giao dịch thanh toán (Payments)
CREATE TABLE IF NOT EXISTS `payments` (
    `payment_id` INT AUTO_INCREMENT PRIMARY KEY,
    `invoice_id` INT NOT NULL,
    `payment_method` ENUM('CASH', 'CREDIT_CARD', 'BANK_TRANSFER', 'E_WALLET') NOT NULL,
    `amount` DECIMAL(12,2) NOT NULL,
    `transaction_reference` VARCHAR(100) NULL,
    `paid_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_pay_invoice` FOREIGN KEY (`invoice_id`) REFERENCES `invoices` (`invoice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 24. Đánh giá của khách hàng (Reviews)
CREATE TABLE IF NOT EXISTS `reviews` (
    `review_id` INT AUTO_INCREMENT PRIMARY KEY,
    `booking_id` INT NOT NULL UNIQUE,
    `customer_id` INT NOT NULL,
    `rating` TINYINT NOT NULL CHECK (`rating` BETWEEN 1 AND 5),
    `comment` TEXT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_rev_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`),
    CONSTRAINT `fk_rev_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 25. Thông báo hệ thống (Notifications)
CREATE TABLE IF NOT EXISTS `notifications` (
    `notification_id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `title` VARCHAR(150) NOT NULL,
    `message` TEXT NOT NULL,
    `is_read` BOOLEAN DEFAULT FALSE,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_notif_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
