package com.carcare.service;

import com.carcare.dal.InvoiceDAO;
import com.carcare.model.Invoice;

/**
 * Quản lý tính toán hóa đơn và thanh toán (Sprint 7)
 */
public class InvoiceService {
    private final InvoiceDAO invoiceDAO;

    public InvoiceService() {
        this.invoiceDAO = new InvoiceDAO();
    }

    // TODO: Tính toán giảm giá thành viên, thuế VAT 8% / 10%
    public boolean generateInvoice(Invoice invoice) {
        if (invoice == null) return false;
        return invoiceDAO.insert(invoice);
    }
}
