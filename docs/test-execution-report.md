# Báo cáo Thực thi Kiểm thử Sprint 1: CarCareBooking

**Mã tài liệu:** `TER-SPRINT-01`  
**Ngày thực hiện:** `2026-11-14`  
**Môi trường kiểm thử:**
- **Hệ điều hành:** Windows 11
- **JDK:** OpenJDK 17.0.21 HotSpot
- **Web Container:** Apache Tomcat 10.1.61
- **Build Tool:** Apache Maven 3.9+
- **Hệ quản trị CSDL:** MariaDB / MySQL 10.4.32 (XAMPP), Cổng 3306, Database: `carcare_db`
- **Thư viện kiểm thử:** JUnit 5 (Jupiter 5.10.2) + OpenTest4J + Python Integration Harness

---

## 1. Tóm tắt Kết quả Thực thi

| Phân loại kiểm thử | Tổng số ca | Đạt (PASS) | Không đạt (FAIL) | Bỏ qua (SKIP) | Tỷ lệ thành công |
|---|---|---|---|---|---|
| **JUnit 5 Unit / DAO Tests** | 22 | 22 | 0 | 0 | **100%** |
| **Live Integration Tests (Tomcat 10.1)** | 7 | 7 | 0 | 0 | **100%** |
| **Tổng cộng** | **29** | **29** | **0** | **0** | **100%** |

**Trạng thái Build tổng thể:** **`BUILD SUCCESS`**  
**Gói sản phẩm tạo ra:** `target/carcare.war` (Hoàn chỉnh, sẵn sàng triển khai)

---

## 2. Chi tiết Kết quả Kiểm thử Tự động (JUnit 5)

### 2.1. Lớp kiểm thử: `FoundationAuthTest` (10 Ca kiểm thử)
*Kiểm thử các chức năng nền tảng của Sprint 1: Kết nối DB, xác thực, phân quyền, trạng thái tài khoản.*

| Mã TC | Tên phương thức kiểm thử | Mục tiêu kiểm tra | Kết quả | Thời gian |
|---|---|---|---|---|
| TC-01 | `testMySQLConnection` | Kết nối thành công tới `carcare_db`, trạng thái mở | **PASS** | 0.045s |
| TC-02 | `testAuthenticateSuccess` | Đăng nhập đúng `admin`/`Admin@123`, trả về User active, vai trò `ADMIN` | **PASS** | 0.285s |
| TC-03 | `testAuthenticateByEmail` | Đăng nhập đúng bằng email `manager@carcare.vn` | **PASS** | 0.098s |
| TC-04 | `testAuthenticateWrongPassword` | Từ chối khi mật khẩu sai, trả về `null` | **PASS** | 0.092s |
| TC-05 | `testAuthenticateNonExistentUser` | Từ chối tài khoản không tồn tại, trả về `null` | **PASS** | 0.008s |
| TC-06 | `testAuthenticateLockedUser` | Nhận diện tài khoản `locked_user` có trạng thái `LOCKED`, không active | **PASS** | 0.095s |
| TC-07 | `testFindRolesByUserId` | Truy vấn đúng vai trò `ADMIN` từ bảng liên kết `user_roles` | **PASS** | 0.015s |
| TC-08 | `testBCryptSecurity` | BCrypt sinh salt ngẫu nhiên khác nhau cho cùng mật khẩu, xác thực đúng | **PASS** | 0.180s |
| TC-09 | `testUserRoleChecking` | Phương thức `hasRole` hoạt động chính xác theo phân quyền | **PASS** | 0.095s |
| TC-10 | `testDashboardStatistics` | `ReportDAO` truy vấn số liệu thống kê Dashboard không gây lỗi | **PASS** | 0.072s |

### 2.2. Lớp kiểm thử: `RegistrationPasswordResetTest` (6 Ca kiểm thử)
*Kiểm thử đăng ký khách hàng, ràng buộc quan hệ, token đặt lại mật khẩu và transaction.*

| Mã TC | Tên phương thức kiểm thử | Mục tiêu kiểm tra | Kết quả | Thời gian |
|---|---|---|---|---|
| REG-VAL | `validation` | Kiểm tra định dạng username, mật khẩu, email, số điện thoại | **PASS** | 0.005s |
| REG-LNK | `registerLinksUserAndCustomer` | Đăng ký tạo liên kết 1-1 giữa `users` và `customers`, mật khẩu băm BCrypt | **PASS** | 0.320s |
| REG-DUP | `duplicateRejected` | Từ chối đăng ký khi trùng lặp username, email hoặc số điện thoại | **PASS** | 0.280s |
| STF-LNK | `staffAccountsLinkedToEmployees` | Kiểm tra tài khoản nội bộ đều có hồ sơ trong bảng `employees` | **PASS** | 0.025s |
| RST-SGL | `tokenIsSingleUse` | Token đặt lại mật khẩu chỉ dùng được một lần, không thể tái sử dụng | **PASS** | 0.220s |
| RST-EXP | `tokenExpiresAndSuperseded` | Token hết hạn hoặc bị thay thế bởi token mới đều bị từ chối | **PASS** | 0.173s |

