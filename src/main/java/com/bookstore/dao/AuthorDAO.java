package com.bookstore.dao;

import com.bookstore.dto.AuthorDTO;
import com.bookstore.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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


    public int add(AuthorDTO author){
        String sql= "INSERT  INTO author (author_name, nationality)" +
                    "VALUES (?,?)";
        try(Connection c = DatabaseConnection.getConnection();
            PreparedStatement ps  = c.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS)){

                ps.setString(1, author.getAuthorName());
                ps.setString(2, author.getNationality());


                int rowsAffected = ps.executeUpdate();
        

        if (rowsAffected > 0) {
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return -1;
    }


    public boolean update(AuthorDTO author){
        String sql  = "UPDATE author SET author_name= ?, nationality = ? "  +
                        "WHERE author_id = ?";

        try (Connection c = DatabaseConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(sql);){
                ps.setString(1, author.getAuthorName());
                ps.setString(2, author.getNationality());
                ps.setInt(3, author.getAuthorId());

                int rowsAffected = ps.executeUpdate();
                return rowsAffected >0;
            } catch (Exception e){
                e.printStackTrace();
            }
            return false;
    }

    public boolean exists(String authorname){
        String sql ="SELECT * FROM author WHERE author_name = ?";
        try(Connection c  = DatabaseConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(sql);){

            ps.setString(1, authorname);
            try(ResultSet rs = ps.executeQuery();){
                return rs.next();
            }
            } catch (Exception e){
                e.printStackTrace();
            }
        return false;
    }

    public List <AuthorDTO> searchByName(String authorname){
        List<AuthorDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM author WHERE author_name LIKE ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);) {

            ps.setString(1, "%" + authorname + "%");
            try (ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    list.add(new AuthorDTO(
                            rs.getInt("author_id"),
                            rs.getString("author_name"),
                            rs.getString("nationality")
                    ));
                }
            }
            } catch (Exception e) {
            e.printStackTrace();
            }
        return list;
    }

    public AuthorDTO getAuthorById(int id){
        String sql = "SELECT * FROM author WHERE author_id = ?";
        try(Connection c = DatabaseConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(sql);){

            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery();){
                if(rs.next()){
                    return new AuthorDTO(
                            rs.getInt("author_id"),
                            rs.getString("author_name"),
                            rs.getString("nationality")
                    );
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public List<AuthorDTO> getByBookId(int bookId) {
        List<AuthorDTO> authors = new ArrayList<>();
        String sql = "SELECT a.* FROM author a " +
                     "JOIN book_author ba ON a.author_id = ba.author_id " +
                     "WHERE ba.book_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                AuthorDTO author = new AuthorDTO(
                    rs.getInt("author_id"),
                    rs.getString("author_name"),
                    rs.getString("nationality")
                );
                authors.add(author);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authors;
    }
}

