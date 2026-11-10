package com.carcare.service;

import com.carcare.dal.AdditionalServiceDAO;
import com.carcare.model.AdditionalServiceRequest;

/**
 * Quản lý dịch vụ phát sinh ngoài lịch hẹn (Sprint 6)
 */
public class AdditionalServiceManager {
    private final AdditionalServiceDAO additionalServiceDAO;

    public AdditionalServiceManager() {
        this.additionalServiceDAO = new AdditionalServiceDAO();
    }

    // TODO: Gửi yêu cầu phát sinh và xử lý phê duyệt từ khách hàng
    public boolean submitRequest(AdditionalServiceRequest request) {
        if (request == null) return false;
        return additionalServiceDAO.insert(request);
    }
}
