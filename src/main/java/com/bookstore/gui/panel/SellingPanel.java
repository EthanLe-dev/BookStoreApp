package com.bookstore.gui.panel;

import com.bookstore.util.AppConstant;
import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import java.awt.*;

public class SellingPanel extends JPanel {

    public SellingPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_AREA_ALIGNMENT, FlatClientProperties.TABBED_PANE_ALIGN_CENTER);
        tabbedPane.addTab("Bán Hàng", createContentPanel("Giao diện bán hàng"));
        tabbedPane.addTab("Khách Hàng", createContentPanel("Giao diện quản lý khách hàng"));
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createContentPanel(String text) {
        JPanel p = new JPanel(new GridBagLayout());
        JLabel lb = new JLabel(text);
        lb.setFont(new Font(AppConstant.FONT_NAME, Font.BOLD, 24));
        lb.setForeground(Color.GRAY);
        p.add(lb);
        return p;
    }
}