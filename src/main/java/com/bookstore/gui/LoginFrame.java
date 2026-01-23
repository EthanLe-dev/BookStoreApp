package com.bookstore.gui;

import com.bookstore.bus.AccountBUS;
import com.bookstore.dto.EmployeeDTO;
import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private AccountBUS accountBUS = new AccountBUS();

    public LoginFrame() {
        setTitle("Ứng Dụng Quản Lý Bán Sách");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(152, 185, 255));
        add(mainPanel);

        JPanel loginCard = new JPanel();
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBackground(Color.WHITE);
        loginCard.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        loginCard.putClientProperty(FlatClientProperties.STYLE, "arc: 20;");

        JLabel lbTitle = new JLabel("Đăng nhập");
        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lbTitle.setForeground(Color.BLACK);
        lbTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtUsername = new JTextField();
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tài khoản của bạn");
        styleField(txtUsername);

        txtPassword = new JPasswordField();
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Mật khẩu");
        styleField(txtPassword);

        btnLogin = new JButton("Đăng Nhập Ngay");
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setFocusPainted(false);
        btnLogin.putClientProperty(FlatClientProperties.STYLE,
                "background: #ff7777;" +
                "foreground: #FFFFFF;" +
                "font: bold 16;" +
                "borderWidth: 0;" +
                "focusWidth: 0;" +
                "innerFocusWidth: 0;" +
                "borderColor: #ff7777;" +
                "focusedBorderColor: #ff7777;" +
                "arc: 10;" +
                "margin: 10,20,10,20");

        loginCard.add(lbTitle);
        loginCard.add(Box.createVerticalStrut(30));
        loginCard.add(createInputGroup("Tên đăng nhập:", txtUsername));
        loginCard.add(Box.createVerticalStrut(15));
        loginCard.add(createInputGroup("Mật khẩu:", txtPassword));
        loginCard.add(Box.createVerticalStrut(30));
        loginCard.add(btnLogin);
        loginCard.setPreferredSize(new Dimension(450, 400));

        mainPanel.add(loginCard);

        btnLogin.addActionListener(e -> handleLogin());
        JRootPane rootPane = this.getRootPane();
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "clickLogin");
        rootPane.getActionMap().put("clickLogin", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnLogin.doClick();
            }
        });
    }

    private JPanel createInputGroup(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(1000, 70));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private void styleField(JComponent field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.putClientProperty(FlatClientProperties.STYLE,
                "arc: 10;" +
                "borderColor: #CCCCCC;" +
                "focusWidth: 1;" +
                "margin: 5,10,5,10;" +
                "showClearButton: true"
        );
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ thông tin!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            EmployeeDTO employee = accountBUS.login(username, password);
            JOptionPane.showMessageDialog(this, "Xin chào " + employee.getEmployeeName() + "!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
//            this.dispose();
//            new MainFrame().setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }
}