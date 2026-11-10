-- =============================================================================
-- Dữ liệu mẫu khởi tạo (Seed Data) cho hệ thống CarCareBooking
-- =============================================================================
-- TODO: Dữ liệu bên dưới là dữ liệu mẫu demo để tham khảo cấu trúc.
-- Cần điều chỉnh tài khoản thực tế và mã hóa BCrypt khi triển khai.

USE `carcare_db`;

-- Roles
INSERT IGNORE INTO `roles` (`role_id`, `role_name`, `description`) VALUES
(1, 'ADMIN', 'Quản trị viên toàn hệ thống'),
(2, 'MANAGER', 'Quản lý xưởng dịch vụ & điều phối'),
(3, 'RECEPTIONIST', 'Nhân viên lễ tân tiếp nhận xe'),
(4, 'TECHNICIAN', 'Kỹ thuật viên thực hiện dịch vụ'),
(5, 'CUSTOMER', 'Khách hàng sử dụng dịch vụ');

-- Services
INSERT IGNORE INTO `services` (`service_id`, `service_name`, `description`, `base_price`, `estimated_duration_minutes`, `status`) VALUES
(1, 'Rửa xe chi tiết & Hút bụi nội thất', 'Rửa bọt tuyết siêu sạch, vệ sinh khoang lái, hút bụi sàn trần xe', 150000, 45, 'ACTIVE'),
(2, 'Đánh bóng sơn & Phủ Ceramic cao cấp', 'Xóa xước xoáy, đánh bóng 3 bước và phủ bảo vệ bề mặt sơn', 2500000, 180, 'ACTIVE'),
(3, 'Vệ sinh khoang máy chuyên sâu', 'Dọn khoang động cơ bằng hơi nước nóng, dưỡng phục hồi chi tiết nhựa', 600000, 60, 'ACTIVE'),
(4, 'Khử mùi ozon & Diệt khuẩn nội thất', 'Khử mùi ẩm mốc, lọc sạch không khí điều hòa', 300000, 30, 'ACTIVE'),
(5, 'Thay dầu nhớt & Bảo dưỡng định kỳ', 'Thay dầu nhớt tổng hợp cao cấp và kiểm tra 20 hạng mục an toàn', 950000, 60, 'ACTIVE');

-- Service Bays
INSERT IGNORE INTO `service_bays` (`bay_id`, `bay_name`, `bay_type`, `status`) VALUES
(1, 'Khoang Rửa Xe 01 (Bay 1)', 'WASHING', 'AVAILABLE'),
(2, 'Khoang Rửa Xe 02 (Bay 2)', 'WASHING', 'AVAILABLE'),
(3, 'Khoang Detailing 01 (Bay 3)', 'DETAILING', 'AVAILABLE'),
(4, 'Khoang Detailing 02 (Bay 4)', 'DETAILING', 'AVAILABLE'),
(5, 'Cầu Nâng Kỹ Thuật 01 (Bay 5)', 'MECHANICAL', 'AVAILABLE');
