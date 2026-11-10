package com.carcare.service;

import com.carcare.dal.DispatchDAO;
import com.carcare.model.Assignment;

/**
 * Quản lý nghiệp vụ Điều phối khoang & Kỹ thuật viên 
 */
public class DispatchService {
    private final DispatchDAO dispatchDAO;

    public DispatchService() {
        this.dispatchDAO = new DispatchDAO();
    }

    public DispatchService(DispatchDAO dispatchDAO) {
        this.dispatchDAO = dispatchDAO;
    }

    // TODO: Triển khai kiểm tra tình trạng khả dụng của thợ và khoang 
    public boolean assignTechnicianAndBay(Assignment assignment) {
        if (assignment == null) return false;
        return dispatchDAO.insert(assignment);
    }
}
