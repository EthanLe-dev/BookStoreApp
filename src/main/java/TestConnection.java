import com.bookstore.util.DatabaseConnection;
import java.sql.Connection;

public class TestConnection {
    // Class này chỉ dùng để TEST KẾT NỐI DATABASE
    public static void main(String[] args) {
        System.out.println("--- Đang kiểm tra kết nối đến Database ---");
        Connection conn = DatabaseConnection.getConnection();

        if (conn != null) {
            System.out.println("KẾT NỐI THÀNH CÔNG!");
            System.out.println("Tên Database: bookstore_db");

            try {
                DatabaseConnection.closeConnection(conn);
                System.out.println("Đã đóng kết nối an toàn.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("KẾT NỐI THẤT BẠI!");
            System.out.println("Vui lòng kiểm tra lại:");
            System.out.println("2. Tên database 'bookstore_db' đã tạo trong MySQL chưa?");
            System.out.println("3. Password có đúng không?");
        }
    }
}