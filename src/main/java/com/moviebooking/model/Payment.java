package com.moviebooking.model;

public class Payment {
    private final String paymentId;
    private final String bookingRequestId;
    private final String customerName;
    private final String customerEmail;
    private final PaymentMethod paymentMethod;
    private final double amount;
    private PaymentStatus paymentStatus;
    private String paidAt;
    private String cardLastFourDigits;
    private String transactionReference;

    public Payment(String paymentId, String bookingRequestId, String customerName, String customerEmail,
                   PaymentMethod paymentMethod, double amount) {
        this(paymentId, bookingRequestId, customerName, customerEmail, paymentMethod, amount,
                PaymentStatus.PENDING, "", "", "");
    }

    public Payment(String paymentId, String bookingRequestId, String customerName, String customerEmail,
                   PaymentMethod paymentMethod, double amount, PaymentStatus paymentStatus, String paidAt,
                   String cardLastFourDigits, String transactionReference) {
        this.paymentId = paymentId;
        this.bookingRequestId = bookingRequestId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.paymentStatus = paymentStatus == null ? PaymentStatus.PENDING : paymentStatus;
        this.paidAt = paidAt;
        this.cardLastFourDigits = cardLastFourDigits;
        this.transactionReference = transactionReference;
    }

    public void markPaid(String paidAt, String cardLastFourDigits, String transactionReference) {
        this.paymentStatus = PaymentStatus.PAID;
        this.paidAt = paidAt;
        this.cardLastFourDigits = cardLastFourDigits;
        this.transactionReference = transactionReference;
    }

    public void markFailed(String paidAt, String cardLastFourDigits, String transactionReference) {
        this.paymentStatus = PaymentStatus.FAILED;
        this.paidAt = paidAt;
        this.cardLastFourDigits = cardLastFourDigits;
        this.transactionReference = transactionReference;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getBookingRequestId() {
        return bookingRequestId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public String getPaidAt() {
        return paidAt;
    }

    public String getCardLastFourDigits() {
        return cardLastFourDigits;
    }

    public String getTransactionReference() {
        return transactionReference;
    }
}
