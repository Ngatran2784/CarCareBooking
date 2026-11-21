# Tài liệu Kịch bản Kiểm thử Toàn diện: CarCareBooking

Tài liệu này bao gồm danh mục kiểm thử chuẩn hóa cho toàn dự án và đặc tả chi tiết các bộ kịch bản kiểm thử (Test Cases) phục vụ nghiệm thu Sprint 1: **Xác thực, Phân quyền & Nền tảng (Foundation & Auth)**.

---

## 1. Kịch bản Kiểm thử Chi tiết Sprint 1 (Foundation & Auth)

### 1.1. Nhóm Kịch bản Đăng nhập & Xác thực (Authentication)

| Mã TC | Tiêu đề kịch bản | Điều kiện đầu vào | Các bước thực hiện | Kết quả mong đợi |
|---|---|---|---|---|
| **AUTH-01** | Đăng nhập hợp lệ với tài khoản Quản trị viên (`admin`) | Tài khoản `admin` tồn tại, mật khẩu `Admin@123`, trạng thái `ACTIVE` | 1. Truy cập `/auth?action=login`<br>2. Nhập username `admin`, password `Admin@123`<br>3. Bấm "Đăng nhập" | Đăng nhập thành công, chuyển hướng đến `/carcare/home`, Header hiển thị "Quản Trị Viên Hệ Thống" và vai trò `ADMIN`. |
| **AUTH-02** | Đăng nhập hợp lệ bằng địa chỉ email (`manager@carcare.vn`) | Tài khoản `manager` có email `manager@carcare.vn`, password `Manager@123` | 1. Nhập username/email `manager@carcare.vn`, pass `Manager@123`<br>2. Bấm "Đăng nhập" | Đăng nhập thành công, hệ thống nhận diện đúng `manager` và chuyển hướng về Dashboard vai trò `MANAGER`. |
| **AUTH-03** | Đăng nhập sai mật khẩu | Tài khoản `admin` tồn tại, mật khẩu nhập sai `WrongPass@999` | 1. Nhập username `admin`, password `WrongPass@999`<br>2. Bấm "Đăng nhập" | Từ chối đăng nhập, hiển thị thông báo lỗi "Tên đăng nhập hoặc mật khẩu không chính xác", giữ lại giá trị username trên form. |
| **AUTH-04** | Đăng nhập với tài khoản không tồn tại | Tài khoản `unknown_user_99` không có trong CSDL | 1. Nhập username `unknown_user_99`, pass bất kỳ<br>2. Bấm "Đăng nhập" | Từ chối đăng nhập, hiển thị thông báo lỗi bảo mật chung, không làm lộ thông tin tài khoản. |
| **AUTH-05** | Đăng nhập tài khoản ở trạng thái bị khóa (`LOCKED`) | Tài khoản `locked_user`, pass `Locked@123`, status `LOCKED` trong DB | 1. Nhập username `locked_user`, pass `Locked@123`<br>2. Bấm "Đăng nhập" | Chặn đăng nhập, thông báo "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ ban quản trị để được hỗ trợ." |
| **AUTH-06** | Xác thực mật khẩu băm chuẩn BCrypt | Tài khoản có `password_hash` sinh bởi `BCrypt.hashpw` | 1. Kiểm tra xác thực qua `BCrypt.checkpw`<br>2. Kiểm tra độ phân kỳ của salt | Mật khẩu thô không bao giờ lưu trong CSDL; hai lần băm cùng mật khẩu cho hai chuỗi hash khác nhau nhưng đều xác thực thành công. |

---

### 1.2. Nhóm Kịch bản Quản lý Phiên làm việc (Session Management)

