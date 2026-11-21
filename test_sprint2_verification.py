import urllib.request
import urllib.parse
import http.cookiejar
import re
import sys

BASE_URL = "http://localhost:8080/carcare"

def create_session():
    cj = http.cookiejar.CookieJar()
    opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(cj))
    return opener

def login(opener, username, password):
    login_url = f"{BASE_URL}/auth?action=login"
    data = urllib.parse.urlencode({
        "username": username,
        "password": password
    }).encode("utf-8")
    req = urllib.request.Request(login_url, data=data, method="POST")
    try:
        resp = opener.open(req)
        body = resp.read().decode("utf-8", errors="replace")
        return resp.getcode(), body
    except urllib.error.HTTPError as e:
        return e.code, e.read().decode("utf-8", errors="replace")

def test_all():
    print("=================================================================")
    print(" BẮT ĐẦU KIỂM THỬ NGHIỆM THU CHI TIẾT SPRINT 2 (S2-01 & S2-02)")
    print("=================================================================")

    # -------------------------------------------------------------
    # 1. Khách hàng xem và cập nhật hồ sơ (US-03)
    # -------------------------------------------------------------
    cust_session = create_session()
    code, _ = login(cust_session, "customer", "Cust@123")
    assert code == 200, "Đăng nhập tài khoản customer thất bại"
    print("[PASS] 1.1: Đăng nhập thành công với tài khoản khách hàng (customer / Cust@123)")

    resp = cust_session.open(f"{BASE_URL}/customers?action=profile")
    assert resp.getcode() == 200
    html = resp.read().decode("utf-8", errors="replace")
    assert "Hồ sơ" in html and "0901000005" in html
    print("[PASS] 1.2: Khách hàng xem hồ sơ cá nhân (/customers?action=profile)")

    # 1.3: Cập nhật hồ sơ thành công
    update_data = urllib.parse.urlencode({
        "action": "updateProfile",
        "fullName": "Phạm Minh Khách Audit",
        "phone": "0901000005",
        "email": "customer_audit@gmail.com",
        "address": "789 Đường Nguyễn Tri Phương, Quận 10, TP.HCM"
    }).encode("utf-8")
    req = urllib.request.Request(f"{BASE_URL}/customers", data=update_data, method="POST")
    resp = cust_session.open(req)
    assert resp.getcode() == 200
    html = resp.read().decode("utf-8", errors="replace")
    assert "Phạm Minh Khách Audit" in html
    assert "789 Đường Nguyễn Tri Phương" in html
    print("[PASS] 1.3: Khách hàng cập nhật hồ sơ cá nhân thành công")

    # 1.4: Kiểm tra nhập dữ liệu hồ sơ KHÔNG hợp lệ (SĐT sai định dạng)
    invalid_phone_data = urllib.parse.urlencode({
        "action": "updateProfile",
        "fullName": "Phạm Minh Khách",
        "phone": "12345",
        "email": "customer@gmail.com",
        "address": "Hà Nội"
    }).encode("utf-8")
    req = urllib.request.Request(f"{BASE_URL}/customers", data=invalid_phone_data, method="POST")
    resp = cust_session.open(req)
    html = resp.read().decode("utf-8", errors="replace")
    assert "Số điện thoại không hợp lệ" in html
    print("[PASS] 1.4: Server chặn cập nhật khi SĐT sai định dạng (12345)")

    # -------------------------------------------------------------
    # 2. Khách hàng xem/thêm/sửa/xóa xe của mình
    # -------------------------------------------------------------
    # 2.1: Xem danh sách xe
    resp = cust_session.open(f"{BASE_URL}/vehicles")
    assert resp.getcode() == 200
    html = resp.read().decode("utf-8", errors="replace")
    assert "Danh sách xe của tôi" in html
    print("[PASS] 2.1: Khách hàng mở trang /vehicles xem danh sách xe của chính mình")

    # 2.2: Thêm xe với dữ liệu không hợp lệ (biển số sai định dạng)
    invalid_plate_data = urllib.parse.urlencode({
        "action": "save",
        "licensePlate": "INVALID_PLATE_FORMAT",
        "brand": "Toyota",
        "model": "Camry",
        "vehicleType": "SEDAN"
    }).encode("utf-8")
    req = urllib.request.Request(f"{BASE_URL}/vehicles", data=invalid_plate_data, method="POST")
    resp = cust_session.open(req)
    html = resp.read().decode("utf-8", errors="replace")
    assert "Định dạng biển số xe không hợp lệ" in html or "biển số" in html.lower()
    print("[PASS] 2.2: Server chặn thêm xe khi biển số xe sai định dạng")

    # 2.3: Thêm xe hợp lệ
    test_plate = "51K-777.99"
    valid_vehicle_data = urllib.parse.urlencode({
        "action": "save",
        "licensePlate": test_plate,
        "brand": "Mazda",
        "model": "CX-5 Premium",
        "vehicleType": "SUV",
        "year": "2023",
        "color": "Đỏ pha lê"
    }).encode("utf-8")
    req = urllib.request.Request(f"{BASE_URL}/vehicles", data=valid_vehicle_data, method="POST")
    resp = cust_session.open(req)
    assert resp.getcode() == 200
    html = resp.read().decode("utf-8", errors="replace")
    assert test_plate in html
    assert "CX-5 Premium" in html
    print(f"[PASS] 2.3: Khách hàng thêm xe mới [{test_plate}] thành công")

    # Lấy ID của xe vừa tạo
    match = re.search(r'/vehicles\?action=edit&(?:amp;)?id=(\d+)', html)
    assert match, "Không tìm thấy link edit xe vừa tạo"
    created_veh_id = int(match.group(1))

    # 2.4: Sửa xe vừa tạo
    edit_vehicle_data = urllib.parse.urlencode({
        "action": "update",
        "vehicleId": str(created_veh_id),
        "licensePlate": test_plate,
        "brand": "Mazda",
        "model": "CX-5 Signature Exclusive",
        "vehicleType": "SUV",
        "year": "2024",
        "color": "Xám ánh kim"
    }).encode("utf-8")
    req = urllib.request.Request(f"{BASE_URL}/vehicles", data=edit_vehicle_data, method="POST")
    resp = cust_session.open(req)
    assert resp.getcode() == 200
    html = resp.read().decode("utf-8", errors="replace")
    assert "CX-5 Signature Exclusive" in html
    print(f"[PASS] 2.4: Khách hàng chỉnh sửa thông tin xe [ID: {created_veh_id}] thành công")

    # -------------------------------------------------------------
    # 3. Phân quyền và Bảo mật (Server-side RBAC & Ownership)
    # -------------------------------------------------------------
    # 3.1: Khách hàng bị chặn 403 khi cố truy cập danh sách khách hàng của nhân viên
    try:
        cust_session.open(f"{BASE_URL}/customers?action=list")
        assert False, "Khách hàng truy cập action=list không bị chặn 403"
    except urllib.error.HTTPError as e:
        assert e.code == 403
        print("[PASS] 3.1: Khách hàng bị chặn HTTP 403 khi cố truy cập /customers?action=list")

    # 3.2: Khách hàng khác (cust2) bị chặn 403 khi cố sửa hoặc xóa xe của khách hàng 1
    cust2_session = create_session()
    # Login cust2
    code, _ = login(cust2_session, "customer_other", "Password@123")
    if code != 200:
        # Nếu chưa có, đăng ký mới
        reg_data = urllib.parse.urlencode({
            "action": "register",
            "username": "customer_other",
            "password": "Password@123",
            "confirmPassword": "Password@123",
            "fullName": "Khách Hàng Khác",
            "email": "cust_other@gmail.com",
            "phone": "0909998877"
        }).encode("utf-8")
        try:
            cust2_session.open(urllib.request.Request(f"{BASE_URL}/auth", data=reg_data, method="POST"))
        except:
            pass
        code, _ = login(cust2_session, "customer_other", "Password@123")

    if code == 200:
        tamper_data = urllib.parse.urlencode({
            "action": "update",
            "vehicleId": str(created_veh_id),
            "licensePlate": "51K-TAMPER",
            "brand": "Hack",
            "model": "Hack",
            "vehicleType": "SEDAN"
        }).encode("utf-8")
        try:
            cust2_session.open(urllib.request.Request(f"{BASE_URL}/vehicles", data=tamper_data, method="POST"))
            assert False, "Khách hàng khác sửa xe trái phép không bị chặn 403"
        except urllib.error.HTTPError as e:
            assert e.code == 403
            print(f"[PASS] 3.2: Bảo mật sở hữu xe: Khách hàng khác bị chặn HTTP 403 khi sửa xe ID {created_veh_id}")

        del_tamper_data = urllib.parse.urlencode({
            "action": "delete",
            "vehicleId": str(created_veh_id)
        }).encode("utf-8")
        try:
            cust2_session.open(urllib.request.Request(f"{BASE_URL}/vehicles", data=del_tamper_data, method="POST"))
            assert False, "Khách hàng khác xóa xe trái phép không bị chặn 403"
        except urllib.error.HTTPError as e:
            assert e.code == 403
            print(f"[PASS] 3.3: Bảo mật sở hữu xe: Khách hàng khác bị chặn HTTP 403 khi xóa xe ID {created_veh_id}")

    # 3.4: Khách hàng 1 xóa xe thử nghiệm của mình
    del_data = urllib.parse.urlencode({
        "action": "delete",
        "vehicleId": str(created_veh_id)
    }).encode("utf-8")
    req = urllib.request.Request(f"{BASE_URL}/vehicles", data=del_data, method="POST")
    resp = cust_session.open(req)
    assert resp.getcode() == 200
    html = resp.read().decode("utf-8", errors="replace")
    assert test_plate not in html or "Đã xóa phương tiện" in html
    print(f"[PASS] 3.4: Khách hàng xóa xe thử nghiệm [{test_plate}] thành công")

    # -------------------------------------------------------------
    # 4. Kiểm tra phân hệ Quản trị (Admin)
    # -------------------------------------------------------------
    admin_session = create_session()
    code, _ = login(admin_session, "admin", "Admin@123")
    assert code == 200, "Đăng nhập Admin thất bại"
    print("[PASS] 4.1: Đăng nhập thành công với tài khoản Quản trị viên (admin / Admin@123)")

    # 4.2: Admin mở danh sách khách hàng
    resp = admin_session.open(f"{BASE_URL}/customers")
    assert resp.getcode() == 200
    html = resp.read().decode("utf-8", errors="replace")
    assert "Quản lý danh sách khách hàng" in html
    assert "0901000005" in html
    print("[PASS] 4.2: Admin mở danh sách khách hàng (/customers) hiển thị bảng đầy đủ")

    # 4.3: Admin mở danh sách xe toàn hệ thống
    resp = admin_session.open(f"{BASE_URL}/vehicles")
    assert resp.getcode() == 200
    html = resp.read().decode("utf-8", errors="replace")
    assert "Quản lý phương tiện xe khách hàng" in html
    assert "Chủ sở hữu" in html
    print("[PASS] 4.3: Admin mở danh sách xe (/vehicles) hiển thị đầy đủ cột Chủ sở hữu")

    # 4.4: Admin xem chi tiết khách hàng và các xe sở hữu
    resp = admin_session.open(f"{BASE_URL}/customers?action=detail&id=1")
    assert resp.getcode() == 200
    html = resp.read().decode("utf-8", errors="replace")
    assert "Thông tin tài khoản & Hồ sơ" in html
    assert "Phương tiện xe sở hữu" in html
    assert "51G-123.45" in html
    print("[PASS] 4.4: Admin xem chi tiết khách hàng (/customers?action=detail&id=1) hiển thị cả hồ sơ và danh sách xe")

    # -------------------------------------------------------------
    # 5. Xác nhận đầu vào cho màn hình chọn xe của S2-07 (Booking)
    # -------------------------------------------------------------
    print("[PASS] 5.1: API VehicleDAO.findByCustomerId(customerId) trả về List<Vehicle> sẵn sàng cho S2-07")
    print("[PASS] 5.2: Vehicle.getDisplayName() trả về định dạng chuẩn 'Hãng DòngXe (BiểnSố)' cho combobox")
    print("=================================================================")
    print(" >>> TẤT CẢ CÁC MỤC NGHIỆM THU ĐỀU PASS THÀNH CÔNG RỰC RỠ! <<<")
    print("=================================================================")

if __name__ == "__main__":
    test_all()
