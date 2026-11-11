# CarCareBooking - Hệ thống Quản lý Đặt lịch & Điều phối Dịch vụ Chăm sóc Xe

Dự án Java Web xây dựng theo kiến trúc MVC chuẩn thực hành Jakarta Servlet 6.0, JSP/JSTL 3.0, JDBC và MySQL 8.x trên Apache Tomcat 10.1.

---

## 1. Công nghệ sử dụng
- **Ngôn ngữ**: Java 17
- **Quản lý dự án**: Apache Maven (WAR packaging)
- **Web Standard**: Jakarta Servlet API 6.0, JSP / JSTL 3.0 (Jakarta EE 10)
- **Cơ sở dữ liệu**: MySQL 8.x qua MySQL Connector/J 8.3.0
- **Máy chủ Web**: Apache Tomcat 10.1.x
- **Unit Testing**: JUnit 5 (Jupiter 5.10.2)
- **Giao diện**: HTML5, CSS3, JavaScript, Bootstrap 5 CDN

---

## 2. Cấu trúc thư mục dự án
```text
CarCareBooking/
├── pom.xml
├── README.md
├── .gitignore
├── .vscode/
│   └── extensions.json
├── database/
│   ├── schema.sql
│   └── seed.sql
├── docs/
│   ├── database-design.md
│   ├── user-story.md
│   ├── sprint-plan.md
│   └── test-cases.md
├── src/
│   ├── main/
│   │   ├── java/com/carcare/
│   │   │   ├── controller/      (21 Servlet điều hướng)
│   │   │   ├── dal/             (18 DAO & DBContext)
│   │   │   ├── filter/          (EncodingFilter, AuthFilter)
│   │   │   ├── model/           (23 Model JavaBean)
│   │   │   ├── service/         (4 Business Logic Manager)
│   │   │   └── util/            (Validation, DateTime, FileUpload)
│   │   ├── resources/
│   │   │   └── db.properties.example
│   │   └── webapp/
│   │       ├── index.jsp
│   │       ├── assets/          (CSS, JS, Images)
│   │       └── WEB-INF/
│   │           ├── web.xml
│   │           └── views/       (30 giao diện JSP module)
│   └── test/java/com/carcare/   (Unit tests)
```

---

## 2b. Chạy nhanh trên Windows (khuyến nghị)

