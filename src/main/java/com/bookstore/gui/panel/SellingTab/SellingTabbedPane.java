package com.bookstore.gui.panel.SellingTab;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import java.awt.*;

public class SellingTabbedPane extends JPanel {
    public SellingTabbedPane() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_AREA_ALIGNMENT, FlatClientProperties.TABBED_PANE_ALIGN_CENTER);
        tabbedPane.addTab("Bán Hàng", new SellingPanel());
        tabbedPane.addTab("Khách Hàng", new CustomerPanel());
        add(tabbedPane, BorderLayout.CENTER);
    }
}