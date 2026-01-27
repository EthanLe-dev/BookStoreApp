package com.bookstore.dao;

import com.bookstore.dto.CustomerDTO;
import com.bookstore.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDAO {
    public CustomerDTO selectById(int id) {
        CustomerDTO customer = null;
        try {
            Connection c = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM customer WHERE customer_id = ? ";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                customer = new CustomerDTO();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setCustomerName(rs.getString("customer_name"));
                customer.setCustomerPhone(rs.getString("customer_phone"));
                customer.setPoint(rs.getInt("point"));
                customer.setRankId(rs.getInt("rank_id"));
            }
            DatabaseConnection.closeConnection(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customer;
    }

    public CustomerDTO selectByPhone(String phone) {
        CustomerDTO customer = null;
        try {
            Connection c = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM customer WHERE customer_phone = ? ";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                customer = new CustomerDTO();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setCustomerName(rs.getString("customer_name"));
                customer.setCustomerPhone(rs.getString("customer_phone"));
                customer.setPoint(rs.getInt("point"));
                customer.setRankId(rs.getInt("rank_id"));
            }
            DatabaseConnection.closeConnection(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customer;
    }

    public CustomerDTO selectByName(String name) {
        CustomerDTO customer = null;
        try {
            Connection c = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM customer WHERE customer_name = ? ";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                customer = new CustomerDTO();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setCustomerName(rs.getString("customer_name"));
                customer.setCustomerPhone(rs.getString("customer_phone"));
                customer.setPoint(rs.getInt("point"));
                customer.setRankId(rs.getInt("rank_id"));
            }
            DatabaseConnection.closeConnection(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customer;
    }

    public boolean updatePointAndRank(int customerId, int newPoint, int newRankId) {
        Connection c = null;
        PreparedStatement ps = null;
        boolean result = false;

        try {
            c = DatabaseConnection.getConnection();

            String sql = "UPDATE customer SET point = ?, rank_id = ? WHERE customer_id = ?";
            ps = c.prepareStatement(sql);
            ps.setInt(1, newPoint);
            ps.setInt(2, newRankId);
            ps.setInt(3, customerId);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                DatabaseConnection.closeConnection(c);
                if (ps != null) ps.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
