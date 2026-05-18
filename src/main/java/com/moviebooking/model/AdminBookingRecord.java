package com.moviebooking.model;

public class AdminBookingRecord {
    private final String bookingId;
    private final String customer;
    private final String movie;
    private final String seats;
    private final String status;
    private final double total;

    public AdminBookingRecord(String bookingId, String customer, String movie, String seats, String status, double total) {
        this.bookingId = bookingId;
        this.customer = customer;
        this.movie = movie;
        this.seats = seats;
        this.status = status;
        this.total = total;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getCustomer() {
        return customer;
    }

    public String getMovie() {
        return movie;
    }

    public String getSeats() {
        return seats;
    }

    public String getStatus() {
        return status;
    }

    public double getTotal() {
        return total;
    }
}
