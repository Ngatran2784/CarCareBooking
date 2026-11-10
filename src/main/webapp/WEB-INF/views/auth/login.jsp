<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập hệ thống - CarCareBooking</title>
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
        .login-card {
            max-width: 440px;
            width: 100%;
            background: #ffffff;
            border-radius: 12px;
            box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.3), 0 8px 10px -6px rgba(0, 0, 0, 0.2);
            overflow: hidden;
        }
        .login-header {
            background-color: #0d6efd;
            color: #ffffff;
            padding: 2rem 1.5rem;
            text-align: center;
        }
        .demo-account-badge {
            cursor: pointer;
            transition: all 0.2s;
        }
        .demo-account-badge:hover {
            opacity: 0.85;
            transform: translateY(-1px);
        }
    </style>
</head>
<body>

<div class="login-card">
    <div class="login-header">
        <div class="d-flex justify-content-center align-items-center gap-2 mb-2">
            <i class="bi bi-car-front-fill fs-2"></i>
            <h3 class="fw-bold mb-0">CarCareBooking</h3>
        </div>
        <p class="mb-0 text-white-50 small">Hệ thống Quản lý Đặt lịch & Điều phối Dịch vụ Chăm sóc Xe</p>
    </div>

    <div class="p-4">
        <!-- Thông báo lỗi đăng nhập -->
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger d-flex align-items-center py-2 px-3 mb-3" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2 fs-5"></i>
                <div class="small"><c:out value="${errorMessage}" /></div>
            </div>
        </c:if>

        <!-- Thông báo đăng xuất thành công -->
        <c:if test="${param.logout == 'success'}">
            <div class="alert alert-success d-flex align-items-center py-2 px-3 mb-3" role="alert">
                <i class="bi bi-check-circle-fill me-2 fs-5"></i>
                <div class="small">Đăng xuất thành công. Phiên làm việc đã kết thúc an toàn.</div>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/auth" method="post" autocomplete="off">
            <div class="mb-3">
                <label for="username" class="form-label fw-semibold small text-secondary">Tên đăng nhập hoặc Email</label>
                <div class="input-group">
                    <span class="input-group-text bg-light"><i class="bi bi-person"></i></span>
                    <input type="text" class="form-control" id="username" name="username" 
                           placeholder="Nhập tên đăng nhập hoặc email" 
                           value="<c:out value='${username}' />" required autofocus>
                </div>
            </div>

            <div class="mb-3">
                <div class="d-flex justify-content-between align-items-center">
                    <label for="password" class="form-label fw-semibold small text-secondary">Mật khẩu</label>
                </div>
                <div class="input-group">
                    <span class="input-group-text bg-light"><i class="bi bi-lock"></i></span>
                    <input type="password" class="form-control" id="password" name="password" 
                           placeholder="Nhập mật khẩu" required>
                </div>
            </div>

            <div class="d-grid mt-4">
                <button type="submit" class="btn btn-primary py-2 fw-semibold">
                    <i class="bi bi-box-arrow-in-right me-1"></i> Đăng nhập
                </button>
            </div>
        </form>

        <div class="text-center mt-3">
            <span class="small text-muted">Chưa có tài khoản khách hàng? </span>
            <a href="${pageContext.request.contextPath}/auth?action=register" class="small text-decoration-none fw-semibold">Đăng ký ngay</a>
        </div>

        <!-- Bảng thông tin tài khoản mẫu môi trường phát triển -->
        <div class="mt-4 pt-3 border-top">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <span class="text-muted small fw-semibold"><i class="bi bi-key me-1"></i>Tài khoản kiểm thử:</span>
            </div>
            <div class="d-flex flex-wrap gap-1">
                <span class="badge bg-danger-subtle text-danger border demo-account-badge" onclick="fillAccount('admin', 'Admin@123')">Admin</span>
                <span class="badge bg-primary-subtle text-primary border demo-account-badge" onclick="fillAccount('manager', 'Manager@123')">Manager</span>
                <span class="badge bg-info-subtle text-info border demo-account-badge" onclick="fillAccount('receptionist', 'Recept@123')">Receptionist</span>
                <span class="badge bg-warning-subtle text-warning border demo-account-badge" onclick="fillAccount('technician', 'Tech@123')">Technician</span>
                <span class="badge bg-success-subtle text-success border demo-account-badge" onclick="fillAccount('customer', 'Cust@123')">Customer</span>
                <span class="badge bg-secondary-subtle text-secondary border demo-account-badge" onclick="fillAccount('locked_user', 'Locked@123')">Locked</span>
            </div>
        </div>
    </div>
</div>

<script>
function fillAccount(username, password) {
    document.getElementById('username').value = username;
    document.getElementById('password').value = password;
}
</script>

<!-- Bootstrap 5 Bundle JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
