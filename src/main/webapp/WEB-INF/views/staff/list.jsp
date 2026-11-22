<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Ca & Kỹ năng kỹ thuật viên"/><%@ include file="../layout/header.jspf" %>
<%@ include file="../layout/sidebar.jspf" %>
<main class="content-wrapper"><div class="container-fluid">

<div class="d-flex justify-content-between align-items-center mb-4"><div><h3 class="fw-bold mb-1">Ca & Kỹ năng kỹ thuật viên</h3><p class="text-muted mb-0">Sắp xếp ca làm việc và năng lực theo từng dịch vụ.</p></div><a class="btn btn-outline-primary" href="${ctx}/settings">Cấu hình vận hành</a></div>
<c:if test="${not empty error}"><div class="alert alert-danger" role="alert"><c:out value="${error}"/></div></c:if>
<c:if test="${not empty success}"><div class="alert alert-success" role="status"><c:out value="${success}"/></div></c:if>
<c:if test="${ready}">
<div class="alert alert-light border">Giờ hoạt động: <strong><c:out value="${settings.OPENING_TIME}"/> – <c:out value="${settings.CLOSING_TIME}"/></strong> · Đệm giữa hai lượt dịch vụ: <strong><c:out value="${settings.BUFFER_MINUTES}"/> phút</strong>. Ca không được trùng nhau hoặc rơi vào ngày nghỉ.</div>
<div class="row g-4 mb-4"><div class="col-xl-6"><section class="card h-100"><div class="card-body">
<h5 class="card-title mb-3">Lập / sửa ca làm việc</h5>
<form method="post" id="shiftForm" class="row g-3"><input type="hidden" name="csrf" value="${sessionScope.operationsCsrf}"><input type="hidden" name="action" value="shift"><input type="hidden" name="id" value="0">
<div class="col-12"><label class="form-label" for="shiftstaff">Kỹ thuật viên</label><select class="form-select" id="shiftstaff" name="staffId" required><option value="">Chọn kỹ thuật viên</option><c:forEach var="t" items="${technicians}"><option value="${t.staff_id}"><c:out value="${t.full_name}"/></option></c:forEach></select></div>
<div class="col-12"><label class="form-label" for="shiftdate">Ngày làm việc</label><input class="form-control" id="shiftdate" type="date" name="date" required></div>
<div class="col-6"><label class="form-label" for="shiftstart">Bắt đầu</label><input class="form-control" id="shiftstart" type="time" name="start" required></div>
<div class="col-6"><label class="form-label" for="shiftend">Kết thúc</label><input class="form-control" id="shiftend" type="time" name="end" required></div>
<div class="col-12"><label class="form-label" for="shiftstatus">Trạng thái</label><select class="form-select" id="shiftstatus" name="status"><option value="SCHEDULED">Đã lên lịch</option><option value="ATTENDED">Có mặt</option><option value="ABSENT">Vắng mặt</option></select></div>
<div class="col-12"><button class="btn btn-primary">Lưu ca làm việc</button> <button type="reset" class="btn btn-outline-secondary">Tạo mới</button></div></form>
</div></section></div><div class="col-xl-6"><section class="card h-100"><div class="card-body">
<h5 class="card-title mb-3">Gán / sửa kỹ năng</h5><form method="post" id="skillForm" class="row g-3"><input type="hidden" name="csrf" value="${sessionScope.operationsCsrf}"><input type="hidden" name="action" value="skill"><input type="hidden" name="id" value="0">
<div class="col-12"><label class="form-label" for="skillstaff">Kỹ thuật viên</label><select class="form-select" id="skillstaff" name="staffId" required><option value="">Chọn kỹ thuật viên</option><c:forEach var="t" items="${technicians}"><option value="${t.staff_id}"><c:out value="${t.full_name}"/></option></c:forEach></select></div>
<div class="col-12"><label class="form-label" for="skillservice">Dịch vụ</label><select class="form-select" id="skillservice" name="serviceId" required><option value="">Chọn dịch vụ</option><c:forEach var="v" items="${services}"><option value="${v.service_id}"><c:out value="${v.service_name}"/></option></c:forEach></select></div>
<div class="col-12"><label class="form-label" for="skilllevel">Trình độ</label><select class="form-select" id="skilllevel" name="level"><option value="BEGINNER">Cơ bản</option><option value="INTERMEDIATE">Trung cấp</option><option value="ADVANCED">Nâng cao</option><option value="EXPERT">Chuyên gia</option></select></div>
<p class="text-muted small mb-0">Mỗi kỹ thuật viên có một mức trình độ cho mỗi dịch vụ. Chọn Sửa trong danh sách để cập nhật.</p>
<div class="col-12"><button class="btn btn-primary">Lưu kỹ năng</button> <button type="reset" class="btn btn-outline-secondary">Tạo mới</button></div></form>
</div></section></div></div>
<section class="card mb-4"><div class="card-body"><h5 class="mb-3">Danh sách ca làm việc <span class="badge bg-primary">${shifts.size()}</span></h5><div class="table-responsive"><table class="table align-middle"><thead><tr><th>Kỹ thuật viên</th><th>Ngày</th><th>Khung giờ</th><th>Trạng thái</th><th>Thao tác</th></tr></thead><tbody>
<c:forEach var="r" items="${shifts}"><tr><td><c:out value="${r.full_name}"/></td><td>${r.shift_date}</td><td>${r.start_time} – ${r.end_time}</td><td><c:choose><c:when test="${r.status == 'SCHEDULED'}">Đã lên lịch</c:when><c:when test="${r.status == 'ATTENDED'}">Có mặt</c:when><c:otherwise>Vắng mặt</c:otherwise></c:choose></td><td class="text-nowrap">
<button type="button" class="btn btn-sm btn-outline-primary edit-shift" data-id="${r.shift_id}" data-staff="${r.staff_id}" data-date="${r.shift_date}" data-start="${r.start_time}" data-end="${r.end_time}" data-status="${r.status}">Sửa</button>
<form method="post" class="d-inline delete-form"><input type="hidden" name="csrf" value="${sessionScope.operationsCsrf}"><input type="hidden" name="action" value="deleteShift"><input type="hidden" name="id" value="${r.shift_id}"><button class="btn btn-sm btn-outline-danger">Xóa</button></form></td></tr></c:forEach>
<c:if test="${empty shifts}"><tr><td colspan="5" class="text-center text-muted py-4">Chưa có ca làm việc.</td></tr></c:if>
</tbody></table></div></div></section>
<section class="card mb-4"><div class="card-body"><h5 class="mb-3">Kỹ năng theo dịch vụ <span class="badge bg-primary">${skills.size()}</span></h5><div class="table-responsive"><table class="table align-middle"><thead><tr><th>Kỹ thuật viên</th><th>Dịch vụ</th><th>Trình độ</th><th>Thao tác</th></tr></thead><tbody>
<c:forEach var="r" items="${skills}"><tr><td><c:out value="${r.full_name}"/></td><td><c:out value="${r.service_name}"/></td><td><c:choose><c:when test="${r.proficiency_level == 'BEGINNER'}">Cơ bản</c:when><c:when test="${r.proficiency_level == 'INTERMEDIATE'}">Trung cấp</c:when><c:when test="${r.proficiency_level == 'ADVANCED'}">Nâng cao</c:when><c:otherwise>Chuyên gia</c:otherwise></c:choose></td><td class="text-nowrap">
<button type="button" class="btn btn-sm btn-outline-primary edit-skill" data-id="${r.skill_id}" data-staff="${r.staff_id}" data-service="${r.service_id}" data-level="${r.proficiency_level}">Sửa</button>
<form method="post" class="d-inline delete-form"><input type="hidden" name="csrf" value="${sessionScope.operationsCsrf}"><input type="hidden" name="action" value="deleteSkill"><input type="hidden" name="id" value="${r.skill_id}"><button class="btn btn-sm btn-outline-danger">Xóa</button></form></td></tr></c:forEach>
<c:if test="${empty skills}"><tr><td colspan="4" class="text-center text-muted py-4">Chưa có kỹ năng được gán.</td></tr></c:if>
</tbody></table></div></div></section>
<c:if test="${not empty form}"><div id="submittedForm" hidden><c:forEach var="entry" items="${form}"><span data-key="<c:out value='${entry.key}'/>" data-value="<c:out value='${entry.value}'/>"></span></c:forEach></div></c:if>
<script src="${ctx}/assets/js/operations.js" defer></script></c:if></div></main><%@ include file="../layout/footer.jspf" %>