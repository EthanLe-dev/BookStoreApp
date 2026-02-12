package com.bookstore.bus;

import com.bookstore.dao.BookDAO;
import com.bookstore.dto.BookDTO;

import java.util.ArrayList;
import java.util.List;

public class BookBUS {
    private final BookDAO bookDAO = new BookDAO();
    private List<BookDTO> listBook;
    public BookBUS() {
        this.listBook = bookDAO.selectAllBooks();
    }   
    
    public List<BookDTO> selectAll() {
        this.listBook = bookDAO.selectAllBooks();
        return this.listBook;
    }

    public String addBook(BookDTO book) {
        if (book.getBookName() == null || book.getBookName().trim().isEmpty())
            return "Tên sách không được để trống!";
        if (book.getAuthorIdsList().isEmpty())
            return "Vui lòng chọn ít nhất một tác giả cho sách!";   
        if (book.getCategoryId() <= 0)
            return "Vui lòng chọn thể loại cho sách!";
        if (book.getSupplierId() <= 0)
            return "Vui lòng chọn nhà cung cấp cho sách!";
        
        String duplicateCheck = validateDuplicate(book, false);
        if (duplicateCheck != null) {
            return duplicateCheck;
        }
        
        int generatedId = bookDAO.add(book);
        if (generatedId != -1) {
            book.setBookId(generatedId);
            listBook.add(book);
            return "Thêm sách thành công!";
        }
        return "Thêm sách thất bại!";
    }

    private String validateDuplicate(BookDTO candidate, boolean isUpdate) {
    if (!bookDAO.existsByName(candidate.getBookName())) return null;

    String normalizedName = normalizeText(candidate.getBookName());

    for (BookDTO existing : listBook) {
        // Nếu là Update, bỏ qua chính nó dựa trên ID
        if (isUpdate && existing.getBookId() == candidate.getBookId()) {
            continue;
        }

        // Logic so sánh chung
        if (normalizedName.equals(normalizeText(existing.getBookName())) &&
            sameAuthorIds(existing.getAuthorIdsList(), candidate.getAuthorIdsList()) &&
            existing.getSupplierId() == candidate.getSupplierId() &&
            normalizeText(existing.getTranslator()).equals(normalizeText(candidate.getTranslator()))) {
            
            return "Sách đã tồn tại! (Trùng tên, tác giả, nhà cung cấp và người dịch)";
        }
    }
    return null;
}


    private boolean sameAuthorIds(List<Integer> firstAuthors, List<Integer> secondAuthors) {
        if (firstAuthors == null || secondAuthors == null) {
            return firstAuthors == secondAuthors;
        }

        if (firstAuthors.size() != secondAuthors.size()) {
            return false;
        }

        for (Integer firstId : firstAuthors) {
            boolean found = false;
            for (Integer secondId : secondAuthors) {
                if (firstId != null && firstId.equals(secondId)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }

        return true;
    }


    private String normalizeText(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }


    public String updateBook(BookDTO book) {
        if (book.getBookName().trim().isEmpty())
            return "Tên sách không được để trống!";
        
        String duplicateCheck = validateDuplicate(book, true);
        if (duplicateCheck != null) {
            return duplicateCheck;
        }

        if (bookDAO.update(book)) {
            for (int i = 0; i < listBook.size(); i++) {
                if (listBook.get(i).getBookId() == book.getBookId()) {
                    listBook.set(i, book);
                    break;
                }
            }
            return "Cập nhật thành công!";
        }
        return "Cập nhật thất bại!";
    }

    
    public List<BookDTO> search(String name) {
        List<BookDTO> result = new ArrayList<>();
        String searchName = name.toLowerCase();
        for (BookDTO b : listBook) {
            if (b.getBookName().toLowerCase().contains(searchName)) {
                result.add(b);
            }
        }
        return result;
    }

    public BookDTO getBookById(int bookId) {
        for (BookDTO b : listBook) {
            if (b.getBookId() == bookId) {
                return b;
            }
        }
        return bookDAO.getBookbyId(bookId);
    }
}