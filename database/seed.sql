-- =============================================================================
-- Dữ liệu mẫu khởi tạo (Seed Data) cho hệ thống CarCareBooking
-- =============================================================================
-- Mật khẩu mẫu phát triển:
-- 1. admin       / Admin@123   (ADMIN - Quản trị viên hệ thống)
-- 2. manager     / Manager@123 (MANAGER - Quản lý cơ sở / xưởng dịch vụ)
-- 3. receptionist/ Recept@123  (RECEPTIONIST - Lễ tân tiếp nhận & điều phối)
-- 4. technician  / Tech@123    (TECHNICIAN - Kỹ thuật viên chăm sóc xe)
-- 5. customer    / Cust@123    (CUSTOMER - Khách hàng thành viên)
-- 6. locked_user / Locked@123  (CUSTOMER - Tài khoản bị khóa)
-- =============================================================================

USE `carcare_db`;

-- 1. Danh sách Vai trò (Roles)
INSERT INTO `roles` (`role_id`, `role_name`, `description`) VALUES
(1, 'ADMIN', 'Quản trị viên toàn hệ thống'),
(2, 'MANAGER', 'Quản lý cơ sở dịch vụ'),
(3, 'RECEPTIONIST', 'Nhân viên lễ tân tiếp nhận & điều phối'),
(4, 'TECHNICIAN', 'Kỹ thuật viên chăm sóc xe'),
(5, 'CUSTOMER', 'Khách hàng sử dụng dịch vụ')
ON DUPLICATE KEY UPDATE `description` = VALUES(`description`);

-- 2. Tài khoản Người dùng (Users - Mật khẩu băm chuẩn BCrypt)
INSERT INTO `users` (`user_id`, `username`, `password_hash`, `full_name`, `email`, `phone`, `role_id`, `status`) VALUES
(1, 'admin', '$2a$10$K2grv.2r4rV2pohar2TNbufNw31wvYgEAAclQuPNw7JFu1WLLpFB.', 'Quản Trị Viên Hệ Thống', 'admin@carcare.vn', '0901000001', 1, 'ACTIVE'),
(2, 'manager', '$2a$10$apsGz3x/C0woAHxzDeFyZO9MIUVuGwN9sxaTs8lNSY.tnkh2Hdm1S', 'Trần Văn Quản Lý', 'manager@carcare.vn', '0901000002', 2, 'ACTIVE'),
(3, 'receptionist', '$2a$10$gB2flRNqK1LSPT3gmGuDUe0Qz2kV3k8h2j6OZvzmoFi1htGYLP/Z2', 'Lê Thị Lễ Tân', 'receptionist@carcare.vn', '0901000003', 3, 'ACTIVE'),
(4, 'technician', '$2a$10$Zym113f0y7IK7x/VxNRCpuaa1KgxbybzNpAFkUWA3RIHwCqOOFeJG', 'Nguyễn Văn Kỹ Thuật', 'technician@carcare.vn', '0901000004', 4, 'ACTIVE'),
(5, 'customer', '$2a$10$rOUzqky5sraXgnFTQ2/dr.CaSbcBAy2BgOq7onOZc3UnZEz326kTm', 'Phạm Minh Khách', 'customer@gmail.com', '0901000005', 5, 'ACTIVE'),
(6, 'locked_user', '$2a$10$5GF5IOMfVeLN0At.8ohCceOYtAjWNhE5SkzYJrELFsOlHcKVlgHnq', 'Tài Khoản Bị Khóa', 'locked@gmail.com', '0901000006', 5, 'LOCKED')
ON DUPLICATE KEY UPDATE 
    `password_hash` = VALUES(`password_hash`),
    `full_name` = VALUES(`full_name`),
    `status` = VALUES(`status`),
    `role_id` = VALUES(`role_id`);

-- 3. Phân quyền Người dùng - Vai trò (User Roles: N - N)
INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES
(1, 1), -- admin -> ADMIN
(2, 2), -- manager -> MANAGER
(3, 3), -- receptionist -> RECEPTIONIST
(4, 4), -- technician -> TECHNICIAN
(5, 5), -- customer -> CUSTOMER
(6, 5)  -- locked_user -> CUSTOMER
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);

