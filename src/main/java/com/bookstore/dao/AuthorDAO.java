package com.bookstore.dao;

import com.bookstore.dto.AuthorDTO;
import com.bookstore.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AuthorDAO {
    public List<AuthorDTO> selectAllAuthors() {
        List<AuthorDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM author";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new AuthorDTO(
                        rs.getInt("author_id"),
                        rs.getString("author_name"),
                        rs.getString("nationality")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
