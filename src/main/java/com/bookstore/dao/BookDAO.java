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
        String sql = "SELECT b.*, c.category_name," +
                    "GROUP_CONCAT(ba.author_id) as author_ids_list " +
                    "FROM book b " +
                    "JOIN category c ON b.category_id = c.category_id " +
                    "LEFT JOIN book_author ba ON b.book_id = ba.book_id " +
                    "WHERE b.status = 1 " +
                    "GROUP BY b.book_id";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery();) {

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
                book.setAuthorIdsFromString(rs.getString("author_ids_list"));
                list.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
