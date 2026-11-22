<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN"/>

<section class="card mb-4" id="maintenanceEditor"><div class="card-body"><h5 class="mb-3">${not empty maintenanceForm.id && maintenanceForm.id != '0' ? 'Sửa lịch bảo trì' : 'Lập lịch bảo trì'}</h5>
<form method="post" action="${ctx}/bays" id="maintenanceForm" class="row g-3"><input type="hidden" name="csrf" value="${sessionScope.catalogCsrf}">
<input type="hidden" name="action" value="maintenance"><input type="hidden" name="id" value="<c:out value='${empty maintenanceForm.id ? 0 : maintenanceForm.id}'/>">
<div class="col-lg-4"><label class="form-label" for="maintenanceBay">Khoang cần bảo trì</label><select class="form-select" id="maintenanceBay" name="bay_id" required><option value="">Chọn khoang</option><c:forEach var="b" items="${items}"><option value="${b.bay_id}" ${maintenanceForm.bay_id == b.bay_id ? 'selected' : ''}><c:out value="${b.bay_name}"/></option></c:forEach></select></div>
<div class="col-sm-6 col-lg-3"><label class="form-label" for="maintenanceStart">Bắt đầu</label><input class="form-control" id="maintenanceStart" type="datetime-local" name="start_time" required value="<c:out value='${maintenanceForm.start_time}'/>"></div>
<div class="col-sm-6 col-lg-3"><label class="form-label" for="maintenanceEnd">Kết thúc</label><input class="form-control" id="maintenanceEnd" type="datetime-local" name="end_time" required value="<c:out value='${maintenanceForm.end_time}'/>"></div>
<div class="col-lg-2"><label class="form-label" for="maintenanceStatus">Trạng thái</label><select class="form-select" id="maintenanceStatus" name="status"><option value="SCHEDULED" ${maintenanceForm.status == 'SCHEDULED' ? 'selected' : ''}>Đã lên lịch</option><option value="IN_PROGRESS" ${maintenanceForm.status == 'IN_PROGRESS' ? 'selected' : ''}>Đang bảo trì</option><option value="COMPLETED" ${maintenanceForm.status == 'COMPLETED' ? 'selected' : ''}>Hoàn thành</option><option value="CANCELLED" ${maintenanceForm.status == 'CANCELLED' ? 'selected' : ''}>Đã hủy</option></select></div>
<div class="col-12"><label class="form-label" for="maintenanceReason">Lý do / nội dung bảo trì</label><input class="form-control" id="maintenanceReason" name="reason" required maxlength="255" value="<c:out value='${maintenanceForm.reason}'/>"></div>
<div class="col-12 d-flex gap-2"><button class="btn btn-primary">Lưu lịch bảo trì</button><a class="btn btn-outline-secondary" href="${ctx}/bays">Tạo mới / Hủy sửa</a></div>
</form><p class="text-muted small mt-3 mb-0">Múi giờ Việt Nam. Khoang tự hiển thị Bảo trì trong khoảng đã lên lịch/đang bảo trì; hết khoảng này trở về trạng thái cơ sở. Hai lịch liền kề được phép.</p>
</div></section>
