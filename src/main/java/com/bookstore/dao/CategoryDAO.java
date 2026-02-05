package com.bookstore.dao;

import com.bookstore.dto.CategoryDTO;
import com.bookstore.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    public List<CategoryDTO> selectAllCategories() {
        List<CategoryDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM category";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new CategoryDTO(
                        rs.getInt("category_id"),
                        rs.getString("category_name")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
