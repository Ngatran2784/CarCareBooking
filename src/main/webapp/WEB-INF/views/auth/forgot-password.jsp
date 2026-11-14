<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quên mật khẩu - CarCareBooking</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
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
        .auth-card {
            max-width: 480px;
            width: 100%;
            background: #ffffff;
            border-radius: 12px;
            box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.3), 0 8px 10px -6px rgba(0, 0, 0, 0.2);
            overflow: hidden;
        }
        .auth-header {
            background-color: #0d6efd;
            color: #ffffff;
            padding: 1.75rem 1.5rem;
            text-align: center;
        }
    </style>
</head>
<body>

<div class="auth-card">
    <div class="auth-header">
        <div class="d-flex justify-content-center align-items-center gap-2 mb-2">
            <i class="bi bi-car-front-fill fs-2"></i>
            <h3 class="fw-bold mb-0">CarCareBooking</h3>
        </div>
        <p class="mb-0 text-white-50 small">Quên mật khẩu</p>
    </div>

    <div class="p-4">
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger d-flex align-items-center py-2 px-3 mb-3" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2 fs-5"></i>
                <div class="small"><c:out value="${errorMessage}" /></div>
            </div>
        </c:if>

        <c:choose>
            <c:when test="${sent}">
                <div class="alert alert-success py-2 px-3 mb-3" role="alert">
                    <i class="bi bi-check-circle-fill me-1"></i>
                    <span class="small">Nếu email tồn tại trong hệ thống, liên kết đặt lại mật khẩu đã được tạo.
                    Liên kết chỉ dùng được <strong>một lần</strong> và hết hạn sau <strong><c:out value="${ttlMinutes}" /> phút</strong>.</span>
                </div>
                <c:if test="${not empty devResetLink}">
                    <div class="alert alert-warning py-2 px-3 mb-3 small" role="alert">
                        <div class="fw-semibold mb-1"><i class="bi bi-tools me-1"></i>Môi trường phát triển (chưa cấu hình gửi email):</div>
                        <a id="devResetLink" href="<c:out value='${devResetLink}' />" class="text-break"><c:out value="${devResetLink}" /></a>
                    </div>
                </c:if>
            </c:when>
            <c:otherwise>
                <p class="small text-muted">Nhập email đã đăng ký. Chúng tôi sẽ tạo liên kết đặt lại mật khẩu có thời hạn.</p>
                <form action="${pageContext.request.contextPath}/auth" method="post" autocomplete="off">
                    <input type="hidden" name="action" value="forgot">
                    <div class="mb-3">
                        <label for="email" class="form-label fw-semibold small text-secondary">Địa chỉ Email</label>
                        <div class="input-group">
                            <span class="input-group-text bg-light"><i class="bi bi-envelope"></i></span>
                            <input type="email" class="form-control" id="email" name="email" placeholder="email@example.com"
                                   maxlength="100" value="<c:out value='${email}' />" required autofocus>
                        </div>
                    </div>
                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary py-2 fw-semibold">
                            <i class="bi bi-send me-1"></i> Gửi yêu cầu đặt lại mật khẩu
                        </button>
                    </div>
                </form>
            </c:otherwise>
        </c:choose>

        <div class="text-center mt-3">
            <a href="${pageContext.request.contextPath}/auth?action=login" class="small text-decoration-none">
                <i class="bi bi-arrow-left me-1"></i>Quay lại đăng nhập
            </a>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
