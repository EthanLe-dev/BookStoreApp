package com.bookstore.bus;

import com.bookstore.dao.BillDAO;
import com.bookstore.dao.BookDAO; // Cần tạo hàm updateQuantity trong BookDAO
import com.bookstore.dao.CustomerDAO; // Cần tạo hàm updatePoint trong CustomerDAO
import com.bookstore.dto.BillDTO;
import com.bookstore.dto.BillDetailDTO;

import java.util.List;

public class BillBUS {
    private BillDAO billDAO = new BillDAO();
    private BookDAO bookDAO = new BookDAO(); // Giả sử bạn đã có
    private CustomerDAO customerDAO = new CustomerDAO(); // Giả sử bạn đã có

    public boolean createBill(BillDTO bill, List<BillDetailDTO> details) {
        // 1. Insert Hóa đơn lấy ID
        int billId = billDAO.insertBill(bill);
        if (billId == -1) return false;

        // 2. Insert Chi tiết & Trừ tồn kho
        for (BillDetailDTO detail : details) {
            detail.setBillId(billId); // Gán ID hóa đơn cho chi tiết
            billDAO.insertBillDetail(detail);

            // 3. Trừ tồn kho (Cần viết hàm này trong BookDAO: UPDATE book SET quantity = quantity - ? WHERE book_id = ?)
            bookDAO.decreaseQuantity(detail.getBookId(), detail.getQuantity());
        }

        // 4. Cộng điểm cho khách (Nếu có khách)
        if (bill.getCustomerId() > 0 && bill.getEarnedPoints() > 0) {
            // Cần viết hàm trong CustomerDAO: UPDATE customer SET point = point + ? WHERE customer_id = ?
            customerDAO.addPoint(bill.getCustomerId(), bill.getEarnedPoints());
        }

        return true;
    }
}