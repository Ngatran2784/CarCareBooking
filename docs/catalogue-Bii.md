# Dịch vụ, bảng giá và khoang bảo trì — Bii

## Sử dụng

- Đăng nhập Admin/Manager, mở **Bảng giá dịch vụ** (`/services`) để thêm/sửa/xóa dịch vụ, nhập giá niêm yết VNĐ và thời lượng theo phút. Tìm kiếm ngay trong danh sách. Lễ tân chỉ xem danh mục và giá.
- Khi sửa giá, hệ thống lưu giá cũ/mới, thời điểm và người thay đổi trong `service_price_history` cùng transaction với dịch vụ. Không sửa giá đã chốt trong `booking_services`.
- Dịch vụ có đơn đặt, phát sinh, kỹ năng kỹ thuật viên hoặc lịch sử giá được bảo vệ khi xóa. Có thể chuyển sang **Ngừng cung cấp** để giữ lịch sử.
- Mở **Khoang & Lịch bảo trì** (`/bays`) để thêm/sửa/xóa khoang, loại khoang và trạng thái cơ sở.
- Lập/sửa/xóa lịch bảo trì theo khoang, thời điểm bắt đầu/kết thúc, lý do và trạng thái: đã lên lịch, đang bảo trì, hoàn thành, đã hủy. Thời gian sử dụng múi giờ Việt Nam.
- Các lịch đã lên lịch/đang bảo trì không được trùng nhau hoặc trùng công việc đã phân công. Hai lịch liền kề được phép. Lịch hủy/hoàn thành không chặn lịch mới.
- Trong khung giờ bảo trì đang hiệu lực, danh sách khoang hiển thị **Bảo trì** khi tải trang. Khi hết giờ, hủy hoặc hoàn thành, khoang trở về trạng thái cơ sở. Bảo trì thủ công vẫn được giữ nếu đó là trạng thái cơ sở.
- Không xóa khoang đang sử dụng hoặc còn phân công/lịch bảo trì. Có thể xóa lịch bảo trì qua thao tác riêng trước khi xóa khoang chưa sử dụng.

## Triển khai

1. Dùng CSDL nền theo README. Với CSDL cũ, chạy lần lượt migration `004_operations_configuration.sql` và `005_bay_maintenance.sql` bằng MySQL client. Migration 005 chỉ tạo bảng nếu thiếu, không xóa dữ liệu.
2. Cấu hình kết nối CSDL như README, chạy `mvn clean test package`, triển khai `target/carcare.war` lên Tomcat 10.1 (Java 17+).
3. Hai servlet mẫu ServiceServlet/ServiceBayServlet được thay bằng CatalogServlet, giữ nguyên URL `/services`, `/bays`. CatalogDAO xử lý JDBC thật, CatalogRules kiểm tra dữ liệu; các phần staff/settings của lần trước giữ nguyên.

Admin/Manager mới được ghi dữ liệu. POST kiểm tra CSRF; dữ liệu hiển thị được escape; SQL dùng tham số. Các thao tác ghi lấy khóa cấu hình chung để tránh hai yêu cầu cùng tạo tên/lịch trùng, rollback nếu lỗi.

## Xác minh ngày 22/11/2026

- Build WAR thành công, **36 kiểm thử JUnit** qua, không lỗi; trong đó có 4 bài kiểm tra mới về giá, thời lượng, tên và khoảng bảo trì.
- **66 kiểm tra tích hợp HTTP/MySQL** qua: CRUD, giá chính xác, lịch sử giá, giữ nguyên giá của đơn đã đặt, dữ liệu không hợp lệ, CSRF, phân quyền, bảo vệ dữ liệu liên quan, xung đột phân công/bảo trì, lịch liền kề, hủy/hoàn thành và hai yêu cầu lập lịch đồng thời.
- Migration 005 chạy hai lần thành công, giữ dữ liệu hiện có.
- Playwright/Chrome kiểm tra thêm/sửa/xóa dịch vụ, khoang và lịch bảo trì qua giao diện thật, xác nhận hộp thoại xóa, hiển thị giá VNĐ, form sửa, cảnh báo trùng lịch và giao diện điện thoại 390px.

Chạy lại kiểm tra tích hợp bằng `python scripts/verify_catalog.py` trên CSDL thử nghiệm với Python có `requests`, MySQL CLI và app đang chạy. Cần đặt:

- `CARCARE_TEST_DB`: tên CSDL thử nghiệm có dữ liệu seed.
- `CARCARE_MYSQL`: đường dẫn MySQL CLI; `CARCARE_DB_USER` nếu khác root, `MYSQL_PWD` nếu cần mật khẩu CSDL.
- `CARCARE_ADMIN_PASSWORD`, `CARCARE_MANAGER_PASSWORD`, `CARCARE_RECEPTIONIST_PASSWORD`, `CARCARE_CUSTOMER_PASSWORD`, `CARCARE_TECHNICIAN_PASSWORD`: mật khẩu tài khoản thử nghiệm, không lưu trong script.
- `CARCARE_URL` nếu địa chỉ khác `http://localhost:8080/carcare`.

Script tạo dữ liệu riêng có tên ngẫu nhiên; cleanup chỉ các ID do lần chạy tạo, gồm fixtures lịch sử giá/đơn đặt/phân công.

## Minh chứng

7 ảnh chụp tại `C:\Users\Admin\Downloads\CarCareBooking-Bii-DichVu-Khoang-MinhChung`:

1. Dịch vụ, bảng giá, thời lượng và lịch sử đổi giá.
2. Form sửa dịch vụ.
3. Danh sách khoang, trạng thái bảo trì và lịch bảo trì.
4. Form sửa lịch bảo trì.
5. Cảnh báo chặn lịch bảo trì trùng.
6. Dịch vụ trên điện thoại.
7. Khoang và lịch bảo trì trên điện thoại.
