# Kế hoạch Sprint (Sprint Plan 1 - 7): CarCareBooking

Hệ thống được phát triển theo mô hình Scrum/Agile gồm 7 Sprint chính. Dưới đây là phân chia phạm vi, module và các file mã nguồn tương ứng:

---

### Sprint 1: Khởi tạo Kiến trúc, Xác thực & Phân quyền (Foundation & Auth)
- **Mục tiêu**: Thiết lập cấu trúc dự án Maven, Jakarta Servlet 6.0, kết nối DBContext, bộ lọc mã hóa UTF-8, xác thực đăng nhập/đăng xuất và trang chủ Dashboard.
- **Mã nguồn liên quan**:
  - `pom.xml`, `web.xml`, `.gitignore`, `README.md`
  - `filter/EncodingFilter.java`, `filter/AuthFilter.java`
  - `dal/DBContext.java`, `dal/UserDAO.java`
  - `model/User.java`, `model/Role.java`
  - `controller/HomeServlet.java`, `controller/AuthServlet.java`, `controller/UserServlet.java`
  - `util/ValidationUtil.java`, `util/DateTimeUtil.java`
  - `views/layout/*`, `views/auth/*`, `views/dashboard/*`, `views/user/*`

---

### Sprint 2: Quản lý Khách hàng, Phương tiện & Dịch vụ (Customer, Vehicle & Services)
- **Mục tiêu**: Xây dựng module quản lý khách hàng, quản lý xe (biển số, đời xe), danh mục dịch vụ và lịch sử giá.
- **Mã nguồn liên quan**:
  - `model/Customer.java`, `model/Vehicle.java`, `model/Service.java`, `model/ServicePriceHistory.java`
  - `dal/CustomerDAO.java`, `dal/VehicleDAO.java`, `dal/ServiceDAO.java`
  - `controller/CustomerServlet.java`, `controller/VehicleServlet.java`, `controller/ServiceServlet.java`
  - `views/customer/*`, `views/vehicle/*`, `views/service/*`

---

### Sprint 3: Quản lý Nhân sự, Ca làm việc & Khoang dịch vụ (Staff, Bay & Shift)
- **Mục tiêu**: Quản lý hồ sơ kỹ thuật viên, kỹ năng tay nghề, lịch phân ca và trạng thái khoang/cầu dịch vụ.
- **Mã nguồn liên quan**:
  - `model/Staff.java`, `model/TechnicianSkill.java`, `model/WorkShift.java`, `model/ServiceBay.java`
  - `dal/StaffDAO.java`, `dal/ServiceBayDAO.java`
  - `controller/StaffServlet.java`, `controller/ServiceBayServlet.java`
  - `views/staff/*`, `views/bay/*`

---

### Sprint 4: Đặt lịch trực tuyến & Tiếp nhận xe (Booking & Check-in)
- **Mục tiêu**: Đặt lịch chọn khung giờ và dịch vụ, theo dõi lịch sử trạng thái đặt lịch, quy trình check-in tiếp nhận xe tại xưởng.
- **Mã nguồn liên quan**:
  - `model/Booking.java`, `model/BookingService.java`, `model/BookingHistory.java`
  - `dal/BookingDAO.java`, `dal/BookingHistoryDAO.java`
  - `service/BookingServiceManager.java`
  - `controller/BookingServlet.java`, `controller/BookingHistoryServlet.java`, `controller/CheckInServlet.java`
  - `views/booking/*`, `assets/js/booking.js`

---

### Sprint 5: Điều phối Khoang & Quản lý Thẻ công việc (Dispatch & Job Card)
- **Mục tiêu**: Bảng điều phối phân công kỹ thuật viên và khoang thực hiện, tạo thẻ công việc (Job Card), kiểm tra hiện trạng xe và upload hình ảnh.
- **Mã nguồn liên quan**:
  - `model/Assignment.java`, `model/JobCard.java`, `model/VehicleInspection.java`, `model/InspectionPhoto.java`
  - `dal/DispatchDAO.java`, `dal/JobDAO.java`, `dal/VehicleInspectionDAO.java`
  - `service/DispatchService.java`
  - `util/FileUploadUtil.java`
  - `controller/DispatchServlet.java`, `controller/JobServlet.java`, `controller/VehicleInspectionServlet.java`
  - `views/dispatch/*`, `views/job/list.jsp`, `views/job/detail.jsp`, `views/job/inspection.jsp`

---

### Sprint 6: Dịch vụ phát sinh, Kiểm tra chất lượng & Nghiệm thu (Additional Service & Quality Check)
- **Mục tiêu**: Xử lý yêu cầu phát sinh cần phê duyệt của khách hàng, quy trình kiểm định chất lượng (QC) trước khi xuất xưởng.
- **Mã nguồn liên quan**:
  - `model/AdditionalServiceRequest.java`, `model/QualityCheck.java`
  - `dal/AdditionalServiceDAO.java`, `dal/QualityCheckDAO.java`
  - `service/AdditionalServiceManager.java`
  - `controller/AdditionalServiceServlet.java`, `controller/QualityCheckServlet.java`
  - `views/job/additional-service.jsp`, `views/job/quality-check.jsp`

---

### Sprint 7: Hóa đơn, Thanh toán, Đánh giá & Báo cáo (Billing, Review & Analytics)
- **Mục tiêu**: Xuất hóa đơn, tính giảm giá/VAT, ghi nhận thanh toán đa phương thức, thu thập đánh giá của khách hàng và báo cáo thống kê quản trị.
- **Mã nguồn liên quan**:
  - `model/Invoice.java`, `model/Payment.java`, `model/Review.java`, `model/Notification.java`
  - `dal/InvoiceDAO.java`, `dal/PaymentDAO.java`, `dal/ReviewDAO.java`, `dal/ReportDAO.java`
  - `service/InvoiceService.java`
  - `controller/InvoiceServlet.java`, `controller/PaymentServlet.java`, `controller/ReviewServlet.java`, `controller/NotificationServlet.java`, `controller/ReportServlet.java`
  - `views/invoice/*`, `views/review/*`, `views/report/*`
