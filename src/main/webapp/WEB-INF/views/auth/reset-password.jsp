<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt lại mật khẩu - CarCareBooking</title>
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
        <p class="mb-0 text-white-50 small">Đặt lại mật khẩu</p>
    </div>

    <div class="p-4">
        <c:choose>
            <c:when test="${tokenInvalid}">
                <div class="alert alert-danger py-2 px-3 mb-3" role="alert">
                    <i class="bi bi-x-circle-fill me-1"></i>
                    <span class="small">Liên kết đặt lại mật khẩu không hợp lệ, đã được sử dụng hoặc đã hết hạn.</span>
                </div>
                <div class="d-grid">
                    <a href="${pageContext.request.contextPath}/auth?action=forgot" class="btn btn-primary fw-semibold">
                        Yêu cầu liên kết mới
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger d-flex align-items-center py-2 px-3 mb-3" role="alert">
                        <i class="bi bi-exclamation-triangle-fill me-2 fs-5"></i>
                        <div class="small"><c:out value="${errorMessage}" /></div>
                    </div>
                </c:if>
                <form action="${pageContext.request.contextPath}/auth" method="post" autocomplete="off">
                    <input type="hidden" name="action" value="reset">
                    <input type="hidden" name="token" value="<c:out value='${token}' />">
                    <div class="mb-3">
                        <label for="password" class="form-label fw-semibold small text-secondary">Mật khẩu mới</label>
                        <input type="password" class="form-control" id="password" name="password"
                               placeholder="Tối thiểu 8 ký tự, có chữ và số" maxlength="72" required autofocus>
                    </div>
                    <div class="mb-3">
                        <label for="confirmPassword" class="form-label fw-semibold small text-secondary">Xác nhận mật khẩu mới</label>
                        <input type="password" class="form-control" id="confirmPassword" name="confirmPassword"
                               placeholder="Nhập lại mật khẩu mới" maxlength="72" required>
                    </div>
                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary py-2 fw-semibold">
                            <i class="bi bi-key me-1"></i> Đặt lại mật khẩu
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
