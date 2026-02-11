package com.bookstore.dao;

import com.bookstore.dto.BookDTO;
import com.bookstore.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    public List<BookDTO> selectAllBooks() {
        List<BookDTO> list = new ArrayList<>();
        String sql = "SELECT b.*, c.category_name, " +
                "GROUP_CONCAT(DISTINCT a.author_name SEPARATOR ', ') as author_names, " + // Lấy tên: "Nguyễn Nhật Ánh, Tô Hoài"
                "GROUP_CONCAT(DISTINCT ba.author_id SEPARATOR ',') as author_ids " +      // Lấy ID: "1,2"
                "FROM book b " +
                "JOIN category c ON b.category_id = c.category_id " +
                "LEFT JOIN book_author ba ON b.book_id = ba.book_id " +
                "LEFT JOIN author a ON ba.author_id = a.author_id " +
                "WHERE b.status = 1 " +
                "GROUP BY b.book_id";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                BookDTO book = new BookDTO(
                        rs.getInt("book_id"),
                        rs.getString("book_name"),
                        rs.getDouble("selling_price"),
                        rs.getInt("quantity"),
                        rs.getString("translator"),
                        rs.getString("image"),
                        rs.getString("description"),
                        rs.getInt("status"),
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("tag_detail"),
                        rs.getInt("supplier_id")
                );
                book.setAuthorIdsFromString(rs.getString("author_ids"));
                book.setAuthorsName(rs.getString("author_names"));
                list.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean decreaseQuantity(int bookId, int quantitySold) {
        // Câu lệnh SQL: Lấy số lượng hiện tại TRỪ đi số lượng bán
        String sql = "UPDATE book SET quantity = quantity - ? WHERE book_id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, quantitySold); // Tham số 1: Số lượng bán
            ps.setInt(2, bookId);       // Tham số 2: ID sách

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu update thành công

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