### 2.3. Lớp kiểm thử: `AuthSecurityTest` (1 Ca kiểm thử)
| Mã TC | Tên phương thức kiểm thử | Mục tiêu kiểm tra | Kết quả | Thời gian |
|---|---|---|---|---|
| SEC-01 | `testPasswordHashing` | Thuật toán băm BCrypt với work-factor 10 tạo chuỗi `$2a$` hợp chuẩn | **PASS** | 0.409s |

### 2.4. Các lớp kiểm thử Dịch vụ Khung (5 Ca kiểm thử)
| Lớp kiểm thử | Tên phương thức | Mục tiêu kiểm tra | Kết quả |
|---|---|---|---|
| `BookingServiceManagerTest` | `testCalculateEstimatedEndTime` | Tính thời gian hoàn thành dịch vụ | **PASS** |
| `BookingServiceManagerTest` | `testFormatVND` | Định dạng tiền tệ VNĐ | **PASS** |
| `BookingServiceManagerTest` | `testDefaultValues` | Giá trị mặc định của dịch vụ | **PASS** |
| `DispatchServiceTest` | `testValidateAssignmentTimeValid` | Khung giờ điều phối hợp lệ | **PASS** |
| `DispatchServiceTest` | `testValidateAssignmentTimeInvalid` | Khung giờ điều phối không hợp lệ | **PASS** |

---

## 3. Chi tiết Kết quả Kiểm thử Tích hợp Trực tiếp trên Tomcat 10.1

Kiểm thử qua giao thức HTTP thực tế trên ứng dụng web đang chạy tại context `/carcare`:

| STT | Luồng kiểm thử | URL kiểm tra | Dữ liệu đầu vào | Kết quả mong đợi | Kết quả thực tế | Trạng thái |
|---|---|---|---|---|---|---|
| 1 | **Đăng ký khách hàng mới** | `POST /carcare/auth?action=register` | Username: `testcust_audit1`<br>Pass: `Password@123`<br>Phone: `0987111222`<br>Email: `testcust_audit1@example.com` | Tạo bản ghi users + customers, chuyển hướng về trang đăng nhập | HTTP Status 200, tạo thành công tài khoản và hồ sơ khách hàng | **PASS** |
| 2 | **Đăng nhập Customer** | `POST /carcare/auth?action=login` | `customer` / `Cust@123` | Đăng nhập thành công, chuyển hướng `/carcare/home` | Chuyển hướng đúng, hiển thị Dashboard dành riêng cho Khách hàng | **PASS** |
| 3 | **Đăng xuất an toàn** | `GET /carcare/auth?action=logout` | Phiên đang hoạt động | Hủy phiên, xóa session, chuyển về `/login?logout=success` | Session bị hủy ngay lập tức, chuyển về trang login an toàn | **PASS** |
| 4 | **Yêu cầu quên mật khẩu** | `POST /carcare/auth?action=forgot` | Email: `admin@carcare.vn` | Sinh token an toàn lưu bảng `password_reset_tokens`, hiển thị thông báo | Sinh token thành công, ghi nhận link demo trên localhost | **PASS** |
| 5 | **Admin truy cập trang quản trị** | `GET /carcare/users` | Session `admin` | Cho phép truy cập danh sách người dùng hệ thống | HTTP Status 200 OK, giao diện quản lý người dùng hiển thị chuẩn | **PASS** |
| 6 | **Chặn Customer truy cập URL quản trị** | `GET /carcare/users` | Session `customer` | Chặn truy cập trái phép với mã **HTTP 403 Forbidden** | Trả về chính xác **HTTP 403 Forbidden** (không thể vượt quyền) | **PASS** |
| 7 | **Hiển thị dữ liệu MySQL lên Dashboard** | `GET /carcare/home` | Session `admin` | Hiển thị tên tài khoản và danh sách khoang bãi đúng tiếng Việt | Hiển thị "Quản Trị Viên Hệ Thống", các khoang `Khoang Rửa Xe 01`, `Cầu Nâng Kỹ Thuật 01` chuẩn UTF-8 | **PASS** |

---

## 4. Kết luận

- Toàn bộ **22/22 ca kiểm thử JUnit** và **7/7 ca kiểm thử tích hợp HTTP** đều đạt kết quả **PASS 100%**.
- Ứng dụng đã khắc phục hoàn toàn 3 lỗi phát hiện từ đợt nghiệm thu trước nhờ áp dụng migrations bổ sung bảng `password_reset_tokens` và liên kết hồ sơ `admin` với bảng `employees`.
- Dự án đáp ứng đầy đủ tiêu chí chất lượng và kỹ thuật để kết thúc Sprint 1.