-- 4. Hồ sơ Nhân viên (Employees)
INSERT INTO `employees` (`employee_id`, `user_id`, `employee_code`, `full_name`, `phone`, `email`, `position`, `status`) VALUES
(1, 2, 'EMP-MGR01', 'Trần Văn Quản Lý', '0901000002', 'manager@carcare.vn', 'Quản lý cơ sở', 'ACTIVE'),
(2, 3, 'EMP-REC01', 'Lê Thị Lễ Tân', '0901000003', 'receptionist@carcare.vn', 'Nhân viên lễ tân', 'ACTIVE'),
(3, 4, 'EMP-TEC01', 'Nguyễn Văn Kỹ Thuật', '0901000004', 'technician@carcare.vn', 'Kỹ thuật viên Detailing', 'ACTIVE')
ON DUPLICATE KEY UPDATE `full_name` = VALUES(`full_name`), `position` = VALUES(`position`);

-- 5. Hồ sơ Khách hàng (Customers)
INSERT INTO `customers` (`customer_id`, `user_id`, `customer_code`, `full_name`, `phone`, `email`, `address`, `membership_level`) VALUES
(1, 5, 'CUST-0001', 'Phạm Minh Khách', '0901000005', 'customer@gmail.com', '123 Nguyễn Văn Cừ, Quận 5, TP.HCM', 'GOLD')
ON DUPLICATE KEY UPDATE `full_name` = VALUES(`full_name`), `membership_level` = VALUES(`membership_level`);

-- 6. Nhân sự kỹ thuật xưởng (Staff)
INSERT INTO `staff` (`staff_id`, `employee_id`, `user_id`, `position`, `experience_level`, `status`) VALUES
(1, 3, 4, 'Kỹ thuật viên Detailing', 'Chuyên viên bậc 3', 'AVAILABLE')
ON DUPLICATE KEY UPDATE `status` = VALUES(`status`);

-- 7. Khoang / Cầu dịch vụ mẫu (Service Bays)
INSERT INTO `service_bays` (`bay_id`, `bay_name`, `bay_type`, `status`) VALUES
(1, 'Khoang Rửa Xe 01 (Bay 1)', 'WASHING', 'AVAILABLE'),
(2, 'Khoang Rửa Xe 02 (Bay 2)', 'WASHING', 'AVAILABLE'),
(3, 'Khoang Detailing 01 (Bay 3)', 'DETAILING', 'AVAILABLE'),
(4, 'Khoang Detailing 02 (Bay 4)', 'DETAILING', 'AVAILABLE'),
(5, 'Cầu Nâng Kỹ Thuật 01 (Bay 5)', 'MECHANICAL', 'AVAILABLE')
ON DUPLICATE KEY UPDATE `status` = VALUES(`status`);

-- 8. Danh mục Dịch vụ mẫu (Services)
INSERT INTO `services` (`service_id`, `service_name`, `description`, `base_price`, `estimated_duration_minutes`, `status`) VALUES
(1, 'Rửa xe chi tiết & Hút bụi nội thất', 'Rửa bọt tuyết siêu sạch, vệ sinh khoang lái, hút bụi sàn trần xe', 150000, 45, 'ACTIVE'),
(2, 'Đánh bóng sơn & Phủ Ceramic cao cấp', 'Xóa xước xoáy, đánh bóng 3 bước và phủ bảo vệ bề mặt sơn', 2500000, 180, 'ACTIVE'),
(3, 'Vệ sinh khoang máy chuyên sâu', 'Dọn khoang động cơ bằng hơi nước nóng, dưỡng phục hồi chi tiết nhựa', 600000, 60, 'ACTIVE'),
(4, 'Khử mùi ozon & Diệt khuẩn nội thất', 'Khử mùi ẩm mốc, lọc sạch không khí điều hòa', 300000, 30, 'ACTIVE'),
(5, 'Thay dầu nhớt & Bảo dưỡng định kỳ', 'Thay dầu nhớt tổng hợp cao cấp và kiểm tra 20 hạng mục an toàn', 950000, 60, 'ACTIVE')
ON DUPLICATE KEY UPDATE `base_price` = VALUES(`base_price`);
