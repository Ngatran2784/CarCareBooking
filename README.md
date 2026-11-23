# CarCareBooking - Hệ thống Quản lý Đặt lịch & Điều phối Dịch vụ Chăm sóc Xe

Dự án Java Web xây dựng theo kiến trúc MVC chuẩn thực hành Jakarta Servlet 6.0, JSP/JSTL 3.0, JDBC và MySQL 8.x trên máy chủ ứng dụng Apache Tomcat 10.1.

---

## 1. Công nghệ sử dụng
- **Ngôn ngữ**: Java 17 (OpenJDK / Eclipse Temurin LTS)
- **Quản lý dự án & Build**: Apache Maven 3.9+ (WAR packaging)
- **Web Standard**: Jakarta Servlet API 6.0, JSP / JSTL 3.0 (Jakarta EE 10)
- **Cơ sở dữ liệu**: MySQL 8.x / MariaDB 10.4+ qua MySQL Connector/J 8.3.0
- **Máy chủ Web**: Apache Tomcat 10.1.x
- **Bảo mật**: Mật khẩu băm an toàn với BCrypt (work factor 10), Token SHA-256 dùng một lần có thời hạn, chống Session Fixation
- **Unit Testing**: JUnit 5 (Jupiter 5.10.2)
- **Giao diện**: HTML5, CSS3, JavaScript, Bootstrap 5 CDN

---

## 2. Cấu trúc thư mục dự án
```text
CarCareBooking/
├── pom.xml                               # Cấu hình Maven dependencies & WAR packaging
├── README.md                             # Hướng dẫn cài đặt & vận hành hệ thống
├── .gitignore                            # Loại trừ file nhạy cảm và target/
├── .vscode/
│   └── extensions.json                   # Danh sách extension Java khuyến nghị
├── database/
│   ├── schema.sql                        # DDL khởi tạo cấu trúc 25 bảng CSDL
│   ├── seed.sql                          # Dữ liệu mẫu ban đầu (tài khoản, khoang bãi, dịch vụ)
│   ├── migrations/                       # Các migration bổ sung an toàn
│   │   ├── 001_add_password_reset_tokens.sql
│   │   └── 002_add_admin_employee_profile.sql
│   └── backup_before_sprint1_migrations.sql
├── docs/
│   ├── database-design.md                # Thiết kế chi tiết CSDL
│   ├── user-story.md                     # Đặc tả yêu cầu người dùng
│   ├── sprint-plan.md                    # Kế hoạch phát triển 7 Sprint
│   ├── test-cases.md                     # Kịch bản kiểm thử chi tiết
│   ├── test-execution-report.md          # Báo cáo thực thi kiểm thử
│   ├── defect-log.md                     # Nhật ký xử lý lỗi
│   └── sprint-1-final-audit.md           # Báo cáo nghiệm thu hoàn thành Sprint 1
├── scripts/
│   └── run-tomcat.bat                    # Script tự động build, deploy và chạy Tomcat
├── src/
│   ├── main/
│   │   ├── java/com/carcare/
│   │   │   ├── controller/               (Các Servlet điều hướng MVC)
│   │   │   ├── dal/                      (Lớp DAO & DBContext kết nối CSDL)
│   │   │   ├── filter/                   (EncodingFilter UTF-8, AuthFilter phân quyền)
│   │   │   ├── model/                    (Các thực thể dữ liệu JavaBean)
│   │   │   ├── service/                  (Lớp xử lý nghiệp vụ)
│   │   │   └── util/                     (Validation, DateTime, FileUpload)
│   │   ├── resources/
│   │   │   ├── db.properties.example     (File mẫu cấu hình kết nối CSDL)
│   │   │   └── db.properties             (File cấu hình cục bộ - không commit)
│   │   └── webapp/
│   │       ├── index.jsp                 (Trang chủ điều hướng)
│   │       ├── assets/                   (CSS, JavaScript, hình ảnh)
│   │       └── WEB-INF/
│   │           ├── web.xml               (Cấu hình Filter, Servlet & Session)
│   │           └── views/                (Giao diện JSP theo từng phân hệ)
│   └── test/java/com/carcare/            (Bộ kiểm thử tự động JUnit 5)
```

