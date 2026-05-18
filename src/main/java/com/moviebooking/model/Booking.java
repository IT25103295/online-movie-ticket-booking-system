package com.moviebooking.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Booking {
    private String bookingId;
    private String requestId;
    private String customerName;
    private String customerEmail;
    private String movieId;
    private String movieTitle;
    private String showtimeId;
    private String showtimeDate;
    private String showtimeTime;
    private String cinemaHall;
    private List<String> seats;
    private double totalPrice;
    private String confirmedAt;
    private BookingStatus status;

    // Default constructor
    public Booking() {
        this.seats = new ArrayList<>();
    }

    // Constructor from BookingRequest (first file style)
    public Booking(String bookingId, BookingRequest request) {
        this.bookingId = bookingId;
        this.customerName = request.getCustomerName();
        this.customerEmail = request.getCustomerEmail();
        this.movieId = request.getMovieId();
        this.showtimeId = request.getShowtimeId();
        this.seats = new ArrayList<>(request.getSelectedSeats());
        this.totalPrice = request.getTotalPrice();
        this.confirmedAt = request.getProcessedAt();
        this.status = request.getStatus();
        this.requestId = null;
        this.movieTitle = null;
        this.showtimeDate = null;
        this.showtimeTime = null;
        this.cinemaHall = null;
    }

    // Full constructor (second file style)
    public Booking(String bookingId, String requestId, String customerName, String customerEmail, String movieId,
                   String movieTitle, String showtimeId, String showtimeDate, String showtimeTime, String cinemaHall,
                   List<String> seats, double totalPrice, String confirmedAt, BookingStatus status) {
        this.bookingId = bookingId;
        this.requestId = requestId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.showtimeId = showtimeId;
        this.showtimeDate = showtimeDate;
        this.showtimeTime = showtimeTime;
        this.cinemaHall = cinemaHall;
        this.seats = seats == null ? new ArrayList<>() : new ArrayList<>(seats);
        this.totalPrice = totalPrice;
        this.confirmedAt = confirmedAt;
        this.status = status == null ? BookingStatus.CONFIRMED : status;
    }

    // WithStatus method for immutability pattern
    public Booking withStatus(BookingStatus newStatus) {
        return new Booking(bookingId, requestId, customerName, customerEmail, movieId, movieTitle, showtimeId,
                showtimeDate, showtimeTime, cinemaHall, seats, totalPrice, confirmedAt, newStatus);
    }

    // Getters
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

    public String getMovieId() {
        return movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getShowtimeId() {
        return showtimeId;
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

    // Setters (for mutable usage)
    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public void setShowtimeId(String showtimeId) {
        this.showtimeId = showtimeId;
    }

    public void setShowtimeDate(String showtimeDate) {
        this.showtimeDate = showtimeDate;
    }

    public void setShowtimeTime(String showtimeTime) {
        this.showtimeTime = showtimeTime;
    }

    public void setCinemaHall(String cinemaHall) {
        this.cinemaHall = cinemaHall;
    }

    public void setSeats(List<String> seats) {
        this.seats = seats == null ? new ArrayList<>() : new ArrayList<>(seats);
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setConfirmedAt(String confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}