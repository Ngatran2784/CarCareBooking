<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN"/>

<section class="card mb-4" id="serviceEditor"><div class="card-body">
<h5 class="mb-3">${not empty form.id && form.id != '0' ? 'Sửa dịch vụ' : 'Thêm dịch vụ'}</h5>
<form method="post" action="${ctx}/services" id="serviceForm" class="row g-3"><input type="hidden" name="csrf" value="${sessionScope.catalogCsrf}">
<input type="hidden" name="action" value="service"><input type="hidden" name="id" value="<c:out value='${empty form.id ? 0 : form.id}'/>">
<div class="col-lg-5"><label class="form-label" for="serviceName">Tên dịch vụ</label><input class="form-control" id="serviceName" name="service_name" required maxlength="100" value="<c:out value='${form.service_name}'/>"></div>
<div class="col-sm-6 col-lg-3"><label class="form-label" for="servicePrice">Giá niêm yết (VNĐ)</label><input class="form-control" id="servicePrice" name="base_price" type="number" min="0" max="9999999999.99" step="0.01" required value="<c:out value='${form.base_price}'/>"></div>
<div class="col-sm-6 col-lg-2"><label class="form-label" for="serviceDuration">Thời lượng (phút)</label><input class="form-control" id="serviceDuration" name="estimated_duration_minutes" type="number" min="1" max="1440" step="1" required value="<c:out value='${form.estimated_duration_minutes}'/>"></div>
<div class="col-lg-2"><label class="form-label" for="serviceStatus">Trạng thái</label><select class="form-select" id="serviceStatus" name="status"><option value="ACTIVE" ${form.status == 'ACTIVE' ? 'selected' : ''}>Đang cung cấp</option><option value="INACTIVE" ${form.status == 'INACTIVE' ? 'selected' : ''}>Ngừng cung cấp</option></select></div>
<div class="col-12"><label class="form-label" for="serviceDescription">Mô tả</label><textarea class="form-control" id="serviceDescription" name="description" rows="2" maxlength="5000"><c:out value="${form.description}"/></textarea></div>
<div class="col-12 d-flex gap-2"><button class="btn btn-primary">Lưu dịch vụ</button><a class="btn btn-outline-secondary" href="${ctx}/services">Tạo mới / Hủy sửa</a></div>
</form><p class="text-muted small mt-3 mb-0">Mỗi lần đổi giá sẽ lưu giá cũ, giá mới, thời điểm và người thay đổi. Giá trong các đơn đã đặt được giữ nguyên.</p>
</div></section>
