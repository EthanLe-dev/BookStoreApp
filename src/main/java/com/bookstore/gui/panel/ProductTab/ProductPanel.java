package com.bookstore.gui.panel.ProductTab;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

import com.bookstore.util.AppConstant;
import com.bookstore.util.SearchableComboBox;
import com.bookstore.bus.*;
import com.bookstore.dto.*;
import com.bookstore.dao.BookAuthorDAO;

public class ProductPanel extends JPanel {
    // Colors
    private static final Color MAIN_COLOR = Color.decode(AppConstant.GREEN_COLOR_CODE);
    private static final Color BUTTON_COLOR = Color.decode(AppConstant.BUTTON_COLOR);
    private static final Color BORDER_COLOR = Color.decode("#E0E0E0");
    
    // Components
    private JTextField searchField;
    private SearchableComboBox<String> authorCombo;
    private JComboBox<String> categoryCombo;
    private SearchableComboBox<String> supplierCombo;
    private JComboBox<String> statusCombo;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private Set<String> selectedTags = new HashSet<>();
    private JButton tagFilterButton;
    private JLabel noResultsLabel;
    private JScrollPane scrollPane;
    private BookBUS bookBUS;
    private AuthorBUS authorBUS;
    private CategoryBUS categoryBUS;
    private SupplierBUS supplierBUS;
    private BookAuthorDAO bookAuthorDAO;

    // Data
    private List<BookDTO> allBooks;
    private List<AuthorDTO> authors;
    private List<CategoryDTO> categories;
    private List<SupplierDTO> suppliers;
    private Set<String> allTags;

    public ProductPanel() {
        // Khởi tạo BUS
        bookBUS = new BookBUS();
        authorBUS = new AuthorBUS();
        categoryBUS = new CategoryBUS();
        supplierBUS = new SupplierBUS();
        bookAuthorDAO = new BookAuthorDAO();
        
        loadDataFromDatabase();
        
        initUI();
        loadTableData();
    }

