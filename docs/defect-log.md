# Nhật ký Ghi nhận và Xử lý Lỗi (Defect Log): CarCareBooking

Tài liệu này ghi nhận toàn bộ các sự cố, lỗi phát hiện trong quá trình kiểm thử, rà soát mã nguồn và cơ sở dữ liệu của Sprint 1, kèm theo phân tích nguyên nhân gốc rễ (Root Cause), giải pháp khắc phục và kết quả kiểm tra xác minh.

---

## Bảng Tổng hợp Danh sách Lỗi (Defect Tracking Matrix)

| Mã Lỗi | Module / Vị trí | Mô tả ngắn gọn | Mức độ nghiêm trọng | Trạng thái | Ngày phát hiện | Ngày đóng |
|---|---|---|---|---|---|---|
| **DEF-01** | Database & UI | Lỗi mã hóa ký tự tiếng Việt (Mojibake) trong CSDL `carcare_db` | **HIGH** | **CLOSED / VERIFIED** | 2026-11-10 | 2026-11-10 |
| **DEF-02** | Database / Auth | Thiếu bảng `password_reset_tokens` trong CSDL MySQL đang chạy | **HIGH** | **CLOSED / VERIFIED** | 2026-11-14 | 2026-11-14 |
| **DEF-03** | Database / Employee | Tài khoản `admin` thiếu hồ sơ trong bảng `employees` | **MEDIUM** | **CLOSED / VERIFIED** | 2026-11-14 | 2026-11-14 |
| **DEF-04** | Git / Workflow | Nhánh làm việc `origin/anhs` chưa được tích hợp vào `main` | **MEDIUM** | **PENDING TEAM ACTION** | 2026-11-14 | *(Đang theo dõi)* |

---

## Chi tiết Từng Lỗi và Phương án Xử lý

### DEF-01: Lỗi hiển thị sai ký tự tiếng Việt (Mojibake)
- **Vị trí ảnh hưởng:** Bảng `users`, `service_bays`, `roles`, `customers`, `services` và hiển thị trên Dashboard/Header.
- **Triệu chứng:**
  - Tên người dùng hiển thị thành `Quß║ún Trß╗ï Vi├¬n Hß╗ç Thß╗æng`.
  - Tên khoang dịch vụ hiển thị thành `Khoang Rß╗¡a Xe 01 (Bay 1)`.
- **Nguyên nhân gốc rễ (Root Cause):**
  - File `seed.sql` lưu định dạng UTF-8 nhưng không có lệnh chỉ thị kết nối `SET NAMES utf8mb4;`. Khi nạp bằng dòng lệnh console Windows (`cmd.exe /c ... < seed.sql`), console sử dụng bảng mã OEM CP437 để đọc byte UTF-8 gửi lên MySQL, dẫn đến lỗi chuyển đổi ký tự kép (double encoding mojibake) trong CSDL.
- **Biện pháp khắc phục (Fix):**
  1. Xuất và lưu trữ các bản ghi bị ảnh hưởng vào `database/backup_corrupted_records_before_fix.sql`.
  2. Dùng JDBC kết nối UTF-8 cập nhật lại các bản ghi chuẩn xác trong MySQL.
  3. Bổ sung `SET NAMES utf8mb4; SET CHARACTER SET utf8mb4;` ngay sau lệnh `USE carcare_db;` trong cả 2 tệp `schema.sql` và `seed.sql`.
  4. Cấu hình tham số `useUnicode=true&characterEncoding=UTF-8` trong `db.properties` và `DBContext.java`.
- **Kết quả xác minh (Verification):**
  - Đã truy vấn kiểm tra dữ liệu thật trong MySQL và giao diện web: 0 chuỗi ký tự lỗi, 100% tiếng Việt hiển thị sắc nét.

---

### DEF-02: Thiếu bảng `password_reset_tokens` trong CSDL MySQL đang chạy
- **Vị trí ảnh hưởng:** `PasswordResetDAO.java`, chức năng Quên mật khẩu (`/auth?action=forgot`), bài kiểm thử `RegistrationPasswordResetTest`.
- **Triệu chứng:**
  - Nhật ký log máy chủ báo lỗi: `Table 'carcare_db.password_reset_tokens' doesn't exist`.
  - Các bài kiểm thử `tokenIsSingleUse` và `tokenExpiresAndSuperseded` bị FAIL.