| Mã TC | Tiêu đề kịch bản | Điều kiện đầu vào | Các bước thực hiện | Kết quả mong đợi |
|---|---|---|---|---|
| **SESS-01** | Tạo session hợp lệ khi đăng nhập thành công | Trình duyệt chưa có session | 1. Đăng nhập thành công với tài khoản bất kỳ | Tạo mới `HttpSession`, lưu đối tượng `User` vào attribute `currentUser`, cấp cookie `JSESSIONID` hợp lệ. |
| **SESS-02** | Hủy session hoàn toàn khi Đăng xuất | Đang trong phiên đăng nhập hợp lệ | 1. Bấm nút "Đăng xuất" (gửi GET/POST `/auth?action=logout`) | Hủy phiên (`session.invalidate()`), xóa attribute `currentUser`, chuyển hướng về `/login?logout=success`. |
| **SESS-03** | Chống tấn công Session Fixation | Session ẩn danh trước khi đăng nhập | 1. Ghi nhận `JSESSIONID` trước đăng nhập<br>2. Thực hiện đăng nhập thành công | Hệ thống gọi `request.changeSessionId()` để sinh ID phiên mới, vô hiệu hóa session fixation. |
| **SESS-04** | Chặn truy cập trang nội bộ khi chưa đăng nhập | Không có session / session đã hết hạn | 1. Mở cửa sổ ẩn danh<br>2. Truy cập thẳng URL `/home`, `/users`, `/bays` | `AuthFilter` chặn yêu cầu, chuyển hướng người dùng về trang `/login` kèm thông báo đăng nhập. |
| **SESS-05** | Hết hạn phiên làm việc (Session Timeout) | Cấu hình `session-timeout` 60 phút trong `web.xml` | 1. Không gửi request trong thời gian timeout<br>2. Gửi request bất kỳ | Session tự hủy; request tiếp theo bị chuyển hướng về `/login`. |

---

### 1.3. Nhóm Kịch bản Kiểm soát Phân quyền (Role-Based Access Control - RBAC)

| Mã TC | Tiêu đề kịch bản | Điều kiện đầu vào | Các bước thực hiện | Kết quả mong đợi |
|---|---|---|---|---|
| **RBAC-01** | Quản trị viên (`ADMIN`) truy cập trang quản lý người dùng | Đã đăng nhập tài khoản `admin` | 1. Truy cập trực tiếp URL `/users`<br>2. Kiểm tra mã phản hồi HTTP | HTTP Status **200 OK**, hiển thị danh sách người dùng hệ thống. |
| **RBAC-02** | Khách hàng (`CUSTOMER`) cố tình truy cập trang quản trị `/users` | Đã đăng nhập tài khoản `customer` | 1. Gõ trực tiếp trên thanh địa chỉ URL `/carcare/users` | `AuthFilter` nhận diện người dùng thiếu quyền `ADMIN`/`MANAGER`, trả về mã **HTTP 403 Forbidden** hoặc trang `403.jsp`. |
| **RBAC-03** | Kỹ thuật viên (`TECHNICIAN`) chỉ truy cập chức năng kỹ thuật | Đã đăng nhập tài khoản `technician` | 1. Truy cập `/jobs`, `/inspection`<br>2. Truy cập `/users` | Truy cập được các chức năng kỹ thuật; bị chặn HTTP 403 khi truy cập quản trị người dùng `/users`. |
| **RBAC-04** | Không chặn nhầm các tài nguyên tĩnh công khai | Người dùng ẩn danh chưa đăng nhập | 1. Tải tài nguyên `/assets/css/style.css`, `/assets/js/main.js`, favicon | `AuthFilter` cho phép truy cập trực tiếp, HTTP Status **200 OK**. |

---

### 1.4. Nhóm Kịch bản Đăng ký Khách hàng (Customer Registration)

