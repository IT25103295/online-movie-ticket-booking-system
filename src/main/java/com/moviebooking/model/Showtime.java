package com.moviebooking.model;

public class Showtime {
    private String id;
    private String movieId;
    private String cinemaHall;
    private String date;
    private String time;

    public Showtime() {
    }

    public Showtime(String id, String movieId, String cinemaHall, String date, String time) {
        this.id = id;
        this.movieId = movieId;
        this.cinemaHall = cinemaHall;
        this.date = date;
        this.time = time;
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
}
