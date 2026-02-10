package com.bookstore.bus;

import com.bookstore.dao.AuthorDAO;
import com.bookstore.dto.AuthorDTO;
import java.util.ArrayList;
import java.util.List;

public class AuthorBUS {
    private final AuthorDAO authorDAO = new AuthorDAO();
    private List<AuthorDTO> listAuthor;

    public AuthorBUS() {
        this.listAuthor = authorDAO.selectAllAuthors();
    }

    public List<AuthorDTO> selectAll() {
        this.listAuthor = authorDAO.selectAllAuthors();
        return this.listAuthor;
    }

    public String addAuthor(AuthorDTO author) {
        if (author.getAuthorName().trim().isEmpty()) 
            return "Tên tác giả không được để trống!";
        if (authorDAO.exists(author.getAuthorName())) 
            return "Tên tác giả đã tồn tại trong hệ thống!";
        if(author.getNationality().trim().isEmpty())
            return "Quốc tịch tác giả không được để trống!";

        int id = authorDAO.add(author);
        if (id != -1) {
            author.setAuthorId(id);
            this.listAuthor.add(author);
            return "Thêm tác giả thành công!";
        }
        return "Thêm tác giả thất bại!";
    }


    public String updateAuthor(AuthorDTO author) {
        if (author.getAuthorName().trim().isEmpty()) 
            return "Tên tác giả không được để trống!";
        if(author.getNationality().trim().isEmpty())
            return "Quốc tịch tác giả không được để trống!";
        if (authorDAO.update(author)) {
            for (int i = 0; i < listAuthor.size(); i++) {
                if (listAuthor.get(i).getAuthorId() == author.getAuthorId()) {
                    listAuthor.set(i, author);
                    break;
                }
            }
            return "Cập nhật tác giả thành công!";
        }
        return "Cập nhật tác giả thất bại!";
    }

    public List<AuthorDTO> search(String name) {
        List<AuthorDTO> result = new ArrayList<>();
        for (AuthorDTO a : listAuthor) {
            if (a.getAuthorName().toLowerCase().contains(name.toLowerCase())) {
                result.add(a);
            }
        }
        return result;
    }
    public AuthorDTO getById(int id) {
        for (AuthorDTO a : listAuthor) {
            if (a.getAuthorId() == id) return a;
        }
        return authorDAO.getAuthorById(id);
    }
}