package com.bookstore.dao;

import com.bookstore.dto.PromotionDTO;
import com.bookstore.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PromotionDAO {
    public List<PromotionDTO> selectAllValidPromotions() {
        List<PromotionDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM promotion " +
                "WHERE status = 1 " +
                "AND NOW() BETWEEN start_date AND end_date";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PromotionDTO p = new PromotionDTO(
                        rs.getInt("promotion_id"),
                        rs.getString("promotion_name"),
                        rs.getDouble("percent"),
                        rs.getTimestamp("start_date"),
                        rs.getTimestamp("end_date"),
                        rs.getInt("status")
                );
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public double getPromotionPercentByBook(int bookId) {
        double percent = 0;

        // SQL: Join 2 bảng để tìm khuyến mãi
        // 1. pd.book_id = ?: Tìm sách này trong bảng chi tiết
        // 2. p.status = 1: Chương trình phải đang hoạt động
        // 3. NOW() BETWEEN...: Thời gian hiện tại phải nằm trong hạn
        // 4. ORDER BY... DESC LIMIT 1: Nếu có nhiều KM trùng nhau, lấy cái giảm sâu nhất
        String sql = "SELECT p.percent FROM promotion p " +
                "JOIN promotion_detail pd ON p.promotion_id = pd.promotion_id " +
                "WHERE pd.book_id = ? " +
                "AND p.status = 1 " +
                "AND NOW() BETWEEN p.start_date AND p.end_date " +
                "ORDER BY p.percent DESC LIMIT 1";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, bookId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    percent = rs.getDouble("percent");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return percent;
    }
}