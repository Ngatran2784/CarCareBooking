<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN"/>

<section class="card mb-4" id="bayEditor"><div class="card-body"><h5 class="mb-3">${not empty form.id && form.id != '0' ? 'Sửa khoang dịch vụ' : 'Thêm khoang dịch vụ'}</h5>
<form method="post" action="${ctx}/bays" id="bayForm" class="row g-3"><input type="hidden" name="csrf" value="${sessionScope.catalogCsrf}">
<input type="hidden" name="action" value="bay"><input type="hidden" name="id" value="<c:out value='${empty form.id ? 0 : form.id}'/>">
<div class="col-lg-5"><label class="form-label" for="bayName">Tên khoang / cầu</label><input class="form-control" id="bayName" name="bay_name" required maxlength="50" value="<c:out value='${form.bay_name}'/>"></div>
<div class="col-lg-4"><label class="form-label" for="bayType">Loại khoang</label><input class="form-control" id="bayType" name="bay_type" list="bayTypes" required maxlength="50" value="<c:out value='${empty form.bay_type ? "GENERAL" : form.bay_type}'/>"><datalist id="bayTypes"><option value="GENERAL">Tổng hợp</option><option value="WASHING">Rửa xe</option><option value="DETAILING">Chăm sóc chuyên sâu</option><option value="MECHANICAL">Cơ khí</option></datalist></div>
<div class="col-lg-3"><label class="form-label" for="bayStatus">Trạng thái cơ sở</label><select class="form-select" id="bayStatus" name="status"><option value="AVAILABLE" ${form.status == 'AVAILABLE' ? 'selected' : ''}>Trống</option><option value="OCCUPIED" ${form.status == 'OCCUPIED' ? 'selected' : ''}>Đang sử dụng</option><option value="MAINTENANCE" ${form.status == 'MAINTENANCE' ? 'selected' : ''}>Bảo trì thủ công</option></select></div>
<div class="col-12 d-flex gap-2"><button class="btn btn-primary">Lưu khoang</button><a class="btn btn-outline-secondary" href="${ctx}/bays">Tạo mới / Hủy sửa</a></div>
</form></div></section>
