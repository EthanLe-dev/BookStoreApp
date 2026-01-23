package com.bookstore.dao;

import com.bookstore.dto.AccountDTO;
import com.bookstore.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AccountDAO {
    public AccountDTO selectByUsername(String username) {
        AccountDTO acc = null;

        try {
            Connection c = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM account WHERE username = ? ";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                acc = new AccountDTO(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("employee_id"),
                        rs.getInt("status")
                );
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return acc;
    }
}
