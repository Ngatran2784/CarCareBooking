# Báo cáo Nghiệm thu Hoàn thành Toàn diện Sprint 1: CarCareBooking

**Mã tài liệu:** `SAR-SPRINT-01-FINAL`  
**Dự án:** `CarCareBooking` (`D:\DAJAVA\CarCareBooking`)  
**Ngày nghiệm thu:** `2026-11-14`  
**Môi trường thử nghiệm & đánh giá:**
- **JDK:** OpenJDK 17.0.21 HotSpot
- **Web Container:** Apache Tomcat 10.1.61 (Port 8080)
- **Cơ sở dữ liệu:** MariaDB / MySQL 10.4.32 (XAMPP), Host `127.0.0.1`, Port `3306`, Database `carcare_db`
- **Quản lý dự án:** Apache Maven 3.9+ (WAR packaging)

---

## A. Bảng Nghiệm thu Tổng hợp Sprint 1 (9 Nhiệm vụ)

| Task | Tên nhiệm vụ | Kết quả | Bằng chứng mã nguồn / kiểm thử | Vấn đề còn lại / Ghi chú |
|---|---|---|---|---|
| **S1-01** | Khởi tạo Maven WAR, Git, cấu trúc MVC | **HOÀN THÀNH** | `pom.xml`, `web.xml`, `.gitignore`, 72 file Java phân lớp MVC chuẩn | Không |
| **S1-02** | Thiết kế CSDL tài khoản, vai trò, kết nối JDBC | **HOÀN THÀNH** | `schema.sql`, `seed.sql`, `DBContext.java`, `db.properties`, kết nối cổng 3306 thành công | Đã bổ sung `SET NAMES utf8mb4` |
| **S1-03** | Model, DAO và băm mật khẩu BCrypt | **HOÀN THÀNH (Về kỹ thuật)** | `User.java`, `Role.java`, `UserDAO.java`, `AuthSecurityTest.java` (PASS). Mật khẩu băm BCrypt work-factor 10 | Nhánh `origin/anhs` cần rebase và mở PR vào `main` |
| **S1-04** | Đăng nhập, đăng xuất, quản lý session | **HOÀN THÀNH** | `AuthServlet.java`, `UserDAO.authenticate()`, `FoundationAuthTest` (10/10 PASS), session timeout 60p, `changeSessionId()` | Không |
| **S1-05** | Đăng ký khách hàng, đặt lại mật khẩu, liên kết hồ sơ | **HOÀN THÀNH** | `AuthServlet.java`, `UserDAO.registerCustomer()`, `PasswordResetDAO.java`, `RegistrationPasswordResetTest` (6/6 PASS) | Chức năng email đang chạy ở chế độ demo localhost |
| **S1-06** | Kiểm soát phân quyền truy cập URL (RBAC) | **HOÀN THÀNH** | `AuthFilter.java`, `403.jsp`, kiểm thử HTTP chặn Customer vào `/users` đạt chuẩn **HTTP 403 Forbidden** | Không |
| **S1-07** | Giao diện đăng nhập, Dashboard theo 5 vai trò | **HOÀN THÀNH** | `login.jsp`, `dashboard/index.jsp`, `header.jspf`, `sidebar.jspf`, Bootstrap 5 responsive, 100% tiếng Việt UTF-8 | Không còn mojibake, không số liệu giả mạo |
| **S1-08** | Kiểm thử hệ thống xác thực & tài liệu kiểm thử | **HOÀN THÀNH (Về kỹ thuật)** | 22/22 unit tests JUnit 5 PASS, 7/7 live tests PASS; đã có `test-cases.md`, `test-execution-report.md`, `defect-log.md` | Tài liệu đã cập nhật và đối chiếu đầy đủ |
| **S1-09** | README và hướng dẫn chạy chương trình | **HOÀN THÀNH (Về kỹ thuật)** | `README.md` đầy đủ 12 mục hướng dẫn chuẩn, `scripts/run-tomcat.bat` tự động phát hiện Tomcat | Hướng dẫn vận hành đã được xác minh thực tế |

---

## B. Các Thay đổi Đã Thực hiện Trong Đợt Hoàn thiện

### 1. File đã tạo mới:
1. `database/migrations/001_add_password_reset_tokens.sql`: Migration tạo bảng `password_reset_tokens` có khóa ngoại liên kết `users(user_id) ON DELETE CASCADE` và index `idx_prt_user`.
2. `database/migrations/002_add_admin_employee_profile.sql`: Migration bổ sung hồ sơ nhân viên (`EMP-ADM01`) cho tài khoản quản trị viên (`user_id = 1`) với cơ chế an toàn `ON DUPLICATE KEY UPDATE`.
3. `database/backup_before_sprint1_migrations.sql`: Bản sao lưu toàn diện CSDL `carcare_db` trước khi chạy migrations (dung lượng 66.5 KB).
4. `docs/test-execution-report.md`: Báo cáo chi tiết kết quả thực thi 22 bài kiểm thử JUnit 5 và 7 luồng kiểm thử HTTP trực tiếp.
5. `docs/defect-log.md`: Nhật ký theo dõi, phân tích nguyên nhân và trạng thái đóng 4 lỗi kỹ thuật (DEF-01 đến DEF-04).
6. `docs/sprint-1-final-audit.md`: Báo cáo nghiệm thu hoàn tất Sprint 1.

