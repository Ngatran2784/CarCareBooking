# TÀI LIỆU BÀN GIAO KỸ THUẬT SPRINT 2 – DỮ LIỆU NỀN PHỤC VỤ ĐẶT LỊCH

**Dự án:** CarCareBooking – Hệ thống Quản lý Đặt lịch và Điều phối Dịch vụ Chăm sóc Xe  
**Giai đoạn:** Nghiệm thu kỹ thuật thẻ S2-01 & S2-02 (Sprint 2)  
**Thời gian xác nhận:** 21/11/2026  
**Công nghệ:** Java JDK 17 | Apache Tomcat 10.1.61 | MySQL 8 / MariaDB (Port 3306, `carcare_db`) | Maven WAR  

---

## 1. KẾT LUẬN NGHIỆM THU BÀN GIAO S2-01 VÀ S2-02

| Thẻ nghiệp vụ | Nội dung công việc | Kết luận nghiệm thu | Minh chứng & Ghi chú |
| :--- | :--- | :---: | :--- |
| **S2-01** | Thiết kế CSDL nền tảng: `customers`, `vehicles`, `services`, `service_bays`, `employees`, `work_shifts`, `operating_settings`, `holiday_schedules`, `bay_maintenance_schedules` | **ĐẠT** | - Migration `003_sprint2_baseline_support.sql` chạy thành công trên CSDL cổng 3306.<br>- CSDL chuẩn `schema.sql` và `seed.sql` đã đồng bộ.<br>- Bản sao lưu `backup_before_sprint2_migrations.sql` an toàn.<br>- ENUM `vehicle_type` mở rộng đầy đủ 7 loại xe chuẩn Sprint 2 và 2 loại xe kế thừa. |
| **S2-02** | Customer/Vehicle Model, DAO, Controller Servlet, Giao diện JSP, Kiểm thử phân quyền & bảo mật sở hữu | **ĐẠT** | - 27/27 bài kiểm thử JUnit 5 PASS (`mvn clean test package`).<br>- 13/13 kịch bản kiểm thử Live trên Tomcat 10.1 PASS.<br>- Bảo mật Server-side chặn HTTP 403 khi thao tác trái phép trên xe người khác.<br>- Sẵn sàng API và cấu trúc dữ liệu cho màn hình đặt lịch S2-07. |

---

## 2. BẢNG TÍCH HỢP DỮ LIỆU VÀ MÃ NGUỒN CHO CÁC THẺ TIẾP THEO (S2-03 ĐẾN S2-08)

| Mã thẻ | Dữ liệu hoặc mã nguồn dùng chung | Cách tích hợp | Trạng thái nền tảng |
| :--- | :--- | :--- | :---: |
| **S2-03** | - Bảng CSDL: `services`, `service_price_history`.<br>- Thuộc tính liên kết: `vehicles.vehicle_type` (`SEDAN`, `SUV`, `CUV`, `MPV`, `HATCHBACK`, `PICKUP`, `OTHER`). | Danh mục dịch vụ định cấu hình thời lượng (`estimated_duration_minutes`) và bảng giá theo phân loại xe để tính chi phí tự động khi đặt lịch. | **ĐÃ SẴN SÀNG** |
| **S2-04** | - Bảng CSDL: `service_bays` (vật lý), bảng mới `bay_maintenance_schedules`.<br>- Khóa ngoại: `bay_maintenance_schedules.bay_id` -> `service_bays(bay_id)`. | Module quản lý khoang kiểm tra trạng thái khả dụng của khoang, đối chiếu đồng thời lịch hẹn và lịch bảo trì thiết bị khoang. | **ĐÃ SẴN SÀNG** |
| **S2-05** | - Bảng CSDL: `employees`, `staff`, `skills`, `technician_skills`, `work_shifts`.<br>- Quan hệ: `work_shifts.staff_id` -> `staff(staff_id)` -> `employees(employee_id)`. | Module kỹ thuật viên & ca trực phân ca làm việc, tra cứu kỹ năng chuyên môn (`technician_skills`) theo từng dịch vụ chăm sóc xe. | **ĐÃ SẴN SÀNG** |
| **S2-06** | - Bảng CSDL: `operating_settings`, `holiday_schedules`.<br>- Cấu hình: `OPENING_TIME` (07:30), `CLOSING_TIME` (18:30), `BUFFER_MINUTES` (15), `SLOT_INTERVAL_MINUTES` (30). | Module cấu hình vận hành đọc tham số giờ hoạt động, ngày nghỉ lễ để thuật toán sinh lưới khung giờ (time-slot grid) tự động. | **ĐÃ SẴN SÀNG** |
| **S2-07** | - Model: `Customer`, `Vehicle`.<br>- DAO: `CustomerDAO.findByUserId(userId)`, `VehicleDAO.findByCustomerId(customerId)`.<br>- Route: `/vehicles`, `/customers`.<br>- Phương thức: `Vehicle.getDisplayName()`. | Khi khách hàng mở form đặt lịch, gọi `CustomerDAO` lấy hồ sơ theo tài khoản session, sau đó gọi `VehicleDAO` nạp danh sách xe vào dropdown với nhãn hiển thị `Hãng DòngXe (BiểnSố)`. | **ĐÃ SẴN SÀNG** |
| **S2-08** | - Cơ chế phân quyền: `AuthFilter.java`.<br>- Quản lý phiên: `session.getAttribute("currentUser")`.<br>- Toàn bộ cấu trúc CSDL và kiểm thử tự động của Sprint 2. | Kiểm thử tích hợp luồng đặt lịch hoàn chỉnh (End-to-End Booking Flow) kết hợp khách hàng, xe, dịch vụ, khoang, kỹ thuật viên và khung giờ. | **ĐÃ SẴN SÀNG** |

