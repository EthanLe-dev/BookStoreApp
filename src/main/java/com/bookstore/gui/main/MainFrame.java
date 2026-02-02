package com.bookstore.gui.main;

import com.bookstore.dto.EmployeeDTO;
import com.bookstore.gui.panel.SellingPanel;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContentPanel, sidebarPanel;
    private JButton btnLogout, btnSelling, btnProduct, btnPrice, btnImport, btnInventory, btnBill, btnEmployee, btnStats, btnAccount;
    private JPanel welcomePanel;
    private EmployeeDTO employee;

    public MainFrame(EmployeeDTO employee) {
        this.employee = employee;
        initUI();

        applyAuthorization();
    }

    private void initUI() {
        setTitle("Ứng Dụng Quản Lý Bán Sách");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        sidebarPanel = createSidebar();
        add(sidebarPanel, BorderLayout.WEST);

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(Color.WHITE);

        welcomePanel = createWelcomePanel();
        mainContentPanel.add(welcomePanel, "WELCOME");
        mainContentPanel.add(new SellingPanel(), "SELLING");
        mainContentPanel.add(createDummyPanel("Sản Phẩm"), "PRODUCT");
        mainContentPanel.add(createDummyPanel("Giá Bán"), "PRICE");
        mainContentPanel.add(createDummyPanel("Phiếu Nhập"), "IMPORT");
        mainContentPanel.add(createDummyPanel("Tồn Kho"), "INVENTORY");
        mainContentPanel.add(createDummyPanel("Hóa Đơn"), "BILL");
        mainContentPanel.add(createDummyPanel("Nhân Viên"), "EMPLOYEE");
        mainContentPanel.add(createDummyPanel("Thống Kê"), "STATS");
        mainContentPanel.add(createDummyPanel("Tài Khoản"), "ACCOUNT");

        add(mainContentPanel, BorderLayout.CENTER);
        cardLayout.show(mainContentPanel, "WELCOME");
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 20, 0);

        JLabel lbWelcome1 = new JLabel("Chào Mừng Trở Lại Hệ Thống");
        lbWelcome1.setFont(new Font("Segoe UI", Font.BOLD, 40));
        lbWelcome1.setForeground(Color.decode("#062D1E"));
        panel.add(lbWelcome1, gbc);

        gbc.gridy++;

        String username = employee.getEmployeeName();
        JLabel lbWelcome2 = new JLabel("Xin chào, " + username + "!");
        lbWelcome2.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        lbWelcome2.setForeground(Color.GRAY);
        panel.add(lbWelcome2, gbc);

        return panel;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.decode("#062D1E"));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel lbHeader1 = new JLabel("Quản Lý");
        lbHeader1.setForeground(Color.WHITE);
        lbHeader1.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lbHeader1.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbHeader1.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lbHeader2 = new JLabel("Bán Sách");
        lbHeader2.setForeground(Color.WHITE);
        lbHeader2.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lbHeader2.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbHeader2.setHorizontalAlignment(SwingConstants.CENTER);

        sidebar.add(lbHeader1);
        sidebar.add(lbHeader2);
        sidebar.add(Box.createVerticalStrut(20));
        JSeparator separator1 = new JSeparator();
        separator1.setMaximumSize(new Dimension(160, 1));
        separator1.setForeground(Color.GRAY);
        separator1.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(separator1);
        sidebar.add(Box.createVerticalStrut(20));

        btnSelling = createMenuButton("Bán Hàng", "cart_icon.svg");
        btnProduct = createMenuButton("Sản Phẩm", "book_icon.svg");
        btnPrice = createMenuButton("Giá Bán", "coin_icon.svg");
        btnImport = createMenuButton("Phiếu Nhập", "import_icon.svg");
        btnInventory = createMenuButton("Tồn Kho", "inventory_icon.svg");
        btnBill = createMenuButton("Hóa Đơn", "bill_icon.svg");
        btnEmployee = createMenuButton("Nhân Viên", "employee_icon.svg");
        btnStats = createMenuButton("Thống Kê", "stats_icon.svg");
        btnAccount = createMenuButton("Tài Khoản", "account_icon.svg");

        btnLogout = new JButton("Đăng xuất");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setBackground(Color.decode("#062D1E"));
        btnLogout.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnLogout.setMaximumSize(new Dimension(200, 70));
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        try {
            FlatSVGIcon svgIcon = new FlatSVGIcon("icon/logout_icon.svg");
            FlatSVGIcon.ColorFilter colorFilter = new FlatSVGIcon.ColorFilter(color -> Color.WHITE);
            svgIcon.setColorFilter(colorFilter);
            btnLogout.setIcon(svgIcon.derive(24, 24));
            btnLogout.setIconTextGap(15);

        } catch (Exception e) {
            System.err.println("Lỗi load icon SVG: logout_icon.svg");
        }
        btnLogout.setHorizontalAlignment(SwingConstants.LEFT);
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setFocusPainted(false);
        btnLogout.putClientProperty(FlatClientProperties.STYLE,
                "hoverBackground: #ff4f4f;"
        );

        btnSelling.addActionListener(e -> cardLayout.show(mainContentPanel, "SELLING"));
        btnProduct.addActionListener(e -> cardLayout.show(mainContentPanel, "PRODUCT"));
        btnPrice.addActionListener(e -> cardLayout.show(mainContentPanel, "PRICE"));
        btnImport.addActionListener(e -> cardLayout.show(mainContentPanel, "IMPORT"));
        btnInventory.addActionListener(e -> cardLayout.show(mainContentPanel, "INVENTORY"));
        btnBill.addActionListener(e -> cardLayout.show(mainContentPanel, "BILL"));
        btnEmployee.addActionListener(e -> cardLayout.show(mainContentPanel, "EMPLOYEE"));
        btnStats.addActionListener(e -> cardLayout.show(mainContentPanel, "STATS"));
        btnAccount.addActionListener(e -> cardLayout.show(mainContentPanel, "ACCOUNT"));
        btnLogout.addActionListener(e -> {
            this.dispose();
            new LoginFrame().setVisible(true);
        });

        sidebar.add(btnSelling);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnProduct);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnPrice);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnImport);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnInventory);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnBill);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnEmployee);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnStats);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnAccount);

        sidebar.add(Box.createVerticalGlue());
        JSeparator separator2 = new JSeparator();
        separator2.setMaximumSize(new Dimension(160, 1));
        separator2.setForeground(Color.GRAY);
        separator2.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(separator2);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnLogout);

        return sidebar;
    }

    private JButton createMenuButton(String text, String iconName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(Color.decode("#062D1E"));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setMaximumSize(new Dimension(200, 70));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        try {
            FlatSVGIcon svgIcon = new FlatSVGIcon("icon/" + iconName);
            FlatSVGIcon.ColorFilter colorFilter = new FlatSVGIcon.ColorFilter(color -> Color.WHITE);
            svgIcon.setColorFilter(colorFilter);
            btn.setIcon(svgIcon.derive(24, 24));
            btn.setIconTextGap(15);

        } catch (Exception e) {
            System.err.println("Lỗi load icon SVG: " + iconName);
        }
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);

        btn.putClientProperty(FlatClientProperties.STYLE,
                "hoverBackground: #00A364;"
        );
        return btn;
    }

    private JPanel createDummyPanel(String text) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        JLabel lb = new JLabel(text);
        lb.setFont(new Font("Segoe UI", Font.BOLD, 40));
        lb.setForeground(Color.LIGHT_GRAY);
        p.add(lb);
        return p;
    }

    private void applyAuthorization() {
        if (employee == null) return;

        int roleId = employee.getRoleId();

        if (roleId == 2) {
            btnProduct.setVisible(false);
            btnEmployee.setVisible(false);
            btnStats.setVisible(false);
            btnImport.setVisible(false);
            btnAccount.setVisible(false);
        }
    }
}