- **Nguyên nhân gốc rễ (Root Cause):**
  - Nhánh `feature/sprint1-register-reset-password` bổ sung tính năng quên mật khẩu và thêm câu lệnh CREATE TABLE vào `database/schema.sql`, nhưng CSDL `carcare_db` trên máy chủ XAMPP của môi trường kiểm thử được khởi tạo từ trước đó và chưa được cập nhật cấu trúc mới.
- **Biện pháp khắc phục (Fix):**
  1. Sao lưu toàn bộ CSDL ra tệp `database/backup_before_sprint1_migrations.sql` bằng `mysqldump`.
  2. Tạo migration `database/migrations/001_add_password_reset_tokens.sql` lấy chính xác DDL chuẩn từ `schema.sql` (có khóa ngoại FK `users(user_id)` ON DELETE CASCADE và index `idx_prt_user`).
  3. Thực thi migration trên CSDL `carcare_db`.
- **Kết quả xác minh (Verification):**
  - Bảng `password_reset_tokens` đã được tạo hoàn chỉnh.
  - Chức năng Quên mật khẩu hoạt động tốt, sinh token an toàn.
  - Các bài kiểm thử liên quan chuyển từ FAIL sang **PASS 100%**.

---

### DEF-03: Tài khoản `admin` thiếu hồ sơ trong bảng `employees`
- **Vị trí ảnh hưởng:** Bài kiểm thử `RegistrationPasswordResetTest.staffAccountsLinkedToEmployees`.
- **Triệu chứng:**
  - Assert thất bại: `Mọi tài khoản nhân viên phải có hồ sơ employees ==> expected: <0> but was: <1>`.
  - Truy vấn CSDL cho thấy `user_id = 1` (`admin`, vai trò `ADMIN`) có `employee_id = NULL`.
- **Nguyên nhân gốc rễ (Root Cause):**
  - File `database/seed.sql` ở commit `b0bffe6` đã có chỉ định chèn hồ sơ nhân viên `EMP-ADM01` cho admin, nhưng CSDL MySQL đang chạy chưa được nạp bản ghi này.
- **Biện pháp khắc phục (Fix):**
  1. Tạo migration `database/migrations/002_add_admin_employee_profile.sql` chèn bản ghi `employees` cho `user_id = 1` với mã `EMP-ADM01`, chức vụ `Quản trị hệ thống` và cơ chế `ON DUPLICATE KEY UPDATE` an toàn.
  2. Thực thi migration trên `carcare_db`.
- **Kết quả xác minh (Verification):**
  - Bản ghi nhân viên của tài khoản `admin` đã tồn tại trong CSDL.
  - Bài kiểm thử `staffAccountsLinkedToEmployees` chuyển sang **PASS 100%**.

---

### DEF-04: Nhánh làm việc (`origin/anhs`) chưa được merge vào `origin/main`
- **Vị trí ảnh hưởng:** Phân nhánh Git, quy trình tích hợp mã nguồn.
- **Triệu chứng:**
  - Nhánh `origin/anhs` tách ra từ commit đầu tiên (`7f318ff`) và chứa commit `e9cedb9`, chưa từng có Pull Request mở hoặc merge vào nhánh `main`.
  - Trên `main`, các tính năng `UserDAO` và `authenticate` đã được triển khai theo phong cách đồng bộ (`authenticate` thay vì `login`, trả về `User` thay vì ném checked `SQLException`).
- **Nguyên nhân gốc rễ (Root Cause):**
  - Nhánh riêng làm việc cục bộ trong thời gian dài mà không kéo cập nhật (pull/rebase) từ nhánh chính, dẫn đến lệch lịch sử commit.
- **Khuyến nghị xử lý (Recommendation):**
  1. Cần rebase nhánh `anhs` dựa trên `origin/main` mới nhất.
  2. Giữ lại tiện ích hữu ích `PasswordUtil.java` và lớp kiểm thử `PasswordUtilTest.java`.
  3. Điều chỉnh các lời gọi trong `UserDAO.java` trên nhánh `anhs` để tương thích với các phương thức đang hoạt động trên `main` (`authenticate`, `registerCustomer`, v.v.).
  4. Mở Pull Request chính thức lên GitHub để review và merge.