---

## 3. THIẾT KẾ CƠ SỞ DỮ LIỆU NỀN TẢNG (S2-01)

### 3.1. Danh mục bảng vật lý và đối chiếu
Tên bảng thực tế trong MySQL (`carcare_db`) đã chuẩn hóa:

1. **Khách hàng (`customers`):**
   - Khóa chính: `customer_id` (INT, AI).
   - Khóa ngoại / liên kết: `user_id` -> `users(user_id)` (1-1, cho phép NULL nếu khách vãng lai).
   - Cột nghiệp vụ: `customer_code` (VARCHAR(20), UNIQUE, sinh tự động `CUST-xxxx`), `full_name` (VARCHAR(100)), `phone` (VARCHAR(20), UNIQUE), `email` (VARCHAR(100)), `address` (VARCHAR(255)), `membership_level` (ENUM: `STANDARD`, `SILVER`, `GOLD`, `PLATINUM`).
2. **Phương tiện (`vehicles`):**
   - Khóa chính: `vehicle_id` (INT, AI).
   - Khóa ngoại: `customer_id` -> `customers(customer_id)` ON DELETE CASCADE.
   - Cột nghiệp vụ: `license_plate` (VARCHAR(20), UNIQUE), `brand` (VARCHAR(50)), `model` (VARCHAR(50)), `year` (INT), `color` (VARCHAR(30)), `vehicle_type` (ENUM).
3. **Dịch vụ (`services`):**
   - Khóa chính: `service_id` (INT, AI).
   - Cột nghiệp vụ: `service_name` (VARCHAR(100)), `description` (TEXT), `base_price` (DECIMAL(12,2)), `estimated_duration_minutes` (INT, mặc định 60), `status` (ENUM: `ACTIVE`, `INACTIVE`).
4. **Khoang dịch vụ (Tên vật lý: `service_bays`):**
   - Khóa chính: `bay_id` (INT, AI).
   - Cột nghiệp vụ: `bay_name` (VARCHAR(50), UNIQUE), `bay_type` (VARCHAR(50)), `status` (ENUM: `AVAILABLE`, `OCCUPIED`, `MAINTENANCE`).
5. **Nhân viên & Kỹ thuật viên (Tên vật lý: `employees` và `staff`):**
   - Bảng `employees`: Hồ sơ nhân sự chung (`employee_id`, `user_id`, `employee_code`, `full_name`, `phone`, `email`, `position`, `status`).
   - Bảng `staff`: Phân công kỹ thuật xưởng (`staff_id`, `employee_id`, `user_id`, `position`, `experience_level`, `status`).
6. **Ca làm việc (Tên vật lý: `work_shifts`):**
   - Khóa chính: `shift_id` (INT, AI).
   - Khóa ngoại: `staff_id` -> `staff(staff_id)` ON DELETE CASCADE.
   - Cột nghiệp vụ: `shift_date` (DATE), `start_time` (TIME), `end_time` (TIME), `status` (ENUM: `SCHEDULED`, `ATTENDED`, `ABSENT`).
7. **Lịch bảo trì khoang (Bảng mới: `bay_maintenance_schedules`):**
   - Khóa chính: `maintenance_id` (INT, AI).
   - Khóa ngoại: `bay_id` -> `service_bays(bay_id)` ON DELETE CASCADE.
   - Cột nghiệp vụ: `start_time` (DATETIME), `end_time` (DATETIME), `reason` (VARCHAR(255)), `status` (ENUM: `SCHEDULED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`).
8. **Cấu hình vận hành (Bảng mới: `operating_settings`):**
   - Khóa chính: `setting_id` (INT, AI).
   - Cột nghiệp vụ: `setting_key` (VARCHAR(50), UNIQUE), `setting_value` (VARCHAR(255)), `description` (VARCHAR(255)).
