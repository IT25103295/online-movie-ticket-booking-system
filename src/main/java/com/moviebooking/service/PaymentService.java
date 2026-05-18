package com.moviebooking.service;

import com.moviebooking.model.Payment;
import com.moviebooking.model.PaymentMethod;
import com.moviebooking.model.PaymentStatus;
import jakarta.servlet.ServletContext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PaymentService {
    private static final String PAYMENTS_FILE_PATH = "/WEB-INF/classes/data/payments.txt";
    private static final Object PAYMENT_FILE_LOCK = new Object();

    private final ServletContext servletContext;

    public PaymentService(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public Payment processPayment(Payment payment) {
        return processPayment(payment, "", "", "", "");
    }

    public Payment processPayment(Payment payment, String cardHolderName, String cardNumber, String expiryDate, String cvv) {
        String paidAt = LocalDateTime.now().toString();
        String reference = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String lastFourDigits = getLastFourDigits(cardNumber);

        if (!validateBasePayment(payment)) {
            payment.markFailed(paidAt, lastFourDigits, reference);
            return payment;
        }

        if (isCardPayment(payment.getPaymentMethod()) && !validateCardDetails(cardHolderName, cardNumber, expiryDate, cvv)) {
            payment.markFailed(paidAt, lastFourDigits, reference);
            return payment;
        }

        if (isCardPayment(payment.getPaymentMethod()) && normalizeDigits(cardNumber).endsWith("0000")) {
            payment.markFailed(paidAt, lastFourDigits, reference);
            return payment;
        }

        payment.markPaid(paidAt, lastFourDigits, reference);
        return payment;
    }

    public Payment getPaymentById(String paymentId) throws IOException {
        if (isBlank(paymentId)) {
            return null;
        }
        for (Payment payment : loadPayments()) {
            if (payment.getPaymentId().equals(paymentId)) {
                return payment;
            }
        }
        return null;
    }

    public List<Payment> getPaymentsForUser(String customerEmail) throws IOException {
        ArrayList<Payment> matchingPayments = new ArrayList<>();
        for (Payment payment : loadPayments()) {
            if (payment.getCustomerEmail().equalsIgnoreCase(customerEmail == null ? "" : customerEmail.trim())) {
                matchingPayments.add(payment);
            }
        }
        return matchingPayments;
    }

    public void savePayment(Payment payment) throws IOException {
        synchronized (PAYMENT_FILE_LOCK) {
            ensureDataFile();
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(getPaymentsFile(), true)))) {
                writer.println(formatPayment(payment));
            }
        }
    }

    private ArrayList<Payment> loadPayments() throws IOException {
        synchronized (PAYMENT_FILE_LOCK) {
            ensureDataFile();
            ArrayList<Payment> payments = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(getPaymentsFile()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Payment payment = parsePayment(line);
                    if (payment != null) {
                        payments.add(payment);
                    }
                }
            }
            return payments;
        }
    }

    private Payment parsePayment(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 10) {
            return null;
        }
        try {
            return new Payment(parts[0], parts[1], parts[2], parts[3], PaymentMethod.valueOf(parts[4]),
                    Double.parseDouble(parts[5]), PaymentStatus.valueOf(parts[6]), parts[7], parts[8], parts[9]);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private String formatPayment(Payment payment) {
        return String.join("|",
                clean(payment.getPaymentId()),
                clean(payment.getBookingRequestId()),
                clean(payment.getCustomerName()),
                clean(payment.getCustomerEmail()),
                payment.getPaymentMethod().name(),
                String.valueOf(payment.getAmount()),
                payment.getPaymentStatus().name(),
                clean(payment.getPaidAt()),
                clean(payment.getCardLastFourDigits()),
                clean(payment.getTransactionReference()));
    }

    private boolean validateBasePayment(Payment payment) {
        return payment != null
                && !isBlank(payment.getPaymentId())
                && !isBlank(payment.getBookingRequestId())
                && !isBlank(payment.getCustomerName())
                && !isBlank(payment.getCustomerEmail())
                && payment.getPaymentMethod() != null
                && payment.getAmount() > 0;
    }

    private boolean validateCardDetails(String cardHolderName, String cardNumber, String expiryDate, String cvv) {
        String digits = normalizeDigits(cardNumber);
        String cvvDigits = normalizeDigits(cvv);
        return !isBlank(cardHolderName)
                && !isBlank(expiryDate)
                && digits.length() >= 12
                && digits.length() <= 19
                && cvvDigits.length() >= 3
                && cvvDigits.length() <= 4;
    }

    private boolean isCardPayment(PaymentMethod paymentMethod) {
        return PaymentMethod.CREDIT_CARD.equals(paymentMethod) || PaymentMethod.DEBIT_CARD.equals(paymentMethod);
    }

    private String getLastFourDigits(String cardNumber) {
        String digits = normalizeDigits(cardNumber);
        return digits.length() < 4 ? "" : digits.substring(digits.length() - 4);
    }

    private String normalizeDigits(String value) {
        return value == null ? "" : value.replaceAll("\\D", "");
    }

    private void ensureDataFile() throws IOException {
        File file = getPaymentsFile();
        ensureParentDirectory(file);
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Unable to create payments data file.");
        }
    }

    private File getPaymentsFile() throws IOException {
        String realPath = servletContext.getRealPath(PAYMENTS_FILE_PATH);
        if (realPath == null) {
            throw new IOException("Unable to resolve payments.txt with ServletContext. Deploy the WAR as an expanded Tomcat application.");
        }
        File file = new File(realPath);
        ensureParentDirectory(file);
        return file;
    }

    private void ensureParentDirectory(File file) throws IOException {
        File parentDirectory = file.getParentFile();
        if (parentDirectory != null && !parentDirectory.exists() && !parentDirectory.mkdirs()) {
            throw new IOException("Unable to create payment data directory: " + parentDirectory.getPath());
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.replace("|", " ").trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