---

## 3. Hướng dẫn Cài đặt Môi trường (Yêu cầu Tiên quyết)

### 3.1. Cài đặt Java 17
1. Tải và cài đặt **JDK 17 LTS** (ví dụ: [Eclipse Adoptium Temurin 17](https://adoptium.net/temurin/releases/?version=17)).
2. Thiết lập biến môi trường hệ thống:
   - `JAVA_HOME`: Trỏ tới thư mục cài đặt JDK 17 (ví dụ: `C:\Program Files\Eclipse Adoptium\jdk-17.0.21.9-hotspot`).
   - Bổ sung `%JAVA_HOME%\bin` vào biến `PATH`.
3. Kiểm tra bằng lệnh: `java -version` (yêu cầu hiển thị `openjdk version "17.x.x"`).

### 3.2. Cài đặt Apache Maven
1. Tải bản zip **Apache Maven 3.9+** từ [Maven Download](https://maven.apache.org/download.cgi).
2. Giải nén vào thư mục công cụ của bạn (ví dụ: `C:\tools\apache-maven-3.9.9`).
3. Bổ sung đường dẫn thư mục `bin` của Maven vào biến `PATH`.
4. Kiểm tra bằng lệnh: `mvn -v`.

### 3.3. Cài đặt Apache Tomcat 10.1
1. Tải bản zip **Apache Tomcat 10.1.x** (chọn bản Core zip) từ [Tomcat 10 Software Downloads](https://tomcat.apache.org/download-10.cgi).
2. Giải nén vào thư mục máy chủ (ví dụ: `C:\apache-tomcat-10.1.61` hoặc `%USERPROFILE%\tools\apache-tomcat-10.1.61`).
3. Đặt biến môi trường `CATALINA_HOME` trỏ tới thư mục giải nén nếu chạy thủ công.

### 3.4. Cấu hình cơ sở dữ liệu MySQL (cổng 3307)
1. Khởi động dịch vụ MySQL80 trong Windows Services (Win + R → services.msc). Trong MySQL Workbench, mở Local instance MySQL80 (localhost:3307) để kiểm tra kết nối. Dự án dùng MySQL Server cài riêng nên có thể chạy khi XAMPP đã tắt.
2. Nếu máy chưa có database carcare_db, mở Command Prompt (CMD) tại thư mục gốc CarCareBooking và chạy lần lượt các lệnh sau. Tùy chọn -p sẽ yêu cầu nhập mật khẩu MySQL trên máy bạn.
   mysql -h 127.0.0.1 -P 3307 -u root -p < database\schema.sql
   mysql -h 127.0.0.1 -P 3307 -u root -p carcare_db < database\seed.sql
3. Chạy các migration bổ sung theo thứ tự sau:
   mysql -h 127.0.0.1 -P 3307 -u root -p carcare_db < database\migrations\001_add_password_reset_tokens.sql
   mysql -h 127.0.0.1 -P 3307 -u root -p carcare_db < database\migrations\002_add_admin_employee_profile.sql
   
   Nếu đã nhập bản sao lưu carcare_db.sql và bản sao lưu đã có đủ dữ liệu cùng các thay đổi từ migration, chuyển thẳng sang bước 4.
4. Sao chép src/main/resources/db.properties.example thành src/main/resources/db.properties. Trong file vừa tạo, đổi db.url sang jdbc:mysql://localhost:3307/carcare_db, giữ các tham số có sẵn sau dấu ?, rồi điền db.username và db.password của MySQL trên máy mình. Chỉ đưa db.properties.example lên Git và thêm /src/main/resources/db.properties vào .gitignore để giữ mật khẩu trong cấu hình máy cá nhân.

### 3.5. Cấu hình Kết nối JDBC (`db.properties`)
1. Sao chép tệp `src/main/resources/db.properties.example` thành `src/main/resources/db.properties`.
2. Điền thông tin kết nối MySQL của máy bạn:
   ```properties
   db.driver=com.mysql.cj.jdbc.Driver
   db.url=jdbc:mysql://localhost:3306/carcare_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh&useUnicode=true&characterEncoding=UTF-8
   db.username=root
   db.password=your_mysql_password
   ```
   *(Lưu ý: Không commit tệp `db.properties` có chứa mật khẩu lên Git repository; tệp này đã được khai báo trong `.gitignore`)*.
3. Hoặc có thể cấu hình thông qua biến môi trường hệ thống: `DB_URL`, `DB_USER`, `DB_PASSWORD`.

---

## 4. Hướng dẫn Chạy Nhanh trên Windows

Nếu đã cài đặt Tomcat 10.1 tại `C:\apache-tomcat-10.1.61` hoặc `%USERPROFILE%\tools\apache-tomcat-10.1.61`:

1. Chạy script tự động:
   ```cmd
   scripts\run-tomcat.bat
   ```
   Script sẽ tự động:
   - Build gói WAR bằng Maven.
   - Triển khai vào thư mục `webapps\carcare.war` của Tomcat.
   - Khởi động Tomcat 10.1 và mở cổng 8080.
2. Mở trình duyệt truy cập: **`http://localhost:8080/carcare/`** (hoặc `/carcare/home`).
3. Để dừng máy chủ, nhấn tổ hợp phím `Ctrl + C` trong cửa sổ dòng lệnh.

---

## 5. Hướng dẫn Chạy trên VS Code
1. Cài đặt các extension khuyến nghị:
   - `Extension Pack for Java` (Microsoft)
   - `Tomcat for Java` (Community Server Connectors)
2. Mở thư mục dự án `CarCareBooking` trong VS Code.
3. Trong thanh điều hướng cạnh bên, mở mục **Tomcat Servers**:
   - Nhấn dấu `+` để thêm Tomcat Server, chọn thư mục cài đặt Tomcat 10.1 của bạn.
4. Chuột phải vào máy chủ Tomcat vừa thêm > chọn **Add Web App (.war)** > chọn tệp `target/carcare.war` (hoặc chuột phải vào thư mục dự án chọn **Debug / Run on Tomcat Server**).
5. Truy cập ứng dụng: `http://localhost:8080/carcare/`.

---

## 6. Hướng dẫn Import và Chạy trên Eclipse Enterprise
1. Khởi động **Eclipse IDE for Enterprise Java and Web Developers**.
2. Chọn menu **File** > **Import...**
3. Chọn mục **Maven** > **Existing Maven Projects**, nhấn **Next**.
4. Tại ô **Root Directory**, nhấn **Browse...** trỏ đến thư mục `D:\DAJAVA\CarCareBooking`.
5. Đảm bảo file `pom.xml` được tích chọn, sau đó nhấn **Finish** và đợi Eclipse tải dependencies.
6. Thêm máy chủ Apache Tomcat 10.1:
   - Mở view **Servers** (Window > Show View > Other... > Servers).
   - Chuột phải vào vùng trống > **New** > **Server** > Chọn **Apache** > **Tomcat v10.1 Server**.
   - Trỏ đường dẫn đến thư mục cài đặt Tomcat 10.1 của bạn, chọn JRE 17.
7. Chuột phải vào project `CarCareBooking` > **Run As** > **Run on Server**.
8. Truy cập ứng dụng trên trình duyệt: `http://localhost:8080/carcare/home`.

---

## 7. Đóng gói WAR và Triển khai Thủ công
1. Đóng gói ứng dụng:
   ```bash
   mvn clean package
   ```
   File WAR được sinh ra tại: `target/carcare.war`.
2. Triển khai thủ công:
   - Sao chép file `target/carcare.war` vào thư mục `%CATALINA_HOME%\webapps\`.
   - Khởi động Tomcat bằng lệnh: `%CATALINA_HOME%\bin\catalina.bat run` (hoặc `startup.bat`).
   - Tomcat sẽ tự động giải nén thành thư mục ứng dụng `carcare`.

---

## 8. Hướng dẫn Chạy Kiểm thử (Testing)
Chạy toàn bộ bộ kiểm thử tự động JUnit 5:
```bash
mvn clean test
```
- Kiểm tra kết quả chi tiết từng bài test tại: `target/surefire-reports/`.
- Tài liệu kịch bản kiểm thử: [docs/test-cases.md](file:///d:/DAJAVA/CarCareBooking/docs/test-cases.md).
- Báo cáo thực thi kiểm thử: [docs/test-execution-report.md](file:///d:/DAJAVA/CarCareBooking/docs/test-execution-report.md).
- Nhật ký ghi nhận lỗi: [docs/defect-log.md](file:///d:/DAJAVA/CarCareBooking/docs/defect-log.md).

---

## 9. Danh sách Tài khoản Thử nghiệm (Sprint 1)

| Vai trò | Tên đăng nhập | Mật khẩu mẫu | Quyền hạn chính |
|---|---|---|---|
| **Quản trị viên** | `admin` | `Admin@123` | Quản trị toàn hệ thống, quản lý người dùng (`/users`), khoang bãi, cấu hình |
| **Quản lý cơ sở** | `manager` | `Manager@123` | Bảng điều phối, quản lý nhân sự, dịch vụ, báo cáo doanh thu |
| **Nhân viên lễ tân** | `receptionist` | `Recept@123` | Tiếp nhận xe, check-in, quản lý lịch hẹn, thông báo |
| **Kỹ thuật viên** | `technician` | `Tech@123` | Thực hiện thẻ công việc (Job Card), kiểm tra hiện trạng xe, kiểm định QC |
| **Khách hàng** | `customer` | `Cust@123` | Đặt lịch dịch vụ, theo dõi tiến độ xe, xem hóa đơn và đánh giá |
| **Tài khoản khóa** | `locked_user` | `Locked@123` | Kiểm thử từ chối truy cập (Tài khoản bị khóa bởi quản trị viên) |

### Chức năng Đăng ký & Quên mật khẩu
- **Đăng ký khách hàng mới**: Truy cập `/auth?action=register`. Dữ liệu đăng ký tự động tạo tài khoản `users`, phân vai trò `CUSTOMER` và sinh hồ sơ khách hàng `customers`.
- **Yêu cầu đặt lại mật khẩu**: Truy cập `/auth?action=forgot`. Nhập email đã đăng ký. Hệ thống sinh token ngẫu nhiên an toàn (chỉ lưu hash SHA-256) hết hạn sau 30 phút. Trên môi trường `localhost`, liên kết đổi mật khẩu được hiển thị trực tiếp để tiện thử nghiệm.

---

## 10. Cách Khắc phục Sự cố Thường gặp (Troubleshooting)

1. **Cổng 8080 bị chiếm dụng (Address already in use):**
   - Kiểm tra tiến trình đang chiếm cổng: `netstat -ano | findstr :8080`.
   - Tắt tiến trình cũ hoặc đổi cổng sang 8081/8082 trong tệp `%CATALINA_HOME%\conf\server.xml`.
2. **Lỗi kết nối CSDL (Communications link failure / Access denied):**
   - Đảm bảo dịch vụ MySQL trên cổng 3306 đang chạy.
   - Kiểm tra lại tài khoản và mật khẩu trong `src/main/resources/db.properties`.
3. **Lỗi hiển thị ký tự tiếng Việt (Lỗi font / Mojibake):**
   - Đảm bảo CSDL sử dụng bảng mã `utf8mb4` và collation `utf8mb4_unicode_ci`.
   - Trong chuỗi JDBC URL luôn có `useUnicode=true&characterEncoding=UTF-8`.
4. **Lỗi thiếu bảng khi quên mật khẩu:**
   - Thực thi tệp migration: `database/migrations/001_add_password_reset_tokens.sql`.
5. **Lỗi "Không tìm thấy Tomcat" khi chạy script:**
   - Đặt biến môi trường `TOMCAT_HOME` trỏ đúng thư mục cài đặt Tomcat trước khi gọi `run-tomcat.bat` (ví dụ: `set TOMCAT_HOME=C:\apache-tomcat-10.1.61`).
