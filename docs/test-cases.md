# Tài liệu Kịch bản Kiểm thử: CarCareBooking

| Mã TC | Module | Tiêu đề kịch bản | Dữ liệu đầu vào / Điều kiện | Kết quả mong đợi |
|---|---|---|---|---|
| **TC-01** | Auth | Đăng nhập tài khoản hợp lệ | Username/Password chính xác | Đăng nhập thành công, chuyển hướng đến Dashboard theo vai trò |
| **TC-02** | Auth | Đăng nhập sai mật khẩu | Username đúng, Password sai | Báo lỗi đăng nhập không thành công, giữ lại username |
| **TC-03** | Booking | Đặt lịch chăm sóc xe | Ngày hẹn, giờ hẹn hợp lệ, chọn dịch vụ | Tạo lịch đặt ở trạng thái `PENDING`, sinh mã đặt lịch duy nhất |
| **TC-04** | Booking | Đặt lịch vào giờ đã kín khoang | Khung giờ không còn khoang trống | Hệ thống cảnh báo khung giờ đã hết chỗ, gợi ý khung giờ khác |
| **TC-05** | Dispatch | Điều phối kỹ thuật viên & khoang | Chọn khoang và thợ có kỹ năng phù hợp | Cập nhật khoang sang `OCCUPIED`, thợ sang `BUSY`, sinh JobCard |
| **TC-06** | Inspection | Kiểm tra tiếp nhận xe ban đầu | Số km, tình trạng thân vỏ xe, tải ảnh | Lưu biên bản kiểm tra xe kèm danh sách ảnh hiện trạng |
| **TC-07** | Additional | Đề xuất dịch vụ phát sinh | Thêm dịch vụ ngoài lịch hẹn kèm lý do | Trạng thái yêu cầu là `PENDING`, chờ khách hàng đồng ý |
| **TC-08** | QC | Kiểm tra chất lượng hoàn thiện | Tiêu chí đánh giá, trạng thái `PASSED` | Chuyển trạng thái Job sang hoàn thành, sẵn sàng lập hóa đơn |
| **TC-09** | Billing | Lập hóa đơn và tính tiền | Tổng tiền dịch vụ chính + phát sinh | Tính chính xác thuế VAT, giảm giá và tổng tiền phải trả |
| **TC-10** | Review | Khách hàng đánh giá dịch vụ | Xếp hạng 1-5 sao, nhận xét | Ghi nhận phản hồi, cập nhật điểm đánh giá trung bình |
