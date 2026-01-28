import com.bookstore.bus.BillBUS;
import com.bookstore.dto.BillDetailDTO;
import com.bookstore.dto.BillDTO;
import java.util.ArrayList;
import java.util.List;

public class TestBanHang {
    public static void main(String[] args) {
        BillDTO bill = new BillDTO();
        bill.setEmployeeId(2);
        bill.setCustomerId(1);
        bill.setPaymentMethodId(1);

        List<BillDetailDTO> cart = new ArrayList<>();
        cart.add(new BillDetailDTO(0, 1, 2, 120000));
        cart.add(new BillDetailDTO(0, 2, 1, 250000));

        BillBUS billBUS = new BillBUS();
        String result = billBUS.sellBook(bill, cart);

        System.out.println("KẾT QUẢ: " + result);
    }
}