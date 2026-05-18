package com.moviebooking.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Booking {
    private final String bookingId;
    private final String requestId;
    private final String customerName;
    private final String customerEmail;
    private final String movieTitle;
    private final String showtimeDate;
    private final String showtimeTime;
    private final String cinemaHall;
    private final List<String> seats;
    private final double totalPrice;
    private final String confirmedAt;
    private final BookingStatus status;

    public Booking(String bookingId, String requestId, String customerName, String customerEmail, String movieTitle,
                   String showtimeDate, String showtimeTime, String cinemaHall, List<String> seats, double totalPrice,
                   String confirmedAt, BookingStatus status) {
        this.bookingId = bookingId;
        this.requestId = requestId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.movieTitle = movieTitle;
        this.showtimeDate = showtimeDate;
        this.showtimeTime = showtimeTime;
        this.cinemaHall = cinemaHall;
        this.seats = seats == null ? new ArrayList<>() : new ArrayList<>(seats);
        this.totalPrice = totalPrice;
        this.confirmedAt = confirmedAt;
        this.status = status == null ? BookingStatus.CONFIRMED : status;
    }

    public Booking withStatus(BookingStatus newStatus) {
        return new Booking(bookingId, requestId, customerName, customerEmail, movieTitle, showtimeDate, showtimeTime,
                cinemaHall, seats, totalPrice, confirmedAt, newStatus);
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getShowtimeDate() {
        return showtimeDate;
    }

    public String getShowtimeTime() {
        return showtimeTime;
    }

    public String getCinemaHall() {
        return cinemaHall;
    }

    public List<String> getSeats() {
        return Collections.unmodifiableList(seats);
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getConfirmedAt() {
        return confirmedAt;
    }

    public BookingStatus getStatus() {
        return status;
    }
}
