package com.bookstore.bus;

import com.bookstore.dao.CategoryDAO;
import com.bookstore.dto.CategoryDTO;

import java.util.List;

public class CategoryBUS {
    private final CategoryDAO categoryDAO = new CategoryDAO();

    public List<CategoryDTO> selectAllCategories() {
        return categoryDAO.selectAllCategories();
    }
}
