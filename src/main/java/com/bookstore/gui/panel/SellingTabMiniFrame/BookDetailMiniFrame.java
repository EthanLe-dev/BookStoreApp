package com.bookstore.gui.panel.SellingTabMiniFrame;

import javax.swing.*;
import java.awt.*;

public class BookDetailMiniFrame extends JFrame {
    private JPanel header, mainContent;

    public BookDetailMiniFrame() {
        initUI();
    }

    public void initUI() {
        setTitle("Chi Tiết Sản Phẩm");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(500, 700);
        setResizable(false);
        setLayout(new BorderLayout());


    }
}
