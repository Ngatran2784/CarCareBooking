# Tài liệu Thiết kế Cơ sở dữ liệu: CarCareBooking

## 1. Giới thiệu tổng quan
Hệ thống **CarCareBooking** phục vụ quy trình quản lý đặt lịch, tiếp nhận phương tiện, điều phối kỹ thuật viên, theo dõi tiến độ thi công, kiểm tra chất lượng và xuất hóa đơn dịch vụ chăm sóc xe hơi (Detailing & Maintenance).

## 2. Mô hình Thực thể - Quan hệ (ERD Architecture)
- **Quản lý tài khoản & phân quyền**: `roles`, `users`
- **Khách hàng & Phương tiện**: `customers`, `vehicles`
- **Danh mục dịch vụ & Đơn giá**: `services`, `service_price_history`
- **Nhân sự & Năng lực kỹ thuật**: `staff`, `technician_skills`, `work_shifts`
- **Cơ sở vật chất**: `service_bays` (Khoang dịch vụ, cầu nâng)
- **Quy trình Đặt lịch**: `bookings`, `booking_services`, `booking_history`
- **Điều phối & Thực hiện**: `assignments`, `job_cards`
- **Kiểm định & Giám sát**: `vehicle_inspections`, `inspection_photos`, `additional_service_requests`, `quality_checks`
- **Tài chính & Thanh toán**: `invoices`, `payments`
- **Tương tác**: `reviews`, `notifications`

## 3. Chi tiết các bảng chính
| Tên bảng | Ý nghĩa | Khóa chính | Khóa ngoại |
|---|---|---|---|
| `roles` | Phân quyền vai trò | `role_id` | - |
| `users` | Tài khoản đăng nhập | `user_id` | `role_id` |
| `customers` | Hồ sơ khách hàng | `customer_id` | `user_id` |
| `vehicles` | Thông tin phương tiện | `vehicle_id` | `customer_id` |
| `services` | Danh mục dịch vụ | `service_id` | - |
| `service_bays` | Khoang / Cầu làm việc | `bay_id` | - |
| `bookings` | Phiếu đặt lịch chăm sóc xe | `booking_id` | `customer_id`, `vehicle_id` |
| `assignments` | Điều phối khoang & thợ | `assignment_id` | `booking_id`, `bay_id`, `technician_id` |
| `job_cards` | Thẻ theo dõi công việc xưởng | `job_id` | `booking_id`, `assignment_id` |
| `invoices` | Hóa đơn thanh toán | `invoice_id` | `booking_id` |

> [!NOTE]
> Thiết kế CSDL hiện tại được chuẩn hóa ở dạng 3NF, hỗ trợ mở rộng nghiệp vụ chuỗi xưởng dịch vụ trong tương lai.
