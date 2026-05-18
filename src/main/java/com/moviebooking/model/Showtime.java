package com.moviebooking.model;

public class Showtime {
    private String id;
    private String movieId;
    private String cinemaHall;
    private String date;
    private String time;
    private SeatMap seatMap;

    public Showtime() {
    }

    public Showtime(String id, String movieId, String cinemaHall, String date, String time) {
        this.id = id;
        this.movieId = movieId;
        this.cinemaHall = cinemaHall;
        this.date = date;
        this.time = time;
        this.seatMap = null; // Will be set separately if needed
    }

    public Showtime(String id, String movieId, String cinemaHall, String date, String time, SeatMap seatMap) {
        this.id = id;
        this.movieId = movieId;
        this.cinemaHall = cinemaHall;
        this.date = date;
        this.time = time;
        this.seatMap = seatMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getCinemaHall() {
        return cinemaHall;
    }

    public void setCinemaHall(String cinemaHall) {
        this.cinemaHall = cinemaHall;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public SeatMap getSeatMap() {
        return seatMap;
    }

    public void setSeatMap(SeatMap seatMap) {
        this.seatMap = seatMap;
    }
}