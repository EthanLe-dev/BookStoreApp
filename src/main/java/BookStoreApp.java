import com.bookstore.gui.LoginFrame;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class BookStoreApp {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