**Chuẩn bị (làm một lần)**
1. Cài **JDK 17** và **Apache Maven 3.9+** (kiểm tra bằng `java -version`, `mvn -v`).
2. Tải **Apache Tomcat 10.1** (bản zip, https://tomcat.apache.org/download-10.cgi) và giải nén, ví dụ vào `C:\Users\<tên bạn>\tools\apache-tomcat-10.1.61`.
3. Bật **MySQL 8** (ví dụ XAMPP) rồi nạp cơ sở dữ liệu:
   ```bash
   mysql -u root -p < database/schema.sql
   mysql -u root -p < database/seed.sql
   ```
   Mặc định ứng dụng kết nối `localhost:3306`, user `root`, mật khẩu rỗng. Nếu khác, sao chép `src/main/resources/db.properties.example` thành `db.properties` và sửa, hoặc đặt biến môi trường `DB_URL`, `DB_USER`, `DB_PASSWORD`.

**Chạy**
1. Chạy `scripts\run-tomcat.bat` (nhấp đôi, hoặc gõ trong terminal). Script tự build WAR, deploy lên Tomcat và khởi động; giữ cửa sổ mở, nhấn `Ctrl+C` để dừng.
   - Tomcat đặt ở thư mục khác `%USERPROFILE%\tools\apache-tomcat-10.1.61` thì đặt biến `TOMCAT_HOME` trước khi chạy: `set TOMCAT_HOME=D:\tomcat` rồi chạy script.
2. Mở http://localhost:8080/carcare/ trong trình duyệt.

**Tài khoản thử** (xem thêm đầu file `database/seed.sql`)

| Vai trò | Tài khoản | Mật khẩu |
|---|---|---|
| Quản trị viên | `admin` | `Admin@123` |
| Quản lý | `manager` | `Manager@123` |
| Lễ tân | `receptionist` | `Recept@123` |
| Kỹ thuật viên | `technician` | `Tech@123` |
| Khách hàng | `customer` | `Cust@123` |

**Đăng ký và quên mật khẩu (Sprint 1)**
- Đăng ký khách hàng: `/auth?action=register`. Tài khoản mới được liên kết với hồ sơ trong bảng `customers`.
- Quên mật khẩu: `/auth?action=forgot`. Token đặt lại dùng một lần, hết hạn sau 30 phút.
- Dự án chưa cấu hình gửi email: liên kết đặt lại được ghi vào log của Tomcat và chỉ hiện trực tiếp trên trang khi truy cập từ `localhost`.

**Chạy kiểm thử:** `mvn test` (cần MySQL đã nạp dữ liệu).

**Lỗi thường gặp**
- *Trang không mở / cổng 8080 bận*: tắt chương trình đang dùng cổng 8080 hoặc đổi cổng trong `conf\server.xml` của Tomcat.
- *"Không tìm thấy Tomcat"*: kiểm tra `TOMCAT_HOME` trỏ đúng thư mục chứa `bin\catalina.bat`.
- *Đăng nhập báo lỗi / trang trống*: kiểm tra MySQL đang chạy và đã nạp `schema.sql`, `seed.sql`.
- *Trang quên mật khẩu hoặc đăng ký lỗi sau khi cập nhật code*: nạp lại `schema.sql` (có thêm bảng `password_reset_tokens`).

---

## 3. Hướng dẫn chạy trên VS Code
1. Cài đặt các extension Java được khuyến nghị trong `.vscode/extensions.json`:
   - `Extension Pack for Java`
   - `Tomcat for Java` (Community Server Connectors)
2. Mở thư mục `CarCareBooking` trong VS Code.
3. Cấu hình Apache Tomcat 10.1 trong tab Server của VS Code.
4. Deploy dự án lên Tomcat và khởi chạy máy chủ.
5. Truy cập ứng dụng tại địa chỉ: `http://localhost:8080/carcare/` hoặc `http://localhost:8080/carcare/home`.

---

## 4. Hướng dẫn Import và chạy trên Eclipse Enterprise
1. Mở **Eclipse IDE for Enterprise Java and Web Developers**.
2. Chọn menu **File** > **Import...**
3. Trong hộp thoại Import, chọn: **Maven** > **Existing Maven Projects** rồi nhấn **Next**.
4. Tại ô **Root Directory**, nhấn **Browse...** và chọn thư mục `D:\DAJAVA\CarCareBooking`.
5. Đảm bảo file `pom.xml` được tích chọn, sau đó nhấn **Finish**.
6. Đợi Eclipse đồng bộ dependencies từ Maven.
7. Thêm máy chủ Apache Tomcat 10.1:
   - Mở view **Servers** (Window > Show View > Servers).
   - Nhấp chuột phải chọn **New** > **Server** > Chọn **Apache** > **Tomcat v10.1 Server**.
   - Trỏ đường dẫn đến thư mục cài đặt Tomcat 10.1 của bạn.
8. Chuột phải vào project `CarCareBooking` > **Run As** > **Run on Server**.
9. Trình duyệt nội bộ hoặc ngoại bộ sẽ mở trang: `http://localhost:8080/carcare/home`.

---

## 5. Cấu hình Cơ sở dữ liệu (bắt buộc để đăng nhập)
- Tạo database trong MySQL:
  ```bash
  mysql -u root -p < database/schema.sql
  mysql -u root -p < database/seed.sql
  ```
- Sao chép `src/main/resources/db.properties.example` thành `src/main/resources/db.properties` và điền mật khẩu kết nối.
- *Lưu ý*: đăng nhập, đăng ký và quên mật khẩu đều cần MySQL đã nạp `schema.sql` và `seed.sql`.