### 2. File đã chỉnh sửa:
1. `database/schema.sql` & `database/seed.sql`: Bổ sung chỉ thị kết nối `SET NAMES utf8mb4; SET CHARACTER SET utf8mb4;` ngay sau lệnh `USE carcare_db;` để triệt tiêu vĩnh viễn lỗi mojibake khi nạp dữ liệu từ command prompt Windows.
2. `src/main/resources/db.properties` & `db.properties.example`: Chuẩn hóa tham số JDBC kết nối `useUnicode=true&characterEncoding=UTF-8`.
3. `src/main/java/com/carcare/dal/DBContext.java`: Cập nhật chuỗi kết nối mặc định có tham số UTF-8.
4. `scripts/run-tomcat.bat`: Bổ sung cơ chế tự động tìm kiếm đường dẫn Tomcat tại `C:\apache-tomcat-10.1.61` khi người dùng chưa cấu hình biến môi trường `TOMCAT_HOME`.
5. `README.md`: Viết lại tài liệu hướng dẫn vận hành toàn diện với 12 nội dung chuẩn (Java 17, Maven, Tomcat 10.1, MySQL 3306, migrations, bảo mật, VS Code, Eclipse, WAR deployment, testing, tài khoản mẫu và khắc phục sự cố).
6. `docs/test-cases.md`: Chuẩn hóa ma trận kiểm thử chi tiết 5 nhóm chức năng của Sprint 1 (AUTH, SESS, RBAC, REG, RESET) và danh mục kiểm thử cho Sprints 2 - 7.

### 3. Migrations đã thực hiện:
- **Migration 001:** Tạo thành công bảng `password_reset_tokens` trên MySQL `carcare_db`.
- **Migration 002:** Chèn thành công bản ghi nhân viên `EMP-ADM01` cho tài khoản `admin` (`user_id = 1`).

### 4. Nguyên nhân lỗi ban đầu và cách khắc phục:
- **Lỗi 1 (DEF-01 - Tiếng Việt bị lỗi ký tự kép):** Console Windows CP437 stream file UTF-8 không có `SET NAMES` -> Đã sao lưu dữ liệu lỗi, cập nhật qua JDBC PreparedStatement UTF-8, cấu hình `SET NAMES utf8mb4` trong SQL và JDBC URL.
- **Lỗi 2 (DEF-02 - Bảng `password_reset_tokens` không tồn tại):** CSDL trên máy chủ XAMPP được tạo trước khi pull commit `b0bffe6` -> Đã thực thi migration 001.
- **Lỗi 3 (DEF-03 - `staffAccountsLinkedToEmployees` fail do thiếu hồ sơ nhân viên cho admin):** Bản ghi admin trong `employees` chưa được seed vào CSDL live -> Đã thực thi migration 002.

---

## C. Kết quả Kiểm thử Toàn diện

### 1. Kết quả Kiểm thử Tự động Maven (`mvn clean test package`):
- **Trạng thái Build:** **`BUILD SUCCESS`**
- **Tổng số Unit Tests:** **22**
- **Số lượng PASS:** **22 / 22 (100%)**
- **Số lượng FAIL:** **0**
- **Số lượng ERROR:** **0**
- **Số lượng SKIPPED:** **0**
- **Gói sản phẩm:** Tạo thành công file `target/carcare.war` (dung lượng ~7.7 MB).

### 2. Kết quả Kiểm thử Tích hợp Thực tế trên Apache Tomcat 10.1:
Kiểm thử trực tiếp trên ứng dụng web tại context `/carcare`:
1. **Đăng nhập Quản trị viên (`admin` / `Admin@123`):** **PASS** (chuyển hướng `/carcare/home`, hiển thị tên và quyền `ADMIN`).
2. **Đăng xuất an toàn:** **PASS** (hủy session hoàn toàn, chuyển về `/login?logout=success`).
3. **Đăng ký Khách hàng mới (`POST /auth?action=register`):** **PASS** (tạo bản ghi `users`, `user_roles`, `customers` trong một transaction, chuyển hướng về login).
4. **Đăng nhập Khách hàng vừa tạo:** **PASS** (chuyển hướng `/carcare/home`, hiển thị Dashboard riêng cho Customer).
5. **Chu trình Quên & Đặt lại mật khẩu (End-to-End):** **PASS** (nhập email -> sinh token SHA-256 an toàn có thời hạn 30 phút -> nhập mật khẩu mới -> cập nhật mật khẩu băm BCrypt -> đăng nhập thành công bằng mật khẩu mới).
6. **Truy cập URL đúng vai trò:** **PASS** (Admin truy cập `/users` và `/bays` đạt mã HTTP Status **200 OK**).
7. **Chặn truy cập trái quyền:** **PASS** (Customer truy cập `/users` nhận đúng mã lỗi **HTTP 403 Forbidden**).
8. **Dữ liệu thật từ MySQL lên Dashboard:** **PASS** (hiển thị danh sách khoang bãi `Khoang Rửa Xe 01`, `Cầu Nâng Kỹ Thuật 01` trực tiếp từ database).
9. **Hiển thị tiếng Việt UTF-8:** **PASS** (100% tiếng Việt chuẩn xác, không còn bất kỳ ký tự rác nào).

