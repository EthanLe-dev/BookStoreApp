package com.bookstore.bus;

import com.bookstore.dao.BillDAO;
import com.bookstore.dao.BookDAO;
import com.bookstore.dto.BillDTO;
import com.bookstore.dto.BillDetailDTO;
import com.bookstore.dto.BookDTO;

import java.util.List;

public class BillBUS {
    private BillDAO billDAO = new BillDAO();
    private BookDAO bookDAO = new BookDAO();

    public String sellBook(BillDTO bill, List<BillDetailDTO> cart) {
        if (cart == null || cart.isEmpty()) {
            return "Giỏ hàng đang trống, vui lòng chọn sách!";
        }

        for (BillDetailDTO item : cart) {
            BookDTO book = bookDAO.selectById(item.getBookId());
            if (book == null) {
                return "Sách ID " + item.getBookId() + " không tồn tại!";
            }

            if (book.getQuantity() < item.getQuantity()) {
                return "Sách '" + book.getBookName() + "' chỉ còn " + book.getQuantity() + " quyền. Không đủ bán!";
            }
        }

        double totalAmount = 0;
        for (BillDetailDTO item : cart) {
            totalAmount += item.getQuantity() * item.getUnitPrice();
        }
        bill.setTotalBillPrice(totalAmount);

        boolean success = billDAO.createBill(bill, cart);
        if (success) {
            return "Thành công";
        } else {
            return "Lỗi hệ thống: không thể tạo hóa đơn!";
        }
    }
}
