package com.bookstore.bus;

import com.bookstore.dao.BookDAO;
import com.bookstore.dto.BookDTO;

import java.util.List;

public class BookBUS {
    private final BookDAO bookDAO = new BookDAO();

    public List<BookDTO> selectAllBooks() {
        return bookDAO.selectAllBooks();
    }
}