    private void loadDataFromDatabase() {
        try {
            // 1. Load Authors
            authors = authorBUS.selectAll();
            
            // 2. Load Categories
            categories = categoryBUS.selectAll();
            
            // 3. Load Suppliers
            suppliers = supplierBUS.selectAll();
            
            // 4. Load Books
            List<BookDTO> booksFromDB = bookBUS.selectAll();
            allBooks = new ArrayList<>(booksFromDB);
            
            // 5. Load all tags từ books
            allTags = new HashSet<>();
            for (BookDTO book : booksFromDB) {
                if (book.getTagDetail() != null && !book.getTagDetail().isEmpty()) {
                    String[] tags = book.getTagDetail().split(",");
                    for (String tag : tags) {
                        allTags.add(tag.trim());
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi load dữ liệu từ database: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            
            // Fallback to empty lists
            authors = new ArrayList<>();
            categories = new ArrayList<>();
            suppliers = new ArrayList<>();
            allBooks = new ArrayList<>();
            allTags = new HashSet<>();
        }
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 0));
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        
        // Top panel (Search + Filters + Add button)
        JPanel topPanel = createTopPanel();
        contentPanel.add(topPanel, BorderLayout.NORTH);
        
        // Table panel
        JPanel tablePanel = createTablePanel();
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        
        // Search and Add button row
        JPanel searchRow = new JPanel(new BorderLayout(15, 0));
        
        // Search field
        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterBooks();
            }
        });
        
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchPanel.add(searchIcon, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        searchRow.add(searchPanel, BorderLayout.CENTER);
        
        // Add book button
        JButton addButton = createStyledButton("+ THÊM SÁCH", BUTTON_COLOR);
        addButton.setPreferredSize(new Dimension(160, 38));
        addButton.addActionListener(e -> showAddBookDialog());
        searchRow.add(addButton, BorderLayout.EAST);
        
        topPanel.add(searchRow, BorderLayout.NORTH);
        
        // Filters row
        JPanel filtersPanel = createFiltersPanel();
        topPanel.add(filtersPanel, BorderLayout.CENTER);
        
        return topPanel;
    }

    private JPanel createFiltersPanel() {
        JPanel filtersPanel = new JPanel();
        filtersPanel.setLayout(new BoxLayout(filtersPanel, BoxLayout.Y_AXIS));
        
        // Main grid panel for 2x2 filters
        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Row 0, Col 0: Author Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gridPanel.add(createFilterLabel("Tác giả:"), gbc);
        
        // Row 0, Col 1: Author ComboBox
        gbc.gridx = 1;
        gbc.weightx = 1;
        authorCombo = new SearchableComboBox<>(getAuthorNamesList());
        authorCombo.setPreferredSize(new Dimension(220, 35));
        authorCombo.addActionListener(e -> filterBooks());
        gridPanel.add(authorCombo, gbc);
        
        // Row 0, Col 2: Category Label
        gbc.gridx = 2;
        gbc.weightx = 0;
        gridPanel.add(createFilterLabel("Thể loại:"), gbc);
        
        // Row 0, Col 3: Category ComboBox
        gbc.gridx = 3;
        gbc.weightx = 1;
        categoryCombo = new JComboBox<>(getCategoryNamesList().toArray(new String[0]));
        categoryCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        categoryCombo.setBackground(Color.WHITE);
        categoryCombo.setPreferredSize(new Dimension(220, 35));
        categoryCombo.addActionListener(e -> filterBooks());
        gridPanel.add(categoryCombo, gbc);
        
        // Row 1, Col 0: Supplier Label
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gridPanel.add(createFilterLabel("Nhà cung cấp:"), gbc);
        
        // Row 1, Col 1: Supplier ComboBox
        gbc.gridx = 1;
        gbc.weightx = 1;
        supplierCombo = new SearchableComboBox<>(getSupplierNamesList());
        supplierCombo.setPreferredSize(new Dimension(220, 35));
        supplierCombo.addActionListener(e -> filterBooks());
        gridPanel.add(supplierCombo, gbc);
        
        // Row 1, Col 2: Status Label
        gbc.gridx = 2;
        gbc.weightx = 0;
        gridPanel.add(createFilterLabel("Trạng thái:"), gbc);
        
        // Row 1, Col 3: Status ComboBox
        gbc.gridx = 3;
        gbc.weightx = 1;
        statusCombo = new JComboBox<>(new String[]{"Tất cả trạng thái", "Đang bán", "Ngừng bán"});
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusCombo.setBackground(Color.WHITE);
        statusCombo.setPreferredSize(new Dimension(220, 35));
        statusCombo.addActionListener(e -> filterBooks());
        gridPanel.add(statusCombo, gbc);
        
        filtersPanel.add(gridPanel);
        filtersPanel.add(Box.createVerticalStrut(12));
        
        // Row for Tag filter and Reset button
        JPanel buttonRow = new JPanel(new GridLayout(0, 5, 8, 8));
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Tag filter button
        tagFilterButton = new JButton("Tags +");
        tagFilterButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tagFilterButton.setForeground(BUTTON_COLOR);
        tagFilterButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BUTTON_COLOR, 2),
            BorderFactory.createEmptyBorder(7, 18, 7, 18)
        ));
        tagFilterButton.setFocusPainted(false);
        tagFilterButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tagFilterButton.addActionListener(e -> showTagFilterPanel());
        buttonRow.add(tagFilterButton);
        
        // Reset filter button
        JButton resetButton = new JButton("✖ Xóa bộ lọc");
        resetButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        resetButton.setForeground(Color.decode("#666666"));
        resetButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(7, 15, 7, 15)
        ));
        resetButton.setFocusPainted(false);
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetButton.addActionListener(e -> resetFilters());
        buttonRow.add(resetButton);
        
        filtersPanel.add(buttonRow);
        
        return filtersPanel;
    }

    private JLabel createFilterLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(Color.decode("#333333"));
        return label;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        
        // Table
        String[] columns = {"Sách", "Tác giả", "Thể loại", "Nhà cung cấp", "Trạng thái", "Thao tác"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only action column editable
            }
        };
        
        bookTable = new JTable(tableModel);
        bookTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bookTable.setRowHeight(80);
        bookTable.setShowVerticalLines(true);
        bookTable.setShowHorizontalLines(true);
        bookTable.setGridColor(BORDER_COLOR);
        bookTable.setSelectionBackground(Color.decode("#E8F5E9"));
        bookTable.setSelectionForeground(Color.BLACK);
        
        // Fix: Ensure row is selected immediately on first click
        bookTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = bookTable.rowAtPoint(e.getPoint());
                int col = bookTable.columnAtPoint(e.getPoint());
                if (row >= 0 && col >= 0) {
                    bookTable.setRowSelectionInterval(row, row);
                }
            }
        });
        
        bookTable.getTableHeader().setReorderingAllowed(false);
        bookTable.getTableHeader().setResizingAllowed(false);
        
        // Custom header
        JTableHeader header = bookTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(MAIN_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        
        // Column widths - Make action column smaller
        bookTable.getColumnModel().getColumn(0).setPreferredWidth(280);
        bookTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        bookTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        bookTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        bookTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        bookTable.getColumnModel().getColumn(5).setPreferredWidth(150);
        
        // Custom renderers
        bookTable.getColumnModel().getColumn(0).setCellRenderer(new BookCellRenderer());
        bookTable.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());
        bookTable.getColumnModel().getColumn(5).setCellRenderer(new ActionCellRenderer());
        bookTable.getColumnModel().getColumn(5).setCellEditor(new ActionCellEditor());
        
        // Center align other columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        bookTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        bookTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        bookTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        
        scrollPane = new JScrollPane(bookTable);
        scrollPane.setBorder(null);
        
        noResultsLabel = new JLabel("Không có kết quả tương thích", SwingConstants.CENTER);
        noResultsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        noResultsLabel.setForeground(Color.decode("#999999"));
        noResultsLabel.setVisible(false);
        
        JPanel tableContainer = new JPanel();
        tableContainer.setLayout(new OverlayLayout(tableContainer));
        
        // Center panel for no results label
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(noResultsLabel);
        
        tableContainer.add(centerPanel);
        tableContainer.add(scrollPane);
        
        tablePanel.add(tableContainer, BorderLayout.CENTER);
        
        return tablePanel;
    }

    private void loadTableData() {
        loadTableData(allBooks);
    }

    private void loadTableData(List<BookDTO> books) {
        tableModel.setRowCount(0);
        
        // FIX 1: Show/hide no results label
        if (books.isEmpty()) {
            noResultsLabel.setVisible(true);
            bookTable.setVisible(false);
        } else {
            noResultsLabel.setVisible(false);
            bookTable.setVisible(true);
            
            for (BookDTO book : books) {
                Object[] row = new Object[6];
                row[0] = book;
                row[1] = getAuthorNamesForBook(book);
                row[2] = getCategoryName(book.getCategoryId());
                row[3] = getSupplierName(book.getSupplierId());
                row[4] = book.getStatus() == 1 ? "Đang bán" : "Ngừng bán";
                row[5] = book;
                tableModel.addRow(row);
            }
        }
    }

    private void filterBooks() {
        String searchText = searchField.getText().toLowerCase().trim();
        Object selectedAuthorObj = authorCombo.getSelectedItem();
        Object selectedCategoryObj = categoryCombo.getSelectedItem();
        Object selectedSupplierObj = supplierCombo.getSelectedItem();
        String selectedStatus = (String) statusCombo.getSelectedItem();
        
        String selectedAuthor = getSelectedFilterValue(selectedAuthorObj);
        String selectedCategory = getSelectedFilterValue(selectedCategoryObj);
        String selectedSupplier = getSelectedFilterValue(selectedSupplierObj);
        
        List<BookDTO> filtered = new ArrayList<>();
        
        for (BookDTO book : allBooks) {
            // Search filter
            if (!searchText.isEmpty() && !book.getBookName().toLowerCase().contains(searchText)) {
                continue;
            }
            
            // Author filter
            if (!selectedAuthor.isEmpty() && !selectedAuthor.equals("Tất cả tác giả")) {
                String authorNames = getAuthorNamesForBook(book);
                if (!authorNames.contains(selectedAuthor)) {
                    continue;
                }
            }
            
            // Category filter
            if (!selectedCategory.isEmpty() && !selectedCategory.equals("Tất cả thể loại")) {
                if (!getCategoryName(book.getCategoryId()).equals(selectedCategory)) {
                    continue;
                }
            }
            
            // Supplier filter
            if (!selectedSupplier.isEmpty() && !selectedSupplier.equals("Tất cả nhà cung cấp")) {
                if (!getSupplierName(book.getSupplierId()).equals(selectedSupplier)) {
                    continue;
                }
            }
            
            // Status filter
            if (!selectedStatus.equals("Tất cả trạng thái")) {
                int requiredStatus = selectedStatus.equals("Đang bán") ? 1 : 0;
                if (book.getStatus() != requiredStatus) {
                    continue;
                }
            }
            
            // Tag filter
            if (!selectedTags.isEmpty()) {
                boolean hasTag = false;
                if (book.getTagDetail() != null) {
                    String[] bookTags = book.getTagDetail().split(",");
                    for (String tag : bookTags) {
                        if (selectedTags.contains(tag.trim())) {
                            hasTag = true;
                            break;
                        }
                    }
                }
                if (!hasTag) {
                    continue;
                }
            }
            
            filtered.add(book);
        }
        
        loadTableData(filtered);
    }

    private void showTagFilterPanel() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Lọc theo Tags", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel title = new JLabel("Chọn Tags để lọc");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        contentPanel.add(title, BorderLayout.NORTH);
        
        JPanel chipsPanel = new JPanel(new GridLayout(0, 3, 8, 8));        
        for (String tag : allTags) {
            JToggleButton chip = createTagChip(tag);
            chip.setSelected(selectedTags.contains(tag));
            chipsPanel.add(chip);
        }
        
        JScrollPane scrollPane = new JScrollPane(chipsPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        JButton cancelButton = createStyledButton("Hủy", Color.decode("#757575"));
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton applyButton = createStyledButton("Áp dụng", BUTTON_COLOR);
        applyButton.addActionListener(e -> {
            selectedTags.clear();
            for (Component comp : chipsPanel.getComponents()) {
                if (comp instanceof JToggleButton) {
                    JToggleButton chip = (JToggleButton) comp;
                    if (chip.isSelected()) {
                        selectedTags.add(chip.getText());
                    }
                }
            }
            updateTagButtonText();
            filterBooks();
            dialog.dispose();
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(applyButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(contentPanel);
        dialog.setVisible(true);
    }

    private JToggleButton createTagChip(String text) {
        JToggleButton chip = new JToggleButton(text);
        chip.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chip.setFocusPainted(false);
        chip.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chip.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        
        chip.setBackground(Color.decode("#F5F5F5"));
        chip.setForeground(Color.decode("#333333"));
        
        chip.addItemListener(e -> {
            if (chip.isSelected()) {
                chip.setBackground(BUTTON_COLOR);
                chip.setForeground(Color.WHITE);
            } else {
                chip.setBackground(Color.decode("#F5F5F5"));
                chip.setForeground(Color.decode("#333333"));
            }
        });
        
        return chip;
    }

    private void updateTagButtonText() {
        if (selectedTags.isEmpty()) {
            tagFilterButton.setText("Tags +");
        } else {
            tagFilterButton.setText("Tags (" + selectedTags.size() + ")");
        }
    }

    private void resetFilters() {
        searchField.setText("");
        authorCombo.resetSelection();
        if (categoryCombo.getItemCount() > 0) {
            categoryCombo.setSelectedIndex(0);
        }
        supplierCombo.resetSelection();
        statusCombo.setSelectedIndex(0);
        selectedTags.clear();
        updateTagButtonText();
        filterBooks();
    }


    private String getSelectedFilterValue(Object selectedObj) {
        if (selectedObj == null) {
            return "";
        }
        String value = selectedObj.toString();
        return value != null ? value.trim() : "";
    }

    private void showAddBookDialog() {
        BookFormDialog dialog = new BookFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), 
            "Thêm sách mới", 
            null,
            authors, categories, suppliers, allTags
        );
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            BookDTO newBook = dialog.getBook();
            
            try {
                BookDTO bookDTO = new BookDTO();
                bookDTO.setBookName(newBook.getBookName());
                bookDTO.setSellingPrice(newBook.getSellingPrice());
                bookDTO.setQuantity(newBook.getQuantity());
                bookDTO.setTranslator(newBook.getTranslator());
                bookDTO.setImage(newBook.getImage());
                bookDTO.setDescription(newBook.getDescription());
                bookDTO.setStatus(newBook.getStatus());
                bookDTO.setCategoryId(newBook.getCategoryId());
                bookDTO.setSupplierId(newBook.getSupplierId());
                bookDTO.setTagDetail(newBook.getTagDetail());
                if (newBook.getAuthorIdsList() != null) {
                    bookDTO.getAuthorIdsList().addAll(newBook.getAuthorIdsList());
                }
                
                String result = bookBUS.addBook(bookDTO);
                
                if (result.contains("thành công")) {
                    if (newBook.getAuthorIdsList() != null && !newBook.getAuthorIdsList().isEmpty()) {
                        bookAuthorDAO.addAuthorsToBook(bookDTO.getBookId(), newBook.getAuthorIdsList());
                    }
                    loadDataFromDatabase();
                    filterBooks();
                    
                    JOptionPane.showMessageDialog(this, result, 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, result, 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Lỗi: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditBookDialog(BookDTO book) {
        BookFormDialog dialog = new BookFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), 
            "Chỉnh sửa sách", 
            book,
            authors, categories, suppliers, allTags
        );
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            BookDTO updatedBook = dialog.getBook();
            
            try {
                BookDTO bookDTO = new BookDTO();
                bookDTO.setBookId(book.getBookId());
                bookDTO.setBookName(updatedBook.getBookName());
                bookDTO.setSellingPrice(updatedBook.getSellingPrice());
                bookDTO.setQuantity(updatedBook.getQuantity());
                bookDTO.setTranslator(updatedBook.getTranslator());
                bookDTO.setImage(updatedBook.getImage());
                bookDTO.setDescription(updatedBook.getDescription());
                bookDTO.setStatus(updatedBook.getStatus());
                bookDTO.setCategoryId(updatedBook.getCategoryId());
                bookDTO.setSupplierId(updatedBook.getSupplierId());
                bookDTO.setTagDetail(updatedBook.getTagDetail());
                if (updatedBook.getAuthorIdsList() != null) {
                    bookDTO.getAuthorIdsList().addAll(updatedBook.getAuthorIdsList());
                }
                
                String result = bookBUS.updateBook(bookDTO);
                
                if (result.contains("thành công")) {
                    bookAuthorDAO.removeAllAuthorsFromBook(book.getBookId());
                    if (updatedBook.getAuthorIdsList() != null && !updatedBook.getAuthorIdsList().isEmpty()) {
                        bookAuthorDAO.addAuthorsToBook(book.getBookId(), updatedBook.getAuthorIdsList());
                    }
                    
                    loadDataFromDatabase();
                    filterBooks();
                    
                    JOptionPane.showMessageDialog(this, result, 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, result, 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Lỗi: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // FIX 4: Improved book detail dialog with better layout
    private void showBookDetail(BookDTO book) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi tiết sách", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(700, 550);
        dialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title at top
        JLabel title = new JLabel(book.getBookName());
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        contentPanel.add(title, BorderLayout.NORTH);

        // Main panel with image on left and details on right
        JPanel mainPanel = new JPanel(new BorderLayout(15, 0));
        
        // Image on left - adjusted to proper book cover ratio (2:3 ratio)
        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(160, 240)); // 2:3 ratio
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.TOP);
        imageLabel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        imageLabel.setIcon(new ImageIcon(getBookCoverImage(book, 160, 240)));
        mainPanel.add(imageLabel, BorderLayout.WEST);
        
        // Details on right
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        addDetailRow(detailsPanel, "Tác giả:", getAuthorNamesForBook(book));
        addDetailRow(detailsPanel, "Thể loại:", getCategoryName(book.getCategoryId()));
        addDetailRow(detailsPanel, "Nhà cung cấp:", getSupplierName(book.getSupplierId()));
        addDetailRow(detailsPanel, "Trạng thái:", book.getStatus() == 1 ? "Đang bán" : "Ngừng bán");
        addDetailRow(detailsPanel, "Dịch giả:", book.getTranslator() == null || book.getTranslator().trim().isEmpty() ? "Không có" : book.getTranslator());
        addDetailRow(detailsPanel, "Tags:", book.getTagDetail() == null || book.getTagDetail().trim().isEmpty() ? "Không có" : book.getTagDetail());
        
        mainPanel.add(detailsPanel, BorderLayout.CENTER);
        contentPanel.add(mainPanel, BorderLayout.CENTER);

        // Description at bottom
        if (book.getDescription() != null && !book.getDescription().trim().isEmpty()) {
            JPanel descPanel = new JPanel(new BorderLayout(5, 5));
            
            JLabel descLabel = new JLabel("Mô tả:");
            descLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            descPanel.add(descLabel, BorderLayout.NORTH);
            
            JTextArea descArea = new JTextArea(book.getDescription());
            descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            descArea.setLineWrap(true);
            descArea.setWrapStyleWord(true);
            descArea.setEditable(false);
            descArea.setBackground(Color.decode("#F5F5F5"));
            descArea.setBorder(new EmptyBorder(10, 10, 10, 10));
            
            JScrollPane descScroll = new JScrollPane(descArea);
            descScroll.setPreferredSize(new Dimension(0, 120));
            descScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
            descPanel.add(descScroll, BorderLayout.CENTER);
            
            contentPanel.add(descPanel, BorderLayout.SOUTH);
        }

        // Button panel
        JButton closeButton = createStyledButton("Đóng", BUTTON_COLOR);
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(closeButton);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 8));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLabel.setPreferredSize(new Dimension(110, 20));
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        row.add(lblLabel);
        row.add(lblValue);
        panel.add(row);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    // THAY ĐỔI: Trả về List<String> thay vì String[]
    private List<String> getAuthorNamesList() {
        List<String> names = new ArrayList<>();
        names.add("Tất cả tác giả");
        for (AuthorDTO author : authors) {
            names.add(author.getAuthorName());
        }
        return names;
    }

    private List<String> getCategoryNamesList() {
        List<String> names = new ArrayList<>();
        names.add("Tất cả thể loại");
        for (CategoryDTO category : categories) {
            names.add(category.getCategoryName());
        }
        return names;
    }

    private List<String> getSupplierNamesList() {
        List<String> names = new ArrayList<>();
        names.add("Tất cả nhà cung cấp");
        for (SupplierDTO supplier : suppliers) {
            names.add(supplier.getSupplierName());
        }
        return names;
    }

    private String getAuthorNamesForBook(BookDTO book) {
        if (book.getAuthorIdsList() == null || book.getAuthorIdsList().isEmpty()) {
            return "Không rõ";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < book.getAuthorIdsList().size(); i++) {
            int authorId = book.getAuthorIdsList().get(i);
            for (AuthorDTO author : authors) {
                if (author.getAuthorId() == authorId) {
                    sb.append(author.getAuthorName());
                    if (i < book.getAuthorIdsList().size() - 1) {
                        sb.append(", ");
                    }
                    break;
                }
            }
        }
        return sb.toString();
    }

    private String getCategoryName(int categoryId) {
        for (CategoryDTO cat : categories) {
            if (cat.getCategoryId() == categoryId) {
                return cat.getCategoryName();
            }
        }
        return "Không rõ";
    }

    private String getSupplierName(int supplierId) {
        for (SupplierDTO sup : suppliers) {
            if (sup.getSupplierId() == supplierId) {
                return sup.getSupplierName();
            }
        }
        return "Không rõ";
    }

    private Image createBookPlaceholder(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        
        g2d.setColor(Color.decode("#E0E0E0"));
        g2d.fillRect(0, 0, width, height);
        
        g2d.setColor(Color.decode("#BDBDBD"));
        g2d.drawRect(0, 0, width - 1, height - 1);
        
        g2d.setColor(Color.decode("#9E9E9E"));
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        String text = "📖";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        int y = ((height - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(text, x, y);
        
        g2d.dispose();
        return img;
    }

    private Image getBookCoverImage(BookDTO book, int width, int height) {
        if (book != null && book.getImage() != null && !book.getImage().trim().isEmpty()) {
            File imageFile = new File(book.getImage());
            if (imageFile.exists()) {
                ImageIcon icon = new ImageIcon(book.getImage());
                if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                    return icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                }
            }
        }
        return createBookPlaceholder(width, height);
    }

    // FIX 2: Completely rewritten text truncation - smarter word wrapping
    private String truncateText(String text, Font font, int maxWidth, int maxLines) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }

        FontMetrics fm = getFontMetrics(font);
        String[] words = text.split("\\s+");
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        boolean needsEllipsis = false;

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            String testLine;
            
            if (currentLine.length() == 0) {
                testLine = word;
            } else {
                testLine = currentLine + " " + word;
            }
            
            int width = fm.stringWidth(testLine);
            
            if (width <= maxWidth) {
                // Word fits on current line
                currentLine = new StringBuilder(testLine);
                
                // If this is the last word and we haven't exceeded max lines
                if (i == words.length - 1 && lines.size() < maxLines) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                }
            } else {
                // Word doesn't fit on current line
                if (currentLine.length() > 0) {
                    // Save current line and start new one
                    lines.add(currentLine.toString());
                    
                    if (lines.size() >= maxLines) {
                        // We've hit the line limit
                        needsEllipsis = true;
                        break;
                    }
                    
                    currentLine = new StringBuilder(word);
                    
                    // If this is the last word
                    if (i == words.length - 1) {
                        if (lines.size() < maxLines) {
                            lines.add(currentLine.toString());
                        } else {
                            needsEllipsis = true;
                        }
                        currentLine = new StringBuilder();
                    }
                } else {
                    // Single word is too long for one line
                    String truncatedWord = truncateSingleWord(word, fm, maxWidth - fm.stringWidth("..."));
                    lines.add(truncatedWord + "...");
                    needsEllipsis = false; // Already added ellipsis
                    
                    if (lines.size() >= maxLines) {
                        break;
                    }
                    currentLine = new StringBuilder();
                }
            }
        }

        // Handle any remaining text
        if (currentLine.length() > 0) {
            if (lines.size() < maxLines) {
                lines.add(currentLine.toString());
            } else {
                needsEllipsis = true;
            }
        }

        // Build result
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                result.append("<br>");
            }
            
            String line = lines.get(i);
            
            // Add ellipsis to last line if needed
            if (i == lines.size() - 1 && needsEllipsis) {
                String withEllipsis = line + "...";
                if (fm.stringWidth(withEllipsis) > maxWidth) {
                    // Need to truncate to fit ellipsis
                    line = truncateSingleWord(line, fm, maxWidth - fm.stringWidth("..."));
                }
                result.append(line).append("...");
            } else {
                result.append(line);
            }
        }

        return result.toString();
    }

    private String truncateSingleWord(String word, FontMetrics fm, int maxWidth) {
        if (fm.stringWidth(word) <= maxWidth) {
            return word;
        }
        
        StringBuilder truncated = new StringBuilder();
        for (char c : word.toCharArray()) {
            String test = truncated.toString() + c;
            if (fm.stringWidth(test) > maxWidth) {
                break;
            }
            truncated.append(c);
        }
        return truncated.toString();
    }

    // Cell renderers
    class BookCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            BookDTO book = (BookDTO) value;
            
            JPanel panel = new JPanel(new BorderLayout(10, 0));
            panel.setOpaque(true);
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR),
                new EmptyBorder(5, 10, 5, 10)
            ));
            
            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                panel.setBackground(table.getBackground());
            }
            
            JLabel imageLabel = new JLabel();
            imageLabel.setPreferredSize(new Dimension(50, 70));
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            imageLabel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
            imageLabel.setOpaque(true);
            imageLabel.setBackground(Color.decode("#F5F5F5"));
            imageLabel.setIcon(new ImageIcon(getBookCoverImage(book, 50, 70)));
            
            panel.add(imageLabel, BorderLayout.WEST);
            
            // FIX 2: Better text truncation with proper width (account for image + padding)
            int availableWidth = table.getColumnModel().getColumn(0).getWidth() - 80; // 50px image + 30px padding
            String truncatedName = truncateText(book.getBookName(), new Font("Segoe UI", Font.BOLD, 13), availableWidth, 2);
            
            JLabel nameLabel = new JLabel("<html><b>" + truncatedName + "</b></html>");
            nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            nameLabel.setVerticalAlignment(SwingConstants.CENTER);
            nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
            panel.add(nameLabel, BorderLayout.CENTER);
            
            return panel;
        }
    }

    class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            JLabel label = new JLabel(value.toString());
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setOpaque(true);
            label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR),
                new EmptyBorder(5, 10, 5, 10)
            ));
            
            if (value.toString().equals("Đang bán")) {
                label.setForeground(Color.decode("#2E7D32"));
                label.setBackground(Color.decode("#E8F5E9"));
            } else {
                label.setForeground(Color.decode("#C62828"));
                label.setBackground(Color.decode("#FFEBEE"));
            }
            
            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
            }
            
            return label;
        }
    }

    class ActionCellRenderer extends JPanel implements TableCellRenderer {
        private JButton editButton;
        private JButton viewButton;
        
        public ActionCellRenderer() {
            setLayout(new GridBagLayout());
            setOpaque(true);
            setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(0, 4, 0, 4);
            
            editButton = new JButton("Sửa");
            editButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            editButton.setForeground(Color.WHITE);
            editButton.setBackground(Color.decode("#2196F3"));
            editButton.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
            editButton.setFocusPainted(false);
            
            viewButton = new JButton("Xem");
            viewButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            viewButton.setForeground(Color.WHITE);
            viewButton.setBackground(Color.decode("#757575"));
            viewButton.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
            viewButton.setFocusPainted(false);
            
            add(editButton, gbc);
            gbc.gridx = 1;
            add(viewButton, gbc);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            
            return this;
        }
    }

    class ActionCellEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton editButton;
        private JButton viewButton;
        private BookDTO currentBook;
        
        public ActionCellEditor() {
            panel = new JPanel(new GridBagLayout());
            panel.setOpaque(true);
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(0, 4, 0, 4);
            
            editButton = new JButton("Sửa");
            editButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            editButton.setForeground(Color.WHITE);
            editButton.setBackground(Color.decode("#2196F3"));
            editButton.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
            editButton.setFocusPainted(false);
            editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            editButton.addActionListener(e -> {
                fireEditingStopped();
                SwingUtilities.invokeLater(() -> showEditBookDialog(currentBook));
            });
            
            viewButton = new JButton("Xem");
            viewButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            viewButton.setForeground(Color.WHITE);
            viewButton.setBackground(Color.decode("#757575"));
            viewButton.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
            viewButton.setFocusPainted(false);
            viewButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            viewButton.addActionListener(e -> {
                fireEditingStopped();
                // Use SwingUtilities.invokeLater to ensure dialog opens after cell editing stops
                SwingUtilities.invokeLater(() -> showBookDetail(currentBook));
            });
            
            panel.add(editButton, gbc);
            gbc.gridx = 1;
            panel.add(viewButton, gbc);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentBook = (BookDTO) value;
            
            // Always highlight the row when editing
            panel.setBackground(table.getSelectionBackground());
            
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return currentBook;
        }
    }
}