9. **Lịch nghỉ lễ (Bảng mới: `holiday_schedules`):**
   - Khóa chính: `holiday_id` (INT, AI).
   - Cột nghiệp vụ: `holiday_date` (DATE, UNIQUE), `holiday_name` (VARCHAR(100)), `is_closed` (BOOLEAN).

### 3.2. Quy cách xử lý loại xe (`vehicle_type`) và giá trị `TRUCK` / `VAN`
- **Trong CSDL (`vehicles`):**
  - Cột `vehicle_type` được định nghĩa:
    ```sql
    ENUM('SEDAN', 'SUV', 'CUV', 'MPV', 'HATCHBACK', 'PICKUP', 'OTHER', 'TRUCK', 'VAN') NOT NULL DEFAULT 'SEDAN'
    ```
  - CSDL chứa đầy đủ cả 7 phân loại xe chuẩn Sprint 2 (`SEDAN`, `SUV`, `CUV`, `MPV`, `HATCHBACK`, `PICKUP`, `OTHER`) và 2 giá trị cũ của Sprint 1 (`TRUCK`, `VAN`) nhằm đảm bảo tương thích ngược 100% với các bản ghi hoặc script nạp dữ liệu cũ.
- **Trong Java (`VehicleServlet.java` & `Vehicle.java`):**
  - Biểu mẫu web chuẩn hóa danh mục nhập liệu thành 7 loại xe hiện đại: `SEDAN`, `SUV`, `CUV`, `MPV`, `HATCHBACK`, `PICKUP`, `OTHER`.
  - Phương thức `Vehicle.getTypeBadgeClass()` hỗ trợ hiển thị badge màu sắc cho cả `TRUCK` (`bg-warning text-dark`) và `VAN` (`bg-secondary`), giúp mọi bản ghi lịch sử đều hiển thị trực quan không lỗi.
- **Trong Bảng giá dịch vụ (S2-03):**
  - Thiết lập giá theo 7 phân loại xe chuẩn Sprint 2. Trường hợp xe bán tải dùng `PICKUP`, xe gia đình đa dụng dùng `MPV`.

---

## 4. CHI TIẾT TRIỂN KHAI PHÂN HỆ CUSTOMER / VEHICLE (S2-02)

### 4.1. Cấu trúc Lớp Backend
- **Model:**
  - `Customer.java`: Quản lý mã khách hàng `customerCode`, số lượng xe sở hữu `vehicleCount`, huy hiệu hội viên `getMembershipBadgeClass()`.
  - `Vehicle.java`: Quản lý quan hệ với chủ xe, cung cấp `getDisplayName()` chuẩn hóa: `Hãng DòngXe (BiểnSố)`.
- **DAO:**
  - `CustomerDAO.java`: Tìm kiếm theo ID, UserID, SĐT, họ tên, mã KH; thêm mới tự động sinh `CUST-xxxx`; cập nhật hồ sơ cá nhân (`updateProfile`) bảo toàn hạng thẻ; xóa; kiểm tra tính duy nhất của SĐT.
  - `VehicleDAO.java`: Tìm theo ID, biển số, mã khách hàng; CRUD xe; kiểm tra tính duy nhất của biển số; xác thực quyền sở hữu `isOwner(vehicleId, customerId)`.
- **Bảo mật và Phân quyền:**
  - `AuthFilter.java`: Cấp quyền truy cập `/customers` và `/vehicles` cho `CUSTOMER` cùng với `ADMIN`, `MANAGER`, `RECEPTIONIST`.
  - `CustomerServlet.java`: Phân luồng vai trò:
    - Khách hàng: Mặc định vào `/customers?action=profile` (xem/cập nhật hồ sơ cá nhân). Bị từ chối **HTTP 403 Forbidden** nếu cố tình truy cập danh sách khách hàng (`action=list`) của nhân viên.
    - Nhân viên: Quản trị danh sách khách hàng, tìm kiếm từ khóa, xem chi tiết và các xe sở hữu, thêm/sửa/xóa khách hàng.
  - `VehicleServlet.java`:
    - Khách hàng: Chỉ xem xe của mình. Khi thêm xe, `customerId` được gán cứng từ session tài khoản đăng nhập (không tin cậy client). Khi sửa/xóa, máy chủ bắt buộc kiểm tra `vehicleDAO.isOwner(vehicleId, customerId)`, trả về **HTTP 403 Forbidden** nếu vi phạm sở hữu.
    - Nhân viên: Quản trị danh sách xe toàn hệ thống kèm thông tin chủ xe (họ tên, SĐT).