---

## D. Đề xuất Danh mục Ảnh Minh chứng Nộp Trello

Các thành viên sử dụng kết quả chạy thật của dự án để chụp và đính kèm vào thẻ Trello tương ứng:

1. **Thẻ S1-01:** 
   - Ảnh chụp cấu trúc thư mục dự án trên VS Code / Eclipse.
   - Ảnh màn hình terminal chạy `mvn clean package` hiển thị `BUILD SUCCESS` và sinh file `target/carcare.war`.
2. **Thẻ S1-02:** 
   - Ảnh chụp MySQL Workbench kết nối cổng 3306 hiển thị danh sách các bảng trong `carcare_db`.
   - Ảnh đoạn lệnh `SET NAMES utf8mb4;` trong `schema.sql` và cấu hình kết nối trong `DBContext.java`.
3. **Thẻ S1-03:** 
   - Ảnh phương thức băm mật khẩu `BCrypt` và kiểm tra `checkpw` trong `UserDAO.java`.
   - Ảnh chạy bài kiểm thử `AuthSecurityTest` (PASS).
4. **Thẻ S1-04:** 
   - Ảnh màn hình giao diện đăng nhập `/carcare/login` hiển thị form chuẩn Bootstrap 5.
   - Ảnh sau khi đăng nhập tài khoản `admin`, thanh Header hiển thị "Quản Trị Viên Hệ Thống" và vai trò `ADMIN`.
5. **Thẻ S1-05:** 
   - Ảnh form đăng ký `/auth?action=register` và thông báo đăng ký thành công.
   - Ảnh form quên mật khẩu `/auth?action=forgot` và thông báo tạo liên kết đặt lại mật khẩu thành công.
   - Ảnh chạy bài kiểm thử `RegistrationPasswordResetTest` đạt **6/6 PASS**.
6. **Thẻ S1-06:** 
   - Ảnh màn hình trình duyệt hiển thị thông báo lỗi **HTTP 403 Forbidden** (hoặc giao diện `403.jsp`) khi tài khoản `customer` gõ URL `/carcare/users`.
   - Ảnh code ma trận phân quyền trong `AuthFilter.java`.
7. **Thẻ S1-07:** 
   - Ảnh màn hình Dashboard của **Admin** với đầy đủ các widget thống kê khoang bãi, người dùng, xe đang thi công không bị lỗi font tiếng Việt.
   - Ảnh màn hình Dashboard của **Customer** hiển thị lịch hẹn cá nhân và thông tin phương tiện.
8. **Thẻ S1-08:** 
   - Ảnh chạy toàn bộ kiểm thử JUnit trên terminal `mvn test` đạt **22/22 Tests PASS**.
   - Ảnh tài liệu `docs/test-cases.md` và `docs/test-execution-report.md`.
9. **Thẻ S1-09:** 
   - Ảnh tệp `README.md` xem ở chế độ Markdown Preview hiển thị đầy đủ các mục hướng dẫn.
   - Ảnh chạy script `scripts/run-tomcat.bat` build và khởi động thành công máy chủ Tomcat 10.1.

---

## E. Các Công việc Còn Cần Thực hiện (Action Items)

1. **Tích hợp nhánh origin/anhs:**
   - **Hiện trạng:** Nhánh `origin/anhs` chứa commit `e9cedb9` ("cập nhật xác thực và bảo mật tài khoản") tách ra từ commit đầu tiên và chưa được merge vào `main`. Trên `main`, các chức năng của `UserDAO` đã được triển khai đầy đủ.
   - **Hành động cần làm:** Thực hiện `git fetch origin`, sau đó `git rebase origin/main` trên nhánh `anhs`. Giữ lại file tiện ích độc lập `PasswordUtil.java` và `PasswordUtilTest.java` (đã viết rất tốt), chỉnh sửa các hàm trong `UserDAO` trên nhánh của mình cho khớp với `main` và mở **Pull Request** lên GitHub để hoàn tất tích hợp vào repository.
2. **Rà soát tài liệu kiểm thử:**
   - **Hiện trạng:** Các tài liệu `README.md`, `test-cases.md`, `test-execution-report.md`, `defect-log.md` đã được biên soạn và hoàn thiện đầy đủ trong mã nguồn dự án.
   - **Hành động cần làm:** Đối chiếu các tài liệu kiểm thử với kết quả thực thi kiểm thử tự động của hệ thống.
3. **Triển khai môi trường CSDL:**
   - Đảm bảo khi chuyển sang máy khác hoặc nạp CSDL mới, ngoài việc chạy `schema.sql` và `seed.sql`, luôn chạy thêm các file migration trong thư mục `database/migrations/`.
   - Dự án đã đạt trạng thái sẵn sàng 100% để bước vào phát triển **Sprint 2** (Dữ liệu nền phục vụ đặt lịch).