| Mã TC | Tiêu đề kịch bản | Điều kiện đầu vào | Các bước thực hiện | Kết quả mong đợi |
|---|---|---|---|---|
| **REG-01** | Đăng ký tài khoản khách hàng thành công | Thông tin hợp lệ, username/email/SĐT chưa từng tồn tại | 1. Vào `/auth?action=register`<br>2. Nhập họ tên, username, password >= 8 ký tự, SĐT, email hợp lệ<br>3. Bấm "Đăng ký" | 1. Tạo bản ghi `users` (mật khẩu băm BCrypt)<br>2. Gán vai trò `CUSTOMER` trong `user_roles`<br>3. Tạo hồ sơ `customers` sinh mã `CUST-xxxx`<br>4. Chuyển hướng về trang đăng nhập với thông báo thành công. |
| **REG-02** | Đăng ký với tên đăng nhập (Username) đã tồn tại | Username `admin` đã có trong hệ thống | 1. Nhập username `admin`, các thông tin khác hợp lệ<br>2. Bấm "Đăng ký" | Báo lỗi "Tên đăng nhập đã được sử dụng", không tạo dữ liệu mới. |
| **REG-03** | Đăng ký với Email đã tồn tại | Email `admin@carcare.vn` đã có trong hệ thống | 1. Nhập email `admin@carcare.vn`<br>2. Bấm "Đăng ký" | Báo lỗi "Email đã được đăng ký", không tạo dữ liệu mới. |
| **REG-04** | Đăng ký với Số điện thoại đã tồn tại | SĐT `0901000001` đã có trong hệ thống | 1. Nhập SĐT `0901000001`<br>2. Bấm "Đăng ký" | Báo lỗi "Số điện thoại đã được đăng ký", không tạo dữ liệu mới. |
| **REG-05** | Kiểm tra tính toàn vẹn Transaction khi Đăng ký lỗi | Dữ liệu đầu vào vi phạm ràng buộc tại bước tạo hồ sơ | 1. Giả lập lỗi ở bước chèn `customers` | Transaction tự động Rollback, không tạo `users` mồ côi hoặc `user_roles` rác. |
| **REG-06** | Kiểm tra xác thực định dạng dữ liệu đầu vào (Validation) | Dữ liệu sai: username có khoảng trắng, pass < 8 ký tự, email thiếu `@`, SĐT không đúng định dạng VN | 1. Nhập dữ liệu không đúng quy cách<br>2. Bấm "Đăng ký" | Form hiển thị thông báo lỗi chi tiết tương ứng từng trường, không gửi truy vấn ghi DB. |

---

### 1.5. Nhóm Kịch bản Quên & Đặt lại Mật khẩu (Password Reset)

| Mã TC | Tiêu đề kịch bản | Điều kiện đầu vào | Các bước thực hiện | Kết quả mong đợi |
|---|---|---|---|---|
| **RESET-01** | Yêu cầu đặt lại mật khẩu với email hợp lệ | Email `admin@carcare.vn` tồn tại | 1. Truy cập `/auth?action=forgot`<br>2. Nhập email `admin@carcare.vn`<br>3. Bấm "Gửi yêu cầu" | 1. Sinh token ngẫu nhiên 32-byte an toàn<br>2. Lưu bản băm SHA-256 vào `password_reset_tokens`<br>3. Hiển thị thông báo hướng dẫn chung<br>4. Ở môi trường local hiển thị link đặt lại cho người phát triển. |
| **RESET-02** | Yêu cầu đặt lại mật khẩu với email không tồn tại | Email `notfound@example.com` không có trong DB | 1. Nhập email `notfound@example.com`<br>2. Bấm "Gửi yêu cầu" | Hiển thị thông báo thành công chung (Generic Message) để chống dò quét tài khoản (User Enumeration). |
| **RESET-03** | Đặt lại mật khẩu thành công bằng Token hợp lệ | Token mới sinh, chưa hết hạn, chưa sử dụng | 1. Mở liên kết `/auth?action=reset&token=...`<br>2. Nhập mật khẩu mới hợp lệ và xác nhận<br>3. Bấm "Cập nhật mật khẩu" | Mật khẩu mới được băm BCrypt và cập nhật vào `users`; token được đánh dấu `used_at = NOW()`; chuyển về trang đăng nhập; đăng nhập được bằng mật khẩu mới. |
| **RESET-04** | Đặt lại mật khẩu với Token đã sử dụng (Single-Use Token) | Token đã dùng thành công ở RESET-03 | 1. Sử dụng lại liên kết token cũ để đổi mật khẩu | Hệ thống từ chối yêu cầu, hiển thị cảnh báo "Liên kết đặt lại mật khẩu không hợp lệ hoặc đã được sử dụng". |
| **RESET-05** | Đặt lại mật khẩu với Token hết hạn (Expired Token) | Token sinh quá 30 phút (`expires_at < NOW()`) | 1. Truy cập liên kết chứa token đã hết hạn | Hệ thống từ chối yêu cầu, hiển thị thông báo liên kết đã hết hạn. |
| **RESET-06** | Sinh token mới vô hiệu hóa token cũ | Người dùng gửi 2 yêu cầu reset liên tiếp | 1. Gửi yêu cầu 1 sinh Token A<br>2. Gửi tiếp yêu cầu 2 sinh Token B<br>3. Thử dùng Token A | Token A lập tức bị vô hiệu hóa; chỉ Token B có hiệu lực. |

