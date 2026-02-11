package com.bookstore.gui.panel.SellingTab;

import com.bookstore.bus.*;
import com.bookstore.dto.*;
import com.bookstore.util.AppConstant;
import com.bookstore.util.MoneyFormatter;
import com.bookstore.util.SearchableComboBox;
import com.bookstore.util.SharedData;
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
import java.io.File;
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
    private JButton btnCustomer;
    private JTextField txtEmployee, txtPromo, txtRank;
    private JLabel lbSubTotal, lbDiscountPromo, lbDiscountMember, lbFinalTotal;
    private JButton btnRefresh, btnDelete, btnEdit, btnPay;

    private CategoryBUS categoryBUS = new CategoryBUS();
    private AuthorBUS authorBUS = new AuthorBUS();
    private BookBUS bookBUS = new BookBUS();
    private CustomerBUS customerBUS = new CustomerBUS();
    private PromotionBUS promotionBUS = new PromotionBUS();

    private List<BookDTO> listBooks = new ArrayList<>();
    private List<CustomerDTO> listCustomers = new ArrayList<>();
    private CustomerDTO currentCustomer = null;

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
        Font lbDetailFont = new Font(AppConstant.FONT_NAME, Font.BOLD, 15);
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
        btnViewBookDetail.setBackground(Color.decode(AppConstant.GREEN_COLOR_CODE));
        btnViewBookDetail.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnViewBookDetail.putClientProperty(FlatClientProperties.STYLE, "arc: 10; hoverBackground: #00A364;");

        btnAddToCart = new JButton("Thêm vào hóa đơn");
        btnAddToCart.setFont(new Font(AppConstant.FONT_NAME, Font.BOLD, 15));
        btnAddToCart.setForeground(Color.WHITE);
        btnAddToCart.setBackground(Color.decode(AppConstant.GREEN_COLOR_CODE));
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

        String employeeName = "Chưa đăng nhập";
        if (SharedData.currentUser != null) {
            employeeName = SharedData.currentUser.getEmployeeName();
        }
        txtEmployee = new JTextField(employeeName);
        txtEmployee.setEditable(false);
        txtEmployee.setBorder(BorderFactory.createTitledBorder("Nhân viên"));
        txtEmployee.setBackground(Color.WHITE);

        btnCustomer = new JButton("Khách lẻ");
        btnCustomer.setBorder(BorderFactory.createTitledBorder("Khách hàng (Nhấn để tìm)"));
        btnCustomer.setBackground(Color.WHITE);
        btnCustomer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCustomer.setHorizontalAlignment(SwingConstants.LEFT);

        txtPromo = new JTextField("Không có CTKM nào áp dụng");
        txtPromo.setEditable(false);
        txtPromo.setBorder(BorderFactory.createTitledBorder("Khuyến mãi áp dụng"));
        txtPromo.setBackground(Color.WHITE);
        txtPromo.setFont(new Font(AppConstant.FONT_NAME, Font.PLAIN, 14));
        txtPromo.setForeground(Color.GRAY);

        String rank = "-";
        txtRank = new JTextField(rank);
        txtRank.setEditable(false);
        txtRank.setBorder(BorderFactory.createTitledBorder("Hạng thành viên"));
        txtRank.setBackground(Color.WHITE);

        pInfo.add(txtEmployee);
        pInfo.add(btnCustomer);
        pInfo.add(txtPromo);
        pInfo.add(txtRank);

        JPanel pActions = new JPanel(new GridLayout(1, 3, 10, 0));
        pActions.setOpaque(false);

        btnRefresh = createActionButton("Làm mới", AppConstant.GREEN_COLOR_CODE);
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

        String[] cartHeaders = {"Mã", "Tên sản phẩm", "SL", "Đơn giá", "% Giảm", "Thành tiền"};
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
        setColumnWidth(tblCart, 2, 40);
        setColumnWidth(tblCart, 3,100);
        setColumnWidth(tblCart, 4, 100);
        setColumnWidth(tblCart, 5, 100);
        tblCart.getColumnModel().removeColumn(tblCart.getColumnModel().getColumn(0));

        JScrollPane scrollCart = new JScrollPane(tblCart);
        scrollCart.getViewport().setBackground(Color.WHITE);

        JPanel pSummary = new JPanel(new GridLayout(4, 1, 0, 5));
        pSummary.setBackground(Color.WHITE);

        lbSubTotal = new JLabel("0đ", SwingConstants.RIGHT);
        lbSubTotal.setFont(new Font(AppConstant.FONT_NAME, Font.BOLD, 16));
        lbDiscountPromo = new JLabel("0đ", SwingConstants.RIGHT);
        lbDiscountPromo.setFont(new Font(AppConstant.FONT_NAME, Font.BOLD, 16));
        lbDiscountMember = new JLabel("0đ", SwingConstants.RIGHT);
        lbDiscountMember.setFont(new Font(AppConstant.FONT_NAME, Font.BOLD, 16));
        lbFinalTotal = new JLabel("0đ", SwingConstants.RIGHT);
        lbFinalTotal.setFont(new Font(AppConstant.FONT_NAME, Font.BOLD, 16));
        lbFinalTotal.setForeground(Color.RED);

        pSummary.add(createSummaryLabel("Tổng tiền hàng:", lbSubTotal)); // Tổng giá gốc
        pSummary.add(createSummaryLabel("Giảm giá sách:", lbDiscountPromo)); // Tổng tiền được giảm do CTKM
        pSummary.add(createSummaryLabel("Giảm giá thành viên:", lbDiscountMember));

        JPanel pFinal = createSummaryLabel("CÒN LẠI: ", lbFinalTotal);
        JLabel lbTitleFinal = (JLabel) pFinal.getComponent(0); // Lấy label title
        lbTitleFinal.setForeground(Color.RED);
        pSummary.add(pFinal);

        btnPay = new JButton("Hoàn thành");
        btnPay.setFont(new Font(AppConstant.FONT_NAME, Font.BOLD, 18));
        btnPay.setForeground(Color.WHITE);
        btnPay.setBackground(Color.decode(AppConstant.GREEN_COLOR_CODE));
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

    private JPanel createSummaryLabel(String title, JLabel valueLabel) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel lbTitle = new JLabel(title);
        lbTitle.setFont(new Font(AppConstant.FONT_NAME, Font.BOLD, 14));

        p.add(lbTitle, BorderLayout.WEST);
        p.add(valueLabel, BorderLayout.EAST);
        return p;
    }

    private void styleTable(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.decode(AppConstant.GREEN_COLOR_CODE));
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
            File file = new java.io.File(imagePath);

            if (file.exists()) {
                ImageIcon imageIcon = new ImageIcon(imagePath);
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

    private void openCustomerSearchDialog() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        CustomerSearchDialog dialog = new CustomerSearchDialog(parent);
        dialog.setVisible(true);

        CustomerDTO customer = dialog.getSelectedCustomer();
        if (customer != null) {
            this.currentCustomer = customer;
            btnCustomer.setText(customer.getCustomerName());

            int point = customer.getPoint();
            String rank = "Thành viên";
            if (point >= 1000) rank = "Vàng";
            else if (point >= 500) rank = "Bạc";

            txtRank.setText(rank);
        } else {
            this.currentCustomer = null;
            btnCustomer.setText("Khách lẻ");
            txtRank.setText("-");
        }
        calculateTotal();
    }

    private void openBookDetailDialog() {
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

        if (selectedBook != null) {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            BookDetailDialog detailFrame = new BookDetailDialog(parentFrame, selectedBook);
            detailFrame.setVisible(true);
        }
    }

    private void calculateTotal() {
        double totalOriginal = 0; // Tổng tiền gốc (chưa giảm)
        double totalCart = 0;     // Tổng tiền sau khi giảm giá sách (giá trên bảng)
        boolean hasPromotion = false;

        // 1. Duyệt bảng tính tiền
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            // Lấy số lượng
            int quantity = Integer.parseInt(cartModel.getValueAt(i, 2).toString());

            // Lấy đơn giá gốc (Cột 3) - Cần parse từ chuỗi "50.000đ" -> 50000.0
            double originalPrice = MoneyFormatter.toDouble(cartModel.getValueAt(i, 3).toString());

            // Lấy thành tiền đã giảm (Cột 5)
            double discountedTotalRow = MoneyFormatter.toDouble(cartModel.getValueAt(i, 5).toString());

            // Cộng dồn
            totalOriginal += (originalPrice * quantity);
            totalCart += discountedTotalRow;

            String percentStr = cartModel.getValueAt(i, 4).toString().replace("%", "").trim();
            int percent = 0;
            try {
                percent = Integer.parseInt(percentStr);
            } catch (Exception e) { percent = 0; }

            if (percent > 0) {
                hasPromotion = true;
            }
        }

        if (hasPromotion) {
            txtPromo.setText("Có CTKM được áp dụng");
            txtPromo.setForeground(Color.decode(AppConstant.GREEN_COLOR_CODE));
            txtPromo.setFont(new Font(AppConstant.FONT_NAME, Font.PLAIN, 14));
        } else {
            txtPromo.setText("Không có CTKM nào áp dụng");
            txtPromo.setForeground(Color.GRAY);
            txtPromo.setFont(new Font(AppConstant.FONT_NAME, Font.PLAIN, 14));
        }

        // 2. Tính giảm giá CTKM (Chênh lệch giữa giá gốc và giá thực bán)
        double discountPromo = totalOriginal - totalCart;

        // 3. Tính giảm giá Thành viên (Áp dụng trên totalCart)
        double discountMember = 0;
        if (currentCustomer != null) {
            int point = currentCustomer.getPoint();
            double memberPercent = 0;

            // Logic hạng thành viên (ví dụ)
            if (point >= 10000) memberPercent = 15;
            else if (point >= 5000) memberPercent = 10;
            else if (point >= 2000) memberPercent = 5;

            discountMember = totalCart * (memberPercent / 100.0);
        }

        // 4. Tổng cuối cùng
        double finalTotal = totalCart - discountMember;

        // 5. Cập nhật lên giao diện
        lbSubTotal.setText(MoneyFormatter.toVND(totalOriginal));
        lbDiscountPromo.setText(MoneyFormatter.toVND(discountPromo));
        lbDiscountMember.setText(MoneyFormatter.toVND(discountMember));
        lbFinalTotal.setText(MoneyFormatter.toVND(finalTotal));
    }

    private void addToCart() {
        int selectedRow = tblProduct.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sách cần thêm!");
            return;
        }

        int modelRow = tblProduct.convertRowIndexToModel(selectedRow);
        int bookId = (int) productModel.getValueAt(modelRow, 0);

        BookDTO selectedBook = listBooks.stream()
                .filter(b -> b.getBookId() == bookId)
                .findFirst()
                .orElse(null);

        if (selectedBook == null) return;

        if (selectedBook.getQuantity() <= 0) {
            JOptionPane.showMessageDialog(this, "Sản phẩm này đã hết hàng!");
            return;
        }

        String input = JOptionPane.showInputDialog(this,
                "Nhập số lượng cần thêm (Tồn kho: " + selectedBook.getQuantity() + "):",
                "1");

        if (input == null) return;

        int quantityToAdd;
        try {
            quantityToAdd = Integer.parseInt(input);
            if (quantityToAdd <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0!");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số nguyên hợp lệ!");
            return;
        }

        double promoPercent = promotionBUS.getPromotionPercentByBook(bookId);
        double oldPrice = selectedBook.getSellingPrice();
        double newPrice = oldPrice * (1 - promoPercent / 100.0);

        boolean exists = false;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            int idInCart = Integer.parseInt(cartModel.getValueAt(i, 0).toString());

            if (idInCart == bookId) {
                int currentQty = Integer.parseInt(cartModel.getValueAt(i, 2).toString());
                int newQty = currentQty + quantityToAdd;

                if (newQty > selectedBook.getQuantity()) {
                    JOptionPane.showMessageDialog(this,
                            "Tổng số lượng (" + newQty + ") vượt quá tồn kho hiện tại (" + selectedBook.getQuantity() + ")!");
                    return;
                }

                cartModel.setValueAt(newQty, i, 2);

                double total = newQty * newPrice;
                cartModel.setValueAt(MoneyFormatter.toVND(total), i, 5);
                exists = true;
                break;
            }
        }

        if (!exists) {
            if (quantityToAdd > selectedBook.getQuantity()) {
                JOptionPane.showMessageDialog(this,
                        "Số lượng nhập (" + quantityToAdd + ") vượt quá tồn kho (" + selectedBook.getQuantity() + ")!");
                return;
            }

            double total = quantityToAdd * newPrice;
            cartModel.addRow(new Object[]{
                    selectedBook.getBookId(),
                    selectedBook.getBookName(),
                    quantityToAdd,
                    MoneyFormatter.toVND(oldPrice),
                    (int)promoPercent + "%",
                    MoneyFormatter.toVND(total)
            });
        }

        calculateTotal();
    }

    private void refreshCart() {
        if (cartModel.getRowCount() == 0) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa toàn bộ giỏ hàng không?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            cartModel.setRowCount(0); // Xóa hết dòng trong bảng
            calculateTotal(); // Tính lại tổng tiền (về 0)

            // Reset text thông báo khuyến mãi
            txtPromo.setText("Không có CTKM nào áp dụng");
            txtPromo.setForeground(Color.GRAY);
            txtPromo.setFont(new Font(AppConstant.FONT_NAME, Font.PLAIN, 14));
        }
    }

    private void deleteRowFromCart() {
        int selectedRow = tblCart.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm trong giỏ hàng để xóa!");
            return;
        }

        // Xóa dòng đang chọn
        cartModel.removeRow(selectedRow);

        // Tính lại tổng tiền sau khi xóa
        calculateTotal();
    }

    private void editRowFromCart() {
        int selectedRow = tblCart.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm trong giỏ hàng để sửa số lượng!");
            return;
        }

        // A. Lấy dữ liệu hiện tại của dòng đó
        // Cột 0 là ID sách (ẩn hoặc hiện tùy cấu hình trước đó) -> Lấy ID để check tồn kho
        int bookId = Integer.parseInt(cartModel.getValueAt(selectedRow, 0).toString());
        int currentQty = Integer.parseInt(cartModel.getValueAt(selectedRow, 2).toString());

        // Lấy giá gốc (Cột 3)
        double oldPrice = MoneyFormatter.toDouble(cartModel.getValueAt(selectedRow, 3).toString());

        // Lấy % giảm giá (Cột 4) để tính ra Giá thực bán
        String percentStr = cartModel.getValueAt(selectedRow, 4).toString().replace("%", "").trim();
        int promoPercent = 0;
        try { promoPercent = Integer.parseInt(percentStr); } catch (Exception ex) {}

        // Giá thực bán = Giá gốc * (1 - %giảm)
        double actualPrice = oldPrice * (1 - promoPercent / 100.0);

        // B. Tìm sách trong listBooks để lấy tồn kho thực tế
        BookDTO book = listBooks.stream()
                .filter(b -> b.getBookId() == bookId)
                .findFirst()
                .orElse(null);

        if (book == null) return; // Trường hợp hiếm gặp

        // C. Hiển thị Dialog nhập số lượng mới
        String input = JOptionPane.showInputDialog(this,
                "Cập nhật số lượng cho: " + book.getBookName() + "\n(Tồn kho: " + book.getQuantity() + ")",
                currentQty);

        if (input == null) return; // Người dùng bấm Cancel

        try {
            int newQty = Integer.parseInt(input);

            // Validate số lượng
            if (newQty <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0!");
                return;
            }

            if (newQty > book.getQuantity()) {
                JOptionPane.showMessageDialog(this,
                        "Số lượng nhập (" + newQty + ") vượt quá tồn kho hiện tại (" + book.getQuantity() + ")!");
                return;
            }

            // D. Cập nhật lại bảng
            cartModel.setValueAt(newQty, selectedRow, 2); // Cập nhật cột Số lượng

            // Tính lại Thành tiền mới (SL mới * Giá thực bán)
            double newTotal = newQty * actualPrice;
            cartModel.setValueAt(MoneyFormatter.toVND(newTotal), selectedRow, 5); // Cập nhật cột Thành tiền

            // E. Tính lại tổng bill toàn cục
            calculateTotal();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số nguyên hợp lệ!");
        }
    }

    private void createNewBill() {
        // 1. Kiểm tra giỏ hàng
        if (cartModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng đang trống!");
            return;
        }

        // 2. Chọn phương thức thanh toán
        String[] options = {"Tiền mặt", "Chuyển khoản", "Thẻ tín dụng"};
        int paymentChoice = JOptionPane.showOptionDialog(this,
                "Chọn phương thức thanh toán:", "Thanh toán",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (paymentChoice == -1) return; // Người dùng đóng dialog

        int paymentMethodId = paymentChoice + 1; // 1: Tiền mặt, 2: CK, 3: Thẻ (Tương ứng DB)

        // 3. Chuẩn bị dữ liệu BillDTO
        double finalTotal = MoneyFormatter.toDouble(lbFinalTotal.getText());
        int employeeId = SharedData.currentUser != null ? SharedData.currentUser.getEmployeeId() : 1;

        int customerId = 0;
        if (currentCustomer != null) {
            customerId = currentCustomer.getCustomerId();
        }

        // Tính điểm thưởng (Ví dụ: 10.000đ = 1 điểm)
        int earnedPoints = 0;
        if (customerId > 0) {
            earnedPoints = (int) (finalTotal / 10000);
        }

        BillDTO bill = new BillDTO(0, null, finalTotal, 0.08, employeeId, customerId, paymentMethodId, earnedPoints);

        // 4. Chuẩn bị dữ liệu List<BillDetailDTO>
        List<BillDetailDTO> details = new ArrayList<>();
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            int bookId = Integer.parseInt(cartModel.getValueAt(i, 0).toString());
            int quantity = Integer.parseInt(cartModel.getValueAt(i, 2).toString());

            // Lưu ý: Giá lưu vào BillDetail phải là giá ĐÃ GIẢM (Thực tế bán)
            // Lấy từ cột "Thành tiền" chia cho "Số lượng" để ra đơn giá thực tế
            double totalRow = MoneyFormatter.toDouble(cartModel.getValueAt(i, 5).toString());
            double unitPrice = totalRow / quantity;

            details.add(new BillDetailDTO(0, bookId, quantity, unitPrice));
        }

        // 5. Gọi BUS để lưu xuống Database
        BillBUS billBUS = new BillBUS();
        boolean success = billBUS.createBill(bill, details);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Thanh toán thành công!\nTổng tiền: " + MoneyFormatter.toVND(finalTotal) +
                            "\nĐiểm tích lũy: " + earnedPoints);

            // 6. Reset giao diện để bán đơn mới
            resetSellingPanel();
        } else {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi tạo hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetSellingPanel() {
        cartModel.setRowCount(0);
        currentCustomer = null;
        btnCustomer.setText("Khách lẻ");
        txtRank.setText("-");
        txtPromo.setText("Không có CTKM nào áp dụng");
        txtPromo.setForeground(Color.GRAY);

        calculateTotal(); // Về 0

        // Reset bảng sản phẩm để cập nhật lại số lượng tồn kho mới
        listBooks = bookBUS.selectAllBooks(); // Load lại từ DB
        updateProductTable(listBooks);
    }

    private void addEvents() {
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

        btnViewBookDetail.addActionListener(e -> openBookDetailDialog());
        btnAddToCart.addActionListener(e -> addToCart());

        btnCustomer.addActionListener(e -> openCustomerSearchDialog());
        btnRefresh.addActionListener(e -> refreshCart());
        btnDelete.addActionListener(e -> deleteRowFromCart());
        btnEdit.addActionListener(e -> editRowFromCart());
        btnPay.addActionListener(e -> createNewBill());
    }
}