package com.bookstore.gui.panel;

import com.bookstore.bus.AuthorBUS;
import com.bookstore.bus.BookBUS;
import com.bookstore.bus.CategoryBUS;
import com.bookstore.dto.AuthorDTO;
import com.bookstore.dto.BookDTO;
import com.bookstore.dto.CategoryDTO;
import com.bookstore.util.AppConstant;
import com.bookstore.util.MoneyFormatter;
import com.bookstore.util.SearchableComboBox;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class SellingPanel extends JPanel {
    private SearchableComboBox<CategoryDTO> cboCategory;
    private SearchableComboBox<AuthorDTO> cboAuthor;
    private JTextField txtPriceFrom, txtPriceTo, txtSearch;
    private JTable tblProduct;
    private DefaultTableModel productModel;
    private JLabel lbProductImage, lbBookName, lbCategory, lbPrice, lbQuantity;
    private JButton btnViewBookDetail, btnAddToCart;

    private JTable tblCart;
    private DefaultTableModel cartModel;
    private JTextField txtEmployee, txtCustomer, txtRank, txtPromo;
    private JLabel lbSubTotal, lbDiscountPromo, lbDiscountMember, lbFinalTotal;
    private JButton btnRefresh, btnDelete, btnEdit, btnPay;

    private CategoryBUS categoryBUS = new CategoryBUS();
    private AuthorBUS authorBUS = new AuthorBUS();
    private BookBUS bookBUS = new BookBUS();

    private List<BookDTO> listBooks = new ArrayList<>();

    public SellingPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new GridLayout(1, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        setBackground(Color.WHITE);

        JPanel leftPanel = createLeftPanel();
        add(leftPanel);

        JPanel rightPanel = createRightPanel();
        add(rightPanel);

        loadCategoriesToComBoBox();
        loadAuthorsToComboBox();
        loadBookTable();
        addEvents();
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JPanel pFilter = new JPanel(new GridLayout(3, 1, 10, 10));
        pFilter.setOpaque(false);

        JPanel pRow1 = new JPanel(new GridLayout(1,2,10,10));
        pRow1.setOpaque(false);

        cboCategory = new SearchableComboBox<>();
        cboAuthor = new SearchableComboBox<>();

        pRow1.add(cboCategory);
        pRow1.add(cboAuthor);
        pFilter.add(pRow1);

        JPanel pRow2 = new JPanel(new GridLayout(1,4,10,10));
        pRow2.setOpaque(false);
        pRow2.add(new JLabel("Giá từ:", SwingConstants.RIGHT));
        txtPriceFrom = new JTextField();
        pRow2.add(txtPriceFrom);
        pRow2.add(new JLabel("Đến:", SwingConstants.RIGHT));
        txtPriceTo = new JTextField();
        pRow2.add(txtPriceTo);
        pFilter.add(pRow2);

        txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm kiếm theo tên hoặc mã sách...");
        FlatSVGIcon searchIcon = new FlatSVGIcon("icon/search_icon.svg").derive(20,20);
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, searchIcon);
        pFilter.add(txtSearch);

        String[] headers = {"Mã", "Tên sản phẩm", "Thể loại", "Đơn giá", "SL"};
        productModel = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblProduct = new JTable(productModel);
        tblProduct.setRowHeight(30);
        tblProduct.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        styleTable(tblProduct);
        setColumnWidth(tblProduct, 1, 200);
        setColumnWidth(tblProduct, 3, 100);
        setColumnWidth(tblProduct, 4, 50);
        tblProduct.getColumnModel().removeColumn(tblProduct.getColumnModel().getColumn(0));
        tblProduct.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblProduct.getSelectedRow() != -1) {
                fillProductDetail();
            }
        });

        JScrollPane scrollTable = new JScrollPane(tblProduct);
        scrollTable.getViewport().setBackground(Color.WHITE);

        JPanel pDetail = new JPanel(new BorderLayout(10, 0));
        pDetail.setBackground(Color.WHITE);
        pDetail.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        lbProductImage = new JLabel("Ảnh", SwingConstants.CENTER);
        lbProductImage.setPreferredSize(new Dimension(110, 150));
        lbProductImage.setOpaque(true);
        lbProductImage.setBackground(Color.decode("#06b962"));
        lbProductImage.setForeground(Color.BLACK);
        lbProductImage.setFont(new Font(AppConstant.FONT_NAME, Font.PLAIN, 14));

        lbBookName = new JLabel("Chọn sản phẩm");
        lbCategory = new JLabel("-");
        lbPrice = new JLabel("-");
        lbQuantity = new JLabel("-");
        Font lbDetailFont = new Font(AppConstant.FONT_NAME, Font.BOLD, 16);
        lbBookName.setFont(lbDetailFont);
        lbCategory.setFont(lbDetailFont);
        lbPrice.setFont(lbDetailFont);
        lbQuantity.setFont(lbDetailFont);

        JPanel pInfo = new JPanel(new GridLayout(2, 2, 10, 5));
        pInfo.setBackground(Color.WHITE);
        pInfo.add(createDetailLabel("Tên sản phẩm:", lbBookName));
        pInfo.add(createDetailLabel("Thể loại:", lbCategory));
        pInfo.add(createDetailLabel("Đơn giá:", lbPrice));
        pInfo.add(createDetailLabel("Số lượng tồn:", lbQuantity));

        btnViewBookDetail = new JButton("Xem chi tiết");
        btnViewBookDetail.setFont(new Font(AppConstant.FONT_NAME, Font.BOLD, 15));
        btnViewBookDetail.setForeground(Color.WHITE);
        btnViewBookDetail.setBackground(Color.decode("#114732"));
        btnViewBookDetail.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnViewBookDetail.putClientProperty(FlatClientProperties.STYLE, "arc: 10; hoverBackground: #00A364;");

        btnAddToCart = new JButton("Thêm vào hóa đơn");
        btnAddToCart.setFont(new Font(AppConstant.FONT_NAME, Font.BOLD, 15));
        btnAddToCart.setForeground(Color.WHITE);
        btnAddToCart.setBackground(Color.decode("#114732"));
        btnAddToCart.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAddToCart.putClientProperty(FlatClientProperties.STYLE, "arc: 10; hoverBackground: #00A364;");

        JPanel pActionBtn = new JPanel(new GridLayout(1, 2, 10, 10));
        pActionBtn.setOpaque(false);
        pActionBtn.add(btnViewBookDetail);
        pActionBtn.add(btnAddToCart);

        JPanel pDetailRight = new JPanel(new BorderLayout(0, 10));
        pDetailRight.setOpaque(false);
        pDetailRight.add(pInfo, BorderLayout.CENTER);
        pDetailRight.add(pActionBtn, BorderLayout.SOUTH);

        pDetail.add(lbProductImage, BorderLayout.WEST);
        pDetail.add(pDetailRight, BorderLayout.CENTER);

        panel.add(pFilter, BorderLayout.NORTH);
        panel.add(scrollTable, BorderLayout.CENTER);
        panel.add(pDetail, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JPanel pInfo = new JPanel(new GridLayout(2, 2, 10, 10));
        pInfo.setOpaque(false);

        txtEmployee = new JTextField("Admin");
        txtEmployee.setEditable(false);
        txtEmployee.setBorder(BorderFactory.createTitledBorder("Nhân viên"));

        txtCustomer = new JTextField("Khách lẻ");
        txtCustomer.setEditable(false);
        txtCustomer.setBorder(BorderFactory.createTitledBorder("Khách hàng"));

        txtPromo = new JTextField("Không có");
        txtPromo.setEditable(false);
        txtPromo.setBorder(BorderFactory.createTitledBorder("Chương trình KM"));

        txtRank = new JTextField("Thành viên");
        txtRank.setEditable(false);
        txtRank.setBorder(BorderFactory.createTitledBorder("Hạng thành viên"));

        pInfo.add(txtEmployee);
        pInfo.add(txtCustomer);
        pInfo.add(txtPromo);
        pInfo.add(txtRank);

        JPanel pActions = new JPanel(new GridLayout(1, 3, 10, 0));
        pActions.setOpaque(false);

        btnRefresh = createActionButton("Làm mới", "#114732");
        btnRefresh.putClientProperty(FlatClientProperties.STYLE, "hoverBackground: #00A364;");
        btnDelete = createActionButton("Xóa", "#E53935");
        btnDelete.putClientProperty(FlatClientProperties.STYLE, "hoverBackground: #ff5a5a;");
        btnEdit = createActionButton("Sửa", "#FBC02D");
        btnEdit.putClientProperty(FlatClientProperties.STYLE, "hoverBackground: #ffdd53;");

        pActions.add(btnRefresh);
        pActions.add(btnDelete);
        pActions.add(btnEdit);

        JPanel pTopRight = new JPanel(new BorderLayout(0, 10));
        pTopRight.setOpaque(false);
        pTopRight.add(pInfo, BorderLayout.CENTER);
        pTopRight.add(pActions, BorderLayout.SOUTH);

        String[] cartHeaders = {"Tên sản phẩm", "SL", "Đơn giá", "Thành tiền"};
        cartModel = new DefaultTableModel(cartHeaders, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblCart = new JTable(cartModel);
        tblCart.setRowHeight(30);
        tblCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        styleTable(tblCart);
        setColumnWidth(tblCart, 1, 40);
        setColumnWidth(tblCart, 2,100);
        setColumnWidth(tblCart, 3, 100);

        cartModel.addRow(new Object[]{"Doraemon", 2, "25.000", "50.000"});
        cartModel.addRow(new Object[]{"Mắt Biếc", 1, "110.000", "110.000"});

        JScrollPane scrollCart = new JScrollPane(tblCart);
        scrollCart.getViewport().setBackground(Color.WHITE);

        JPanel pSummary = new JPanel(new GridLayout(4, 1, 0, 5));
        pSummary.setBackground(Color.WHITE);

        pSummary.add(createSummaryRow("Tổng hóa đơn:", "160.000đ", false));
        pSummary.add(createSummaryRow("Giảm giá CTKM:", "0đ", false));
        pSummary.add(createSummaryRow("Giảm giá thành viên:", "0đ", false));
        pSummary.add(createSummaryRow("CÒN LẠI:", "160.000đ", true));
        btnPay = new JButton("Hoàn thành");
        btnPay.setFont(new Font(AppConstant.FONT_NAME, Font.BOLD, 18));
        btnPay.setForeground(Color.WHITE);
        btnPay.setBackground(Color.decode("#114732"));
        btnPay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPay.putClientProperty(FlatClientProperties.STYLE, "arc: 10; hoverBackground: #00A364;");
        btnPay.setPreferredSize(new Dimension(0, 50));

        JPanel pBottomRight = new JPanel(new BorderLayout(0, 10));
        pBottomRight.setOpaque(false);
        pBottomRight.add(pSummary, BorderLayout.CENTER);
        pBottomRight.add(btnPay, BorderLayout.SOUTH);

        panel.add(pTopRight, BorderLayout.NORTH);
        panel.add(scrollCart, BorderLayout.CENTER);
        panel.add(pBottomRight, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDetailLabel(String title, JComponent value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel lbTitle = new JLabel(title);
        lbTitle.setForeground(Color.GRAY);
        lbTitle.setFont(new Font(AppConstant.FONT_NAME, Font.PLAIN, 14));

        p.add(lbTitle, BorderLayout.NORTH);
        p.add(value, BorderLayout.CENTER);
        return p;
    }

    private JButton createActionButton(String text, String colorHex) {
        JButton btn = new JButton(text);
        btn.setFont(new Font(AppConstant.FONT_NAME, Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(Color.decode(colorHex));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 15;");
        return btn;
    }

    private JPanel createSummaryRow(String title, String value, boolean isRed) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel lbTitle = new JLabel(title);
        lbTitle.setFont(new Font(AppConstant.FONT_NAME, Font.BOLD, 14));

        JLabel lbValue = new JLabel(value);
        lbValue.setFont(new Font(AppConstant.FONT_NAME, Font.BOLD, 16));

        if (isRed) {
            lbTitle.setForeground(Color.RED);
            lbValue.setForeground(Color.RED);
        }

        p.add(lbTitle, BorderLayout.WEST);
        p.add(lbValue, BorderLayout.EAST);
        return p;
    }

    private void styleTable(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.decode("#114732"));
        header.setForeground(Color.WHITE);
        header.setFont(new Font(AppConstant.FONT_NAME, Font.BOLD, 15));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        table.setRowHeight(35);
        table.setShowGrid(true);
        table.setSelectionBackground(Color.decode("#d4ffee"));
        table.setSelectionForeground(Color.BLACK);
    }

    private void setColumnWidth(JTable table, int columnIndex, int width) {
        table.getColumnModel().getColumn(columnIndex).setPreferredWidth(width);
        table.getColumnModel().getColumn(columnIndex).setMinWidth(width);
         table.getColumnModel().getColumn(columnIndex).setMaxWidth(width);
    }

    private void loadCategoriesToComBoBox() {
        List<CategoryDTO> list = categoryBUS.selectAllCategories();
        list.add(0, new CategoryDTO(0, "Tất cả thể loại"));
        cboCategory.updateData(list);
    }

    private void loadAuthorsToComboBox() {
        List<AuthorDTO> list = authorBUS.selectAllAuthors();
        list.add(0, new AuthorDTO(0, "Tất cả tác giả", "Tất cả tác giả"));
        cboAuthor.updateData(list);
    }

    private void loadBookTable() {
        listBooks = bookBUS.selectAllBooks();
        updateProductTable(listBooks);
    }

    private void fillProductDetail() {
        int selectedRow = tblProduct.getSelectedRow();
        if (selectedRow == -1) return;

        int modelRow = tblProduct.convertRowIndexToModel(selectedRow);
        int bookId = (int) productModel.getValueAt(modelRow, 0);

        BookDTO selectedBook = null;
        for (BookDTO book : listBooks) {
            if (book.getBookId() == bookId) {
                selectedBook = book;
                break;
            }
        }

        if (selectedBook == null) return;

        lbBookName.setText(selectedBook.getBookName());
        lbCategory.setText(selectedBook.getCategoryName());
        lbPrice.setText(MoneyFormatter.toVND(selectedBook.getSellingPrice()));
        lbQuantity.setText(String.valueOf(selectedBook.getQuantity()));

        String imageName = selectedBook.getImage();
        if (imageName != null && !imageName.trim().isEmpty()) {
            String imagePath = "data/book_covers/" + imageName;
            ImageIcon imageIcon = new ImageIcon(imagePath);

            if (imageIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                Image image = imageIcon.getImage().getScaledInstance(110, 150, Image.SCALE_SMOOTH);
                lbProductImage.setIcon(new ImageIcon(image));
                lbProductImage.setText("");
            } else {
                setDefaultImage();
            }
        } else {
            setDefaultImage();
        }
    }

    private void setDefaultImage() {
        lbProductImage.setIcon(null);
        lbProductImage.setText("Chưa có ảnh");
        lbProductImage.setBackground(Color.decode("#06b962"));
    }

    private void filterBooks() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        CategoryDTO selectedCate = (CategoryDTO) cboCategory.getSelectedItem();
        int cateId = (selectedCate != null) ? selectedCate.getCategoryId() : 0;
        AuthorDTO selectedAuthor = (AuthorDTO) cboAuthor.getSelectedItem();
        int authorId = (selectedAuthor != null) ? selectedAuthor.getAuthorId() : 0;
        double minPrice = 0;
        double maxPrice = Double.MAX_VALUE;

        try {
            if (!txtPriceFrom.getText().trim().isEmpty()) {
                minPrice = MoneyFormatter.toDouble(txtPriceFrom.getText());
            }
            if (!txtPriceTo.getText().trim().isEmpty()) {
                maxPrice = MoneyFormatter.toDouble(txtPriceTo.getText());
            }
        } catch (Exception e) {

        }

        List<BookDTO> filteredList = new ArrayList<>();

        for (BookDTO book : listBooks) {
            boolean matchKeyword = keyword.isEmpty() || book.getBookName().toLowerCase().contains(keyword) || String.valueOf(book.getBookId()).contains(keyword);
            boolean matchCate = (cateId == 0) || (book.getCategoryId() == cateId);
            boolean matchAuthor = (authorId == 0) || (book.getAuthorIdsList().contains(authorId));
            boolean matchPrice = (book.getSellingPrice() >= minPrice && book.getSellingPrice() <= maxPrice);

            if (matchKeyword && matchCate && matchAuthor && matchPrice) {
                filteredList.add(book);
            }
        }
        updateProductTable(filteredList);
    }

    private void updateProductTable(List<BookDTO> list) {
        productModel.setRowCount(0);
        for (BookDTO book : list) {
            productModel.addRow(new Object[]{
                    book.getBookId(),
                    book.getBookName(),
                    book.getCategoryName(),
                    MoneyFormatter.toVND(book.getSellingPrice()),
                    book.getQuantity()
            });
        }
    }

    private void addEvents() {
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterBooks();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterBooks();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterBooks();
            }
        });

        cboCategory.addActionListener(e -> filterBooks());
        cboAuthor.addActionListener(e -> filterBooks());

        txtPriceTo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterBooks();
            }
        });

        txtPriceFrom.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterBooks();
            }
        });
    }
}