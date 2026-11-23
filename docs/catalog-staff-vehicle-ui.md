# Giao diện danh mục, nhân sự và chọn xe

Cập nhật ngày 23/11/2026 trên nhánh `feat/catalog-staff-vehicle-ui`, dựa trên `origin/Bii` (516605b), đã chứa `origin/main` (5ae0ff0).

- `/services`: tổng quan dịch vụ, tìm kiếm tiếng Việt có/không dấu, lọc trạng thái, số kết quả, thông báo không tìm thấy, biểu mẫu thêm/sửa thu gọn; giữ CRUD và lịch sử giá hiện có.
- `/staff`: tổng quan kỹ thuật viên, ca làm, kỹ năng; tìm ca nhanh; đưa danh sách lên trước, mở biểu mẫu khi thêm/sửa hoặc có lỗi. Giữ CSRF và nghiệp vụ hiện có.
- `/bays`: đồng bộ phong cách và điều hướng danh mục theo quyền.
- `/vehicles`: đồng bộ phong cách, thêm lối vào chọn xe và sửa biểu thức liên kết thêm xe theo khách hàng.
- `/bookings?action=create`: chọn xe thật bằng thẻ và radio, tìm theo biển số/hãng/chủ xe, xem lại và xác nhận bằng GET. Khách hàng chỉ có xe của mình; Admin/Manager/Lễ tân chọn từ xe của xưởng; kỹ thuật viên không được truy cập. ID không hợp lệ hoặc ngoài danh sách được phép trả HTTP 400. Bước này chưa tạo lịch hẹn; phân hệ đặt lịch gốc vẫn chưa triển khai nghiệp vụ lưu lịch.
- CSS giới hạn trong `.workspace-page`; hỗ trợ bàn phím, nhãn trường, thông báo trực tiếp và màn hình 390px.

## Kiểm tra

- Maven `test package`: 36 kiểm thử qua, không lỗi, tạo WAR thành công.
- Chrome/Playwright trên Tomcat + MySQL thật: 5 trang tải HTTP 200, tìm kiếm và lọc dịch vụ, mở form dịch vụ/nhân sự, sửa/reset ca, chọn/xác nhận xe, trạng thái không có kết quả. Không có lỗi JavaScript.
- Kiểm tra màn hình 390 × 844: dịch vụ, nhân sự và chọn xe không tràn ngang trang.
- HTTP: Admin/Customer/Receptionist chọn xe thành công; ID sai bị chặn; Technician nhận 403; khách chưa đăng nhập được chuyển về đăng nhập.
- 11 ảnh tại `C:/Users/Admin/Downloads/CarCareBooking-DanhMuc-NhanSu-ChonXe` gồm danh mục, nhân sự, chọn xe, quản lý xe, khoang, form, xác nhận và ba màn hình điện thoại.

Chạy lại giao diện với app đang hoạt động: cài Python Playwright, đặt `CARCARE_ADMIN_PASSWORD`, chạy `python scripts/verify_workspace_ui.py`. Tùy chọn `CARCARE_URL`, `CARCARE_CHROME`, `CARCARE_EVIDENCE_DIR`. Các kiểm tra giao diện không ghi thay đổi vào CSDL; cần dữ liệu xe và ca mẫu để kiểm tra chọn xe/sửa ca.
