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
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger d-flex align-items-center py-2 px-3 mb-3" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2 fs-5"></i>
                <div class="small"><c:out value="${errorMessage}" /></div>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/auth" method="post" autocomplete="off" novalidate>
            <input type="hidden" name="action" value="register">

            <div class="mb-3">
                <label for="fullName" class="form-label fw-semibold small text-secondary">Họ và tên</label>
                <input type="text" class="form-control ${not empty errors.fullName ? 'is-invalid' : ''}" id="fullName" name="fullName"
                       placeholder="Ví dụ: Nguyễn Văn A" maxlength="100" value="<c:out value='${fullName}' />" required>
                <div class="invalid-feedback"><c:out value="${errors.fullName}" /></div>
            </div>

            <div class="row g-2 mb-3">
                <div class="col-md-6">
                    <label for="username" class="form-label fw-semibold small text-secondary">Tên đăng nhập</label>
                    <input type="text" class="form-control ${not empty errors.username ? 'is-invalid' : ''}" id="username" name="username"
                           placeholder="nguyenvana" maxlength="30" value="<c:out value='${username}' />" required>
                    <div class="invalid-feedback"><c:out value="${errors.username}" /></div>
                </div>
                <div class="col-md-6">
                    <label for="phone" class="form-label fw-semibold small text-secondary">Số điện thoại</label>
                    <input type="text" class="form-control ${not empty errors.phone ? 'is-invalid' : ''}" id="phone" name="phone"
                           placeholder="0901xxxxxx" maxlength="11" value="<c:out value='${phone}' />" required>
                    <div class="invalid-feedback"><c:out value="${errors.phone}" /></div>
                </div>
            </div>

            <div class="mb-3">
                <label for="email" class="form-label fw-semibold small text-secondary">Địa chỉ Email</label>
                <input type="email" class="form-control ${not empty errors.email ? 'is-invalid' : ''}" id="email" name="email"
                       placeholder="email@example.com" maxlength="100" value="<c:out value='${email}' />" required>
                <div class="invalid-feedback"><c:out value="${errors.email}" /></div>
            </div>

            <div class="row g-2 mb-3">
                <div class="col-md-6">
                    <label for="password" class="form-label fw-semibold small text-secondary">Mật khẩu</label>
                    <input type="password" class="form-control ${not empty errors.password ? 'is-invalid' : ''}" id="password" name="password"
                           placeholder="Tối thiểu 8 ký tự, có chữ và số" maxlength="72" required>
                    <div class="invalid-feedback"><c:out value="${errors.password}" /></div>
                </div>
                <div class="col-md-6">
                    <label for="confirmPassword" class="form-label fw-semibold small text-secondary">Xác nhận mật khẩu</label>
                    <input type="password" class="form-control ${not empty errors.confirmPassword ? 'is-invalid' : ''}" id="confirmPassword"
                           name="confirmPassword" placeholder="Nhập lại mật khẩu" maxlength="72" required>
                    <div class="invalid-feedback"><c:out value="${errors.confirmPassword}" /></div>
                </div>
            </div>

            <div class="d-grid mt-4">
                <button type="submit" class="btn btn-primary py-2 fw-semibold">
                    <i class="bi bi-person-plus me-1"></i> Đăng ký
                </button>
            </div>
            <div class="text-center mt-3">
                <a href="${pageContext.request.contextPath}/auth?action=login" class="small text-decoration-none">
                    <i class="bi bi-arrow-left me-1"></i>Đã có tài khoản? Đăng nhập
                </a>
            </div>
        </form>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
