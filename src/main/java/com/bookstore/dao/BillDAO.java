package com.bookstore.dao;

import com.bookstore.dto.BillDTO;
import com.bookstore.dto.BillDetailDTO;
import com.bookstore.util.DatabaseConnection;

import java.sql.*;

public class BillDAO {

    // Hàm trả về ID của hóa đơn vừa tạo
    public int insertBill(BillDTO bill) {
        int generatedId = -1;
        String sql = "INSERT INTO bill (created_date, total_bill_price, tax, employee_id, customer_id, payment_method_id, earned_points) " +
                "VALUES (NOW(), ?, 0.08, ?, ?, ?, ?)"; // Tax 8% cứng hoặc lấy từ param

        try (Connection c = DatabaseConnection.getConnection();
             // RETURN_GENERATED_KEYS là tham số bắt buộc để lấy ID
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDouble(1, bill.getTotalBillPrice());
            ps.setInt(2, bill.getEmployeeId());

            if (bill.getCustomerId() == 0) ps.setNull(3, Types.INTEGER);
            else ps.setInt(3, bill.getCustomerId());

            ps.setInt(4, bill.getPaymentMethodId());
            ps.setInt(5, bill.getEarnedPoints());

            int result = ps.executeUpdate();
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1); // Lấy ID vừa sinh ra
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedId;
    }

    public void insertBillDetail(BillDetailDTO detail) {
        String sql = "INSERT INTO bill_detail (bill_id, book_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, detail.getBillId()); // ID lấy từ bước trên
            ps.setInt(2, detail.getBookId());
            ps.setInt(3, detail.getQuantity());
            ps.setDouble(4, detail.getUnitPrice());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}