package com.bookstore.dao;

import com.bookstore.dto.BookDTO;
import com.bookstore.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BookDAO {
    public BookDTO selectById(int bookId) {
        BookDTO book = null;
        try {
            Connection c = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM book WHERE book_id = ? ";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, bookId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                book = new BookDTO();
                book.setBookId(rs.getInt("book_id"));
                book.setBookName(rs.getString("book_name"));
                book.setSellingPrice(rs.getDouble("selling_price"));
                book.setQuantity(rs.getInt("quantity"));
                book.setTranslator(rs.getString("translator"));
                book.setImage(rs.getString("image"));
                book.setDescription(rs.getString("description"));
                book.setStatus(rs.getInt("status"));
                book.setCategoryId(rs.getInt("category_id"));
                book.setSupplierId(rs.getInt("supplier_id"));
            }
            DatabaseConnection.closeConnection(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return book;
    }
}
