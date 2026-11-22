# Ca kỹ thuật viên và cấu hình vận hành (Bii)

## Triển khai

Yêu cầu Java 17+, Maven, Tomcat 10.1 và MySQL/MariaDB như README.

1. Với CSDL mới: chạy `database/schema.sql` và `database/seed.sql`.
2. Với CSDL đang có: chạy `database/migrations/004_operations_configuration.sql` bằng MySQL client (`source database/migrations/004_operations_configuration.sql;`). Migration có thể chạy lại và giữ nguyên giá trị cấu hình hiện có.
3. Cấu hình kết nối bằng `db.properties` hoặc `DB_URL`, `DB_USER`, `DB_PASSWORD`.
4. Chạy `mvn clean test package`, triển khai `target/carcare.war` lên Tomcat.
5. Đăng nhập Admin hoặc Manager, mở menu **Ca & Kỹ năng kỹ thuật viên** (`/staff`) hoặc **Giờ mở cửa & Ngày nghỉ** (`/settings`).

## Chức năng

- Lập, sửa, xóa ca theo kỹ thuật viên, ngày, giờ bắt đầu/kết thúc; trạng thái đã lên lịch, có mặt, vắng mặt.
- Gán, sửa, xóa kỹ năng theo dịch vụ, với bốn mức cơ bản, trung cấp, nâng cao, chuyên gia. Không cho trùng cặp kỹ thuật viên/dịch vụ.
- Cấu hình giờ mở/đóng cửa mỗi ngày, đệm dọn khoang (0–240 phút), bước khung giờ (5–120 phút).
- Thêm ngày nghỉ đóng cửa cả ngày; nhập cùng ngày để cập nhật lý do; xóa để mở lại ngày đó.
- Ca phải trong giờ hoạt động, không rơi vào ngày nghỉ. Các ca không vắng mặt của cùng một người không được trùng; hai ca liền kề được chấp nhận.
- Thu hẹp giờ mở cửa không được làm mất hiệu lực các ca hiện tại/tương lai. Ngày đã có ca không vắng mặt phải điều chỉnh ca trước khi đóng cửa.
- Chỉ Admin/Manager được đọc và thay đổi hai phân hệ. POST kiểm tra CSRF; truy vấn dùng tham số; lỗi CSDL hiển thị thông báo thay vì báo lưu thành công.
- Ghi dữ liệu trong transaction, khóa cấu hình chung để kiểm tra trùng ca và thay đổi giờ/ngày nghỉ nhất quán khi có nhiều yêu cầu cùng lúc.

Các giá trị vận hành được lưu trong `operating_settings`; các phân hệ sinh khung giờ đặt lịch có thể đọc các khóa `OPENING_TIME`, `CLOSING_TIME`, `BUFFER_MINUTES`, `SLOT_INTERVAL_MINUTES`. Phần đặt lịch/điều phối vốn còn TODO trong repo không nằm trong thay đổi này.

## Kiểm thử ngày 22/11/2026

- `mvn clean test package`: BUILD SUCCESS, 32 kiểm thử, không lỗi (gồm 5 bài kiểm thử OperatingRules mới).
- `python scripts/verify_operations.py`: 43 kiểm tra tích hợp qua HTTP/MySQL đều thành công, gồm CRUD, dữ liệu sai, phân quyền, CSRF, rollback và hai yêu cầu tạo ca trùng đồng thời.
- Script tích hợp cần Python và `requests`, ứng dụng đang chạy với tài khoản seed. Cấp mật khẩu thử nghiệm qua các biến môi trường `CARCARE_ADMIN_PASSWORD`, `CARCARE_MANAGER_PASSWORD`, `CARCARE_CUSTOMER_PASSWORD`, `CARCARE_TECHNICIAN_PASSWORD`, `CARCARE_RECEPTIONIST_PASSWORD`; chỉ chạy trên CSDL thử nghiệm. Script chọn kỹ năng chưa gán/ngày chưa có dữ liệu, xóa bản ghi thử và khôi phục cấu hình sau khi chạy. Có thể đặt `CARCARE_URL` nếu context/cổng khác.
- Playwright/Chrome: thao tác tạo ca, tạo và sửa kỹ năng, đặt đệm 20 phút, thêm ngày nghỉ, xác minh form Sửa/Tạo mới, thông báo chặn ca trùng/ngày nghỉ và giao diện điện thoại 390px.

Ảnh minh chứng cục bộ tại `C:\Users\Admin\Downloads\CarCareBooking-Bii-MinhChung`:

1. `01-Ca-va-ky-nang.png`
2. `02-Chan-ca-trung.png`
3. `03-Gio-mo-cua-ngay-nghi-thoi-gian-dem.png`
4. `04-Chan-ca-vao-ngay-nghi.png`
5. `05-Giao-dien-dien-thoai.png`
