package com.bookstore.bus;

import com.bookstore.dao.CategoryDAO;
import com.bookstore.dto.CategoryDTO;
import java.util.List;

public class CategoryBUS {
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private List<CategoryDTO> listCategory;

    public CategoryBUS() {
        this.listCategory = categoryDAO.selectAllCategories();
    }

    public List<CategoryDTO> selectAll() {
        this.listCategory = categoryDAO.selectAllCategories();
        return this.listCategory;
    }

    public String addCategory(CategoryDTO category) {
        if (category.getCategoryName() == null || category.getCategoryName().trim().isEmpty())
            return "Tên thể loại không được để trống!";
        
        int generatedId = categoryDAO.add(category);
        if (generatedId != -1) {
            category.setCategoryId(generatedId);
            this.listCategory.add(category);
            return "Thêm thể loại thành công!";
        }
        return "Thêm thể loại thất bại!";
    }

    public String updateCategory(CategoryDTO category) {
        if (category.getCategoryName().trim().isEmpty())
            return "Tên thể loại không được để trống!";

        if (categoryDAO.update(category)) {
            for (int i = 0; i < listCategory.size(); i++) {
                if (listCategory.get(i).getCategoryId() == category.getCategoryId()) {
                    listCategory.set(i, category);
                    break;
                }
            }
            return "Cập nhật thành công!";
        }
        return "Cập nhật thất bại!";
    }

    public CategoryDTO getById(int id) {
        for (CategoryDTO c : listCategory) {
            if (c.getCategoryId() == id) return c;
        }
        return categoryDAO.getCategoryById(id);
    }
}