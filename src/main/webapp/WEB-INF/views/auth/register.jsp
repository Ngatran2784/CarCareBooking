<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng ký tài khoản - CarCareBooking</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <!-- Custom CSS -->
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 1.5rem;
        }
        .register-card {
            max-width: 520px;
            width: 100%;
            background: #ffffff;
            border-radius: 12px;
            box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.3), 0 8px 10px -6px rgba(0, 0, 0, 0.2);
            overflow: hidden;
        }
        .register-header {
            background-color: #0d6efd;
            color: #ffffff;
            padding: 1.75rem 1.5rem;
            text-align: center;
        }
    </style>
</head>
<body>

<div class="register-card">
    <div class="register-header">
        <div class="d-flex justify-content-center align-items-center gap-2 mb-2">
            <i class="bi bi-car-front-fill fs-2"></i>
            <h3 class="fw-bold mb-0">CarCareBooking</h3>
        </div>
        <p class="mb-0 text-white-50 small">Đăng ký tài khoản khách hàng chăm sóc xe</p>
    </div>

    <div class="p-4">
        <div class="alert alert-info py-2 px-3 mb-3 small d-flex align-items-center" role="alert">
            <i class="bi bi-info-circle-fill me-2 fs-5"></i>
            <div>Cơ sở dữ liệu người dùng và phân quyền đã sẵn sàng. Chức năng tự đăng ký trực tuyến sẽ được mở trong bản phát hành tới. Quý khách có thể sử dụng tài khoản mẫu để trải nghiệm.</div>
        </div>

        <form action="${pageContext.request.contextPath}/auth" method="post" autocomplete="off">
            <div class="mb-3">
                <label for="fullName" class="form-label fw-semibold small text-secondary">Họ và tên</label>
                <input type="text" class="form-control" id="fullName" name="fullName" placeholder="Ví dụ: Nguyễn Văn A" disabled>
            </div>

            <div class="row g-2 mb-3">
                <div class="col-md-6">
                    <label for="username" class="form-label fw-semibold small text-secondary">Tên đăng nhập</label>
                    <input type="text" class="form-control" id="username" name="username" placeholder="nguyenvana" disabled>
                </div>
                <div class="col-md-6">
                    <label for="phone" class="form-label fw-semibold small text-secondary">Số điện thoại</label>
                    <input type="text" class="form-control" id="phone" name="phone" placeholder="0901xxxxxx" disabled>
                </div>
            </div>

            <div class="mb-3">
                <label for="email" class="form-label fw-semibold small text-secondary">Địa chỉ Email</label>
                <input type="email" class="form-control" id="email" name="email" placeholder="email@example.com" disabled>
            </div>

            <div class="mb-3">
                <label for="password" class="form-label fw-semibold small text-secondary">Mật khẩu</label>
                <input type="password" class="form-control" id="password" name="password" placeholder="Tối thiểu 6 ký tự" disabled>
            </div>

            <div class="d-grid mt-4">
                <a href="${pageContext.request.contextPath}/auth?action=login" class="btn btn-primary py-2 fw-semibold">
                    <i class="bi bi-arrow-left me-1"></i> Quay lại trang Đăng nhập
                </a>
            </div>
        </form>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
