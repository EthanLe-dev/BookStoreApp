package com.bookstore.gui.panel.SellingTab;

import com.bookstore.bus.CustomerBUS;
import com.bookstore.dto.CustomerDTO;
import com.bookstore.util.AppConstant;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;

public class CustomerSearchDialog extends JDialog {
    private JTextField txtPhone;
    private JLabel lbName, lbRank, lbPoints;
    private JButton btnSelect, btnGuest, btnFind;

    private CustomerDTO selectedCustomer = null;
    private CustomerBUS customerBUS = new CustomerBUS();

    public CustomerSearchDialog(JFrame parent) {
        super(parent, "Tìm kiếm khách hàng", true);
        initUI();
    }

    private void initUI() {
        setSize(450,350);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10,10));
        setResizable(false);
        setBackground(Color.WHITE);

        JPanel pSearch = new JPanel(new BorderLayout(5,5));
        pSearch.setBorder(BorderFactory.createEmptyBorder(10,20,0,20));
        pSearch.setOpaque(false);

        pSearch.add(new JLabel("Nhập số điện thoại:"), BorderLayout.NORTH);
        txtPhone = new JTextField();
        txtPhone.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ví dụ: 0912345678");
        txtPhone.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");
        txtPhone.setFont(new Font(AppConstant.FONT_NAME, Font.PLAIN, 14));

        txtPhone.addActionListener(e -> performSearch());

        btnFind = new JButton("Tìm");
        btnFind.setBackground(Color.decode(AppConstant.GREEN_COLOR_CODE));
        btnFind.setForeground(Color.WHITE);
        btnFind.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");
        btnFind.addActionListener(e -> performSearch());

        JPanel pInput = new JPanel(new BorderLayout(5,0));
        pInput.setOpaque(false);
        pInput.add(txtPhone, BorderLayout.CENTER);
        pInput.add(btnFind, BorderLayout.EAST);
        pSearch.add(pInput, BorderLayout.CENTER);

        JPanel pResult = new JPanel(new GridLayout(3,1,5,5));
        pResult.setBorder(BorderFactory.createTitledBorder("Kết quả tìm kiếm"));
        pResult.setOpaque(false);

        lbName = createResultLabel("Tên khách hàng: -");
        lbRank = createResultLabel("Hạng thành viên: -");
        lbPoints = createResultLabel("Điểm tích lũy: -");

        pResult.add(lbName);
        pResult.add(lbRank);
        pResult.add(lbPoints);

        JPanel pCenter = new JPanel(new BorderLayout());
        pCenter.setOpaque(false);
        pCenter.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        pCenter.add(pResult, BorderLayout.CENTER);

        JPanel pBottom = new JPanel(new GridLayout(1,2,10,10));
        pBottom.setBorder(BorderFactory.createEmptyBorder(0,20,10,20));
        pBottom.setOpaque(false);

        btnGuest = new JButton("Khách vãng lai");
        btnGuest.setPreferredSize(new Dimension(1, 40));
        btnGuest.setFont(new Font(AppConstant.FONT_NAME, Font.BOLD, 14));
        btnGuest.addActionListener(e -> {
            this.selectedCustomer = null;
            dispose();
        });

        btnSelect = new JButton("Chọn khách hàng này");
        btnSelect.setPreferredSize(new Dimension(1,40));
        btnSelect.setBackground(Color.decode(AppConstant.GREEN_COLOR_CODE));
        btnSelect.setForeground(Color.WHITE);
        btnSelect.setFont(new Font(AppConstant.FONT_NAME, Font.BOLD, 14));
        btnSelect.setEnabled(false);
        btnSelect.addActionListener(e -> {
            if (selectedCustomer != null) {
                dispose();
            }
        });

        pBottom.add(btnGuest);
        pBottom.add(btnSelect);

        add(pSearch, BorderLayout.NORTH);
        add(pCenter, BorderLayout.CENTER);
        add(pBottom, BorderLayout.SOUTH);
    }

    private void performSearch() {
        String phone = txtPhone.getText().trim();
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Số điện thoại!");
            return;
        }

        CustomerDTO customer = customerBUS.selectByPhone(phone);
        if (customer != null) {
            this.selectedCustomer = customer;
            lbName.setText("Tên khách hàng: " + customer.getCustomerName());
            lbName.setFont(new Font(AppConstant.FONT_NAME, Font.PLAIN, 14));
            lbRank.setText("Hạng thành viên: " + getRankByPoint(customer.getPoint()));
            lbRank.setFont(new Font(AppConstant.FONT_NAME, Font.PLAIN, 14));
            lbPoints.setText("Điểm tích lũy: " + customer.getPoint());
            lbPoints.setFont(new Font(AppConstant.FONT_NAME, Font.PLAIN, 14));

            lbName.setForeground(Color.decode(AppConstant.GREEN_COLOR_CODE));
            btnSelect.setEnabled(true);
        } else {
            this.selectedCustomer = null;
            lbName.setText("Không tìm thấy khách hàng");
            lbName.setForeground(Color.RED);
            lbRank.setText("Hạng thành viên: -");
            lbPoints.setText("Điểm tích lũy: -");
            btnSelect.setEnabled(false);
        }
    }

    private String getRankByPoint(int point) {
        if (point >= 1000) return "Vàng";
        if (point >= 500) return "Bạc";
        return "Thành viên";
    }

    private JLabel createResultLabel(String text) {
        JLabel lb = new JLabel(text);
        lb.setFont(new Font(AppConstant.FONT_NAME, Font.PLAIN,14));
        lb.setForeground(Color.DARK_GRAY);
        return lb;
    }

    public CustomerDTO getSelectedCustomer() {
        return selectedCustomer;
    }
}

