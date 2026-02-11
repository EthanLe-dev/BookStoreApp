package com.bookstore.gui.panel.ProductTab;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import java.awt.*;

public class ProductTab extends JPanel {
    public ProductTab() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_AREA_ALIGNMENT, FlatClientProperties.TABBED_PANE_ALIGN_CENTER);
        tabbedPane.addTab("Sản phẩm", new ProductPanel());
        tabbedPane.addTab("Tác giả", new AuthorPanel());
        tabbedPane.addTab("Thể loại", new CategoryPanel());
        tabbedPane.addTab("Nhà cung cấp", new SupplierPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }   
}