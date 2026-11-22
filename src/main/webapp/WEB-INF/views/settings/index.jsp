<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Cấu hình vận hành"/><%@ include file="../layout/header.jspf" %>
<%@ include file="../layout/sidebar.jspf" %>
<main class="content-wrapper"><div class="container-fluid">

<div class="d-flex justify-content-between align-items-center mb-4"><div><h3 class="fw-bold mb-1">Cấu hình vận hành</h3><p class="text-muted mb-0">Giờ mở cửa, ngày nghỉ và thời gian đệm của xưởng.</p></div><a class="btn btn-outline-primary" href="${ctx}/staff">Ca & Kỹ năng</a></div>
<c:if test="${not empty error}"><div class="alert alert-danger" role="alert"><c:out value="${error}"/></div></c:if>
<c:if test="${not empty success}"><div class="alert alert-success" role="status"><c:out value="${success}"/></div></c:if>
<c:if test="${ready}"><div class="row g-4 mb-4"><div class="col-lg-7"><section class="card h-100"><div class="card-body"><h5 class="mb-3">Giờ hoạt động hằng ngày</h5>
<form method="post" class="row g-3"><input type="hidden" name="csrf" value="${sessionScope.operationsCsrf}"><input type="hidden" name="action" value="hours">
<div class="col-sm-6"><label class="form-label" for="opening">Giờ mở cửa</label><input class="form-control" type="time" id="opening" name="opening" required value="<c:out value='${form.action == "hours" ? form.opening : settings.OPENING_TIME}'/>"></div>
<div class="col-sm-6"><label class="form-label" for="closing">Giờ đóng cửa</label><input class="form-control" type="time" id="closing" name="closing" required value="<c:out value='${form.action == "hours" ? form.closing : settings.CLOSING_TIME}'/>"></div>
<div class="col-sm-6"><label class="form-label" for="buffer">Thời gian đệm (phút)</label><input class="form-control" type="number" id="buffer" name="buffer" min="0" max="240" required value="<c:out value='${form.action == "hours" ? form.buffer : settings.BUFFER_MINUTES}'/>"><div class="form-text">Thời gian dọn khoang giữa hai lượt dịch vụ.</div></div>
<div class="col-sm-6"><label class="form-label" for="interval">Bước khung giờ (phút)</label><input class="form-control" type="number" id="interval" name="interval" min="5" max="120" required value="<c:out value='${form.action == "hours" ? form.interval : settings.SLOT_INTERVAL_MINUTES}'/>"><div class="form-text">Khoảng cách giữa các giờ bắt đầu đặt lịch.</div></div>
<div class="col-12"><div class="alert alert-light border small">Áp dụng cho tất cả các ngày, trừ ngày nghỉ bên dưới. Múi giờ: Việt Nam. Điều chỉnh ca hiện tại/tương lai trước khi thu hẹp giờ mở cửa.</div><button class="btn btn-primary">Lưu cấu hình</button></div></form>
</div></section></div><div class="col-lg-5"><section class="card h-100"><div class="card-body"><h5 class="mb-3">Thêm / cập nhật ngày nghỉ</h5>
<form method="post" class="row g-3"><input type="hidden" name="csrf" value="${sessionScope.operationsCsrf}"><input type="hidden" name="action" value="holiday">
<div class="col-12"><label class="form-label" for="holidaydate">Ngày nghỉ</label><input class="form-control" type="date" id="holidaydate" name="date" required value="<c:out value='${form.action == "holiday" ? form.date : ""}'/>"></div>
<div class="col-12"><label class="form-label" for="holidayname">Tên / lý do nghỉ</label><input class="form-control" id="holidayname" name="name" maxlength="100" required placeholder="Ví dụ: Tết Dương lịch" value="<c:out value='${form.action == "holiday" ? form.name : ""}'/>"></div>
<p class="text-muted small mb-0">Xưởng đóng cửa cả ngày. Nhập lại cùng ngày để cập nhật lý do. Ngày nghỉ không được có ca đang làm việc.</p>
<div class="col-12"><button class="btn btn-primary">Lưu ngày nghỉ</button></div></form>
</div></section></div></div>
<section class="card mb-4"><div class="card-body"><h5 class="mb-3">Lịch nghỉ của xưởng</h5><div class="table-responsive"><table class="table align-middle"><thead><tr><th>Ngày</th><th>Lý do</th><th>Trạng thái</th><th>Thao tác</th></tr></thead><tbody>
<c:forEach var="h" items="${holidays}"><tr><td>${h.holiday_date}</td><td><c:out value="${h.holiday_name}"/></td><td><c:choose><c:when test="${h.is_closed}"><span class="badge bg-danger-subtle text-danger">Đóng cửa</span></c:when><c:otherwise>Mở cửa</c:otherwise></c:choose></td><td><form method="post" class="delete-form"><input type="hidden" name="csrf" value="${sessionScope.operationsCsrf}"><input type="hidden" name="action" value="deleteHoliday"><input type="hidden" name="id" value="${h.holiday_id}"><button class="btn btn-sm btn-outline-danger">Xóa ngày nghỉ</button></form></td></tr></c:forEach>
<c:if test="${empty holidays}"><tr><td colspan="4" class="text-center text-muted py-4">Chưa thiết lập ngày nghỉ.</td></tr></c:if></tbody></table></div></div></section>
<script src="${ctx}/assets/js/operations.js" defer></script></c:if></div></main><%@ include file="../layout/footer.jspf" %>