---

## 2. Danh mục Kịch bản Kiểm thử Tổng thể Các Sprint Tiếp theo (Sprints 2 - 7)

| Mã TC | Sprint | Module | Tiêu đề kịch bản | Dữ liệu đầu vào / Điều kiện | Kết quả mong đợi |
|---|---|---|---|---|---|
| **TC-03** | Sprint 2 | Customer & Vehicle | Thêm mới phương tiện cho khách hàng | Biển số xe, dòng xe, năm sản xuất | Lưu phương tiện thành công, gắn đúng `customer_id`. |
| **TC-04** | Sprint 2 | Service Catalog | Quản lý danh mục & cập nhật giá dịch vụ | Dịch vụ, đơn giá mới, ngày áp dụng | Lưu lịch sử giá vào `service_price_history`, cập nhật giá hiện tại. |
| **TC-05** | Sprint 3 | Staff & Shift | Phân ca làm việc cho kỹ thuật viên | Nhân viên, ca làm việc, ngày làm | Ghi nhận lịch trực, kiểm tra không trùng ca. |
| **TC-06** | Sprint 3 | Service Bay | Cập nhật trạng thái khoang dịch vụ | Khoang ID, trạng thái `AVAILABLE`/`OCCUPIED` | Cập nhật tức thời trạng thái khoang bãi trên sơ đồ. |
| **TC-07** | Sprint 4 | Booking | Đặt lịch chăm sóc xe trực tuyến | Ngày giờ hẹn hợp lệ, chọn dịch vụ | Tạo lịch đặt `PENDING`, sinh mã đặt lịch `BK-xxxx`. |
| **TC-08** | Sprint 4 | Booking | Đặt lịch vào khung giờ đã kín chỗ | Khung giờ không còn khoang trống | Cảnh báo hết chỗ, đề xuất khung giờ khả dụng tiếp theo. |
| **TC-09** | Sprint 4 | Check-in | Tiếp nhận xe tại xưởng | Xe đã đặt lịch đến xưởng | Chuyển trạng thái booking sang `CHECKED_IN`, chuẩn bị điều phối. |
| **TC-10** | Sprint 5 | Dispatch | Điều phối khoang & kỹ thuật viên | Chọn khoang và thợ phù hợp tay nghề | Sinh `job_cards`, khoang chuyển `OCCUPIED`, thợ chuyển `BUSY`. |
| **TC-11** | Sprint 5 | Inspection | Biên bản kiểm tra hiện trạng xe ban đầu | Số km, trầy xước thân vỏ, upload ảnh | Lưu biên bản kiểm tra kèm danh sách ảnh hiện trạng. |
| **TC-12** | Sprint 6 | Additional | Đề xuất dịch vụ phát sinh ngoài lịch | Phát hiện lỗi cần xử lý kèm chi phí dự tính | Tạo yêu cầu `PENDING`, chờ khách hàng xác nhận. |
| **TC-13** | Sprint 6 | Quality Check | Kiểm định chất lượng xuất xưởng (QC) | Tiêu chí đánh giá, trạng thái `PASSED` | Chuyển Job sang hoàn thành, sẵn sàng xuất hóa đơn. |
| **TC-14** | Sprint 7 | Invoicing | Lập hóa đơn và tính chiết khấu / thuế | Tiền dịch vụ + phát sinh, VAT, voucher | Tính chính xác tổng tiền, VAT và giảm trừ thanh toán. |
| **TC-15** | Sprint 7 | Payment | Ghi nhận thanh toán đa phương thức | Tiền mặt / Chuyển khoản / Thẻ / Ví | Cập nhật hóa đơn `PAID`, lưu vết giao dịch vào `payments`. |
| **TC-16** | Sprint 7 | Review | Đánh giá chất lượng và phản hồi | Điểm sao 1-5, nhận xét | Ghi nhận đánh giá, cập nhật điểm trung bình của cơ sở. |
| **TC-17** | Sprint 7 | Reporting | Báo cáo doanh thu & hiệu suất khoang | Khoảng thời gian thống kê | Biểu đồ doanh thu, tỷ lệ lấp đầy khoang bãi chính xác. |
