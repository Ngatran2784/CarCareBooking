package com.carcare;

import com.carcare.model.Assignment;
import com.carcare.service.DispatchService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DispatchServiceTest {

    private DispatchService dispatchService;

    @BeforeEach
    public void setUp() {
        dispatchService = new DispatchService();
    }

    @Test
    @DisplayName("Kiểm tra khởi tạo DispatchService thành công")
    public void testInitialization() {
        Assertions.assertNotNull(dispatchService, "DispatchService khong duoc null");
    }

    @Test
    @DisplayName("Phân công với Assignment null trả về false")
    public void testAssignWithNull() {
        boolean result = dispatchService.assignTechnicianAndBay(null);
        Assertions.assertFalse(result, "Phan cong voi null phai tra ve false");
    }
}
