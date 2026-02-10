package com.bookstore.bus;

import com.bookstore.dao.SupplierDAO;
import com.bookstore.dto.SupplierDTO;
import java.util.List;

public class SupplierBUS {
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private List<SupplierDTO> listSupplier;

    public SupplierBUS() {
        this.listSupplier = supplierDAO.selectAllSuppliers();
    }

    public List<SupplierDTO> selectAll() {
        this.listSupplier = supplierDAO.selectAllSuppliers();
        return this.listSupplier;
    }

    public String addSupplier(SupplierDTO supplier) {
        if (supplier.getSupplierName().trim().isEmpty())
            return "Tên nhà cung cấp không được để trống!";
        if (supplier.getPhoneNumber().trim().isEmpty())
            return "Số điện thoại không được để trống!";

        int generatedId = supplierDAO.add(supplier);
        if (generatedId != -1) {
            supplier.setSupplierId(generatedId);
            this.listSupplier.add(supplier);
            return "Thêm nhà cung cấp thành công!";
        }
        return "Thêm nhà cung cấp thất bại!";
    }

    public String updateSupplier(SupplierDTO supplier) {
        if (supplier.getSupplierName().trim().isEmpty())
            return "Tên nhà cung cấp không được để trống!";

        if (supplierDAO.update(supplier)) {
            for (int i = 0; i < listSupplier.size(); i++) {
                if (listSupplier.get(i).getSupplierId() == supplier.getSupplierId()) {
                    listSupplier.set(i, supplier);
                    break;
                }
            }
            return "Cập nhật thành công!";
        }
        return "Cập nhật thất bại!";
    }

    public SupplierDTO getById(int id) {
        for (SupplierDTO s : listSupplier) {
            if (s.getSupplierId() == id) return s;
        }
        return supplierDAO.getBySupplierId(id);
    }
}