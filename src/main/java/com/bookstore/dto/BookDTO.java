package com.bookstore.dto;

public class BookDTO {
    private int bookId;
    private String bookName;
    private double sellingPrice;
    private int quantity;
    private String translator;
    private String image;
    private String description;
    private int status;
    private int categoryId;
    private String categoryName;
    private String tagDetail;
    private int supplierId;

    public BookDTO() {}

    public BookDTO(int bookId, String bookName, double sellingPrice, int quantity, String translator, String image, String description, int status, int categoryId, String categoryName, String tagDetail, int supplierId) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.sellingPrice = sellingPrice;
        this.quantity = quantity;
        this.translator = translator;
        this.image = image;
        this.description = description;
        this.status = status;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.tagDetail = tagDetail;
        this.supplierId = supplierId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getTranslator() {
        return translator;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getTagDetail() {
        return tagDetail;
    }

    public void setTagDetail(String tagDetail) {
        this.tagDetail = tagDetail;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }
}