### 4.2. Giao diện JSP / JSPF
- `WEB-INF/views/customer/list.jsp`: Danh sách khách hàng kèm bộ lọc, số lượng xe, hạng thẻ.
- `WEB-INF/views/customer/form.jsp`: Màn hình kép: Hồ sơ cá nhân (US-03) tích hợp bảng danh sách xe sở hữu cho khách hàng, và form chi tiết cho nhân viên.
- `WEB-INF/views/vehicle/list.jsp`: Màn hình danh sách xe của tôi (khách hàng) và quản lý xe (nhân viên).
- `WEB-INF/views/vehicle/form.jsp`: Biểu mẫu thêm/sửa xe có kiểm tra định dạng biển số xe, phân loại xe và thông số kỹ thuật.
- `WEB-INF/views/layout/header.jspf` & `sidebar.jspf`: Menu "Hồ sơ của tôi" và "Xe của tôi" cho người dùng đăng nhập với vai trò `CUSTOMER`.

---

## 5. NHẬT KÝ KIỂM THỬ THỰC TẾ

### 5.1. Kết quả kiểm thử tự động Maven
- Lệnh thực thi: `mvn clean test package` (không dùng `-DskipTests`).
- Kết quả kiểm thử JUnit 5:
  ```text
  [INFO] Running com.carcare.AuthSecurityTest
  [INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
  [INFO] Running com.carcare.BookingServiceManagerTest
  [INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
  [INFO] Running com.carcare.CustomerVehicleTest
  [INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
  [INFO] Running com.carcare.DispatchServiceTest
  [INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
  [INFO] Running com.carcare.FoundationAuthTest
  [INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
  [INFO] Running com.carcare.RegistrationPasswordResetTest
  [INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
  [INFO] 
  [INFO] Results:
  [INFO] Tests run: 27, Failures: 0, Errors: 0, Skipped: 0
  [INFO] BUILD SUCCESS
  [INFO] Building war: D:\DAJAVA\CarCareBooking\target\carcare.war
  ```
- Thống kê: **PASS: 27 | FAIL: 0 | ERROR: 0 | SKIPPED: 0**.

### 5.2. Kết quả kiểm thử Live HTTP trên Tomcat 10.1 (WAR mới)
- Máy chủ: Apache Tomcat 10.1.61 (Port 8080) | CSDL: MySQL (Port 3306, `carcare_db`).
- Script kiểm thử: `python test_sprint2_verification.py`.
- Nhật ký thực thi:
  ```text
  =================================================================
   BẮT ĐẦU KIỂM THỬ NGHIỆM THU CHI TIẾT SPRINT 2 (S2-01 & S2-02)
  =================================================================
  [PASS] 1.1: Đăng nhập thành công với tài khoản khách hàng (customer / Cust@123)
  [PASS] 1.2: Khách hàng xem hồ sơ cá nhân (/customers?action=profile)
  [PASS] 1.3: Khách hàng cập nhật hồ sơ cá nhân thành công (US-03)
  [PASS] 1.4: Server chặn cập nhật khi SĐT sai định dạng (12345)
  [PASS] 2.1: Khách hàng mở trang /vehicles xem danh sách xe của chính mình
  [PASS] 2.2: Server chặn thêm xe khi biển số xe sai định dạng
  [PASS] 2.3: Khách hàng thêm xe mới [51K-777.99] thành công
  [PASS] 2.4: Khách hàng chỉnh sửa thông tin xe [ID: 17] thành công
  [PASS] 3.1: Khách hàng bị chặn HTTP 403 khi cố truy cập /customers?action=list
  [PASS] 3.2: Bảo mật sở hữu xe: Khách hàng khác bị chặn HTTP 403 khi sửa xe ID 17
  [PASS] 3.3: Bảo mật sở hữu xe: Khách hàng khác bị chặn HTTP 403 khi xóa xe ID 17
  [PASS] 3.4: Khách hàng xóa xe thử nghiệm [51K-777.99] thành công
  [PASS] 4.1: Đăng nhập thành công với tài khoản Quản trị viên (admin / Admin@123)
  [PASS] 4.2: Admin mở danh sách khách hàng (/customers) hiển thị bảng đầy đủ
  [PASS] 4.3: Admin mở danh sách xe (/vehicles) hiển thị đầy đủ cột Chủ sở hữu
  [PASS] 4.4: Admin xem chi tiết khách hàng (/customers?action=detail&id=1) hiển thị cả hồ sơ và danh sách xe
  [PASS] 5.1: API VehicleDAO.findByCustomerId(customerId) trả về List<Vehicle> sẵn sàng cho S2-07
  [PASS] 5.2: Vehicle.getDisplayName() trả về định dạng chuẩn 'Hãng DòngXe (BiểnSố)' cho combobox
  =================================================================
   >>> TẤT CẢ CÁC MỤC NGHIỆM THU ĐỀU PASS THÀNH CÔNG RỰC RỠ! <<<
  =================================================================
  ```
