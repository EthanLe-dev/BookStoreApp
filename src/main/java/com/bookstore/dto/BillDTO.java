package com.bookstore.dto;

import java.sql.Timestamp;

public class BillDTO {
    private int billId;
    private Timestamp createdDate;
    private double totalBillPrice;
    private double tax;
    private int employeeId;
    private int customerId;
    private int paymentMethodId;
    private int earnedPoints;

    public BillDTO(int billId, Timestamp createdDate, double totalBillPrice, double tax, int employeeId, int customerId, int paymentMethodId, int earnedPoints) {
        this.billId = billId;
        this.createdDate = createdDate;
        this.totalBillPrice = totalBillPrice;
        this.tax = tax;
        this.employeeId = employeeId;
        this.customerId = customerId;
        this.paymentMethodId = paymentMethodId;
        this.earnedPoints = earnedPoints;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public double getTotalBillPrice() {
        return totalBillPrice;
    }

    public void setTotalBillPrice(double totalBillPrice) {
        this.totalBillPrice = totalBillPrice;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(int paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public int getEarnedPoints() {
        return earnedPoints;
    }

    public void setEarnedPoints(int earnedPoints) {
        this.earnedPoints = earnedPoints;
    }
}




