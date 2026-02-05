package com.bookstore.bus;

import com.bookstore.dao.AuthorDAO;
import com.bookstore.dto.AuthorDTO;

import java.util.List;

public class AuthorBUS {
    private final AuthorDAO authorDAO = new AuthorDAO();

    public List<AuthorDTO> selectAllAuthors() {
        return authorDAO.selectAllAuthors();
    }
}
