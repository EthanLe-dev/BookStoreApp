package com.bookstore.dao;

import com.bookstore.dto.BillDTO;
import com.bookstore.dto.BillDetailDTO;
import com.bookstore.util.DatabaseConnection;

import java.sql.*;
import java.util.List;

public class BillDAO {
    public boolean createBill(BillDTO bill, List<BillDetailDTO> details) {
        Connection c = null;
        PreparedStatement psBill = null;
        PreparedStatement psBillDetail = null;
        PreparedStatement psUpdateBook = null;
        boolean result = false;

        try {
            c = DatabaseConnection.getConnection();
            c.setAutoCommit(false);

            String sqlBill = "INSERT INTO bill (total_bill_price, employee_id, customer_id, payment_method_id) " +
                    "VALUES (?,?,?,?)";
            psBill = c.prepareStatement(sqlBill, Statement.RETURN_GENERATED_KEYS);
            psBill.setDouble(1, bill.getTotalBillPrice());
            psBill.setInt(2, bill.getEmployeeId());
            psBill.setInt(3, bill.getCustomerId());
            psBill.setInt(4, bill.getPaymentMethodId());

            int affectedRows = psBill.executeUpdate();
            if (affectedRows == 0) throw new SQLException("Tạo hóa đơn thất bại!");

            int generatedBillId;
            try (ResultSet generatedKeys = psBill.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    generatedBillId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Không lấy được ID hóa đơn");
                }
            }

            String sqlBillDetail = "INSERT INTO bill_detail (bill_id, book_id, quantity, unit_price) " +
                    "VALUES (?,?,?,?)";
            String sqlUpdateBook = "UPDATE book SET quantity = quantity - ? WHERE book_id = ?";

            psBillDetail = c.prepareStatement(sqlBillDetail);
            psUpdateBook = c.prepareStatement(sqlUpdateBook);

            for (BillDetailDTO item : details) {
                psBillDetail.setInt(1, generatedBillId);
                psBillDetail.setInt(2, item.getBookId());
                psBillDetail.setInt(3, item.getQuantity());
                psBillDetail.setDouble(4, item.getUnitPrice());
                psBillDetail.addBatch();

                psUpdateBook.setInt(1, item.getQuantity());
                psUpdateBook.setInt(2, item.getBookId());
                psUpdateBook.addBatch();
            }

            psBillDetail.executeBatch();
            psUpdateBook.executeBatch();

            c.commit();
            result = true;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (c != null) c.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (psBill != null) psBill.close();
                if (psBillDetail != null) psBillDetail.close();
                if (psUpdateBook != null) psUpdateBook.close();
                DatabaseConnection.closeConnection(c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
