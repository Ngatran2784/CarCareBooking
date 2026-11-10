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

## 5. Cấu hình Cơ sở dữ liệu (Tùy chọn)
- Tạo database trong MySQL:
  ```bash
  mysql -u root -p < database/schema.sql
  mysql -u root -p < database/seed.sql
  ```
- Sao chép `src/main/resources/db.properties.example` thành `src/main/resources/db.properties` và điền mật khẩu kết nối.
- *Lưu ý*: Trang chủ demo `/home` và giao diện khung sườn hoạt động độc lập ngay cả khi chưa kết nối Database.
