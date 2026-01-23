package com.bookstore.dao;

import com.bookstore.dto.EmployeeDTO;
import com.bookstore.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EmployeeDAO {
    public EmployeeDTO selectById(int id) {
        EmployeeDTO employee = null;

        try {
            Connection c = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM employee WHERE employee_id = ? ";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                employee = new EmployeeDTO();
                employee.setEmployeeId(rs.getInt("employee_id"));
                employee.setEmployeeName(rs.getString("employee_name"));
                employee.setRoleId(rs.getInt("role_id"));
            }
            DatabaseConnection.closeConnection(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return employee;
    }
}
