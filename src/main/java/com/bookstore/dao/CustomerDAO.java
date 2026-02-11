package com.bookstore.dao;

import com.bookstore.dto.CustomerDTO;
import com.bookstore.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CustomerDAO {
    public CustomerDTO selectByPhone(String phone) {
        CustomerDTO customer = null;
        String sql = "SELECT * FROM customer WHERE customer_phone = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    customer = new CustomerDTO(
                            rs.getInt("customer_id"),
                            rs.getString("customer_name"),
                            rs.getString("customer_phone"),
                            rs.getInt("point"),
                            rs.getInt("rank_id")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customer;
    }

    public boolean addPoint(int customerId, int pointsToAdd) {
        // Câu lệnh SQL: Lấy điểm hiện tại CỘNG thêm điểm mới
        String sql = "UPDATE customer SET point = point + ? WHERE customer_id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, pointsToAdd); // Tham số 1: Điểm cộng thêm
            ps.setInt(2, customerId);  // Tham số 2: ID khách

            int rowsAffected = ps.executeUpdate();

            // --- BONUS: TỰ ĐỘNG CẬP NHẬT HẠNG THÀNH VIÊN ---
            // Sau khi cộng điểm, nên kiểm tra để nâng hạng luôn (nếu bạn muốn làm kỹ)
            if (rowsAffected > 0) {
                updateCustomerRank(customerId);
            }

            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updateCustomerRank(int customerId) {
        // Logic:
        // < 2000: Thành viên (1)
        // >= 2000: Bạc (2)
        // >= 5000: Vàng (3)
        // >= 10000: Bạch kim (4)
        // (ID hạng tùy thuộc database của bạn, đây là ví dụ theo data mẫu trước đó)

        String sql = "UPDATE customer SET rank_id = CASE " +
                "WHEN point >= 10000 THEN 4 " +
                "WHEN point >= 5000 THEN 3 " +
                "WHEN point >= 2000 THEN 2 " +
                "ELSE 1 END " +
                "WHERE customer_id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
